package ru.obsession.merchandising.report;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.report.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class ImageDetailActivity extends ActionBarActivity {

    public static final String IMAGE = "image";

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent bundle = getIntent();
        final String path = bundle.getStringExtra(IMAGE);
        try {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        } catch (Exception e){
            e.printStackTrace();
        }
        setContentView(R.layout.progres_image);
        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Display display = getWindowManager().getDefaultDisplay();
        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) {
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        new AsyncTask<Bitmap, Bitmap, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Bitmap[] params) {
                return PhotoEditSize.rotaitPhoto(path, point.x, point.y);
            }

            @Override
            protected void onPostExecute(Bitmap o) {
                super.onPostExecute(o);
                frameLayout.removeView(findViewById(R.id.progressBar));
                if (o != null) {
                    imageView.setImageBitmap(o);
                } else {
                    imageView.setImageResource(R.drawable.izobrajenieotsutstvuet5);
                }
            }
        }.execute();
    }
}
