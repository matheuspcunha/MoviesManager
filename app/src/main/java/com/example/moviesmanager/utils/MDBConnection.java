package com.example.moviesmanager.utils;

import android.util.Log;

import com.example.moviesmanager.model.MovieModel;
import com.google.common.io.CharStreams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MDBConnection {

    private static final String API_KEY = "564ecea917a6190b29fb74f0163a361b";

    private static final String TAG = MDBConnection.class.getSimpleName();

    public static ArrayList<MovieModel> getUpcomingMovies() throws IOException {
        ArrayList<MovieModel> movieModelList = new ArrayList<>();

        try {

            URL url = new URL("https://api.themoviedb.org/3/movie/upcoming?api_key=" + API_KEY + "&language=pt-BR&sort_by=popularity.desc");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            String results = CharStreams.toString(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            parseJson(results, movieModelList);
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return movieModelList;
    }

    public static MovieModel getMovieDetails(String id) throws IOException {
        MovieModel movieModel = null;

        try {
            URL url = new URL("https://api.themoviedb.org/3/movie/" + id + "?api_key=" + API_KEY + "&language=pt-BR");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            String results = CharStreams.toString(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            movieModel = parseJson(results);
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return movieModel;
    }

    public static String getMovieTrailer(String id) throws IOException {
        String videoID = null;

        try {
            URL url = new URL("https://api.themoviedb.org/3/movie/" + id + "/videos" + "?api_key=" + API_KEY + "&language=pt-BR");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            String results = CharStreams.toString(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            videoID = parseJsonTrailer(results);
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return videoID;
    }

    private static void parseJson(String data, ArrayList<MovieModel> moviesList) {
        try {
            JSONObject mainObject = new JSONObject(data);
            JSONArray array = mainObject.getJSONArray("results");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                String id = object.getString("id");
                String title = object.getString("title");
                String description = object.getString("overview");
                String poster = object.getString("poster_path");
                String banner = object.getString("backdrop_path");

                moviesList.add(new MovieModel(id, title, description, "Desconhecido", 0, poster, banner, false));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error occurred during JSON Parsing" + " - " + e.getMessage());
        }
    }

    private static MovieModel parseJson(String data) {
        MovieModel movieModel = null;

        try {

            JSONObject object = new JSONObject(data);

            String id = object.getString("id");
            String title = object.getString("title");
            String description = object.getString("overview");
            String poster = object.getString("poster_path");
            String banner = object.getString("backdrop_path");

            movieModel = new MovieModel(id, title, description, "Desconhecido", 0, poster, banner, false);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error occurred during JSON Parsing" + " - " + e.getMessage());
        }

        return movieModel;
    }

    private static String parseJsonTrailer(String data) {
        String videoID = null;

        try {
            JSONObject mainObject = new JSONObject(data);
            JSONArray array = mainObject.getJSONArray("results");

            JSONObject object = array.getJSONObject(0);
            videoID = object.getString("key");

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error occurred during JSON Parsing" + " - " + e.getMessage());
        }

        return videoID;
    }
}