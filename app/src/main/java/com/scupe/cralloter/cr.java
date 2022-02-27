package com.scupe.cralloter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class cr extends AppCompatActivity {
    Intent getText;

    ListView listView;
    TextView total;
    ImageView image;
    TextView text;
    TextView internet;

    DatabaseReference databaseReference;
    DatabaseReference remove_cr;

    String strCode;
    boolean internetConnection = true;
    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cr);

        getSupportActionBar().setTitle("Class Representative < CR >");

        // Get the Sent Details
        getText = getIntent();

        listView = (ListView) findViewById(R.id.crNames);
        total = (TextView) findViewById(R.id.total);
        image = (ImageView) findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);
        internet = (TextView) findViewById(R.id.internet);

        image.setVisibility(View.INVISIBLE);
        text.setVisibility(View.INVISIBLE);
        listView.setVerticalScrollBarEnabled(false);
        internet.setVisibility(View.INVISIBLE);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                check_Network();

                if(internetConnection) {
                    remove_cr(position);
                }
                return true;
            }
        });

        check_Network();
        print_cr();
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

    private void remove_cr(final int position) {

        remove_cr = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(strCode);
        remove_cr.child("CR").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    final String name = (String) listView.getItemAtPosition(position);

                    DatabaseReference remove_particular_child = FirebaseDatabase.getInstance().getReference()
                            .child("AllUsers").child(strCode).child("CR");

                    remove_particular_child.child(name).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()){

                                final AlertDialog.Builder remove = new AlertDialog.Builder(cr.this);
                                remove.setTitle("Are You Sure");
                                remove.setMessage("Wanna remove " + name + " fron CR");
                                remove.setPositiveButton("Yess..!!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dataSnapshot.getRef().removeValue();
                                    }
                                });
                                remove.setNegativeButton("No..!!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                remove.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(cr.this, R.layout.design_listview, list);
                listView.setAdapter(arrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
