package com.shuri.kinopoisk.ui.home;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.shuri.kinopoisk.R;
import com.shuri.kinopoisk.adapters.MovieAdapter;
import com.shuri.kinopoisk.databinding.FragmentHomeBinding;
import com.shuri.kinopoisk.models.Movie;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    private List<Movie> movies;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        movies = new ArrayList<>();
        initialMovies();
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        //recyclerView.setAdapter(new MovieAdapter(movies));
        MovieAdapter adapter = new MovieAdapter(root.getContext(), movies);
        recyclerView.setAdapter(adapter);


        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
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