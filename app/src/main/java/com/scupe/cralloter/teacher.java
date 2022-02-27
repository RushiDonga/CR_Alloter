package com.scupe.cralloter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

import dmax.dialog.SpotsDialog;

public class teacher extends AppCompatActivity {

    Intent teacher;

    String studentCode;
    String strName;
    Boolean done = false;

    AlertDialog alertDialog;

    DatabaseReference uploadCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        teacher = getIntent();
        strName = teacher.getStringExtra("name");

        // Type Casting
        final TextView code = (TextView) findViewById(R.id.code);
        Button codeGenerator = (Button) findViewById(R.id.codeButton);

        progressDialog();

        // Set the Title of Action Bar
        getSupportActionBar().setTitle("Teacher");

        codeGenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();

                if(!done) {

                    code.setText(generateString(6));
                    studentCode = code.getText().toString();

                    uploadCode = FirebaseDatabase.getInstance().getReference().child("AllUsers").child(studentCode);
                    done = true;

                    Intent intent = new Intent(getApplicationContext(), techer_code_generated.class);
                    intent.putExtra("code", studentCode);
                    intent.putExtra("name", strName);
                    intent.putExtra("newElection", "newCode");
                    alertDialog.dismiss();
                    startActivity(intent);
                }
            }
        });
    }

    private void progressDialog() {

        alertDialog = new SpotsDialog.Builder().setContext(teacher.this).build();
        alertDialog.show();
        alertDialog.dismiss();
    }

    // Generating the Code
    private String generateString(int length) {

        char[] chars = "abcdefghijklmnopqvwrstuvwxyz123456789".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for(int i=0; i<length; i++){
            char c = chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent back = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(back);
    }
}
