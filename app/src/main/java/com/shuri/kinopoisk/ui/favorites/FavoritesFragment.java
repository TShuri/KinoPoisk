package com.shuri.kinopoisk.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.shuri.kinopoisk.R;
import com.shuri.kinopoisk.adapters.RatedMoviesAdapter;
import com.shuri.kinopoisk.databinding.FragmentFavoritesBinding;
import com.shuri.kinopoisk.models.Movie;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;

    private List<Movie> movies;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FavoritesViewModel favoritesViewModel =
                new ViewModelProvider(this).get(FavoritesViewModel.class);

        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        movies = new ArrayList<>();
        initialMovies();


        RecyclerView ratedRecyclerView = root.findViewById(R.id.ratedRecyclerView);
        RatedMoviesAdapter ratedMoviesAdapter = new RatedMoviesAdapter(root.getContext(), movies);
        ratedRecyclerView.setAdapter(ratedMoviesAdapter);


        Button add = root.findViewById(R.id.buttonAdd);

        Button delete = root.findViewById(R.id.buttonDelete);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movies.add(newMovie());
                ratedMoviesAdapter.notifyDataSetChanged();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movies.remove(1);
                ratedMoviesAdapter.notifyDataSetChanged();
            }
        });


        //final TextView textView = binding.textDashboard;
        //favoritesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public View newCardForScrollView() {
        final View view = getLayoutInflater().inflate(R.layout.card_movie_for_favorites, null);
        return view;
    }

    private void initialMovies() {
        movies.add(new Movie(1));
        movies.add(new Movie(2));
        movies.add(new Movie(3));
        movies.add(new Movie(4));
    }

    private Movie newMovie() {
        int i = movies.size() + 1;
        return new Movie(i);
    }
}