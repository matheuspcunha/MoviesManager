package com.example.moviesmanager.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.moviesmanager.model.MovieModel;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM MovieModel")
    List<MovieModel> getAllMovies();

    @Query("SELECT watched FROM MovieModel WHERE id = :movieId")
    boolean checkIfMovieIsWatched(String movieId);

    @Query("SELECT id FROM MovieModel WHERE id = :movieId")
    int isMovieInList(String movieId);

    @Query("UPDATE MovieModel SET watched = :watched WHERE id = :movieId")
    void setAsWatched(String movieId, boolean watched);

    @Query("SELECT COUNT(id) FROM MovieModel WHERE watched = 1")
    int getCountWatchedMovies();

    @Query("SELECT COUNT(id) FROM MovieModel")
    int getCountMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MovieModel movieModel);

    @Update
    void update(MovieModel movieModel);

    @Delete
    int delete(MovieModel movieModel);
}
