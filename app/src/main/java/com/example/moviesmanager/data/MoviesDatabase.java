package com.example.moviesmanager.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.moviesmanager.model.MovieModel;

@Database(entities = {MovieModel.class}, version = 1)
public abstract class MoviesDatabase extends RoomDatabase {

    private static final String DB_NAME = "movieDatabase.db";
    private static volatile MoviesDatabase instance;

    public static synchronized MoviesDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }

        return instance;
    }

    private static MoviesDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                MoviesDatabase.class,
                DB_NAME).build();
    }

    public abstract MovieDao getMovieDao();
}
