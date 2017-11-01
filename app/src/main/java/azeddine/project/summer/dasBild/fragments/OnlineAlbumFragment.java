package azeddine.project.summer.dasBild.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import azeddine.project.summer.dasBild.ApiUtils;
import azeddine.project.summer.dasBild.R;
import azeddine.project.summer.dasBild.adapters.CountryAlbumAdapter;
import azeddine.project.summer.dasBild.loaders.OnlinePhotosLoader;
import azeddine.project.summer.dasBild.objectsUtils.KeysUtil;
import azeddine.project.summer.dasBild.objectsUtils.Photo;

/**
 * Created by azeddine on 31/07/17.
 */

public class OnlineAlbumFragment extends PhotosGalleryFragment {
    public static final String TAG = "OnlineAlbumFragment";
    private int mCurrentAlbumPage = OnlinePhotosLoader.DEFAULT_ALBUM_PAGE;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");

        View view = super.onCreateView(inflater,container,savedInstanceState,false);
        setAlbumOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mCountryAlbumAdapter.isLoading()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                else{
                    OnlinePhotosLoader loader =  (OnlinePhotosLoader)  getLoaderManager().getLoader(KeysUtil.ONLINE_RECENT_PHOTOS_LOADER_ID);
                    if(loader == null )
                        getLoaderManager().initLoader(KeysUtil.ONLINE_RECENT_PHOTOS_LOADER_ID,null,OnlineAlbumFragment.this);
                    else
                        loader.forceLoad();
                }
            }
        },R.color.colorAccent);
        try {
            mCountryAlbumAdapter = initAlbumAdapter(getContext(),3);
            mAlbumRecyclerView.setAdapter(mCountryAlbumAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCountryAlbumAdapter.setOnPhotoClickedListener((CountryAlbumAdapter.OnPhotoClickedListener) getContext());
        mCountryAlbumAdapter.setOnLoadMoreListener(new CountryAlbumAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (ApiUtils.isOnline(getContext())) {
                    mCurrentAlbumPage++;
                    ((OnlinePhotosLoader) getLoaderManager().getLoader(KeysUtil.ONLINE_MORE_PHOTOS_LOADER_ID)).forceLoad(mCurrentAlbumPage);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCountryAlbumAdapter.setAdapterLoadingState(false);
                            Toast.makeText(getContext(), R.string.could_not_refresh_text, Toast.LENGTH_SHORT).show();
                        }
                    },1000);

                }
            }
        });
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentAlbumPage = savedInstanceState.getInt(KeysUtil.ALBUM_PAGE_NUMBER_KEY);
            OnlinePhotosLoader loader = ((OnlinePhotosLoader) getLoaderManager().getLoader(KeysUtil.ONLINE_MORE_PHOTOS_LOADER_ID));
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
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);

        mAlbumName = getArguments().getString(KeysUtil.ALBUM_NAME_KEY);
        mCategoryName = getArguments().getString(KeysUtil.CATEGORY_NAME_KEY);
        mCountryAlbumAdapter.setAdapterLoadingState(true);

        getLoaderManager().initLoader(KeysUtil.ONLINE_MORE_PHOTOS_LOADER_ID, null, OnlineAlbumFragment.this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KeysUtil.ALBUM_PAGE_NUMBER_KEY, mCurrentAlbumPage);
        if(mCountryAlbumAdapter.isLoading()) mCountryAlbumAdapter.setAdapterLoadingState(false);
        ((OnlinePhotosLoader) getLoaderManager().getLoader(KeysUtil.ONLINE_MORE_PHOTOS_LOADER_ID)).setSavedPhotos(mCountryAlbumAdapter.getPhotos());
        }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object album) {
        int id = loader.getId();
        List<Photo> photos = (List<Photo>) album;

        switch (id){
            case KeysUtil.ONLINE_MORE_PHOTOS_LOADER_ID:
    
                mCountryAlbumAdapter.setAdapterLoadingState(false);
                if(photos.isEmpty()){
                    if(mCountryAlbumAdapter.getPhotos().isEmpty()){
                        if(ApiUtils.isOnline(getContext()))
                            showEmptyListBitmap(R.string.no_photos,R.drawable.no_photo);
                        else
                            showEmptyListBitmap(R.string.no_signal,R.drawable.no_signal);
                    }else {
                        Toast.makeText(getContext(), R.string.no_photos, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    mCountryAlbumAdapter.addPhotosToBottom(photos);
                    mAlbumRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mCountryAlbumAdapter.notifyPhotosUpdates();
                        }
                    });
                }
                break;

            case KeysUtil.ONLINE_RECENT_PHOTOS_LOADER_ID:
                mSwipeRefreshLayout.setRefreshing(false);
                if(photos.isEmpty()){
                    Toast.makeText(getContext(), R.string.could_not_refresh_text, Toast.LENGTH_SHORT).show();
                }else {
                    if(isEmptyListViewVisible()) hideEmptyListBitmap();
                    mCountryAlbumAdapter.addPhotosToTop(photos);
                    mAlbumRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mCountryAlbumAdapter.notifyPhotosUpdates();
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
