package com.example.mysosapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private LayoutInflater inflater;
    private String[] sTitles;
    private String[] sContent;

    Adapter(Context context, String[] titles, String[] contents) {
        this.inflater = LayoutInflater.from(context);
        this.sTitles = titles;
        this.sContent = contents;


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.customer_view, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        String title = sTitles[i];
        String content = sContent[i];
        viewHolder.noteTitle.setText(title);
        viewHolder.noteContent.setText(content);


    }

    @Override
    public int getItemCount() {
        return sTitles.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView noteTitle, noteContent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //implement onClick view

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), DetailNotes.class);
                    //send note title and content by recycle view to detail notes
                    i.putExtra("titleOfNote", sTitles[getAdapterPosition()]);
                    i.putExtra("contentOfNotes", sContent[getAdapterPosition()]);
                    v.getContext().startActivity(i);


                }
            });
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteContent = itemView.findViewById(R.id.noteContent);

        }
    }
}
