package com.example.mysosapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class DetailNotes extends AppCompatActivity {
   TextView noteContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_notes);

       noteContent = findViewById(R.id.contentsOfNote);
        Intent i =getIntent();
        String title = i.getStringExtra("titleOfNote");
        String content = i.getStringExtra("contentOfNotes");


        //set the appbar title as note title
       // getSupportActionBar().setTitle(title);

        //set content of the note to textview

        noteContent.setText(content);
       noteContent.setMovementMethod(new ScrollingMovementMethod());

       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}