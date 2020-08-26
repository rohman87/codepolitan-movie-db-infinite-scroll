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
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fetchData(page);
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
                        Log.d(TAG, "api response: "+response);
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