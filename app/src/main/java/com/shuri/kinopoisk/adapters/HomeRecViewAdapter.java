package com.shuri.kinopoisk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shuri.kinopoisk.MainActivity;
import com.shuri.kinopoisk.R;
import com.shuri.kinopoisk.models.Movie;
import com.shuri.kinopoisk.ui.MovieFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeRecViewAdapter extends RecyclerView.Adapter<HomeRecViewAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<Movie> movies;
    private Context context;
    private MainActivity activity;


    public HomeRecViewAdapter(Context context, List<Movie> movies, MainActivity activ) {
        this.movies = movies;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activ;
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
        //holder.imageMovie.setImageResource(R.drawable.movie_logo);
        Picasso.get().load(movie.getPosterUrlPreview()).into(holder.imageMovie);
        holder.imageMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.showMovieFragment(movie.getFilmId());
            }
        });
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
        final LinearLayout cardLayout, textLayout;
        final ImageView imageMovie;
        final TextView nameMovie, genreMovie, yearMovie, ratingMovie;
        ViewHolder(View view){
            super(view);
            cardLayout = view.findViewById(R.id.cardLayout);
            imageMovie = view.findViewById(R.id.imageMovie);
            textLayout = view.findViewById(R.id.textLayout);
            nameMovie = view.findViewById(R.id.textNameMovie);
            genreMovie = view.findViewById(R.id.textGenreMovie);
            yearMovie = view.findViewById(R.id.textYearMovie);
            ratingMovie = view.findViewById(R.id.textRatingMovie);
        }
    }
}
