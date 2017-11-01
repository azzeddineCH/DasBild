package azeddine.project.summer.dasBild.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import azeddine.project.summer.dasBild.R;
import azeddine.project.summer.dasBild.adapters.CountryAlbumAdapter;
import azeddine.project.summer.dasBild.objectsUtils.KeysUtil;
import azeddine.project.summer.dasBild.objectsUtils.Photo;

/**
 * Created by azeddine on 10/22/17.
 */

public class BookmarksAlbumFragment extends PhotosGalleryFragment {
    public static final String TAG = "BookmarksAlbumFragment";

    public BookmarksAlbumFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState,true);
        final AppCompatActivity activity = ((AppCompatActivity) getActivity());
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        final ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.bookmarked_photos_fragment_title);
        }
        try {
            mCountryAlbumAdapter = initAlbumAdapter(getContext(), 3);
            mAlbumRecyclerView.setAdapter(mCountryAlbumAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCountryAlbumAdapter.setOnPhotoClickedListener((CountryAlbumAdapter.OnPhotoClickedListener) getContext());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCountryAlbumAdapter.setAdapterLoadingState(true);
        getLoaderManager().initLoader(KeysUtil.BOOKMARKED_PHOTOS_LOADER_ID, null, this);
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object album) {
        if (loader.getId() == KeysUtil.BOOKMARKED_PHOTOS_LOADER_ID) {
            mCountryAlbumAdapter.setAdapterLoadingState(false);
            if (((List<Photo>) album).isEmpty()) {
                showEmptyListBitmap(R.string.no_bookmarked_photos,R.drawable.no_photo);
            } else {
                mCountryAlbumAdapter.addPhotosToBottom(((List<Photo>) album));
                mAlbumRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mCountryAlbumAdapter.notifyPhotosUpdates();
                    }
                });
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
