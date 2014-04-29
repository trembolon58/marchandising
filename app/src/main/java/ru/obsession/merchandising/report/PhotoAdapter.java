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

import java.io.File;
import java.util.ArrayList;

import ru.obsession.merchandising.R;

public class PhotoAdapter extends ArrayAdapter<Photo> {
    private LayoutInflater lInflater;
    private Resources resources;

    public void setWithChecking(boolean withChecking) {
        this.withChecking = withChecking;
    }

    private boolean withChecking;

    public PhotoAdapter(Context context, ArrayList<Photo> photos, boolean withChecking) {
        super(context, R.layout.image_view_checked, R.id.imageView, photos);
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
        final Photo pathPhoto = getItem(position);
        if (pathPhoto.image == null && new File(pathPhoto.path).exists()) {
            new AsyncTask<Bitmap, Bitmap, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Bitmap[] params) {
                    int hieght = (int) resources.getDimension(R.dimen.image_height);
                    return PhotoEditSize.rotaitPhoto(pathPhoto.path, hieght, hieght);
                }

                @Override
                protected void onPostExecute(Bitmap o) {
                    super.onPostExecute(o);
                    try {
                        getItem(position).image = o;
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        } else {
            if (pathPhoto.image == null) {
                holder.imageView.setImageResource(R.drawable.izobrajenieotsutstvuet5);
            } else {
                holder.imageView.setImageBitmap(pathPhoto.image);
            }
        }
        if (withChecking) {
            holder.frameLayout.setBackgroundResource(R.drawable.selector_image_view);
            if (pathPhoto.checked) {
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
