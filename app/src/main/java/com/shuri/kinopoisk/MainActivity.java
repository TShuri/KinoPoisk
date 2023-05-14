package com.shuri.kinopoisk;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.shuri.kinopoisk.databases.DBHelper;
import com.shuri.kinopoisk.databinding.ActivityMainBinding;
import com.shuri.kinopoisk.models.Movie;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_favorites, R.id.navigation_search)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public void showMovieFragmentFromHome(Movie _movie) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", _movie.getFilmId());
        bundle.putString("url", _movie.getPosterUrlPreview());

        navController.navigate(R.id.action_navigation_home_to_navigation_movie2, bundle);
    }
    public void showMovieFragmentFromSearch(Movie _movie) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", _movie.getFilmId());
        bundle.putString("url", _movie.getPosterUrlPreview());

        navController.navigate(R.id.action_navigation_search_to_navigation_movie, bundle);
    }

    public void showMovieFragmentFromFavorites(Movie _movie) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", _movie.getFilmId());
        bundle.putString("url", _movie.getPosterUrlPreview());

        navController.navigate(R.id.action_navigation_favorites_to_navigation_movie, bundle);
    }
}