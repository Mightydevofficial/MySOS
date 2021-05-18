package com.example.mysosapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

public class Notes extends AppCompatActivity {

    RecyclerView recyclerView;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
//get the list of notes title and content in string array

        String[] titles = getResources().getStringArray(R.array.notes_title);
        String[] content = getResources().getStringArray(R.array.notes_content);

        recyclerView = findViewById(R.id.notesListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this,titles,content); //our adapter takes two string array
        recyclerView.setAdapter(adapter);


    }
}