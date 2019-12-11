package com.example.moviesmanager.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesmanager.R;
import com.example.moviesmanager.adapter.MovieAdapter;
import com.example.moviesmanager.data.MoviesDatabase;
import com.example.moviesmanager.model.MovieModel;
import com.example.moviesmanager.utils.MDBConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MoviesListActivity extends AppCompatActivity {

    public static String ADD_MOVIE = "ADD_MOVIE";
    public static String SAVED_MOVIES = "SAVED_MOVIES";

    private boolean mShowAddMovies = false;
    private boolean mShowSavedMovies = false;

    private RecyclerView mRecyclerView;
    private ImageView mBackButton;
    private ImageView mAddMovies;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            mShowAddMovies = getIntent().getExtras().getBoolean(ADD_MOVIE);
            mShowSavedMovies = getIntent().getExtras().getBoolean(SAVED_MOVIES);
        } else {
            finish();
        }

        setContentView(R.layout.activity_movies_list);
        mRecyclerView = findViewById(R.id.movies_recycler_view);
        mBackButton = findViewById(R.id.back);
        mAddMovies = findViewById(R.id.add_movies);
        mTitle = findViewById(R.id.title);

        setOnClicks();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mShowSavedMovies) {
            mTitle.setText(getText(R.string.title_saved_movies));
            mAddMovies.setVisibility(View.VISIBLE);

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {

                List<MovieModel> mMoviesList = MoviesDatabase
                        .getInstance(MoviesListActivity.this)
                        .getMovieDao()
                        .getAllMovies();

                setAdapter(mMoviesList);
            });
        } else if (mShowAddMovies) {
            mTitle.setText(getText(R.string.title_add_movies));
            new FetchMovies().execute();
        }
    }

    private void setAdapter(List<MovieModel> movieModelList) {
        MovieAdapter mMovieAdapter = new MovieAdapter(this, movieModelList);
        runOnUiThread(() -> mRecyclerView.setAdapter(mMovieAdapter));
    }

    private void setOnClicks() {
        mBackButton.setOnClickListener(v -> this.onBackPressed());

        mAddMovies.setOnClickListener(v -> {
            Intent intent = new Intent(MoviesListActivity.this, MoviesListActivity.class);
            intent.putExtra(MoviesListActivity.ADD_MOVIE, true);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mShowSavedMovies) {
            finish();
        }
        else if (mShowAddMovies)
        {
            Intent intent = new Intent(MoviesListActivity.this, MoviesListActivity.class);
            intent.putExtra(MoviesListActivity.SAVED_MOVIES, true);
            startActivity(intent);
            finish();
        }
    }

    private class FetchMovies extends AsyncTask<String, Void, ArrayList<MovieModel>> {

        @Override
        protected ArrayList<MovieModel> doInBackground(String... params) {
            ArrayList<MovieModel> mMoviesList = new ArrayList<>();

            try {
                mMoviesList = MDBConnection.getUpcomingMovies();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return mMoviesList;
        }

        @Override
        protected void onPreExecute() { super.onPreExecute(); }

        @Override
        protected void onPostExecute(ArrayList<MovieModel> movies) {
            super.onPostExecute(movies);
            setAdapter(movies);
        }
    }
}
