package com.codepolitan.moviedbinifinitescroll.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepolitan.moviedbinifinitescroll.BuildConfig;
import com.codepolitan.moviedbinifinitescroll.R;
import com.codepolitan.moviedbinifinitescroll.databinding.ItemMovieBinding;
import com.codepolitan.moviedbinifinitescroll.model.Movie;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Movie> dataMovies = new ArrayList<>();
    private static final int TYPE_MOVIE = 1;
    private static final int TYPE_LOADING = 2;

    @Override
    public int getItemViewType(int position) {
        return (dataMovies.get(position) != null) ? TYPE_MOVIE : TYPE_LOADING;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOADING){
            return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
        }
        return new MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_MOVIE){
            ((MovieViewHolder) holder).bind(dataMovies.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return dataMovies.size();
    }

    public void addDataMovies(List<Movie> movies){
        if (movies != null){
            dataMovies.addAll(movies);
            notifyDataSetChanged();
        }
    }

    public void addDataLoading(){
        dataMovies.add(null);
        notifyDataSetChanged();
    }

    public void removeDataLoading(){
        dataMovies.remove(dataMovies.size() - 1);
        notifyDataSetChanged();
    }

    public int getDataMovies(){
        return dataMovies.size();
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder{
        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        private ItemMovieBinding binding;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemMovieBinding.bind(itemView);
        }

        public void bind(Movie movie) {
            if (movie != null){
                String urlImage = BuildConfig.BASE_URL_IMAGE_MOVIE_DB + movie.getPosterPath();
                DecimalFormat decimalFormat = new DecimalFormat("#.#");

                Glide.with(itemView).load(urlImage).into(binding.ivMovie);
                binding.tvTitleMovie.setText(movie.getOriginalTitle());
                binding.tvReleaseDate.setText(movie.getReleaseDate());
                binding.tvRatingMovie.setText(decimalFormat.format(movie.movieRate()));
                binding.ratingBarMovie.setRating(movie.movieRate());
            }
        }
    }
}
