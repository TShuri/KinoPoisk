package com.shuri.kinopoisk.parsers;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import com.shuri.kinopoisk.MainActivity;
import com.shuri.kinopoisk.models.Movie;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JSONSimpleParser {
    Object object;
    JSONObject jsonObject;
    List<Movie> movies;
    String json = null;

    public List<Movie> parsing() {
        try {
            object = new JSONParser().parse(json);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        jsonObject = (JSONObject) object;

        movies = new ArrayList<>();

        JSONArray jsonArray = (JSONArray) jsonObject.get("releases");
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

    public String parseToString(Context context) throws FileNotFoundException {

        try {
            InputStream inputStream = context.getAssets().open("data.json");
            int sizeOfFile = inputStream.available();
            byte[] bufferData = new byte[sizeOfFile];
            inputStream.read(bufferData);
            inputStream.close();
            json = new String(bufferData, "UTF-8");
        } catch (
                IOException e) {
            System.out.println(e.getMessage());
        }

        //System.out.println(json);
        return json;
    }
}
