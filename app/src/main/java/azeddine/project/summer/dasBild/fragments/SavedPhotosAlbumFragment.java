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
import android.widget.Toast;

import java.util.List;

import azeddine.project.summer.dasBild.R;
import azeddine.project.summer.dasBild.adapters.CountryAlbumAdapter;
import azeddine.project.summer.dasBild.loaders.SavedPhotosLoader;
import azeddine.project.summer.dasBild.loaders.OnlinePhotosLoader;
import azeddine.project.summer.dasBild.objectsUtils.KeysUtil;
import azeddine.project.summer.dasBild.objectsUtils.Photo;

/**
 * Created by azeddine on 10/22/17.
 */

public class SavedPhotosAlbumFragment extends PhotosGalleryFragment {
    public static final String TAG = "SavedPhotosAlbumFragment";
    private int mCurrentAlbumPage = OnlinePhotosLoader.DEFAULT_ALBUM_PAGE;
    public SavedPhotosAlbumFragment() {

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
            actionBar.setTitle(R.string.saved_photos_fragment_title);
        }
        try {
            mCountryAlbumAdapter = initAlbumAdapter(getContext(), 3);
            mCountryAlbumAdapter.setOnLoadMoreListener(new CountryAlbumAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                        mCurrentAlbumPage++;
                        ((SavedPhotosLoader) getLoaderManager().getLoader(KeysUtil.SAVED_PHOTOS_LOADER_ID)).forceLoad(mCurrentAlbumPage);
                }
            });
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
        if (savedInstanceState != null) {
            mCurrentAlbumPage = savedInstanceState.getInt(KeysUtil.ALBUM_PAGE_NUMBER_KEY);
            SavedPhotosLoader loader = ((SavedPhotosLoader) getLoaderManager().getLoader(KeysUtil.ONLINE_MORE_PHOTOS_LOADER_ID));
            if(loader != null){
                mCountryAlbumAdapter.addPhotosToBottom(loader.getSavedPhotos());
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCountryAlbumAdapter.setAdapterLoadingState(true);
        getLoaderManager().initLoader(KeysUtil.SAVED_PHOTOS_LOADER_ID, null, this);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KeysUtil.ALBUM_PAGE_NUMBER_KEY, mCurrentAlbumPage);
        if(mCountryAlbumAdapter.isLoading()) mCountryAlbumAdapter.setAdapterLoadingState(false);
        ((SavedPhotosLoader) getLoaderManager().getLoader(KeysUtil.SAVED_COUNTRIES_LOADER_ID)).setSavedPhotos(mCountryAlbumAdapter.getPhotos());
    }


    @Override
    public void onLoadFinished(Loader<Object> loader, Object album) {
        if (loader.getId() == KeysUtil.SAVED_PHOTOS_LOADER_ID) {
            mCountryAlbumAdapter.setAdapterLoadingState(false);
            if (((List<Photo>) album).isEmpty()) {
                if(mCountryAlbumAdapter.getPhotos().isEmpty()){
                    showEmptyListBitmap(R.string.no_saved_photos,R.drawable.no_photo);
                }else{
                    Toast.makeText(getContext(), R.string.no_more_saved_photos, Toast.LENGTH_SHORT).show();
                }
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
