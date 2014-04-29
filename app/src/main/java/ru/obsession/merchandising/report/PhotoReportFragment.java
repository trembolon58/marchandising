package ru.obsession.merchandising.report;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import ru.obsession.merchandising.clients.Client;
import ru.obsession.merchandising.clients.ClientsListFragment;
import ru.obsession.merchandising.database.DatabaseApi;
import ru.obsession.merchandising.main.MainActivity;
import ru.obsession.merchandising.shops.Shop;
import ru.obsession.merchandising.shops.ShopsListFragment;

public class PhotoReportFragment extends Fragment {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private ArrayList<Photo> photos;
    private Shop shop;
    private Client client;
    private int numSelected = 0;
    private ActionMode mActionMode;
    private boolean longClick;
    private boolean createdCab;
    private GridView gridView;
    private int userId;

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
            if (photos != null) {
                for (Photo photo : photos) {
                    photo.checked = false;
                }
            }
            numSelected = 0;
        }

        public void onDestroyActionMode(ActionMode mode) {
            if (longClick) {
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

    private void setAdapter() {
        PhotoAdapter gridAdapterImage = new PhotoAdapter(getActivity(), photos, createdCab);
        gridView.setAdapter(gridAdapterImage);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    if (photos.get(position).checked) {
                        photos.get(position).checked = false;
                        numSelected--;
                    } else {
                        photos.get(position).checked = true;
                        numSelected++;
                    }
                    ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
                    String plural = getResources().getQuantityString(R.plurals.num_selected, numSelected, numSelected);
                    mActionMode.setTitle(plural);
                } else {
                    Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
                    intent.putExtra(ImageDetailActivity.IMAGE, photos.get(position).path);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.photo_report_fragment, container, false);
        gridView = (GridView) root.findViewById(R.id.gridView);
        Bundle bundle = getArguments();
        userId = bundle.getInt(MainActivity.USER_ID);
        shop = (Shop) bundle.getSerializable(ShopsListFragment.SHOP_TAG);
        client = (Client) bundle.getSerializable(ClientsListFragment.CLIENT_TAG);
        photos = DatabaseApi.getInstance(getActivity()).getPhotos(client.id, userId, shop.id);
        setAdapter();
        startAdapterGallery();
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.photo_report, menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DatabaseApi.getInstance(getActivity()).insertPhotos(photos, userId, client.id, shop.id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.photo_button:
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File fileImg = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (fileImg == null) {
                    Toast.makeText(getActivity(), R.string.error_flash, Toast.LENGTH_LONG).show();
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
        photos.add(new Photo(mediaFile.getPath()));
        return mediaFile;
    }

    public void delSelected() {
        Photo photo;
        for (int i = photos.size() - 1; i >= 0; --i) {
            photo = photos.get(i);
            if (photo.checked) {
                try {
                    File file = new File(photo.path);
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                photos.remove(i);
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

}
