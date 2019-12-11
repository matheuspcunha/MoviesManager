package com.example.moviesmanager.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MovieModel {

    @PrimaryKey()
    @NonNull
    public final String id;
    public final String title;
    public final String description;
    public final String genre;
    public final long duration;
    public final String poster;
    public final String banner;
    public final boolean watched;

    public MovieModel(@NonNull String id, String title, String description, String genre,
                      long duration, String poster, String banner, boolean watched) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.duration = duration;
        this.poster = poster;
        this.banner = banner;
        this.watched = watched;
    }
}
