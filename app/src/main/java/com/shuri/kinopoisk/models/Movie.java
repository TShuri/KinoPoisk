package com.shuri.kinopoisk.models;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    private int filmId; // id фильма
    private String nameRu; // название на русском
    private int year; // год
    private String posterUrlPreview; // ссылка на обложку
    private double rating; // рейтинг
    private List<String> genres; // жанры

    public Movie(int i) {
        this.filmId = i;
        this.nameRu = "Фильм" + i;
        this.year = 2023;
        this.posterUrlPreview = "URL";
        this.rating = 8;
        genres = new ArrayList<>();
        genres.add("Фантастика");
    }

    public Movie(int _filmId, String _nameRu, int _year, String _posterUrlPreview, double _rating, List<String> _genres) {
        this.filmId = _filmId;
        this.nameRu = _nameRu;
        this.year = _year;
        this.posterUrlPreview = _posterUrlPreview;
        this.rating = _rating;
        this.genres = _genres;
    }

    public int getFilmId() {
        return filmId;
    }

    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getPosterUrlPreview() {
        return posterUrlPreview;
    }

    public void setPosterUrlPreview(String posterUrlPreview) {
        this.posterUrlPreview = posterUrlPreview;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getStringGenres() {
        String arrayMovies = String.join(" ", genres);
        return arrayMovies;
    }
}
