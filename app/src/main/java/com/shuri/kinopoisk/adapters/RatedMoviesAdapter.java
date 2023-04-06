package com.shuri.kinopoisk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shuri.kinopoisk.R;
import com.shuri.kinopoisk.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RatedMoviesAdapter extends RecyclerView.Adapter<RatedMoviesAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<Movie> movies;

    public RatedMoviesAdapter(Context context, List<Movie> movies) {
        this.movies = movies;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_movie_for_favorites, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        Picasso.get().load(movie.getPosterUrlPreview()).into(holder.imageSmallCard);
        holder.nameMovie.setText(movie.getNameRu());
        holder.ratingMovie.setText(String.valueOf(movie.getRating()));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageSmallCard;
        final TextView nameMovie, ratingMovie;
        ViewHolder(View view){
            super(view);
            imageSmallCard = view.findViewById(R.id.imageSmallCard);
            nameMovie = view.findViewById(R.id.nameMovie);
            ratingMovie = view.findViewById(R.id.ratingMovie);
        }
    }
}
