package com.shuri.kinopoisk.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.shuri.kinopoisk.MainActivity;
import com.shuri.kinopoisk.R;
import com.shuri.kinopoisk.adapters.HomeRecViewAdapter;
import com.shuri.kinopoisk.databinding.FragmentHomeBinding;
import com.shuri.kinopoisk.models.Movie;
import com.shuri.kinopoisk.parsers.JSONSimpleParser;
import com.shuri.kinopoisk.ui.MovieFragment;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment{
    private FragmentHomeBinding binding;

    private List<Movie> movies;

    FragmentManager fragmentManager;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        movies = new ArrayList<>();
        JSONSimpleParser jsonSimpleParser = new JSONSimpleParser();
        try {
            jsonSimpleParser.parseToString(getContext());
            movies = jsonSimpleParser.parsing();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        fragmentManager = getActivity().getSupportFragmentManager();
        Button buttonTest = root.findViewById(R.id.buttonTest);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.homeLayout, new MovieFragment());
                fragmentTransaction.commit();
            }
        });


        //initialMovies();

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        HomeRecViewAdapter adapter = new HomeRecViewAdapter(root.getContext(), movies, (MainActivity) getActivity());
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