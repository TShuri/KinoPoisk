package com.shuri.kinopoisk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shuri.kinopoisk.R;
import com.shuri.kinopoisk.models.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<Movie> movies;

    public MovieAdapter(Context context, List<Movie> movies) {
        this.movies = movies;
        this.inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.imageMovie.setImageResource(R.drawable.movie_logo);
        holder.nameMovie.setText(movie.getNameRu());
        holder.yearMovie.setText(String.valueOf(movie.getYear()));
        holder.ratingMovie.setText(String.valueOf(movie.getRating()));
        holder.genreMovie.setText(movie.getStringGenres());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageMovie;
        final TextView nameMovie, genreMovie, yearMovie, ratingMovie;
        ViewHolder(View view){
            super(view);
            imageMovie = view.findViewById(R.id.imageMovie);
            nameMovie = view.findViewById(R.id.textNameMovie);
            genreMovie = view.findViewById(R.id.textGenreMovie);
            yearMovie = view.findViewById(R.id.textYearMovie);
            ratingMovie = view.findViewById(R.id.textRatingMovie);
        }
    }
}
