package com.scupe.cralloter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class battle_ground extends AppCompatActivity {

    TextView status;

    Intent getText;
    String strName;
    String strCode;
    String strVoted;     // Not Voted
    String election;  // Election has not Started
    CardView dialogue;
    Button gotIt;

    ListView listView;
    ImageView image;
    TextView text;
    ArrayList<String> arrayList;

    // Firebase variables
    DatabaseReference databaseReference;
    DatabaseReference election_start_or_not;   // Checking if the Election has Started or not
    DatabaseReference check_removed;  // Checking if the Teacher has Removed him/her

    // Shared Preferences
    public static final String SHARED_PREFERENCES = "sharedPreferences";
    public static final String TEXT = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_ground);

        // Change the Title of Action Bar
        getSupportActionBar().setTitle("Battle Ground");

        getText = getIntent();
        strName = getText.getStringExtra("name");
        strCode = getText.getStringExtra("code");

        // Type Casting
        status = (TextView) findViewById(R.id.status);
        listView = (ListView) findViewById(R.id.listView);
        gotIt = (Button) findViewById(R.id.gotIt);
        image = (ImageView) findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);
        image.setVisibility(View.INVISIBLE);
        text.setVisibility(View.INVISIBLE);
        listView.setVerticalScrollBarEnabled(false);

        dialogue = (CardView) findViewById(R.id.dialogue);
        dialogue.setVisibility(View.INVISIBLE);

        arrayList = new ArrayList<>();

        // Firebase Variables
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(strCode);

        check_if_removed();

        // Saving and Loading the Data when the Activity is Reopened
        loadData();
    }

    private void check_if_removed() {

        check_removed = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(strCode);
        check_removed.child("REMOVED").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        String name = ds.getKey();
                        assert name != null;
                        if(name.equals(strName)){
                            listView.setVisibility(View.INVISIBLE);
                            status.setText("Not Elegible to Vote");

                            dialogue.setVisibility(View.VISIBLE);
                            gotIt.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onClick(View v) {

                                    dialogue.setVisibility(View.INVISIBLE);
                                    image.setVisibility(View.VISIBLE);
                                    text.setText("You are not Elegible to Vote");
                                    text.setVisibility(View.VISIBLE);
                                }
                            });
                        }else{
                            election_situation();
                        }
                    }
                }else{
                    election_situation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Checking if the Election has Started or not
    private void election_situation() {

        election_start_or_not = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(strCode);
        election_start_or_not.child("Situation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    String situation = dataSnapshot.getValue().toString();
                    assert situation != null;
                    if(situation.equals("True")){
                        status.setVisibility(View.VISIBLE);
                        image.setVisibility(View.INVISIBLE);
                        text.setVisibility(View.INVISIBLE);
                        display_CR();
                        election = "true";
                    }else{
                        status.setVisibility(View.INVISIBLE);
                        text.setText("Oops... \nThe Election is Over");
                        image.setVisibility(View.VISIBLE);
                        text.setVisibility(View.VISIBLE);
                        election = "false";
                    }
                }else{
                    election = "false";
                    status.setVisibility(View.INVISIBLE);
                    image.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, strVoted);
        editor.apply();
    }

    @SuppressLint("SetTextI18n")
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        strVoted = sharedPreferences.getString(TEXT, "false");

        if (strVoted.equals("false")){
            status.setText("Status : Not yet Voted");
        }else{
            status.setText("Status : Voted");
        }
    }

    private void display_CR() {

            databaseReference.child("CR").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if ((dataSnapshot.exists())) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            String name = ds.getKey();
                            arrayList.add(name);
                        }

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(battle_ground.this, R.layout.design_listview, arrayList);
                        listView.setAdapter(arrayAdapter);
                    }

                    final DatabaseReference voted_databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(strCode).child("Voted");  // Store the name of the Voter in the Database
                    final DatabaseReference election_databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(strCode).child("Election");  // Register the Vote of the Voter in the Database

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            loadData();

                            if (strVoted.equals("false")) {

                                // If the User has not Voted
                                final String name = listView.getItemAtPosition(position).toString();

                                AlertDialog.Builder alertDialogue = new AlertDialog.Builder(battle_ground.this);
                                alertDialogue.setMessage("Wanna give your Vote to \n" + name + "...?");
                                alertDialogue.setTitle("Are you Sure...?");
                                alertDialogue.setPositiveButton("Yeah", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        election_databaseReference.child(name).child(strName).setValue("Voter");
                                        voted_databaseReference.child("Voted").child(strName).setValue("Voted");
                                        strVoted = "true";  // Voted
                                        saveData();
                                        loadData();
                                        Toast.makeText(battle_ground.this, "Vote have been Registered", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                alertDialogue.setNegativeButton("No..!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                alertDialogue.show();
                            } else if (strVoted.equals("true")) {

                                Toast.makeText(battle_ground.this, "You have Already Voted", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    @Override
    public void onBackPressed() {

        Intent profile = new Intent(getApplicationContext(), election_student.class);
        profile.putExtra("name", strName);
        profile.putExtra("code", strCode);
        startActivity(profile);
    }
}
