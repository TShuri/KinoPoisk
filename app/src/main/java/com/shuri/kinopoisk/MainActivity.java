package com.shuri.kinopoisk;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.shuri.kinopoisk.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    FragmentManager fragmentManager;

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

        fragmentManager = getSupportFragmentManager();

    }

    public void showMovieFragmentFromHome(int idMovie) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", idMovie);

        navController.navigate(R.id.action_navigation_home_to_navigation_movie2, bundle);
    }
    public void showMovieFragmentFromSearch(int idMovie) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", idMovie);

        navController.navigate(R.id.action_navigation_search_to_navigation_movie, bundle);
    }
}