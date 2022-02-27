package com.scupe.cralloter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class student extends AppCompatActivity {
    Intent student;

    // Variables
    EditText code;
    TextView internet;
    TextView name_text;
    TextView incorrect_code;
    Button go;

    String strCode;
    String strName;
    Boolean internetConnection;

    AlertDialog alertDialog;

    // Firebase Variables
    DatabaseReference databaseReference;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        // Get the Passed Data
        student = getIntent();
        strName = student.getStringExtra("name");

        internet = (TextView) findViewById(R.id.internet);
        internet.setVisibility(View.INVISIBLE);
        name_text = (TextView) findViewById(R.id.text);
        name_text.setText("Hii " + strName + "\nAsk your Teacher for the Code \nThen Enter it here");
        incorrect_code = (TextView) findViewById(R.id.code_incorrect);
        incorrect_code.setVisibility(View.INVISIBLE);

        // Set the Title of Action Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Student");
        }

        // Type Casting
        code = (EditText) findViewById(R.id.code);
        go = (Button) findViewById(R.id.go);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();

                // Checking the INTERNET Connection on the Button Click
                if(hasNetwork()){
                    internetConnection = true;
                    internet.setVisibility(View.INVISIBLE);
                }else{
                    internetConnection = false;
                    internet.setVisibility(View.VISIBLE);
                    internet.setText("Please check the INTERNET Connection");
                    alertDialog.dismiss();
                }

                strCode = code.getText().toString();

                if(TextUtils.isEmpty(strCode)){

                    Toast.makeText(student.this, "Code...?", Toast.LENGTH_SHORT).show();
                }

                // If INTERNET Connection is Available
                if(internetConnection = true) {
                    // Firebase Variables Type Casting
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(strCode);

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild("Teacher")) {

                                databaseReference.child("Student").child(strName).setValue("Student");
                                incorrect_code.setVisibility(View.INVISIBLE);

                                Intent student = new Intent(getApplicationContext(), election_student.class);
                                student.putExtra("name", strName);
                                student.putExtra("code", strCode);
                                alertDialog.dismiss();
                                startActivity(student);
                            } else {
                                incorrect_code.setVisibility(View.VISIBLE);
                                alertDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        // Checking the INTERNET Connection
        hasNetwork();
        progressDialog();
    }

    private void progressDialog() {

        alertDialog = new SpotsDialog.Builder().setContext(student.this).build();
        alertDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent back = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(back);
    }

    // Checking the Internet Connection
    private boolean hasNetwork(){

        boolean has_WIFI = false;
        boolean has_mobileNetwork = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for(NetworkInfo networkInfo : networkInfos){

            if(networkInfo.getTypeName().equalsIgnoreCase("WIFI")){
                if(networkInfo.isConnected()){
                    has_WIFI = true;
                }
            }

            if(networkInfo.getTypeName().equalsIgnoreCase("MOBILE")){
                if(networkInfo.isConnected()){
                    has_mobileNetwork = true;
                }
            }
        }

        return has_WIFI || has_mobileNetwork;
    }
}
