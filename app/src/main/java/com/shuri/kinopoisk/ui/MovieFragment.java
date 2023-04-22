package com.shuri.kinopoisk.ui;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuri.kinopoisk.R;
import com.shuri.kinopoisk.models.ExtendedMovie;
import com.shuri.kinopoisk.parsers.JSONSimpleParser;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MovieFragment extends Fragment {
    int idMovie;

    TextView nameMovie;
    TextView slogan;
    TextView description;
    TextView rating;
    TextView genres;
    ImageView imgMovie;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        idMovie = getArguments().getInt("id");

        //nameMovie = getView().findViewById(R.id.titleNameMovie);
        //slogan = getView().findViewById(R.id.titleSloganMovie);
        //description = getView().findViewById(R.id.titleDescriptionMovie);
        //rating = getView().findViewById(R.id.titleRatingMovie);

        MovieApi movieApi = new MovieApi();
        movieApi.execute(idMovie);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie, container, false);

        nameMovie = view.findViewById(R.id.titleNameMovie);
        slogan = view.findViewById(R.id.titleSloganMovie);
        description = view.findViewById(R.id.titleDescriptionMovie);
        rating = view.findViewById(R.id.titleRatingMovie);
        genres = view.findViewById(R.id.titleGenresMovie);
        imgMovie = view.findViewById(R.id.bigImgMovie);

        return view;
    }

    public class MovieApi extends AsyncTask<Integer, Void, ExtendedMovie> {
        private String apiKey = "88916739-94f0-46b3-bdac-b96201304527";
        private String urlMovie = "https://kinopoiskapiunofficial.tech/api/v2.2/films/";

        private URL url = null;
        private HttpsURLConnection connection = null;

        private BufferedReader bfR = null;
        private InputStreamReader isR = null;

        private void readResponse() {
            try { // Reading response
                isR = new InputStreamReader(connection.getInputStream());
                bfR = new BufferedReader(isR);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected ExtendedMovie doInBackground(Integer... integers) {
            try { // HTTPS connection
                urlMovie = urlMovie + idMovie;
                System.out.println(urlMovie);
                url = new URL(urlMovie);

                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.addRequestProperty("X-API-KEY", apiKey);
                connection.addRequestProperty("Content-Type", "application/json");

                connection.connect();
                System.out.println(connection.getResponseCode());
                readResponse();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            finally {
                connection.disconnect();
            }
            ExtendedMovie mov = JSONSimpleParser.parseForMovie(bfR);

            return mov;
        }

        @Override
        protected void onPostExecute(ExtendedMovie extendedMovie) {
            super.onPostExecute(extendedMovie);

            Picasso.get().load(extendedMovie.getPosterUrlPreview()).into(imgMovie);
            nameMovie.setText(extendedMovie.getNameRu());
            slogan.setText(extendedMovie.getSlogan());
            description.setText(extendedMovie.getDescription());
            rating.setText(String.valueOf(extendedMovie.getRating()));
            genres.setText(extendedMovie.getStringGenres());
        }
    }
}