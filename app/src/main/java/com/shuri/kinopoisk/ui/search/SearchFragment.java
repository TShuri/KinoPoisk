package com.shuri.kinopoisk.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.shuri.kinopoisk.MainActivity;
import com.shuri.kinopoisk.R;
import com.shuri.kinopoisk.adapters.HomeRecViewAdapter;
import com.shuri.kinopoisk.databinding.FragmentSearchBinding;
import com.shuri.kinopoisk.models.Movie;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;

    private List<Movie> searchMovies;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        searchMovies = new ArrayList<>();

        initialMovies();

        RecyclerView recyclerSearchView = root.findViewById(R.id.recyclerSearchView);
        HomeRecViewAdapter searchAdapter = new HomeRecViewAdapter(root.getContext(), searchMovies, (MainActivity) getActivity());
        recyclerSearchView.setAdapter(searchAdapter);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initialMovies() {
        searchMovies.add(new Movie(1));
        searchMovies.add(new Movie(2));
        searchMovies.add(new Movie(3));
        searchMovies.add(new Movie(4));
    }
}