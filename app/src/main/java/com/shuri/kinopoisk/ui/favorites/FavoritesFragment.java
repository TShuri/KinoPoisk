package com.shuri.kinopoisk.ui.favorites;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.shuri.kinopoisk.MainActivity;
import com.shuri.kinopoisk.R;
import com.shuri.kinopoisk.adapters.FavRecViewAdapter;
import com.shuri.kinopoisk.databases.DBHelper;
import com.shuri.kinopoisk.databinding.FragmentFavoritesBinding;
import com.shuri.kinopoisk.models.Movie;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;

    private List<Movie> unwatchedMovies, watchedMovies, ratedMovies;

    private TextView unwatchCount, watchCount, rateCount;

    private RecyclerView rvUnwatch, rvWatched, rvRated;
    private FavRecViewAdapter adapterUnwatch, adapterWatched, adapterRated;

    DBHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        unwatchedMovies = new ArrayList<>();
        watchedMovies = new ArrayList<>();
        ratedMovies = new ArrayList<>();

        dbHelper = new DBHelper(getContext());
        //dbHelper.create_db();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FavoritesViewModel favoritesViewModel =
                new ViewModelProvider(this).get(FavoritesViewModel.class);

        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        unwatchCount = root.findViewById(R.id.textCountUnwatch);
        watchCount = root.findViewById(R.id.textCountWatch);
        rateCount = root.findViewById(R.id.textCountRated);

        rvUnwatch = root.findViewById(R.id.UnwatchRecyclerView);
        adapterUnwatch = new FavRecViewAdapter(root.getContext(), unwatchedMovies, unwatchCount, (MainActivity) getActivity());
        rvUnwatch.setAdapter(adapterUnwatch);

        rvWatched = root.findViewById(R.id.WatchRecyclerView);
        adapterWatched = new FavRecViewAdapter(root.getContext(), watchedMovies, watchCount, (MainActivity) getActivity());
        rvWatched.setAdapter(adapterWatched);

        rvRated = root.findViewById(R.id.ratedRecyclerView);
        adapterRated = new FavRecViewAdapter(root.getContext(), ratedMovies, rateCount, (MainActivity) getActivity());
        rvRated.setAdapter(adapterRated);

        //unwatchedMovies = initListMovies(DBHelper.TABLE_UNWATCHED);
        adapterUnwatch.setData(initListMovies(DBHelper.TABLE_UNWATCHED));
        adapterWatched.setData(initListMovies(DBHelper.TABLE_WATCHED));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private List<Movie> initListMovies(String tableName) {
        List<Movie> initList = new ArrayList<>();

        database = dbHelper.getWritableDatabase();

        cursor = database.query(tableName, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.COLUMN_ID);
            int movIdIndex = cursor.getColumnIndex(DBHelper.COLUMN_MOVIE_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.COLUMN_MOVIE_NAME);
            int urlIndex = cursor.getColumnIndex(DBHelper.COLUMN_MOVIE_URL);
            int ratingIndex = cursor.getColumnIndex(DBHelper.COLUMN_MOVIE_RATING);
            do {
                Movie mov = new Movie(); // !!! НЕПОЛНАЯ ИНИЦИАЛИЗАЦИЯ ОБЪЕКТА

                mov.setFilmId(cursor.getInt(movIdIndex));
                mov.setNameRu(cursor.getString(nameIndex));
                mov.setPosterUrlPreview(cursor.getString(urlIndex));
                mov.setRating(cursor.getDouble(ratingIndex));

                Log.d("mLog", "tableName = " + tableName + "ID = " + cursor.getInt(idIndex) +
                        ", idMovie = " + cursor.getInt(movIdIndex) +
                        ", name = " + cursor.getString(nameIndex) +
                        ", url = " + cursor.getString(urlIndex) +
                        ", rating = " + cursor.getDouble(ratingIndex));

                initList.add(mov);
            } while (cursor.moveToNext());
        } else
            Log.d("mLog", "No movies");

        cursor.close();

        dbHelper.close();

        //adapterUnwatch.setData(unwatchedMovies);
        return initList;
    }
}