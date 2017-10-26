package azeddine.project.summer.dasBild.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import azeddine.project.summer.dasBild.R;
import azeddine.project.summer.dasBild.adapters.CountryAlbumAdapter;
import azeddine.project.summer.dasBild.objectsUtils.BookmarkedItem;
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
        View view = super.onCreateView(inflater, container, savedInstanceState);
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
        getLoaderManager().initLoader(KeysUtil.BOOKMARKED_COUNTRY_DETAILS_LOADER_ID, null, this);
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object album) {
        super.onLoadFinished(loader, album);
        if (loader.getId() == KeysUtil.BOOKMARKED_COUNTRY_DETAILS_LOADER_ID) {
            if (((List<Photo>) album).isEmpty()) {

            } else {
                Log.d(TAG, "onLoadFinished: " + ((List<Photo>) album).size());
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
}
