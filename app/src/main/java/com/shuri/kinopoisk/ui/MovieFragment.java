package com.shuri.kinopoisk.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Message;
import android.service.autofill.OnClickAction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shuri.kinopoisk.R;
import com.shuri.kinopoisk.databases.DBHelper;
import com.shuri.kinopoisk.models.ExtendedMovie;
import com.shuri.kinopoisk.parsers.JSONSimpleParser;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ConcurrentModificationException;
import java.util.Currency;

import javax.net.ssl.HttpsURLConnection;

public class MovieFragment extends Fragment implements View.OnClickListener{
    int idMovie;
    String urlPreview;

    TextView nameMovie, slogan, description, rating, genres;

    ImageView imgMovie;

    ImageButton btnRate, btnWillWatch, btnUnwatch;

    DBHelper dbHelperMov;
    SQLiteDatabase database;
    Cursor cursor;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        idMovie = getArguments().getInt("id");
        urlPreview = getArguments().getString("url");

        //nameMovie = getView().findViewById(R.id.titleNameMovie);
        //slogan = getView().findViewById(R.id.titleSloganMovie);
        //description = getView().findViewById(R.id.titleDescriptionMovie);
        //rating = getView().findViewById(R.id.titleRatingMovie);

        MovieApi movieApi = new MovieApi();
        movieApi.execute(idMovie);

        dbHelperMov = new DBHelper(this.getContext());
        //database = dbHelperMov.open();
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

        btnRate = (ImageButton) view.findViewById(R.id.imgBtnRate);
        btnRate.setOnClickListener(this);

        btnWillWatch = (ImageButton) view.findViewById(R.id.imgBtnWillwatch);
        btnWillWatch.setOnClickListener(this);

        btnUnwatch = (ImageButton) view.findViewById(R.id.imgBtnUnwatch);
        btnUnwatch.setOnClickListener(this);

        //Log.d("mLog", imgMovie.)

        return view;
    }

    @Override
    public void onClick(View view) {

        database = dbHelperMov.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        switch (view.getId()) {
            case R.id.imgBtnRate: {
                database.delete(DBHelper.TABLE_UNWATCHED, DBHelper.COLUMN_MOVIE_ID + "= " + idMovie, null);

                break;
            }
            case R.id.imgBtnWillwatch: {
                contentValues.put(DBHelper.COLUMN_MOVIE_ID, (Integer) idMovie);
                contentValues.put(DBHelper.COLUMN_MOVIE_NAME, nameMovie.getText().toString());
                contentValues.put(DBHelper.COLUMN_MOVIE_URL, urlPreview); // Example
                contentValues.put(DBHelper.COLUMN_MOVIE_RATING, Double.parseDouble(rating.getText().toString()));

                database.insert(DBHelper.TABLE_UNWATCHED, null, contentValues);

                Toast toast = Toast.makeText(getContext(), "Added", Toast.LENGTH_SHORT);
                toast.show();

                break;
            }
            case R.id.imgBtnUnwatch: {

                cursor = database.query(DBHelper.TABLE_UNWATCHED, null, null, null, null, null, null);

                if (cursor.moveToFirst()) {
                    int idIndex = cursor.getColumnIndex(DBHelper.COLUMN_ID);
                    int movIdIndex = cursor.getColumnIndex(DBHelper.COLUMN_MOVIE_ID);
                    int nameIndex = cursor.getColumnIndex(DBHelper.COLUMN_MOVIE_NAME);
                    int urlIndex = cursor.getColumnIndex(DBHelper.COLUMN_MOVIE_URL);
                    int ratingIndex = cursor.getColumnIndex(DBHelper.COLUMN_MOVIE_RATING);
                        do {
                            Log.d("mLog", "ID = " + cursor.getInt(idIndex)+
                                                    ", idMovie = " + cursor.getInt(movIdIndex)+
                                                    ", name = " + cursor.getString(nameIndex)+
                                                    ", url = " + cursor.getString(urlIndex)+
                                                    ", rating = " + cursor.getDouble(ratingIndex));
                        } while (cursor.moveToNext());
                } else
                    Log.d("mLog", "No movies");

                cursor.close();
                break;
            }
        }
        dbHelperMov.close();
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