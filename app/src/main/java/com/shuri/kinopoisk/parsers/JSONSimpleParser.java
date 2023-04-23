package com.shuri.kinopoisk.parsers;

import android.content.Context;

import com.shuri.kinopoisk.models.ExtendedMovie;
import com.shuri.kinopoisk.models.Movie;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JSONSimpleParser {
    public String parseToString(Context context) throws FileNotFoundException {
        String jsonStr = null;

        try {
            InputStream inputStream = context.getAssets().open("data.json");
            int sizeOfFile = inputStream.available();
            byte[] bufferData = new byte[sizeOfFile];
            inputStream.read(bufferData);
            inputStream.close();
            jsonStr = new String(bufferData, "UTF-8");
        } catch (
                IOException e) {
            //System.out.println(e.getMessage());
        }

        //System.out.println(json);
        return jsonStr;
    }

    public static List<Movie> parseBufferToJson(BufferedReader buffer) {

        return null;
    }

    public static List<Movie> parseForHome(BufferedReader buffer) {
        Object o;
        JSONObject jsonObj;
        List<Movie> movies = new ArrayList<>();

        try {
            o = new JSONParser().parse(buffer);
            jsonObj = (JSONObject) o;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        JSONArray jsonArray = (JSONArray) jsonObj.get("releases");
        for (Object movieObject : jsonArray) {
            JSONObject movieJSON = (JSONObject) movieObject;
            JSONArray genresArray = (JSONArray) movieJSON.get("genres");

            List<String> genres = new ArrayList<>();
            for (Object genreObject : genresArray) {
                JSONObject genreJSON = (JSONObject) genreObject;
                genres.add((String) genreJSON.get("genre"));
            }

            Double rat = (Double) movieJSON.get("rating");
            if (rat == null) {
                rat = 0.0;
            }

            movies.add(new Movie(Math.toIntExact((Long) movieJSON.get("filmId")),
                    (String) movieJSON.get("nameRu"),
                    (Math.toIntExact((Long) movieJSON.get("year"))),
                    (String) movieJSON.get("posterUrlPreview"),
                    rat,
                    genres));
        }

        return movies;
    }

    public static ExtendedMovie parseForMovie(BufferedReader buffer) {
        Object o;
        JSONObject jsonObj;
        ExtendedMovie movie = new ExtendedMovie();

        try {
            o = new JSONParser().parse(buffer);
            jsonObj = (JSONObject) o;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        movie.setNameRu((String) jsonObj.get("nameRu"));
        movie.setPosterUrlPreview((String) jsonObj.get("posterUrlPreview"));
        //movie.setYear();
        movie.setSlogan((String) jsonObj.get("slogan"));
        movie.setDescription((String) jsonObj.get("description"));

        Double rat = (Double) jsonObj.get("ratingKinopoisk");
        if (rat == null) {
            rat = 0.0;
        }
        movie.setRating(rat);

        JSONArray genresArray = (JSONArray) jsonObj.get("genres");
        List<String> genres = new ArrayList<>();
        for (Object genreObject : genresArray) {
            JSONObject genreJSON = (JSONObject) genreObject;
            genres.add((String) genreJSON.get("genre"));
        }
        movie.setGenres(genres);

        return movie;
    }

    public static List<Movie> parseForSearch(BufferedReader buffer) {
        Object o;
        JSONObject jsonObj;
        List<Movie> movies = new ArrayList<>();

        try {
            o = new JSONParser().parse(buffer);
            jsonObj = (JSONObject) o;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        JSONArray jsonArray = (JSONArray) jsonObj.get("films"); // Парсинг массива фильма
        for (Object movieObject : jsonArray) {
            JSONObject movieJSON = (JSONObject) movieObject;
            JSONArray genresArray = (JSONArray) movieJSON.get("genres");

            List<String> genres = new ArrayList<>();
            for (Object genreObject : genresArray) { // Парсинг списка жанров
                JSONObject genreJSON = (JSONObject) genreObject;
                genres.add((String) genreJSON.get("genre"));
            }

            String ratStr = (String) movieJSON.get("rating"); // Парсинг рейтинга
            Double rat = 0.0;
            try {
                rat = Double.parseDouble(ratStr);
            } catch (NumberFormatException exception) {

            }

            String yearStr = (String) movieJSON.get("year");
            int year = 0;
            try {
                year = Integer.valueOf(yearStr);
            } catch (NumberFormatException exception) {

            }

            movies.add(new Movie(Math.toIntExact((Long) movieJSON.get("filmId")),
                    (String) movieJSON.get("nameRu"),
                    year,
                    (String) movieJSON.get("posterUrlPreview"),
                    rat,
                    genres));
        }

        return movies;
    }
}
