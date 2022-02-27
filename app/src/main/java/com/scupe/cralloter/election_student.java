package com.scupe.cralloter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class election_student extends AppCompatActivity {

    Intent getText;

    TextView name;
    TextView teacher;
    TextView status;
    TextView total_students;
    TextView total_cr;
    TextView total_voters;
    TextView internet;
    Spinner spinner;
    Button register;

    LinearLayout allMembers;
    LinearLayout cr;

    String you;
    String strName;
    String strCode;
    String strReg = "false";
    boolean internetConnection = true;

    int totalCR;
    int total_student;

    // Firebase Variables
    DatabaseReference databaseReference;  //Get the Data Loaded and Store the Data like CR,Voter etc
    DatabaseReference get_status;  // Get the Information regarding the Election
    DatabaseReference get_allMembers; // get the Real Time Update in Total Members in Card View
    DatabaseReference get_cr;  // Get the Real Time Update in Cr in Card View

    // Shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT1 = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_student);

        // Variables Type Casting
        name = (TextView) findViewById(R.id.name);
        teacher = (TextView) findViewById(R.id.teacher);
        status = (TextView) findViewById(R.id.status);
        total_students = (TextView) findViewById(R.id.total_students) ;
        total_cr = (TextView)findViewById(R.id.total_cr);
        total_voters = (TextView) findViewById(R.id.voters);
        spinner = (Spinner) findViewById(R.id.spinner);
        register = (Button) findViewById(R.id.register);
        allMembers = (LinearLayout) findViewById(R.id.member);
        cr = (LinearLayout) findViewById(R.id.cr);
        internet = (TextView) findViewById(R.id.internet);

        internet.setVisibility(View.INVISIBLE);

        // Change the Title of Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

        getSpinnerItems();

        getText = getIntent();
        strName = getText.getStringExtra("name");
        name.setText(strName);
        strCode = getText.getStringExtra("code");

        check_Network();

        // Saving and Loading the Data when the Activity is Reopened
        loadData();

        // Firebase Variables
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsers");

        // Setting up the Name of Teacher
        databaseReference.child(strCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    String teacherName = dataSnapshot.child("Teacher").getValue().toString();
                    teacher.setText(teacherName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_Network();

                if(internetConnection) {
                    display();
                }
            }
        });

        // Pressing the All Members on Card View
        allMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_Network();

                if(internetConnection) {
                    goto_all_Members();
                }
            }
        });

        // Pressing the Cr in card View
        cr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_Network();

                if(internetConnection) {
                    goto_cr();
                }
            }
        });

        // Updating the All Members in Card View
        update_allMembers();
        update_cr();

        upDate_status();
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_Network();

                if(internetConnection) {
                    Intent battleGround = new Intent(getApplicationContext(), battle_ground.class);
                    battleGround.putExtra("name", strName);
                    battleGround.putExtra("code", strCode);
                    startActivity(battleGround);
                }
            }
        });
    }

    // Checking the INTERNET CONNECTION
    private void check_Network() {

        if(getInternetINFO()){
            internet.setVisibility(View.INVISIBLE);
            internetConnection = true;
        }else{
            internet.setVisibility(View.VISIBLE);
            internetConnection = false;
        }
    }

    // Checking the INTERNET CONNECTION
    private boolean getInternetINFO() {

        Boolean have_wifi = false;
        Boolean have_network = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        assert connectivityManager != null;
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

        for(NetworkInfo info : networkInfo){

            if(info.getTypeName().equalsIgnoreCase("WIFI")){
                if(info.isConnected()){
                    have_wifi = true;
                }
            }

            if(info.getTypeName().equalsIgnoreCase("MOBILE")){
                if(info.isConnected()){
                    have_network = true;
                }
            }
        }
        return have_wifi || have_network;
    }

    private void update_cr() {

        get_cr = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(strCode);
        get_cr.child("CR").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    totalCR = (int) dataSnapshot.getChildrenCount();
                    total_cr.setText(Integer.toString(totalCR));

                    int totalVOTERS = total_student - totalCR;
                    total_voters.setText(Integer.toString(totalVOTERS));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Updating All Members in Card View
    private void update_allMembers() {

        get_allMembers = (FirebaseDatabase.getInstance().getReference().child("AllUsers").child(strCode));
        get_allMembers.child("Student").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    total_student = (int) dataSnapshot.getChildrenCount();
                    total_students.setText(Integer.toString(total_student));

                    int totalVOTERS = total_student - totalCR;
                    total_voters.setText(Integer.toString(totalVOTERS));
                }else{
                    total_students.setText(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void upDate_status() {

        get_status = (FirebaseDatabase.getInstance().getReference().child("AllUsers")).child(strCode);
        get_status.child("Situation").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    if(dataSnapshot.getValue().equals("True")){

                        status.setText("Status : Started");
                    }else if(dataSnapshot.getValue().equals("False")){

                        status.setText("Status : Election is over");
                    }
                }else{

                    status.setText("Status : Not yet Started");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void goto_cr() {

        Intent cr = new Intent(getApplicationContext(), student_cr.class);
        cr.putExtra("code", strCode);
        startActivity(cr);
    }

    private void goto_all_Members() {

        Intent allMembers = new Intent(getApplicationContext(), all_members.class);
        allMembers.putExtra("name", strName);
        allMembers.putExtra("code", strCode);
        startActivity(allMembers);
    }

    // get the Data from the Spinner
    private void display() {

        loadData();
        switch (strReg) {

            case "false":

                loadData();
                if (you.equals("You are a...?")) {

                    Toast.makeText(election_student.this, "You are a...?", Toast.LENGTH_SHORT).show();
                } else {

                    if (you.equals("CR (Class Representative)")) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(election_student.this);
                        builder.setTitle("Are You Sure..?");
                        builder.setMessage("Wanna be a CR...? \nIt cannot be changed Later");
                        builder.setPositiveButton("Yes..!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                databaseReference.child(strCode).child("CR").child(strName).setValue("CR");
                                strReg = "CR";
                                saveData();
                                Toast.makeText(election_student.this, "Registered as a CR", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.setNegativeButton("No..!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    } else if (you.equals("Voter")) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(election_student.this);
                        alert.setTitle("Are You Sure...?");
                        alert.setMessage("Wanna Register as a Voter \nYou won't be able to change later");
                        alert.setPositiveButton("Yess..!!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(election_student.this, "Registered as Voter", Toast.LENGTH_SHORT).show();
                                strReg = "Voter";
                                saveData();
                            }
                        });
                        alert.setNegativeButton("No..!!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alert.show();
                    }
                }
                break;
            case "CR":

                Toast.makeText(election_student.this, "Already Registered as CR", Toast.LENGTH_SHORT).show();
                break;
            case "Voter":

                Toast.makeText(election_student.this, "Already Registered as Voter", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // Saved Preferences
    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT1, strReg);
        editor.apply();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        strReg = sharedPreferences.getString(TEXT1, "false");
    }

    // Spinner
    private void getSpinnerItems() {

        ArrayList<Custom_Spinner_Items> list = new ArrayList<>();
        list.add(new Custom_Spinner_Items("You are a...?", R.drawable.you_are_a));
        list.add(new Custom_Spinner_Items("CR (Class Representative)", R.drawable.student));
        list.add(new Custom_Spinner_Items("Voter", R.drawable.cr));

        final Custom_Spinner_Adapter customSpinnerAdapter = new Custom_Spinner_Adapter(this, list);

        if(spinner != null){

            spinner.setAdapter(customSpinnerAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Custom_Spinner_Items items = (Custom_Spinner_Items) customSpinnerAdapter.getItem(position);
                    assert items != null;
                    you = items.getSpinnerText();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alert = new AlertDialog.Builder(election_student.this);
        alert.setTitle("Are you Sure...?");
        alert.setMessage("Wanna leave this Election \nYou will not be able to Join again");
        alert.setPositiveButton("Yess..!!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        alert.setNegativeButton("No..!!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        alert.show();
    }
}
