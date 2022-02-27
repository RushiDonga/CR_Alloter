package com.scupe.cralloter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class teacher_AllStudents extends AppCompatActivity {

    Intent getText;
    String strCode;
    boolean internetConnection = true;

    ListView listView;
    CardView dialog;
    TextView total;
    TextView text;
    TextView internet;
    ImageView image;
    Button gotIt;
    ArrayList<String> arrayList = new ArrayList<>();

    DatabaseReference databaseReference;
    DatabaseReference removed;  // Remove the Student

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher__all_students);

        // Set the Title of ActionBar
        getSupportActionBar().setTitle("All Students");

        // Type Casting
        listView = (ListView) findViewById(R.id.listView);
        total = (TextView) findViewById(R.id.total);
        image = (ImageView) findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);
        dialog = (CardView) findViewById(R.id.dialogue);
        gotIt = (Button) findViewById(R.id.gotIt);
        internet = (TextView )findViewById(R.id.internet);

        image.setVisibility(View.INVISIBLE);
        text.setVisibility(View.INVISIBLE);
        dialog.setVisibility(View.INVISIBLE);
        listView.setVerticalScrollBarEnabled(false);

        check_Network();

        // Get the Data Passed
        getText = getIntent();

        check_Network();

        getData();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                check_Network();

                if(internetConnection) {

                    final String name = (String) listView.getItemAtPosition(position);

                    AlertDialog.Builder alert = new AlertDialog.Builder(teacher_AllStudents.this);
                    alert.setTitle("Are you Sure...?");
                    alert.setMessage("Wanna remove " + name + " from the Election..??");
                    alert.setPositiveButton("Yess..!!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {

                            removed = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(strCode).child("REMOVED");
                            removed.child(name).setValue("Removed");

                            CountDownTimer timer = new CountDownTimer(1000, 500) {
                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {

                                    animate_dialog();
                                }
                            }.start();
                        }
                    });
                    alert.setNegativeButton("No..!!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert.show();
                }

                return true;
            }
        });
    }

    // Checking the INTERNET CONNECTION
    @SuppressLint("SetTextI18n")
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


    // Animate the Dialogue Box
    private void animate_dialog() {

        dialog.setVisibility(View.VISIBLE);
        gotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_Network();
                    dialog.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void getData() {
        strCode = getText.getStringExtra("code");
        Toast.makeText(this, strCode, Toast.LENGTH_SHORT).show();

        // Firebase Variables
        databaseReference = FirebaseDatabase.getInstance().getReference("AllUsers").child(strCode);

        databaseReference.child("Student").addValueEventListener(new ValueEventListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int total_people = (int) dataSnapshot.getChildrenCount();
                total.setText("Total Students : " + total_people);
                arrayList.clear();

                if(total_people == 0){
                    total.setVisibility(View.INVISIBLE);
                    image.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                }else{
                    image.setVisibility(View.INVISIBLE);
                    text.setVisibility(View.INVISIBLE);
                }

                if (dataSnapshot.exists()) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        String name = ds.getKey();
                        arrayList.add(name);
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.design_listview, arrayList);
                    listView.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
