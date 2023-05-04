package com.shuri.kinopoisk.ui;

import android.app.ActionBar;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shuri.kinopoisk.MainActivity;
import com.shuri.kinopoisk.R;
import com.shuri.kinopoisk.databases.DBHelper;
import com.shuri.kinopoisk.models.ExtendedMovie;
import com.shuri.kinopoisk.models.Movie;
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
    Boolean viewed, unwatched, rated;

    TextView nameMovie, slogan, description, rating, genres;
    TextView tvUnwatched, tvRated;

    ImageView imgMovie;

    ImageButton btnRate, btnWillWatch, btnUnwatch;

    DBHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        idMovie = getArguments().getInt("id");
        urlPreview = getArguments().getString("url");

        MovieApi movieApi = new MovieApi();
        movieApi.execute(idMovie);

        dbHelper = new DBHelper(this.getContext());

        checkInDB();
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

        tvUnwatched = view.findViewById(R.id.tvUnwatch);
        tvRated = view.findViewById(R.id.tvRate);

        btnRate = (ImageButton) view.findViewById(R.id.imgBtnRate);
        btnRate.setOnClickListener(this);

        btnWillWatch = (ImageButton) view.findViewById(R.id.imgBtnWillwatch);
        btnWillWatch.setOnClickListener(this);

        btnUnwatch = (ImageButton) view.findViewById(R.id.imgBtnUnwatch);
        btnUnwatch.setOnClickListener(this);

        if (unwatched) {
            btnWillWatch.setImageResource(R.drawable.ic_bookmark_added_32dp);
        }
        if(viewed) {
            btnUnwatch.setImageResource(R.drawable.ic_check_circle_32dp);
            tvUnwatched.setText("Просмотрено");
        }
        if(rated) {
            btnRate.setImageResource(R.drawable.ic_star_32dp);
            tvRated.setText("Оценено");
        }

        return view;
    }

    @Override
    public void onClick(View view) {

        database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        switch (view.getId()) {
            case R.id.imgBtnRate: {
                createRatingDialog();

                break;
            }
            case R.id.imgBtnWillwatch: {
                if (unwatched) {
                    unwatched = false;

                    if (rated) {
                        contentValues.put(DBHelper.COLUMN_ID, (Integer) idMovie);
                        contentValues.put(DBHelper.COLUMN_NAME, nameMovie.getText().toString());
                        contentValues.put(DBHelper.COLUMN_PREVIEW, urlPreview); // Example
                        contentValues.put(DBHelper.COLUMN_RATING, Double.parseDouble(rating.getText().toString()));
                        contentValues.put(DBHelper.COLUMN_UNWATCHED, 0);
                        contentValues.put(DBHelper.COLUMN_VIEWED, 0);
                        contentValues.put(DBHelper.COLUMN_RATED, 1);

                        String idFilter = DBHelper.COLUMN_ID + "=" + idMovie;
                        database.update(DBHelper.TABLE_MOVIES, contentValues, idFilter, null);
                    } else {
                        database.delete(DBHelper.TABLE_MOVIES, DBHelper.COLUMN_ID + "= " + idMovie, null);
                    }

                    btnWillWatch.setImageResource(R.drawable.ic_bookmark_border_32dp);
                    Toast toast = Toast.makeText(getContext(), "Удалено из Буду смотреть", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    unwatched = true;

                    contentValues.put(DBHelper.COLUMN_ID, (Integer) idMovie);
                    contentValues.put(DBHelper.COLUMN_NAME, nameMovie.getText().toString());
                    contentValues.put(DBHelper.COLUMN_PREVIEW, urlPreview); // Example
                    contentValues.put(DBHelper.COLUMN_RATING, Double.parseDouble(rating.getText().toString()));
                    contentValues.put(DBHelper.COLUMN_UNWATCHED, 1);
                    contentValues.put(DBHelper.COLUMN_VIEWED, 0);
                    if (rated) {
                        contentValues.put(DBHelper.COLUMN_RATED, 1);
                    } else {
                        contentValues.put(DBHelper.COLUMN_RATED, 0);
                    }

                    if (viewed) {
                        btnUnwatch.setImageResource(R.drawable.ic_check_circle_outline_32dp);
                        tvUnwatched.setText("Не просмотрено");
                    }

                    if (rated || viewed) {
                        String idFilter = DBHelper.COLUMN_ID + "=" + idMovie;
                        database.update(DBHelper.TABLE_MOVIES, contentValues, idFilter, null);
                    } else {
                        database.insert(DBHelper.TABLE_MOVIES, null, contentValues);
                    }

                    btnWillWatch.setImageResource(R.drawable.ic_bookmark_added_32dp);

                    Toast toast = Toast.makeText(getContext(), "Добавлено в Буду смотреть", Toast.LENGTH_SHORT);
                    toast.show();
                }
                cursor.close();
                break;
            }
            case R.id.imgBtnUnwatch: {
                if (viewed) {
                    viewed = false;

                    if (rated) {
                        contentValues.put(DBHelper.COLUMN_ID, (Integer) idMovie);
                        contentValues.put(DBHelper.COLUMN_NAME, nameMovie.getText().toString());
                        contentValues.put(DBHelper.COLUMN_PREVIEW, urlPreview); // Example
                        contentValues.put(DBHelper.COLUMN_RATING, Double.parseDouble(rating.getText().toString()));
                        contentValues.put(DBHelper.COLUMN_UNWATCHED, 0);
                        contentValues.put(DBHelper.COLUMN_VIEWED, 0);
                        contentValues.put(DBHelper.COLUMN_RATED, 1);

                        String idFilter = DBHelper.COLUMN_ID + "=" + idMovie;
                        database.update(DBHelper.TABLE_MOVIES, contentValues, idFilter, null);
                    } else {
                        database.delete(DBHelper.TABLE_MOVIES, DBHelper.COLUMN_ID + "= " + idMovie, null);
                    }

                    btnUnwatch.setImageResource(R.drawable.ic_check_circle_outline_32dp);
                    tvUnwatched.setText("Не просмотрено");

                    Toast toast = Toast.makeText(getContext(), "Удалено из Просмотрено", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    viewed = true;

                    contentValues.put(DBHelper.COLUMN_ID, (Integer) idMovie);
                    contentValues.put(DBHelper.COLUMN_NAME, nameMovie.getText().toString());
                    contentValues.put(DBHelper.COLUMN_PREVIEW, urlPreview); // Example
                    contentValues.put(DBHelper.COLUMN_RATING, Double.parseDouble(rating.getText().toString()));
                    contentValues.put(DBHelper.COLUMN_UNWATCHED, 0);
                    contentValues.put(DBHelper.COLUMN_VIEWED, 1);
                    if (rated) {
                        contentValues.put(DBHelper.COLUMN_RATED, 1);
                    } else {
                        contentValues.put(DBHelper.COLUMN_RATED, 0);
                    }

                    if (unwatched) {
                        btnWillWatch.setImageResource(R.drawable.ic_bookmark_border_32dp);
                    }

                    if (rated || unwatched) {
                        String idFilter = DBHelper.COLUMN_ID + "=" + idMovie;
                        database.update(DBHelper.TABLE_MOVIES, contentValues, idFilter, null);
                    } else {
                        database.insert(DBHelper.TABLE_MOVIES, null, contentValues);
                    }

                    btnUnwatch.setImageResource(R.drawable.ic_check_circle_32dp);
                    tvUnwatched.setText("Просмотрено");

                    Toast toast = Toast.makeText(getContext(), "Добавлено в Просмотрено", Toast.LENGTH_SHORT);
                    toast.show();
                }
                cursor.close();
                break;
            }
        }
        dbHelper.close();
    }

    private void checkInDB() {
        database = dbHelper.getReadableDatabase();
        String query = "Select * from " + DBHelper.TABLE_MOVIES + " where " + DBHelper.COLUMN_ID + " = " + idMovie;
        cursor = database.rawQuery(query, null);

        if (cursor.getCount() <= 0) {
            unwatched = false;
            viewed = false;
            rated = false;
            return;
        }

        cursor.moveToFirst();

        int unwatchedIndex = cursor.getColumnIndex(DBHelper.COLUMN_UNWATCHED);
        int ratedIndex = cursor.getColumnIndex(DBHelper.COLUMN_RATED);
        int viewedIndex = cursor.getColumnIndex(DBHelper.COLUMN_VIEWED);

        int checkUnw = cursor.getInt(unwatchedIndex); // буду смотреть
        int checkRat = cursor.getInt(ratedIndex); // оцененный
        int checkView = cursor.getInt(viewedIndex); // просмотренный

        if (checkUnw == 1) {
            unwatched = true;
        } else {
            unwatched = false;
        }
        if (checkView == 1){
            viewed = true;
        } else {
            viewed = false;
        }
        if (checkRat == 1){
            rated = true;
        } else {
            rated = false;
        }
    }

    private void createRatingDialog() {
        Dialog ratingDialog = new Dialog(getContext());
        ratingDialog.setContentView(R.layout.dialog_rating);

        RatingBar ratingBar = ratingDialog.findViewById(R.id.ratingBar);
        EditText etReview = ratingDialog.findViewById(R.id.etReview);

        Button btnDelRev = ratingDialog.findViewById(R.id.btnDelRev);
        btnDelRev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = dbHelper.getWritableDatabase();

                rated = false;

                database.delete(DBHelper.TABLE_REVIEWS, DBHelper.COLUMN_ID + "= " + idMovie, null);

                ContentValues contentValues = new ContentValues();
                contentValues.put(DBHelper.COLUMN_ID, (Integer) idMovie);
                contentValues.put(DBHelper.COLUMN_NAME, nameMovie.getText().toString());
                contentValues.put(DBHelper.COLUMN_PREVIEW, urlPreview); // Example
                contentValues.put(DBHelper.COLUMN_RATING, Double.parseDouble(rating.getText().toString()));
                contentValues.put(DBHelper.COLUMN_RATED, 0);
                if (unwatched) {
                    contentValues.put(DBHelper.COLUMN_UNWATCHED, 1);
                } else {
                    contentValues.put(DBHelper.COLUMN_UNWATCHED, 0);
                }

                if (viewed) {
                    contentValues.put(DBHelper.COLUMN_VIEWED, 1);
                } else {
                    contentValues.put(DBHelper.COLUMN_VIEWED, 0);
                }

                if (viewed || unwatched) {
                    String idFilter = DBHelper.COLUMN_ID + "=" + idMovie;
                    database.update(DBHelper.TABLE_MOVIES, contentValues, idFilter, null);
                } else {
                    database.delete(DBHelper.TABLE_MOVIES, DBHelper.COLUMN_ID + "= " + idMovie, null);
                }

                btnRate.setImageResource(R.drawable.ic_star_border_32dp);
                tvRated.setText("Оценить");

                Toast toast = Toast.makeText(getContext(), "Удалено из Оцененные", Toast.LENGTH_SHORT);
                toast.show();

                ratingDialog.dismiss();
                dbHelper.close();
            }
        });
        btnDelRev.setVisibility(View.INVISIBLE);

        Button btnAddRev = ratingDialog.findViewById(R.id.btnAddRev);
        btnAddRev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();

                if (rated) {
                    contentValues.put(DBHelper.COLUMN_RATING, ratingBar.getRating());
                    contentValues.put(DBHelper.COLUMN_REVIEW, String.valueOf(etReview.getText()));

                    String idFilter = DBHelper.COLUMN_ID + "=" + idMovie;
                    database.update(DBHelper.TABLE_REVIEWS, contentValues, idFilter, null);

                    Toast toast = Toast.makeText(getContext(), "Отзыв обновлен", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    rated = true;

                    contentValues.put(DBHelper.COLUMN_ID, (Integer) idMovie);
                    contentValues.put(DBHelper.COLUMN_RATING, ratingBar.getRating());
                    contentValues.put(DBHelper.COLUMN_REVIEW, String.valueOf(etReview.getText()));

                    database.insert(DBHelper.TABLE_REVIEWS, null, contentValues);

                    contentValues = new ContentValues();
                    contentValues.put(DBHelper.COLUMN_ID, (Integer) idMovie);
                    contentValues.put(DBHelper.COLUMN_NAME, nameMovie.getText().toString());
                    contentValues.put(DBHelper.COLUMN_PREVIEW, urlPreview); // Example
                    contentValues.put(DBHelper.COLUMN_RATING, Double.parseDouble(rating.getText().toString()));
                    contentValues.put(DBHelper.COLUMN_RATED, 1);
                    if (unwatched) {
                        contentValues.put(DBHelper.COLUMN_UNWATCHED, 1);
                    } else {
                        contentValues.put(DBHelper.COLUMN_UNWATCHED, 0);
                    }

                    if (viewed) {
                        contentValues.put(DBHelper.COLUMN_VIEWED, 1);
                    } else {
                        contentValues.put(DBHelper.COLUMN_VIEWED, 0);
                    }

                    if (viewed || unwatched) {
                        String idFilter = DBHelper.COLUMN_ID + "=" + idMovie;
                        database.update(DBHelper.TABLE_MOVIES, contentValues, idFilter, null);
                    } else {
                        database.insert(DBHelper.TABLE_MOVIES, null, contentValues);
                    }


                    btnRate.setImageResource(R.drawable.ic_star_32dp);
                    tvRated.setText("Оценено");

                    Toast toast = Toast.makeText(getContext(), "Отзыв добавлен", Toast.LENGTH_SHORT);
                    toast.show();
                }
                ratingDialog.dismiss();
                dbHelper.close();
            }
        });

        if (rated) {
            String query = "Select * from " + DBHelper.TABLE_REVIEWS + " where " + DBHelper.COLUMN_ID + " = " + idMovie;
            cursor = database.rawQuery(query, null);
            cursor.moveToNext();

            int indexMyRating = cursor.getColumnIndex(DBHelper.COLUMN_RATING);
            int indexMyReview = cursor.getColumnIndex(DBHelper.COLUMN_REVIEW);

            ratingBar.setRating(cursor.getFloat(indexMyRating));
            etReview.setText(cursor.getString(indexMyReview));

            btnDelRev.setVisibility(View.VISIBLE);
            btnAddRev.setText("Обновить отзыв");

            cursor.close();
        }

        ratingDialog.show();
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