package com.shuri.kinopoisk.ui.search;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.shuri.kinopoisk.MainActivity;
import com.shuri.kinopoisk.R;
import com.shuri.kinopoisk.adapters.SeacrhRecViewAdapter;
import com.shuri.kinopoisk.databinding.FragmentSearchBinding;
import com.shuri.kinopoisk.models.Movie;
import com.shuri.kinopoisk.parsers.JSONSimpleParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;

    private List<Movie> searchMovies;
    private RecyclerView recyclerSearchView;
    private SeacrhRecViewAdapter searchAdapter;

    private EditText editTextSearch;
    private ImageButton buttonSearch;
    private String keyword;

    private Parcelable recyclerViewState;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchMovies = new ArrayList<>();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //SearchApi searchApi = new SearchApi();

        //searchMovies = new ArrayList<>();

        editTextSearch = root.findViewById(R.id.editTextSearch);

        buttonSearch = root.findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyword = String.valueOf(editTextSearch.getText());
                SearchApi searchApi = new SearchApi();
                searchApi.execute();
            }
        });

        //initialMovies();

        recyclerSearchView = root.findViewById(R.id.recyclerSearchView);
        searchAdapter = new SeacrhRecViewAdapter(root.getContext(), searchMovies, (MainActivity) getActivity());
        recyclerSearchView.setAdapter(searchAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initialMovies() {
        searchMovies.add(new Movie(1));
        searchMovies.add(new Movie(2));
        searchMovies.add(new Movie(3));
        searchMovies.add(new Movie(4));
    }

    public class SearchApi extends AsyncTask<Void, Void, List<Movie>> {
        private String apiKey = "88916739-94f0-46b3-bdac-b96201304527";
        private String urlBase = "https://kinopoiskapiunofficial.tech/api/v2.1/films/search-by-keyword?keyword=";
        private String page = "&page=1";

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
                String urlAdress = urlBase + URLEncoder.encode(keyword) + page;
                url = new URL(urlAdress);

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
            } finally {
                //connection.disconnect();
            }
            List<Movie> movs = JSONSimpleParser.parseForSearch(bfR);

            return movs;

            //searchMovies = new ArrayList<>(_movies);
            //searchAdapter.setData(searchMovies);
            //searchAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);

            searchMovies = new ArrayList<>(movies);
            searchAdapter.setData(searchMovies);
            recyclerSearchView.getLayoutManager().onSaveInstanceState();
            //searchAdapter.notifyDataSetChanged();
        }
    }
}