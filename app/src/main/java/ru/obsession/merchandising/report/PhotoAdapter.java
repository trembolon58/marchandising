package ru.obsession.merchandising.report;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import ru.obsession.merchandising.R;

import java.io.File;
import java.util.ArrayList;

public class PhotoAdapter extends ArrayAdapter<PhotoReportFragment.Image> {
    private LayoutInflater lInflater;
    private Resources resources;

    public void setWithChecking(boolean withChecking) {
        this.withChecking = withChecking;
    }

    private boolean withChecking;

    public PhotoAdapter(Context context, ArrayList<PhotoReportFragment.Image> images, boolean withChecking) {
        super(context, R.layout.image_view_checked, R.id.imageView, images);
        this.withChecking = withChecking;
        resources = context.getResources();
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static class ViewHolder {
        ImageView imageView;
        FrameLayout frameLayout;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (v == null) {
            v = lInflater.inflate(R.layout.image_view_checked, null);
            holder.imageView = (ImageView) v.findViewById(R.id.imageView);
            holder.frameLayout = (FrameLayout) v.findViewById(R.id.frameLayout);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        final PhotoReportFragment.Image pathImage = getItem(position);
        if (pathImage.image == null && new File(pathImage.path).exists()) {
            new AsyncTask<Bitmap, Bitmap, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Bitmap[] params) {
                    int hieght = (int) resources.getDimension(R.dimen.image_height);
                    return PhotoEditSize.rotaitPhoto(pathImage.path, hieght, hieght);
                }

                @Override
                protected void onPostExecute(Bitmap o) {
                    super.onPostExecute(o);
                    getItem(position).image = o;
                    notifyDataSetChanged();
                }
            }.execute();
        } else {
            if (pathImage.image == null) {
                holder.imageView.setImageResource(R.drawable.izobrajenieotsutstvuet5);
            } else {
                holder.imageView.setImageBitmap(pathImage.image);
            }
        }
        if (withChecking) {
            holder.frameLayout.setBackgroundResource(R.drawable.selector_image_view);
            if (pathImage.checked) {
                holder.frameLayout.setSelected(true);
            } else {
                holder.frameLayout.setSelected(false);
            }
        } else {
                holder.frameLayout.setBackgroundDrawable(null);
        }
        return v;
    }
}
