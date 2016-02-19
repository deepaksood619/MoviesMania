package me.branded.deepaksood.moviesmania;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity implements SortOrder.SelectionListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    Toast toast;
    int position = 0;
    int currentPosition = 0;

    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;

    static List<MovieModel> moviesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(currentPosition == 0) {
            new GetMovies().execute("http://api.themoviedb.org/3/movie/popular?api_key=ed3e485287a973b1d147b39aedff970b");

            mGridView = (GridView) findViewById(R.id.gridView);

            //Initialize with empty data
            mGridData = new ArrayList<>();
            mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);
            mGridView.setAdapter(mGridAdapter);
        }

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                if(toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(MainActivity.this,"Settings",Toast.LENGTH_SHORT);
                toast.show();
                return true;

            case R.id.action_sort:
                FragmentManager manager = getFragmentManager();
                SortOrder dialog = new SortOrder();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(SortOrder.DATA, getItems());     // Require ArrayList
                bundle.putInt(SortOrder.SELECTED, position);
                dialog.setArguments(bundle);
                dialog.show(manager, "Dialog");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<String> getItems()
    {
        ArrayList<String> ret_val = new ArrayList<>();
        ret_val.add("Most Popular");
        ret_val.add("Highest Rating");
        return ret_val;
    }

    @Override
    public void selectItem(int position) {
        if(this.position != position) {
            this.position = position;
            Log.v(TAG,"position: "+position);
            if(position == 0) {
                if(currentPosition == 1) {
                    new GetMovies().execute("http://api.themoviedb.org/3/movie/popular?api_key=ed3e485287a973b1d147b39aedff970b");
                    mGridData = new ArrayList<>();
                    mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);
                    mGridView.setAdapter(mGridAdapter);
                    currentPosition = 0;
                }
            }
            else {
                if(currentPosition == 0) {
                    new GetMovies().execute("http://api.themoviedb.org/3/movie/top_rated?api_key=ed3e485287a973b1d147b39aedff970b");
                    mGridData = new ArrayList<>();
                    mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);
                    mGridView.setAdapter(mGridAdapter);
                    currentPosition = 1;
                }

            }
            if(toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(MainActivity.this,getItems().get(position),Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    class GetMovies extends AsyncTask<String, Void, List<MovieModel>> {

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer = null;
        String BASE_URL = "http://image.tmdb.org/t/p/w500/";

        int page;
        int total_results;
        int total_pages;

        @Override
        protected List<MovieModel> doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                stringBuffer = new StringBuffer();
                String line = "";
                while((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
                String finalJson = stringBuffer.toString();
                Log.v(TAG,"JSON Received: "+finalJson);

                JSONObject rootObject = new JSONObject(finalJson);

                page = rootObject.getInt("page");
                total_results = rootObject.getInt("total_results");
                total_pages = rootObject.getInt("total_pages");

                Log.v(TAG,"page: "+page+" total_results: "+total_results+" total_pages: "+total_pages);

                JSONArray rootArray = rootObject.getJSONArray("results");

                moviesList = new ArrayList<>();

                GridItem item;

                for(int i = 0; i < rootArray.length(); i++) {
                    JSONObject childObject = rootArray.getJSONObject(i);
                    MovieModel movieModel = new MovieModel();

                    item = new GridItem();

                    //Instead JSON we can use GSON. Which is easy to use than JSON. But i have used JSON to learn.

                    movieModel.setPoster_path(childObject.getString("poster_path"));
                    String path = BASE_URL+childObject.getString("poster_path");
                    Log.v(TAG,"path: "+path);
                    item.setImage(path);
                    mGridData.add(item);

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

                    moviesList.add(movieModel);
                }
                return moviesList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MovieModel> movieModels) {
            super.onPostExecute(movieModels);
            mGridAdapter.setGridData(mGridData);
        }
    }

}
