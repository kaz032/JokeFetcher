package com.example.dadjokeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class JokeAdapter extends RecyclerView.Adapter<JokeAdapter.JokeViewHolder> {

    private List<Joke> jokeList;

    public JokeAdapter(List<Joke> jokeList) {
        this.jokeList = jokeList;
    }

    @Override
    public JokeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.joke_item, parent, false);
        return new JokeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(JokeViewHolder holder, int position) {
        Joke joke = jokeList.get(position);
        holder.setupTextView.setText(joke.getSetup());
        if (!joke.getPunchline().isEmpty()) {
            holder.punchlineTextView.setText(joke.getPunchline());
            holder.punchlineTextView.setVisibility(View.VISIBLE);
        } else {
            holder.punchlineTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return jokeList != null ? jokeList.size() : 0;
    }

    public static class JokeViewHolder extends RecyclerView.ViewHolder {
        public TextView setupTextView;
        public TextView punchlineTextView;

        public JokeViewHolder(View itemView) {
            super(itemView);
            setupTextView = itemView.findViewById(R.id.jokeSetup);
            punchlineTextView = itemView.findViewById(R.id.jokePunchline);
        }
    }
}