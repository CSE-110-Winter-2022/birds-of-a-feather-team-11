package com.example.birdsoffeather;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.birdsoffeather.model.db.AppDatabase;
import com.example.birdsoffeather.model.db.IPerson;

public class PersonsViewAdapter extends RecyclerView.Adapter<PersonsViewAdapter.ViewHolder> {
    private List<? extends IPerson> persons;
    private AppDatabase db;

    public PersonsViewAdapter(List<? extends IPerson> persons, AppDatabase db) {
        super();
        this.persons = persons;
        this.db = db;
    }

    @NonNull
    @Override
    public PersonsViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.person_row, parent, false);

        return new ViewHolder(view, db);
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


    /** ViewHolder class */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView personNameView;
        private final TextView similarCourses_num;
        private final ImageView personPicView;
        private final ImageButton favoriteView;
        private IPerson person;
        private boolean favorite;
        private final AppDatabase db;

        private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();

        ViewHolder(View itemView, AppDatabase db) {
            super(itemView);
            this.personNameView = itemView.findViewById(R.id.person_row_name);
            this.similarCourses_num = itemView.findViewById(R.id.numberOf_courses);
            this.personPicView = itemView.findViewById(R.id.profile_pic);
            this.favoriteView = itemView.findViewById(R.id.favorite_view);
            this.favoriteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFavoriteClicked();
                }
            });
            this.db = db;
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

            // set favorite
            this.favorite = this.person.getFavorite();
            if(favorite){
                this.favoriteView.setImageResource(R.drawable.full_star);
            }else {
                this.favoriteView.setImageResource(R.drawable.empty_star);
            }

            ImageView wavingHand = itemView.findViewById(R.id.wavingHand);
            if(person.getWaveFrom() > 0) {
                wavingHand.setVisibility(VISIBLE);
            } else {
                wavingHand.setVisibility(INVISIBLE);
            }
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(context, PersonDetailActivity.class);
            intent.putExtra("person_id", this.person.getId());
            context.startActivity(intent);
        }

        public void onFavoriteClicked() {
            if(favorite) {
                this.favoriteView.setImageResource(R.drawable.empty_star);
                Log.w("Favorites", "UnFavorited");
                backgroundThreadExecutor.submit(() -> {
                    db.personsWithCoursesDao().removeFavorite(this.person.getId());
                    return null;
                });
            } else {
                this.favoriteView.setImageResource(R.drawable.full_star);
                Log.w("Favorites", "Favorited");
                backgroundThreadExecutor.submit(() -> {
                    db.personsWithCoursesDao().addFavorite(this.person.getId());
                    return null;
                });
            }

            favorite = !favorite;
        }
    }


}