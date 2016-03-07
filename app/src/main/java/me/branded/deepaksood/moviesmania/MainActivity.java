// I have used both picasso and universal image loader to learn both.
// I will remove one of them in next deliverable p2
// Couldn't find a good tutorial on loading more items in grid view at the end of list please help and
//it will be done in next deliverable p2

package me.branded.deepaksood.moviesmania;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import me.branded.deepaksood.moviesmania.models.MovieModel;

/**
 * Created by deepak on 7/3/16.
 */

public class MainActivity extends AppCompatActivity implements SortOrder.SelectionListener {

    public static final String TAG = MainActivity.class.getSimpleName();    //for logging in androidMonitor
    Toast toast;                                                            //toast object for using toast.cancel();
    int position = 0;                                                       //position of sortOrder
    int currentPosition = 0;                                                //Current position for sortOrder

    public GridView mGridView;                                              //Initializing GridView for images
    static public GridViewAdapter mGridAdapter;                             //GridView adapter for reusing adapters in gridView
    static public ArrayList<GridItem> mGridData;                            //Array for storing GridItem i.e. each image

    static List<MovieModel> moviesList;                                     //List for storing MovieModel that will contain all the information of a movie

    static public ProgressDialog dialog;                                    //progressDialog before the adapter and list loads

    String mostPopularUrl;
    String highestRatingUrl;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar is used
        //Since it provides a lot of functionality over actionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);                              //Setting the title of the toolbar to White (0xFFFFFFFF)

        //Implementing progressDialog
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please Wait...");
        dialog.show();

        //Getting apiKey from local.properties
        String apiKey = null;
        try {
            ApplicationInfo applicationInfo = this.getPackageManager()
                    .getApplicationInfo(this.getPackageName(),
                            PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            apiKey = bundle.getString("api_key");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //Using URI builder to build the url
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath("popular")
                .appendQueryParameter("api_key",apiKey);

        mostPopularUrl = builder.build().toString();

        builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath("top_rated")
                .appendQueryParameter("api_key",apiKey);

        highestRatingUrl = builder.build().toString();

        //by default the sorting order will by Most Popular
        if(currentPosition == 0) {
            new GetMovies().execute(mostPopularUrl);                            //get the mostPopular movies from the movieDb
            mGridView = (GridView) findViewById(R.id.gridView);
            mGridData = new ArrayList<>();
            mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);
            mGridView.setAdapter(mGridAdapter);
        }

        //Adding more items to the list at the end of the grid View for future use (Tried but failed)
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        //open the detailsActivity when a poster is clicked
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        //Setting configuration and default options for universal image loader
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
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
                toast = Toast.makeText(MainActivity.this,"For Future Use",Toast.LENGTH_SHORT);
                toast.show();
                return true;

            case R.id.action_sort:
                FragmentManager manager = getFragmentManager();
                SortOrder dialog = new SortOrder();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(SortOrder.DATA, getItems());
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
            if(position == 0) {
                if(currentPosition == 1) {
                    new GetMovies().execute(mostPopularUrl);
                    mGridData = new ArrayList<>();
                    mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);
                    mGridView.setAdapter(mGridAdapter);
                    currentPosition = 0;
                }
            }
            else {
                if(currentPosition == 0) {
                    new GetMovies().execute(highestRatingUrl);
                    Log.v(TAG,"url: "+highestRatingUrl);
                    mGridData = new ArrayList<>();
                    mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);
                    mGridView.setAdapter(mGridAdapter);
                    currentPosition = 1;
                }
            }
        }
    }
}
