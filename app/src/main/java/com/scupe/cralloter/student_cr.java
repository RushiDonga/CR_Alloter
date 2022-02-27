package com.scupe.cralloter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

public class student_cr extends AppCompatActivity {
    Intent getText;

    ListView listView;
    TextView total;
    ImageView image;
    TextView text;

    DatabaseReference databaseReference;
    DatabaseReference remove_cr;

    String strCode;
    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_cr);

        getSupportActionBar().setTitle("Class Representative < CR >");

        // Get the Sent Details
        getText = getIntent();

        listView = (ListView) findViewById(R.id.crNames);
        total = (TextView) findViewById(R.id.total);
        image = (ImageView) findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);

        image.setVisibility(View.INVISIBLE);
        text.setVisibility(View.INVISIBLE);
        listView.setVerticalScrollBarEnabled(false);

        print_cr();
    }

    private void print_cr() {

        list = new ArrayList<String>();

        strCode = getText.getStringExtra("code");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(strCode);
        databaseReference.child("CR").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();
                int total_cr = (int) dataSnapshot.getChildrenCount();
                total.setText("Total CR : " + total_cr);

                if(total_cr == 0){
                    image.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                    total.setVisibility(View.INVISIBLE);
                }else{
                    image.setVisibility(View.INVISIBLE);
                    text.setVisibility(View.INVISIBLE);
                }

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String name = ds.getKey();
                    list.add(name);
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(student_cr.this, R.layout.design_listview, list);
                listView.setAdapter(arrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
