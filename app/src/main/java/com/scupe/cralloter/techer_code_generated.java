package com.scupe.cralloter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class techer_code_generated extends AppCompatActivity {

    TextView name;
    TextView code;
    Button start;
    LinearLayout members_layout;
    LinearLayout cr_layout;
    CardView card_voted;
    CardView card_winner;
    TextView members;
    TextView all_Cr;
    TextView total_voters;
    TextView all_voters;
    TextView textView;
    TextView internet;
    boolean internet_connection = true;

    String text = "  Start  ";
    int total_CR = 0;

    // Get the Passed Details
    Intent teacher_come;

    String code_teacher;
    String teacher_name;
    String newElectionCode;

    // Firebase Variables
    DatabaseReference firebaseDatabase;
    DatabaseReference situation_database;  // Checking the Status of the Election ie started or not
    DatabaseReference display_all_members;  // Check the Total Number of Students Joined Via Code
    DatabaseReference display_all_CR;  // Check the Total Number of CR's Joined Via Code
    DatabaseReference display_all_voted;  // Check the Total number of People Who Voted

    public static final String SHARED_PREFERENCES = "sharedPreferences";
    public static final String TEXT = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_techer_code_generated);

        // Setting up the Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_teacher_profile_action_bar);

        name = (TextView) findViewById(R.id.name);
        code = (TextView) findViewById(R.id.code);
        start = (Button) findViewById(R.id.start);

        // When the Teacher Comes to the Activity after generating the Code
        teacher_come = getIntent();
        code_teacher = teacher_come.getStringExtra("code");
        teacher_name = teacher_come.getStringExtra("name");
        members_layout = findViewById(R.id.member_layout);
        cr_layout = findViewById(R.id.cr_layout);
        card_voted = findViewById(R.id.card_voted);
        card_winner = findViewById(R.id.card_winner);
        members = (TextView) findViewById(R.id.members);
        all_Cr = (TextView) findViewById(R.id.all_cr);
        total_voters = (TextView) findViewById(R.id.total_voters);
        all_voters = (TextView) findViewById(R.id.all_voters);
        textView = (TextView) findViewById(R.id.text);
        internet = (TextView) findViewById(R.id.internet);

        if(newElectionCode == null){
            newElectionCode = code_teacher;
        }else{
            if(newElectionCode != code_teacher){
                loadData();
                text = "  Start  ";
                newElectionCode = code_teacher;
                saveData();
            }
        }

        // for Starting up the newElection
        newElectionCode = code_teacher;

        internet.setVisibility(View.INVISIBLE);

        check_nerwork();

        members_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_nerwork();

                if(internet_connection) {
                    Intent allStudents = new Intent(getApplicationContext(), teacher_AllStudents.class);
                    allStudents.putExtra("code", code_teacher);
                    startActivity(allStudents);
                }
            }
        });

        cr_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_nerwork();

                if(internet_connection) {
                    Intent allStudents = new Intent(getApplicationContext(), cr.class);
                    allStudents.putExtra("code", code_teacher);
                    startActivity(allStudents);
                }
            }
        });

        card_voted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_nerwork();

                if(internet_connection) {
                    Intent voted = new Intent(getApplicationContext(), voted.class);
                    voted.putExtra("code", code_teacher);
                    startActivity(voted);
                }
            }
        });

        card_winner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_nerwork();

                if(internet_connection) {
                    Intent winner = new Intent(getApplicationContext(), Winner.class);
                    winner.putExtra("code", code_teacher);
                    startActivity(winner);
                }
            }
        });

        // Starting the Election
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_nerwork();
                if(internet_connection) {
                    start_Election();
                }
            }
        });

        // Print the Name in Profile
        print();

        // Type Casting of Firebase Variables
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(code_teacher);

        loadData();
        start.setText(text);

        update_all_CR();
        update_all_members();
        update_all_voted();
    }

    // Checking the INTERNET CONNECTION
    private void check_nerwork() {

        if(haveNetwork()){
            internet.setVisibility(View.INVISIBLE);
            internet_connection = true;
        }else{
            internet.setVisibility(View.VISIBLE);
            internet_connection = false;
        }
    }

    // Checking the INTERNET CONNECTION
    private boolean haveNetwork() {

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

    private void update_all_voted() {

        display_all_voted = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(code_teacher).child("Voted");
        display_all_voted.child("Voted").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int total_voted = (int) dataSnapshot.getChildrenCount();
                total_voters.setText(Integer.toString(total_voted));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void update_all_CR() {

        display_all_CR = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(code_teacher);
        display_all_CR.child("CR").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    total_CR = (int) dataSnapshot.getChildrenCount();
                    all_Cr.setText(Integer.toString(total_CR));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Update all Members INTEGER VALUE realtime
    private void update_all_members() {

        display_all_members = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(code_teacher);
        display_all_members.child("Student").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    int total_members = (int) dataSnapshot.getChildrenCount();
                    members.setText(Integer.toString(total_members));

                    int total_voters = total_members - total_CR;
                    all_voters.setText(Integer.toString(total_voters));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveData(){

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("data", text);
        editor.putString("newElection", newElectionCode);
        editor.apply();
    }

    @SuppressLint("SetTextI18n")
    private void loadData(){

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        text = sharedPreferences.getString("data", "  Start  ");
        newElectionCode = sharedPreferences.getString("newElection", code_teacher);

        Toast.makeText(this, newElectionCode, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, code_teacher, Toast.LENGTH_SHORT).show();

        if(text.equals("  Start  ")){
            textView.setText("Press the Button to Start the Election");
        }else if(text.equals("  Stop  ")){
            textView.setText("Press the Button to Stop the Election");
        }else if(text.equals("  Done  ")){
            textView.setText("Election is Over");
        }
    }

    private void start_Election(){

        situation_database = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(code_teacher).child("Situation");

        // To Stop the Election
        loadData();

        if(text.equals("  Start  ")){

            // To Start the Election

            AlertDialog.Builder alertDialogue = new AlertDialog.Builder(techer_code_generated.this);
            alertDialogue.setTitle("Are You Sure...?");
            alertDialogue.setMessage("Wanna start the Election \nOnce it has been started cannot be stopped");
            alertDialogue.setPositiveButton("Yess..!", new DialogInterface.OnClickListener() {

                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    situation_database.setValue("True");
                    start.setText("  Stop  ");
                    text = "  Stop  ";
                    saveData();
                    Toast.makeText(techer_code_generated.this, "Election has been Started", Toast.LENGTH_SHORT).show();
                }
            });
            alertDialogue.setNegativeButton("No..!!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialogue.show();
        }

        if(text.equals("  Stop  ")){

            AlertDialog.Builder stop = new AlertDialog.Builder(techer_code_generated.this);
            stop.setTitle("Are you Sure...?");
            stop.setMessage("Wanna Stop the Election...?");
            stop.setPositiveButton("Yeah..!!", new DialogInterface.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    situation_database.setValue("False");
                    start.setText("  Done  ");
                    text = "  Done  ";
                    saveData();
                    Toast.makeText(techer_code_generated.this, "Election has been Stopped", Toast.LENGTH_SHORT).show();
                }
            });
            stop.setNegativeButton("No..!!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            stop.show();
        }

        if(text.equals(" Done ")){

            text = " Done ";
            start.setText(text);
            saveData();
        }
    }

    @SuppressLint("SetTextI18n")
    private void print() {

        name.setText(teacher_name);
        code.setText("Code:  " + code_teacher);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getDataForUserProfile();
    }

    private void getDataForUserProfile() {

        firebaseDatabase.child("Teacher").setValue(teacher_name);
        firebaseDatabase.child(code_teacher).setValue("Code");
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder backPress = new AlertDialog.Builder(techer_code_generated.this);
        backPress.setTitle("Are you Sure...?");
        backPress.setMessage("Wanna Quit the Election \nYou will not be able to join Again");
        backPress.setPositiveButton("Yess...!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        backPress.setNegativeButton("No...!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        backPress.show();
    }
}
