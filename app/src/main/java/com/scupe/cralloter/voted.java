package com.scupe.cralloter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class voted extends AppCompatActivity {

    Intent getData;

    String strCode;

    ListView listView;
    TextView total;
    ImageView image;
    TextView text;
    DatabaseReference databaseReference;
    DatabaseReference election_situation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voted);

        // Set the Title of Action Bar
        getSupportActionBar().setTitle("Already Voted");

        getData = getIntent();

        // Type Casting
        listView = (ListView) findViewById(R.id.listView);
        total = (TextView) findViewById(R.id.total);
        image = (ImageView) findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);

        image.setVisibility(View.INVISIBLE);
        text.setVisibility(View.INVISIBLE);
        listView.setVerticalScrollBarEnabled(false);

        election_status();
    }

    private void election_status() {
        strCode = getData.getStringExtra("code");

        election_situation = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(strCode);
        election_situation.child("Situation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String situation = dataSnapshot.getValue().toString();
                    if(situation.equals("True")){
                        data();
                    }else{
                        total.setVisibility(View.INVISIBLE);
                        image.setVisibility(View.VISIBLE);
                        text.setVisibility(View.VISIBLE);
                        text.setText("Oops...! \nElection is Over");
                    }
                }else{
                    total.setVisibility(View.INVISIBLE);
                    image.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                    text.setText("Oops...! \nElection has not yet Started");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void data() {

        final ArrayList<String> list = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(strCode).child("Voted");
        databaseReference.child("Voted").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    int total_people = (int) dataSnapshot.getChildrenCount();
                    total.setText("People who Voted : " + total_people);

                    list.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String name = ds.getKey();
                        list.add(name);
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.design_listview, list);
                    listView.setAdapter(arrayAdapter);
                }else{
                    total.setVisibility(View.INVISIBLE);
                    image.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                    text.setText("Oops...! \nNo one has Voted yet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
