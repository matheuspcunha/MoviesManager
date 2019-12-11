package com.example.moviesmanager.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesmanager.R;
import com.example.moviesmanager.data.MoviesDatabase;
import com.example.moviesmanager.utils.Constants;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    LinearLayout mSeeList;
    ImageView mMenu;
    TextView mWatchedMovies;
    TextView mUserName;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.movies_recycler_view);
        mSeeList = findViewById(R.id.ll_see_lists);
        mMenu = findViewById(R.id.menu);
        mWatchedMovies = findViewById(R.id.watchedMovies);
        mUserName = findViewById(R.id.tv_user_name);

        preferences = getSharedPreferences(Constants.PREFERENCES_USER_NAME, MODE_PRIVATE);

        mSeeList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MoviesListActivity.class);
            intent.putExtra(MoviesListActivity.SAVED_MOVIES, true);
            startActivity(intent);
        });

        String name = preferences.getString(Constants.PREFERENCES_NAME, "No name defined");
        mUserName.setText(name);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            int totalMovies = MoviesDatabase
                    .getInstance(MainActivity.this)
                    .getMovieDao()
                    .getCountMovies();

            int watchedMovies = MoviesDatabase
                    .getInstance(MainActivity.this)
                    .getMovieDao()
                    .getCountWatchedMovies();

            runOnUiThread(() -> setCounts(totalMovies, watchedMovies));
        });

        mMenu.setOnClickListener(v -> showMenu(mMenu, R.menu.menu));
    }

    private void setCounts(int totalMovies, int watchedMovies) {
        mWatchedMovies.setText(String.format(Locale.getDefault(), "Filmes assistidos:   %d/%d", watchedMovies, totalMovies));
    }

    public void showMenu(View view, int menu) {
        PopupMenu popup = new PopupMenu(MainActivity.this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(menu, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.edit_photo:
                    //Not implemented yet
                    return true;
                case R.id.edit_name:
                    openDialogEditName();
                    return true;
                default:
                    return false;
            }
        });
    }

    public void openDialogEditName() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        alert.setTitle(getText(R.string.edit_name));

        final EditText input = new EditText(MainActivity.this);
        input.setHint(getText(R.string.type_name));
        input.setText(mUserName.getText());
        alert.setView(input);

        alert.setPositiveButton(getText(R.string.ok), (dialog, whichButton) -> {
            if(!input.getText().toString().isEmpty()) {

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(Constants.PREFERENCES_NAME, input.getText().toString());
                editor.apply();
                mUserName.setText(input.getText().toString());
            }
            else {
                Toast.makeText(MainActivity.this, getText(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton(getText(R.string.cancel), (dialog, whichButton) -> {});

        alert.show();
    }
}
