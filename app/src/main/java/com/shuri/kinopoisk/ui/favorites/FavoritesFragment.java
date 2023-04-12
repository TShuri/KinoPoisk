package com.shuri.kinopoisk.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.shuri.kinopoisk.R;
import com.shuri.kinopoisk.adapters.FavRecViewAdapter;
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
        FavRecViewAdapter favRecViewAdapter = new FavRecViewAdapter(root.getContext(), movies);
        ratedRecyclerView.setAdapter(favRecViewAdapter);




        //final TextView textView = binding.textDashboard;
        //favoritesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initialMovies() {
        movies.add(new Movie(1));
        movies.add(new Movie(2));
        movies.add(new Movie(3));
        movies.add(new Movie(4));
    }
}