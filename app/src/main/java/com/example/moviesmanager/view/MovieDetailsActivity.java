package com.example.moviesmanager.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.moviesmanager.R;
import com.example.moviesmanager.data.MoviesDatabase;
import com.example.moviesmanager.model.MovieModel;
import com.example.moviesmanager.utils.Constants;
import com.example.moviesmanager.utils.MDBConnection;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MovieDetailsActivity extends AppCompatActivity {

    public static String MOVIE_ID = "MOVIE_ID";

    private ImageView banner;
    private TextView title;
    private TextView duration_genre;
    private TextView resume;
    private ImageView watched;
    private ImageView addTolist;
    private ImageView playTrailer;
    private ImageView map;

    private String mMovieID;
    private String mTrailerID;
    private MovieModel mMovie;
    private Executor executor;
    private boolean alreadyWatched;
    private boolean isAdded;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            mMovieID = getIntent().getExtras().getString(MOVIE_ID);
        } else {
            finish();
        }

        setContentView(R.layout.activity_movie_details);

        banner = findViewById(R.id.banner);
        title = findViewById(R.id.title);
        duration_genre = findViewById(R.id.duration_genre);
        watched = findViewById(R.id.watchedIcon);
        addTolist = findViewById(R.id.listIcon);
        playTrailer = findViewById(R.id.playIcon);
        map = findViewById(R.id.mapIcon);
        LinearLayout backButton = findViewById(R.id.back);
        resume = findViewById(R.id.text_resume);

        backButton.setOnClickListener(v -> finish());

        new FetchMovieDetails().execute(mMovieID);

        executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            alreadyWatched = MoviesDatabase
                    .getInstance(this)
                    .getMovieDao()
                    .checkIfMovieIsWatched(mMovieID);
            watched.setBackground(alreadyWatched ? getDrawable(R.drawable.check) : getDrawable(R.drawable.uncheck));

            isAdded = MoviesDatabase
                    .getInstance(this)
                    .getMovieDao()
                    .isMovieInList(mMovieID) > 1;

            addTolist.setAlpha(isAdded ? 0.5f : 1.0f);
        });

        setOptions();
    }

    private void setOptions() {

        watched.setOnClickListener(v -> {
            alreadyWatched = !alreadyWatched;
            watched.setBackground(alreadyWatched ? getDrawable(R.drawable.check) : getDrawable(R.drawable.uncheck));
            if(alreadyWatched) {
                Toast.makeText(MovieDetailsActivity.this, getText(R.string.movie_watched), Toast.LENGTH_SHORT).show();
            }

            executor.execute(() -> MoviesDatabase
                    .getInstance(MovieDetailsActivity.this)
                    .getMovieDao()
                    .setAsWatched(mMovieID, alreadyWatched));
        });

        addTolist.setOnClickListener(v -> {
            if(isAdded) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailsActivity.this);
                builder.setMessage(getText(R.string.movie_already_added));

                builder.setPositiveButton(getText(R.string.ok), (dialog, id) -> {
                    executor.execute(() -> MoviesDatabase
                                .getInstance(this)
                                .getMovieDao()
                                .delete(mMovie));

                    Toast.makeText(MovieDetailsActivity.this, getText(R.string.movie_removed), Toast.LENGTH_SHORT).show();
                    isAdded = false;
                    addTolist.setAlpha(1.0f);
                });

                builder.setNegativeButton(getText(R.string.cancel), (dialog, id) -> { /*do nothing*/ });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                executor.execute(() -> MoviesDatabase
                        .getInstance(this)
                        .getMovieDao()
                        .insert(mMovie));

                Toast.makeText(MovieDetailsActivity.this, getText(R.string.movie_added), Toast.LENGTH_SHORT).show();
                isAdded = true;
                addTolist.setAlpha(0.5f);
            }
        });

        playTrailer.setOnClickListener(v -> {
            Intent intent = YouTubeStandalonePlayer.createVideoIntent(MovieDetailsActivity.this, getResources().getString(R.string.googleAPIKey), mTrailerID, 0, true, false);
            startActivity(intent);
        });

        map.setOnClickListener(v -> {
            Intent intent = new Intent(MovieDetailsActivity.this, MapScreenActivity.class);
            startActivity(intent);
        });

    }

    private class FetchMovieDetails extends AsyncTask<String, Void, MovieModel> {

        @Override
        protected MovieModel doInBackground(String... params) {
            MovieModel movie = null;

            try {
                movie = MDBConnection.getMovieDetails(params[0]);
                mTrailerID = MDBConnection.getMovieTrailer(mMovieID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return movie;
        }

        @Override
        protected void onPreExecute() { super.onPreExecute(); }

        @Override
        protected void onPostExecute(MovieModel movie) {
            super.onPostExecute(movie);

            if(movie != null) {
                mMovie = movie;

                Glide.with(MovieDetailsActivity.this)
                        .load(Constants.MOVIE_BASE_URL + mMovie.banner)
                        .into(banner);

                title.setText(mMovie.title);
                duration_genre.setText(String.format(Locale.getDefault(), "%dm   |   %s", mMovie.duration, mMovie.genre));
                resume.setText(mMovie.description);

            }
        }
    }
}
