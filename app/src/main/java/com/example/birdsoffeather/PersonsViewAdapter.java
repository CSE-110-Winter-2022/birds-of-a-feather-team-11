package com.example.birdsoffeather;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.example.birdsoffeather.model.db.IPerson;
import com.example.birdsoffeather.model.db.PersonWithCourses;

public class PersonsViewAdapter extends RecyclerView.Adapter<PersonsViewAdapter.ViewHolder> {
    private List<? extends IPerson> persons;

    public PersonsViewAdapter(List<? extends IPerson> persons) {
        super();
        this.persons = persons;
    }

    @NonNull
    @Override
    public PersonsViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.person_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonsViewAdapter.ViewHolder holder, int position) {
        holder.setPerson(persons.get(position));
    }

    public void updateList(List<? extends IPerson> newList) {
        persons = newList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.persons.size();
    }


    /** VIewHolder class */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView personNameView;
        private final TextView similarCourses_num;
        private final ImageView personPicView;
        private IPerson person;

        ViewHolder(View itemView) {
            super(itemView);
            this.personNameView = itemView.findViewById(R.id.person_row_name);
            this.similarCourses_num = itemView.findViewById(R.id.numberOf_courses);
            this.personPicView = itemView.findViewById(R.id.profile_pic);
            itemView.setOnClickListener(this);
        }

        public void setPerson(IPerson person) {

            this.person = person;

            // set name view
            this.personNameView.setText(person.getName());

            // set numberOf_courses view
            int num_courses = this.person.getCourses().size();
            String msg = "Matching Classes: " + String.valueOf(num_courses);
            this.similarCourses_num.setText(msg);

            // set profile pic
            LoadImage loadImage = new LoadImage(personPicView);
            loadImage.execute(person.getUrl());

        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(context, PersonDetailActivity.class);
            intent.putExtra("person_id", this.person.getId());
            context.startActivity(intent);
        }
    }


}