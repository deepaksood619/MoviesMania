package me.branded.deepaksood.moviesmania;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import me.branded.deepaksood.moviesmania.models.MovieModel;

public class DetailsActivity extends AppCompatActivity {

    public static final String TAG = DetailsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        int position = getIntent().getIntExtra("position",0);
        MovieModel temp = MainActivity.moviesList.get(position);

        getSupportActionBar().setTitle(temp.getTitle());
        toolbar.setTitleTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        ImageView poster;
        RatingBar ratingBar;
        TextView releaseDate;
        TextView overview;
        final ProgressBar progressBar;

        poster = (ImageView) findViewById(R.id.poster);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        releaseDate = (TextView)findViewById(R.id.release_date);
        overview = (TextView) findViewById(R.id.overview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        Log.v(TAG,"poster_path: "+temp.getPoster_path());

        String BASE_URL = "http://image.tmdb.org/t/p/w780/";
        String path = BASE_URL+temp.getBackdrop_path();

        Point size = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;
        int height = size.y;

        Picasso.with(this).load(path)
                .resize(width,height/3)
                .centerCrop()
                .into(poster, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        releaseDate.setText("Release Date: "+temp.getRelease_date());

        Log.v(TAG,"vote: "+temp.getVote_average());
        ratingBar.setRating(temp.getVote_average()/2);

        overview.setText("Overview: "+temp.getOverview());

    }
}
