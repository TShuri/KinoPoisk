package com.shuri.kinopoisk.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shuri.kinopoisk.R;


public class MovieFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie, container, false);

        int idMovie = getArguments().getInt("id");
        TextView namefilm = view.findViewById(R.id.titleNameMovie);
        namefilm.setText(String.valueOf(idMovie));

        return view;
    }
}