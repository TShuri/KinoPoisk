package com.shuri.kinopoisk.ui;

import android.app.ProgressDialog;
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
import java.util.Queue;

import javax.net.ssl.HttpsURLConnection;

public class MovieFragment extends Fragment implements View.OnClickListener{
    int idMovie;
    String urlPreview;
    Boolean watch, unwatch, rate;

    TextView nameMovie, slogan, description, rating, genres;
    TextView tvUnwatch;

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

        MovieApi movieApi = new MovieApi();
        movieApi.execute(idMovie);

        dbHelperMov = new DBHelper(this.getContext());

        watch = checkInDB(DBHelper.TABLE_WATCHED, DBHelper.COLUMN_MOVIE_ID, String.valueOf(idMovie));
        unwatch = checkInDB(DBHelper.TABLE_UNWATCHED, DBHelper.COLUMN_MOVIE_ID, String.valueOf(idMovie));
        rate = checkInDB(DBHelper.TABLE_RATED, DBHelper.COLUMN_MOVIE_ID, String.valueOf(idMovie));
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

        tvUnwatch = view.findViewById(R.id.tvUnwatch);

        btnRate = (ImageButton) view.findViewById(R.id.imgBtnRate);
        btnRate.setOnClickListener(this);

        btnWillWatch = (ImageButton) view.findViewById(R.id.imgBtnWillwatch);
        btnWillWatch.setOnClickListener(this);

        btnUnwatch = (ImageButton) view.findViewById(R.id.imgBtnUnwatch);
        btnUnwatch.setOnClickListener(this);

        if (unwatch) {
            btnWillWatch.setImageResource(R.drawable.ic_bookmark_added_32dp);
        }
        if(watch) {
            btnUnwatch.setImageResource(R.drawable.ic_check_circle_32dp);
            tvUnwatch.setText("Просмотрено");
        }

        return view;
    }

    @Override
    public void onClick(View view) {

        database = dbHelperMov.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        switch (view.getId()) {
            case R.id.imgBtnRate: {
                //database.delete(DBHelper.TABLE_UNWATCHED, DBHelper.COLUMN_MOVIE_ID + "= " + idMovie, null);

                break;
            }
            case R.id.imgBtnWillwatch: {
                if (unwatch) {
                    unwatch = false;

                    database.delete(DBHelper.TABLE_UNWATCHED, DBHelper.COLUMN_MOVIE_ID + "= " + idMovie, null);
                    btnWillWatch.setImageResource(R.drawable.ic_bookmark_border_32dp);
                    Toast toast = Toast.makeText(getContext(), "Удалено из Буду смотреть", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    unwatch = true;

                    contentValues.put(DBHelper.COLUMN_MOVIE_ID, (Integer) idMovie);
                    contentValues.put(DBHelper.COLUMN_MOVIE_NAME, nameMovie.getText().toString());
                    contentValues.put(DBHelper.COLUMN_MOVIE_URL, urlPreview); // Example
                    contentValues.put(DBHelper.COLUMN_MOVIE_RATING, Double.parseDouble(rating.getText().toString()));

                    database.insert(DBHelper.TABLE_UNWATCHED, null, contentValues);

                    btnWillWatch.setImageResource(R.drawable.ic_bookmark_added_32dp);
                    Toast toast = Toast.makeText(getContext(), "Добавлено в Буду смотреть", Toast.LENGTH_SHORT);
                    toast.show();
                }

                break;
            }
            case R.id.imgBtnUnwatch: {
                if (watch) {
                    watch = false;

                    database.delete(DBHelper.TABLE_WATCHED, DBHelper.COLUMN_MOVIE_ID + "= " + idMovie, null);

                    btnUnwatch.setImageResource(R.drawable.ic_check_circle_outline_32dp);
                    tvUnwatch.setText("Не просмотрено");

                    Toast toast = Toast.makeText(getContext(), "Удалено из Просмотрено", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    watch = true;

                    contentValues.put(DBHelper.COLUMN_MOVIE_ID, (Integer) idMovie);
                    contentValues.put(DBHelper.COLUMN_MOVIE_NAME, nameMovie.getText().toString());
                    contentValues.put(DBHelper.COLUMN_MOVIE_URL, urlPreview); // Example
                    contentValues.put(DBHelper.COLUMN_MOVIE_RATING, Double.parseDouble(rating.getText().toString()));

                    database.insert(DBHelper.TABLE_WATCHED, null, contentValues);

                    btnUnwatch.setImageResource(R.drawable.ic_check_circle_32dp);
                    tvUnwatch.setText("Просмотрено");

                    Toast toast = Toast.makeText(getContext(), "Добавлено в Просмотрено", Toast.LENGTH_SHORT);
                    toast.show();
                }

                break;
            }
        }
        dbHelperMov.close();
    }

    private boolean checkInDB(String tableName, String dbField, String value) {
        database = dbHelperMov.getReadableDatabase();
        String query = "Select * from " + tableName + " where " + dbField + " = " + value;
        cursor = database.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public class MovieApi extends AsyncTask<Integer, Void, ExtendedMovie> {
        private ProgressDialog loadDialog = new ProgressDialog(getActivity());
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
        protected void onPreExecute() {
            super.onPreExecute();
            loadDialog.show();
        }

        @Override
        protected ExtendedMovie doInBackground(Integer... integers) {
            try { // HTTPS connection
                urlMovie = urlMovie + idMovie;
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

            if (loadDialog.isShowing()) {
                loadDialog.dismiss();
            }
        }
    }
}