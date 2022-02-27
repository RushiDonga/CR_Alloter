package com.scupe.cralloter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Winner extends AppCompatActivity {

    Intent getData;

    ArrayList<String> cr_names = new ArrayList<>();
    ArrayList<Integer> cr_votes = new ArrayList<>();

    String strCode;
    int total_cr;
    int i_names = 0;
    int cr_names_size = 0;
    int highest_votes = 0;
    int highest_index = 0;
    int tie = 0;

    ListView winner;
    ImageView image;
    TextView text;
    TextView name;
    TextView votes;
    TextView emotion;
    LinearLayout linearLayout;

    DatabaseReference databaseReference;
    DatabaseReference total_votes;
    DatabaseReference check_situation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);

        // Set the Title of Action Bar
        getSupportActionBar().setTitle("Winner Winner");

        // Get the Data Passed
        getData = getIntent();
        strCode = getData.getStringExtra("code");

        // List view
        winner = findViewById(R.id.winner);
        image = (ImageView) findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);
        linearLayout = findViewById(R.id.linearLayout);
        name = (TextView) findViewById(R.id.name);
        votes = (TextView) findViewById(R.id.votes);
        emotion = (TextView) findViewById(R.id.emotion);

        image.setVisibility(View.INVISIBLE);
        text.setVisibility(View.INVISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);
        winner.setVisibility(View.INVISIBLE);

        check_status();
    }

    private void check_status() {

        check_situation = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(strCode);
        check_situation.child("Situation").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    String status = dataSnapshot.getValue().toString();

                    if(status.equals("False")) {

                        getCR_names();
                    }else{
                        image.setVisibility(View.VISIBLE);
                        text.setVisibility(View.VISIBLE);
                        text.setText("Election is not yet Over");
                    }
                }else{
                    image.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getCR_names() {

        // Retrieving the name of the CR's
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(strCode);
        databaseReference.child("Election").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    total_cr = (int) dataSnapshot.getChildrenCount();

                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        String name = ds.getKey();
                        cr_names.add(name);
                        i_names++;
                    }
                    cr_names_size = cr_names.size();
                    getCR_Total_votes(cr_names_size);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getCR_Total_votes(int size) {

        image.setVisibility(View.INVISIBLE);
        text.setVisibility(View.INVISIBLE);
        winner.setVisibility(View.INVISIBLE);

        // Retrieving total Votes of the Particular CR
        total_votes = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(strCode).child("Election");

        for(int i=0; i<size; i++){

            final int finalI = i;
            total_votes.child(cr_names.get(i)).addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){

                        int total_votes = (int) dataSnapshot.getChildrenCount();
                        cr_votes.add(total_votes);

                        if(highest_votes < total_votes){
                            highest_votes = total_votes;
                            highest_index = cr_votes.indexOf(highest_votes);

                            CountDownTimer timer = new CountDownTimer(3000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {

                                    tie=0;
                                    for(int i=0; i<cr_votes.size(); i++){

                                        if(cr_votes.get(i) == highest_votes){
                                            tie++;
                                        }
                                    }

                                    if(tie > 1){
                                        linearLayout.setVisibility(View.VISIBLE);
                                        name.setText("There's a Tie");
                                        emotion.setText("Oops..!!");
                                        votes.setVisibility(View.INVISIBLE);
                                    }else {

                                        name.setText(cr_names.get(highest_index));
                                        votes.setText(cr_votes.get(highest_index) + "  Votes");
                                        linearLayout.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onFinish() {

                                    linearLayout.setVisibility(View.INVISIBLE);
                                    winner.setVisibility(View.VISIBLE);

                                }
                            }.start();
                        }
                    }


                    MainAdapter adapter =  new MainAdapter(Winner.this, cr_names, cr_votes);
                    winner.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
