package me.branded.deepaksood.moviesmania;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by deepak on 15/2/16.
 */
public class GridViewAdapter extends ArrayAdapter<GridItem> {
    private Context mContext;
    private int layoutResourceId;
    private ArrayList<GridItem> mGridData = new ArrayList<GridItem>();



    public GridViewAdapter(Context mContext, int layoutResourceId, ArrayList<GridItem> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }


    /**
     * Updates grid data and refresh grid items.
     * @param mGridData
     */
    public void setGridData(ArrayList<GridItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) row.findViewById(R.id.icon);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        GridItem item = mGridData.get(position);

        ProgressBar progressBar = null;
        if(convertView != null) {
            progressBar = (ProgressBar) convertView.findViewById(R.id.gridProgressBar);
            progressBar.setVisibility(View.VISIBLE);
        }




        if(item != null && holder != null) {
            Picasso.with(mContext).load(item.getImage()).into(holder.imageView,  new ImageLoadedCallback(progressBar) {
                @Override
                public void onSuccess() {
                    if (this.progressBar != null) {
                        this.progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }

        return row;
    }

    static class ViewHolder {
        ImageView imageView;

    }

    private class ImageLoadedCallback implements Callback {
        ProgressBar progressBar;

        public  ImageLoadedCallback(ProgressBar progBar){
            progressBar = progBar;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

        }
    }
}
