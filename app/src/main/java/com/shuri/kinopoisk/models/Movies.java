package com.shuri.kinopoisk.models;

import java.util.ArrayList;
import java.util.List;

public class Movies {
    private List<Movie> movies;

    public Movies() {
        this.movies = new ArrayList<>();
    }

    public void addMovie(Movie movie) {
        this.movies.add(movie);
    }

    public void deleteMovie(int id) {
        int index = 0;
        for (Movie mov : this.movies) {
            if (id == mov.getFilmId()) {
                break;
            }
            index ++;
        }
        this.movies.remove(index);
    }

    public int countMovies() {
        return this.movies.size();
    }
}
