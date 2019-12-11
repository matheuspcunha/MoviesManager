package com.example.moviesmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviesmanager.R;
import com.example.moviesmanager.model.MovieModel;
import com.example.moviesmanager.utils.Constants;
import com.example.moviesmanager.view.MovieDetailsActivity;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {

    private Context mContext;
    private List<MovieModel> mMovieModelList;

    public MovieAdapter(Context context, List<MovieModel> moviesList) {
        mContext = context;
        mMovieModelList = moviesList;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movies_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        Glide.with(mContext)
                .load(Constants.MOVIE_BASE_URL + mMovieModelList.get(position).poster)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return mMovieModelList != null ? mMovieModelList.size() : 0;
    }

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView poster;

        MovieHolder(View itemView) {
            super(itemView);

            poster = itemView.findViewById(R.id.movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(mContext, MovieDetailsActivity.class);
                intent.putExtra(MovieDetailsActivity.MOVIE_ID, mMovieModelList.get(position).id);
                mContext.startActivity(intent);
            }
        }
    }
}
