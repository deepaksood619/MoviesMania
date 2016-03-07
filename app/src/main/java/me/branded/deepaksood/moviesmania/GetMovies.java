package me.branded.deepaksood.moviesmania;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.branded.deepaksood.moviesmania.models.MovieModel;

/**
 * Created by deepak on 7/3/16.
 */


class GetMovies extends AsyncTask<String, Void, List<MovieModel>> {

    private static String TAG = GetMovies.class.getSimpleName();

    HttpURLConnection httpURLConnection = null;
    BufferedReader bufferedReader = null;
    StringBuffer stringBuffer = null;
    String BASE_URL = "http://image.tmdb.org/t/p/w500/";

    int page;
    int total_results;
    int total_pages;
    String line;

    @Override
    protected List<MovieModel> doInBackground(String... params) {

        try {
            URL url = new URL(params[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            stringBuffer = new StringBuffer();
            line = "";
            while((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            String finalJson = stringBuffer.toString();


            JSONObject rootObject = new JSONObject(finalJson);

            page = rootObject.getInt("page");
            total_results = rootObject.getInt("total_results");
            total_pages = rootObject.getInt("total_pages");


            JSONArray rootArray = rootObject.getJSONArray("results");

            MainActivity.moviesList = new ArrayList<>();

            GridItem item;

            for(int i = 0; i < rootArray.length(); i++) {
                JSONObject childObject = rootArray.getJSONObject(i);
                MovieModel movieModel = new MovieModel();

                item = new GridItem();

                //Instead JSON we can use GSON. Which is easy to use than JSON. But i have used JSON to learn the whole process

                movieModel.setPoster_path(childObject.getString("poster_path"));
                String path = BASE_URL+childObject.getString("poster_path");
                item.setImage(path);
                MainActivity.mGridData.add(item);

                movieModel.setAdult(childObject.getBoolean("adult"));
                movieModel.setOverview(childObject.getString("overview"));
                movieModel.setRelease_date(childObject.getString("release_date"));

                JSONArray leafArray = childObject.getJSONArray("genre_ids");
                int[] array = new int[leafArray.length()];
                for(int j=0;j<leafArray.length();j++) {
                    array[j] = leafArray.getInt(j);
                }
                movieModel.setGenre_ids(array);

                movieModel.setId(childObject.getInt("id"));
                movieModel.setOriginal_title(childObject.getString("original_title"));
                movieModel.setOriginal_language(childObject.getString("original_language"));
                movieModel.setTitle(childObject.getString("title"));
                movieModel.setBackdrop_path(childObject.getString("backdrop_path"));
                movieModel.setPopularity(childObject.getInt("popularity"));
                movieModel.setVote_count(childObject.getInt("vote_count"));
                movieModel.setVideo(childObject.getBoolean("video"));
                movieModel.setVote_average((float)childObject.getDouble("vote_average"));

                MainActivity.moviesList.add(movieModel);
            }
            return MainActivity.moviesList;

        } catch (MalformedURLException e) {
            Log.e(TAG,"MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG,"IOException");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(TAG,"JSONException");
            e.printStackTrace();
        } finally {
            if(httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            try {
                if(bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                Log.e(TAG,"IOException");
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<MovieModel> movieModels) {
        super.onPostExecute(movieModels);
        MainActivity.dialog.dismiss();
        MainActivity.mGridAdapter.setGridData(MainActivity.mGridData);
    }
}