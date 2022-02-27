package com.scupe.cralloter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class all_members extends AppCompatActivity {

    ListView listView;
    TextView total;

    Intent getText;
    String strName;
    String strCode;
    int totalPeople = 0;

    ArrayList<String> list;

    ArrayAdapter<String> arrayAdapter;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_members);

        // Change the Title of Action Bar
        getSupportActionBar().setTitle("All Members");

        // Type Casting
        listView = (ListView) findViewById(R.id.listView);
        listView.setVerticalScrollBarEnabled(false);
        total = (TextView) findViewById(R.id.total);

        getText = getIntent();
        strName = getText.getStringExtra("name");
        strCode = getText.getStringExtra("code");

        printMembers();
    }

    private void printMembers() {

        list = new ArrayList<String>();

        // Firebase Variables
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(strCode);
        databaseReference.child("Student").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();
                totalPeople = (int) dataSnapshot.getChildrenCount();
                total.setText(  "Total Members : " + totalPeople);

                for(DataSnapshot ds : dataSnapshot.getChildren()){

                    String name = ds.getKey();
                    list.add(name);
                }

                arrayAdapter = new ArrayAdapter<>(all_members.this, R.layout.design_listview, list);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Creating the Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.cr) {
            Intent cr = new Intent(getApplicationContext(), com.scupe.cralloter.cr.class);
            cr.putExtra("code", strCode);
            startActivity(cr);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        Intent profile = new Intent(getApplicationContext(), election_student.class);
        profile.putExtra("name", strName);
        profile.putExtra("code", strCode);
        startActivity(profile);
    }
}
