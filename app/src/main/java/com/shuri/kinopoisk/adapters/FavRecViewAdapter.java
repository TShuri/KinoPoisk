package com.shuri.kinopoisk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shuri.kinopoisk.MainActivity;
import com.shuri.kinopoisk.R;
import com.shuri.kinopoisk.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FavRecViewAdapter extends RecyclerView.Adapter<FavRecViewAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private List<Movie> movies;
    private TextView counter;

    private MainActivity activity;

    public FavRecViewAdapter(Context context, List<Movie> movies, TextView count, MainActivity activ) {
        this.movies = movies;
        this.inflater = LayoutInflater.from(context);
        this.counter = count;
        this.activity = activ;
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
        //holder.imageSmallCard.setImageResource(R.drawable.movie_logo);
        Picasso.get().load(movie.getPosterUrlPreview()).into(holder.imageSmallCard);
        holder.imageSmallCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.showMovieFragmentFromFavorites(movie);
            }
        });
        holder.nameMovie.setText(movie.getNameRu());
        holder.ratingMovie.setText(String.valueOf(movie.getRating()));

        counter.setText(String.valueOf(getItemCount()));
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

    public void setData(List<Movie> _movies) {
        this.movies = new ArrayList<>(_movies);
        notifyDataSetChanged();
    }
}
