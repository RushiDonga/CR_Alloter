package com.scupe.cralloter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText name;
    TextView internet;
    TextView correct_name;
    Spinner spinner;
    Button button;

    String strName;
    String strYou;
    Boolean internetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Type Casting
        spinner = (Spinner) findViewById(R.id.Spinner);
        name = (EditText) findViewById(R.id.name);
        button = (Button) findViewById(R.id.button);
        internet = (TextView) findViewById(R.id.internet);
        internet.setVisibility(View.INVISIBLE);
        correct_name = (TextView) findViewById(R.id.correct_name);
        correct_name.setVisibility(View.INVISIBLE);

        // Teacher or Student
        dropDownList();

        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                // Checking Internet Connection
                if(hasNetwork()){
                    internet.setVisibility(View.INVISIBLE);
                    internetConnection = true;
                }else{
                    internet.setVisibility(View.VISIBLE);
                    internet.setText("Please Check the INTERNET Connection");
                    internetConnection = false;
                }

                if(internetConnection.equals(true)) {

                    strName = name.getText().toString();

                    if (strName.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Name...?", Toast.LENGTH_SHORT).show();
                    }

                    if (strYou.equals("You are a...?")) {
                        Toast.makeText(MainActivity.this, "You are a...?", Toast.LENGTH_SHORT).show();
                    }

                    if (!strName.equals("") && !strYou.equals("You are a...?")) {

                        String check_name = strName;

                        // Checking the Correctness of the Entered Name
                        if(check_name.matches("[a-zA-Z ]*")) {

                            correct_name.setVisibility(View.INVISIBLE);
                            if (strYou.equals("Teacher")) {

                                Intent teacher = new Intent(getApplicationContext(), teacher.class);
                                teacher.putExtra("name", strName);
                                startActivity(teacher);
                            } else if (strYou.equals("Student")) {

                                Intent student = new Intent(getApplicationContext(), student.class);
                                student.putExtra("name", strName);
                                startActivity(student);
                            }
                        }else{

                            correct_name.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    // Spinner Items
    private void dropDownList() {

        ArrayList<Custom_Spinner_Items> list = new ArrayList<>();
        list.add(new Custom_Spinner_Items("You are a...?", R.drawable.you_are_a));
        list.add(new Custom_Spinner_Items("Teacher", R.drawable.teacher));
        list.add(new Custom_Spinner_Items("Student", R.drawable.student));

        final Custom_Spinner_Adapter customSpinnerAdapter = new Custom_Spinner_Adapter(this, list);

        if(spinner != null){
            spinner.setAdapter(customSpinnerAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Custom_Spinner_Items items = (Custom_Spinner_Items) customSpinnerAdapter.getItem(position);
                    strYou = items.getSpinnerText();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    // Checking for the Internet Connection
    private boolean hasNetwork(){

        boolean have_Wifi = false;
        boolean have_mobileData = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        assert connectivityManager != null;
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for(NetworkInfo info : networkInfos){

            if(info.getTypeName().equalsIgnoreCase("WIFI")){
                if(info.isConnected()) {
                    have_Wifi = true;
                }
            }

            if(info.getTypeName().equalsIgnoreCase("MOBILE")){
                if(info.isConnected()){
                    have_mobileData = true;
                }
            }
        }

        return have_mobileData || have_Wifi;
    }

    @Override
    public void onBackPressed() {

        // Exit the Application on Back Pressed
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        finish();
        System.exit(0);
    }
}
