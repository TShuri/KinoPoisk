package com.shuri.kinopoisk.ui.home;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.shuri.kinopoisk.MainActivity;
import com.shuri.kinopoisk.R;
import com.shuri.kinopoisk.adapters.HomeRecViewAdapter;
import com.shuri.kinopoisk.databinding.FragmentHomeBinding;
import com.shuri.kinopoisk.models.Movie;
import com.shuri.kinopoisk.parsers.JSONSimpleParser;

import java.io.BufferedReader;
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
    public RecyclerView recyclerView;
    public HomeRecViewAdapter adapter;

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

        recyclerView = root.findViewById(R.id.recyclerView);
        adapter = new HomeRecViewAdapter(root.getContext(), movies, (MainActivity) getActivity());
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public class KinoPoisk extends AsyncTask <Void, Void, List<Movie>>{
        private ProgressDialog loadDialog = new ProgressDialog(getActivity());
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
        protected void onPreExecute() {
            super.onPreExecute();
            loadDialog.show();
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
                //connection.disconnect();
            }
            List<Movie> movs = JSONSimpleParser.parseForHome(bfR);

            return movs;
        }

        @Override
        protected void onPostExecute(List<Movie> _movies) {
            movies = new ArrayList<>(_movies);
            adapter.setData(_movies);

            if (loadDialog.isShowing()) {
                loadDialog.dismiss();
            }
        }
    }
}

