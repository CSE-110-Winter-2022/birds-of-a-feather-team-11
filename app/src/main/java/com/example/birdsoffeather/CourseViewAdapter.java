package com.example.birdsoffeather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.Consumer;

import com.example.birdsoffeather.model.db.Course;

public class CourseViewAdapter extends RecyclerView.Adapter<CourseViewAdapter.ViewHolder> {

    private final List<Course> courses;

    public CourseViewAdapter(List<Course> courses) {
        super();
        this.courses = courses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.course_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewAdapter.ViewHolder holder, int position) {
        holder.setCourse(courses.get(position));
    }

    @Override
    public int getItemCount() {
        return this.courses.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView courseTextView;
        private Course course;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.courseTextView = itemView.findViewById(R.id.course_row_text);
        }

        public void setCourse(Course course) {
            this.course = course;
            this.courseTextView.setText(course.quarter + " " + course.year + ": " + course.subject + "" + course.number);
        }
    }
}
