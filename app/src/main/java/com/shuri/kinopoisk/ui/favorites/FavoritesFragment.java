package com.shuri.kinopoisk.ui.favorites;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

public class FavoritesFragment extends Fragment implements View.OnClickListener {

    private FragmentFavoritesBinding binding;

    private List<Movie> unwatchedMovies, viewedMovies, ratedMovies;

    private TextView unwatchedCount, viewedCount, ratedCount;
    private ImageButton search, reset;
    private EditText keyword;

    private RecyclerView rvUnwatched, rvViewed, rvRated;
    private FavRecViewAdapter adapterUnwatched, adapterViewed, adapterRated;


    DBHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        unwatchedMovies = new ArrayList<>();
        viewedMovies = new ArrayList<>();
        ratedMovies = new ArrayList<>();

        dbHelper = new DBHelper(getContext());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FavoritesViewModel favoritesViewModel =
                new ViewModelProvider(this).get(FavoritesViewModel.class);

        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        search = root.findViewById(R.id.imgBtnSearch);
        search.setOnClickListener(this);
        reset = root.findViewById(R.id.imgBtnReset);
        reset.setOnClickListener(this);

        keyword = root.findViewById(R.id.etLocalSearch);

        unwatchedCount = root.findViewById(R.id.textCountUnwatch);
        viewedCount = root.findViewById(R.id.textCountWatch);
        ratedCount = root.findViewById(R.id.textCountRated);

        rvUnwatched = root.findViewById(R.id.UnwatchRecyclerView);
        adapterUnwatched = new FavRecViewAdapter(root.getContext(), unwatchedMovies, unwatchedCount, (MainActivity) getActivity());
        rvUnwatched.setAdapter(adapterUnwatched);

        rvViewed = root.findViewById(R.id.WatchRecyclerView);
        adapterViewed = new FavRecViewAdapter(root.getContext(), viewedMovies, viewedCount, (MainActivity) getActivity());
        rvViewed.setAdapter(adapterViewed);

        rvRated = root.findViewById(R.id.ratedRecyclerView);
        adapterRated = new FavRecViewAdapter(root.getContext(), ratedMovies, ratedCount, (MainActivity) getActivity());
        rvRated.setAdapter(adapterRated);

        initListMovies(false);
        adapterUnwatched.setData(unwatchedMovies);
        adapterViewed.setData(viewedMovies);
        adapterRated.setData(ratedMovies);

        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtnSearch: {
                initListMovies(true);
                adapterUnwatched.setData(unwatchedMovies);
                adapterViewed.setData(viewedMovies);
                adapterRated.setData(ratedMovies);

                Toast toast = Toast.makeText(getContext(), "Найдены следующие фильмы", Toast.LENGTH_SHORT);
                if (adapterUnwatched.getItemCount() == 0 && adapterViewed.getItemCount() == 0 && adapterRated.getItemCount() == 0)
                    toast = Toast.makeText(getContext(), "Фильмы не найдены", Toast.LENGTH_SHORT);
                toast.show();

                break;
            }
            case R.id.imgBtnReset: {
                initListMovies(false);
                adapterUnwatched.setData(unwatchedMovies);
                adapterViewed.setData(viewedMovies);
                adapterRated.setData(ratedMovies);

                break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initListMovies(Boolean search) {
        List<Movie> unwatchedList = new ArrayList<>();
        List<Movie> viewedList = new ArrayList<>();
        List<Movie> ratedList = new ArrayList<>();


        database = dbHelper.getWritableDatabase();

        if (search) {
            String searchKeyWord = String.valueOf(keyword.getText());
            String query = "SELECT * FROM " + DBHelper.TABLE_MOVIES + " WHERE " + DBHelper.COLUMN_NAME + " LIKE '%" + searchKeyWord + "%'";
            cursor = database.rawQuery(query, null);
        } else {
            cursor = database.query(DBHelper.TABLE_MOVIES, null, null, null, null, null, null);
        }

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.COLUMN_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.COLUMN_NAME);
            int ratingIndex = cursor.getColumnIndex(DBHelper.COLUMN_RATING);
            int previewIndex = cursor.getColumnIndex(DBHelper.COLUMN_PREVIEW);
            int unwatchedIndex = cursor.getColumnIndex(DBHelper.COLUMN_UNWATCHED);
            int ratedIndex = cursor.getColumnIndex(DBHelper.COLUMN_RATED);
            int viewedIndex = cursor.getColumnIndex(DBHelper.COLUMN_VIEWED);
            int checkUnw = 0; // буду смотреть
            int checkRat = 0; // оцененный
            int checkView = 0; // просмотренный
            do {
                Movie mov = new Movie(); // !!! НЕПОЛНАЯ ИНИЦИАЛИЗАЦИЯ ОБЪЕКТА

                mov.setFilmId(cursor.getInt(idIndex));
                mov.setNameRu(cursor.getString(nameIndex));
                mov.setPosterUrlPreview(cursor.getString(previewIndex));
                mov.setRating(cursor.getDouble(ratingIndex));

                checkUnw = cursor.getInt(unwatchedIndex);
                checkRat = cursor.getInt(ratedIndex);
                checkView = cursor.getInt(viewedIndex);

                if (checkUnw == 1) {
                    unwatchedList.add(mov);
                }
                if (checkView == 1){
                    viewedList.add(mov);
                }
                if (checkRat == 1){
                    ratedList.add(mov);
                }

            } while (cursor.moveToNext());
        }

        cursor.close();

        dbHelper.close();

        unwatchedMovies = new ArrayList<>(unwatchedList);
        viewedMovies = new ArrayList<>(viewedList);
        ratedMovies = new ArrayList<>(ratedList);
    }
}