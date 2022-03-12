package com.example.birdsoffeather;


import static android.app.Activity.RESULT_OK;

import static com.example.birdsoffeather.StopSave.KEY_NAME;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsoffeather.model.db.Session;

import java.util.List;

public class SessionsViewAdapter extends RecyclerView.Adapter<SessionsViewAdapter.ViewHolder> {
    private List<String> sessions;

    public SessionsViewAdapter(List<String> sessions) {
        super();
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public SessionsViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.session_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionsViewAdapter.ViewHolder holder, int position) {
        holder.setSession(sessions.get(position));
    }

    public void updateList(List<String> newList) {
        sessions = newList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.sessions.size();
    }

    /** ViewHolder class */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView sessionView;
        private String session;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.sessionView = itemView.findViewById(R.id.session_name_txtView);
            itemView.setOnClickListener(this);
        }

        public void setSession(String session) {
            this.session = session;

            // update text in session name view to be the session's name
            this.sessionView.setText(this.session);
        }

        @Override
        public void onClick(View view) {
            Log.i("SessionsViewAdapterActivity", "session was selected");
            Context context = view.getContext();

            // new session name will be passed
            Intent intent = new Intent();
            intent.putExtra(KEY_NAME, this.session);
            ((Activity)context).setResult(RESULT_OK, intent);

            // close activity
            ((Activity)context).finish();
        }
    }
}
