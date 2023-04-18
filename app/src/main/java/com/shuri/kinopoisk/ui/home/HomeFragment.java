package com.shuri.kinopoisk.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.shuri.kinopoisk.MainActivity;
import com.shuri.kinopoisk.R;
import com.shuri.kinopoisk.adapters.HomeRecViewAdapter;
import com.shuri.kinopoisk.databinding.FragmentHomeBinding;
import com.shuri.kinopoisk.models.Movie;
import com.shuri.kinopoisk.parsers.JSONSimpleParser;
import com.shuri.kinopoisk.ui.MovieFragment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class HomeFragment extends Fragment{
    private FragmentHomeBinding binding;

    public List<Movie> movies;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        movies = new ArrayList<>();

        KinoPoisk kinoPoisk = new KinoPoisk();
        kinoPoisk.execute();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        HomeRecViewAdapter adapter = new HomeRecViewAdapter(root.getContext(), movies, (MainActivity) getActivity());
        recyclerView.setAdapter(adapter);

        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initialMovies() {
        movies.add(new Movie(1));
        movies.add(new Movie(2));
        movies.add(new Movie(3));
        movies.add(new Movie(4));
    }

    public class KinoPoisk extends AsyncTask <Void, Void, List<Movie>>{
        private String apiKey = "88916739-94f0-46b3-bdac-b96201304527";
        private String urlReleases = "https://kinopoiskapiunofficial.tech/api/v2.1/films/releases?year=2023&month=MARCH&page=1";

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
        protected List<Movie> doInBackground(Void... voids) {
            try { // HTTPS connection
                url = new URL(urlReleases);

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
            List<Movie> movs = JSONSimpleParser.parseForHome(bfR);

            return movs;
        }

        @Override
        protected void onPostExecute(List<Movie> _movies) {
            movies = new ArrayList<>(_movies);
        }
    }
}

