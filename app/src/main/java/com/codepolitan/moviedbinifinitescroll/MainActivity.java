package com.codepolitan.moviedbinifinitescroll;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codepolitan.moviedbinifinitescroll.adapter.MovieAdapter;
import com.codepolitan.moviedbinifinitescroll.databinding.ActivityMainBinding;
import com.codepolitan.moviedbinifinitescroll.model.MovieResponse;
import com.google.gson.Gson;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    private int page = 1;
    private int lastPage = 0;
    private boolean isScroll = true;
    private MovieAdapter movieAdapter = new MovieAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fetchData(page);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvMovies.setLayoutManager(layoutManager);
        binding.rvMovies.setHasFixedSize(true);
        binding.rvMovies.setAdapter(movieAdapter);

        binding.rvMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int countItems = layoutManager.getItemCount();
                int currentItems = layoutManager.getChildCount();
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                int totalScrollItem = currentItems + firstVisiblePosition;

                if (isScroll && totalScrollItem >= countItems && page <= lastPage){
                    isScroll = false;
                    page += 1;
                    fetchData(page);
                }
            }
        });
    }

    private void fetchData(int page) {
        loadLoading();
        requestQueue = Volley.newRequestQueue(this);

        String mainUrl = BuildConfig.BASE_URL_MOVIE_DB + BuildConfig.API_VERSION;
        String movieUrl = mainUrl + "discover/movie?api_key=" + BuildConfig.TOKEN_MOVIE_DB + "&page=" + page;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, movieUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoading();
                        if (response != null){
                            MovieResponse movieResponse = new Gson().fromJson(response, MovieResponse.class);
                            if (movieAdapter.getDataMovies() > 0){
                                movieAdapter.removeDataLoading();
                                isScroll = true;
                            }
                            lastPage = movieResponse.getTotalPages();
                            movieAdapter.addDataMovies(movieResponse.getMovies());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                        Log.e(TAG, Objects.requireNonNull(error.getMessage()));
                    }
                });
        stringRequest.setTag(TAG);
        requestQueue.add(stringRequest);
    }

    private void hideLoading() {
        binding.swipeMain.setRefreshing(false);
    }

    private void loadLoading() {
        if (movieAdapter.getDataMovies() > 0){
            movieAdapter.addDataLoading();
        }
        binding.swipeMain.setRefreshing(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null){
            requestQueue.cancelAll(TAG);
        }
    }
}