package ru.obsession.merchandising.report;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.obsession.merchandising.R;
import ru.obsession.merchandising.main.MainActivity;
import ru.obsession.merchandising.shops.ShopsFragment;
import ru.obsession.merchandising.works.WorkFragment;

public class ReportFragment extends Fragment {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final String NEED_CONTEXT_BAR = "context_bar";
    private static final String NUM_SELECTED = "num_selected";
    private static final String ADAPTER = "adapter";
    private ArrayList<Image> images;
    private int numSelected = 0;
    private ActionMode mActionMode;
    private boolean longClick;
    private boolean createdCab;
    private boolean rotait;
    private GridView gridView;
    private int userId;
    private int shopId;
    private int workId;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (createdCab) {
            rotait = true;
        }
        outState.putParcelableArrayList(ADAPTER, images);
        outState.putInt(NUM_SELECTED, numSelected);
        outState.putBoolean(NEED_CONTEXT_BAR, createdCab);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.contextual_delete, menu);
            String plural = getResources().getQuantityString(R.plurals.num_selected, numSelected, numSelected);
            mode.setTitle(plural);
            if (createdCab) {
                return true;
            }
            createdCab = true;
            startAdapterGallery();
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.unselectAll:
                    unselect();
                    String plural = getResources().getQuantityString(R.plurals.num_selected, numSelected, numSelected);
                    mode.setTitle(plural);
                    startAdapterGallery();
                    return true;
                case R.id.delete:
                    if (numSelected != 0) {
                        DialogFragment dialogFragment = new DeleteDialog();
                        Bundle bundle = new Bundle();
                        bundle.putInt(DeleteDialog.NUM_DELETE, numSelected);
                        dialogFragment.setArguments(bundle);
                        dialogFragment.show(getFragmentManager(), "tag");
                    } else {
                        Toast.makeText(getActivity(), R.string.nothing_selected, Toast.LENGTH_LONG).show();
                    }
                    return true;
                default:
                    return false;
            }
        }

        private void unselect() {
            if (images != null) {
                for (Image image : images) {
                    image.checked = false;
                }
            }
            numSelected = 0;
        }

        public void onDestroyActionMode(ActionMode mode) {
            if (longClick || rotait) {
                return;
            }
            createdCab = false;
            numSelected = 0;
            mActionMode = null;
            unselect();
            startAdapterGallery();
        }
    };

    private void startAdapterGallery() {
        if (gridView.getAdapter() != null) {
            ((PhotoAdapter) gridView.getAdapter()).setWithChecking(createdCab);
            ((PhotoAdapter) gridView.getAdapter()).notifyDataSetChanged();
            return;
        }
        setAdapter();
    }

    private void setAdapter(){
        PhotoAdapter gridAdapterImage = new PhotoAdapter(getActivity(), images, createdCab);
        gridView.setAdapter(gridAdapterImage);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    if (images.get(position).checked) {
                        images.get(position).checked = false;
                        numSelected--;
                    } else {
                        images.get(position).checked = true;
                        numSelected++;
                    }
                    ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
                    String plural = getResources().getQuantityString(R.plurals.num_selected, numSelected, numSelected);
                    mActionMode.setTitle(plural);
                } else {
                    Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
                    intent.putExtra(ImageDetailActivity.IMAGE, images.get(position).path);
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.report_fragment,container,false);
        gridView = (GridView) root.findViewById(R.id.gridView);
        Bundle bundle = getArguments();
        userId = bundle.getInt(MainActivity.USER_ID);
        shopId = bundle.getInt(ShopsFragment.SHOP_ID);
        workId = bundle.getInt(WorkFragment.WORK_ID);
        if (savedInstanceState != null) {
            numSelected = savedInstanceState.getInt(NUM_SELECTED);
            images = savedInstanceState.getParcelableArrayList(ADAPTER);
            boolean withChecking = savedInstanceState.getBoolean(NEED_CONTEXT_BAR);
            if (withChecking) {
                longClick = true;
                mActionMode = ((ActionBarActivity) getActivity())
                        .startSupportActionMode(mActionModeCallback);
                longClick = false;
                }
            setAdapter();
        } else {
            images = new ArrayList<Image>();
        }
        startAdapterGallery();
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.report, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.photo_button:
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File fileImg = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (fileImg == null){
                    Toast.makeText(getActivity(),R.string.error_flash,Toast.LENGTH_LONG).show();
                    return true;
                }
                Uri fileUri = Uri.fromFile(fileImg);
                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(photoIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                return true;
            case R.id.del_button:
                numSelected = 0;
                longClick = true;
                mActionMode = ((ActionBarActivity) getActivity())
                        .startSupportActionMode(mActionModeCallback);
                longClick = false;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private File getOutputMediaFile(int mediaTypeImage) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdir()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (mediaTypeImage == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        images.add(new Image(mediaFile.getPath()));
        return mediaFile;
    }
public void delSelected(){
    Image image;
    for (int i = images.size() - 1; i >= 0; --i){
        image = images.get(i);
        if (image.checked){
            try {
              File file = new File(image.path);
              file.delete();
            }catch (Exception e){
                e.printStackTrace();
            }
            images.remove(i);
        }
    }
    setAdapter();
    mActionMode.finish();
}
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    startAdapterGallery();
                }
                break;
        }

    }
    public static class Image implements Parcelable {
        public String path;
        public Bitmap image;
        boolean checked;

        public Image(String path) {
            this.path = path;
        }

        public static final Parcelable.Creator<Image> CREATOR;

        static {
            CREATOR = new Creator<Image>() {

                @Override
                public Image createFromParcel(Parcel source) {
                    return new Image(source);
                }

                @Override
                public Image[] newArray(int size) {
                    return new Image[size];
                }
            };
        }

        private Image(Parcel in) {
            readFromParcel(in);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte((byte) (checked ? 1 : 0));
            dest.writeString(path);
        }

        private void readFromParcel(Parcel in) {
            checked = in.readByte() != 0;
            path = in.readString();
        }
    }
}
