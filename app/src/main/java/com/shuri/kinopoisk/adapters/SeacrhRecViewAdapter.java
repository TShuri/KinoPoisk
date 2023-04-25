package com.shuri.kinopoisk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shuri.kinopoisk.MainActivity;
import com.shuri.kinopoisk.R;
import com.shuri.kinopoisk.models.Movie;
import com.shuri.kinopoisk.ui.search.SearchFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SeacrhRecViewAdapter extends RecyclerView.Adapter<SeacrhRecViewAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private List<Movie> movies;
    private Context context;
    private MainActivity activity;

    private View.OnClickListener loadMore;

    public SeacrhRecViewAdapter(Context context, List<Movie> movies, MainActivity activ, View.OnClickListener _listener) {
        this.movies = movies;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activ;
        this.loadMore = _listener;
    }

    @NonNull
    @Override
    public SeacrhRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        if (viewType == R.layout.card_movie) {
            itemView = inflater.inflate(R.layout.card_movie, parent, false);
        } else {
            itemView = inflater.inflate(R.layout.button_for_search, parent, false);
        }

        //View view = inflater.inflate(R.layout.card_movie, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SeacrhRecViewAdapter.ViewHolder holder, int position) {
        if (position == movies.size()) {
            if (position == 0) {
                holder.btnMore.setVisibility(View.INVISIBLE);
            } else {
                holder.btnMore.setVisibility(View.VISIBLE);
            }

            holder.btnMore.setOnClickListener(loadMore);

        } else {
            Movie movie = movies.get(position);
            //holder.imageMovie.setImageResource(R.drawable.movie_logo);
            Picasso.get().load(movie.getPosterUrlPreview()).into(holder.imageMovie);
            holder.imageMovie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.showMovieFragmentFromSearch(movie);
                }
            });
            holder.nameMovie.setText(movie.getNameRu());
            holder.yearMovie.setText(String.valueOf(movie.getYear()));
            holder.ratingMovie.setText("Рейтинг: " + String.valueOf(movie.getRating()));
            holder.genreMovie.setText(movie.getStringGenres());
        }
    }

    @Override
    public int getItemCount() {
        return movies.size() + 1;
    }

    public void setData(List<Movie> searchMovies) {
        movies = new ArrayList<>(searchMovies);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == movies.size()) ? R.layout.button_for_search : R.layout.card_movie;
    }

    public void addData(List<Movie> searchMovies) {
        movies.addAll(searchMovies);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final LinearLayout cardLayout, textLayout;
        final ImageView imageMovie;
        final TextView nameMovie, genreMovie, yearMovie, ratingMovie;

        Button btnMore;

        ViewHolder(View view) {
            super(view);
            cardLayout = view.findViewById(R.id.cardLayout);
            imageMovie = view.findViewById(R.id.imageMovie);
            textLayout = view.findViewById(R.id.textLayout);
            nameMovie = view.findViewById(R.id.textNameMovie);
            genreMovie = view.findViewById(R.id.textGenreMovie);
            yearMovie = view.findViewById(R.id.textYearMovie);
            ratingMovie = view.findViewById(R.id.textRatingMovie);

            btnMore = view.findViewById(R.id.btnMore);
        }
    }
}

