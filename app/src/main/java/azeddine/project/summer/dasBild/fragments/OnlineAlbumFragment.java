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

        View view = super.onCreateView(inflater,container,savedInstanceState);
        setAlbumOnRefreshListner(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!mCountryAlbumAdapter.isLoading())  ((OnlinePhotosLoader) getLoaderManager().getLoader(KeysUtil.ONLINE_PHOTOS_LOADER_ID)).forceLoad(1);
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
                    ((OnlinePhotosLoader) getLoaderManager().getLoader(KeysUtil.ONLINE_PHOTOS_LOADER_ID)).forceLoad(mCurrentAlbumPage);
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
            OnlinePhotosLoader loader = ((OnlinePhotosLoader) getLoaderManager().getLoader(KeysUtil.ONLINE_PHOTOS_LOADER_ID));
            if(loader != null){
                mCountryAlbumAdapter.addPhotosToBottom(((OnlinePhotosLoader) getLoaderManager().getLoader(KeysUtil.ONLINE_PHOTOS_LOADER_ID)).getSavedPhotos());
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

        getLoaderManager().initLoader(KeysUtil.ONLINE_PHOTOS_LOADER_ID, null, OnlineAlbumFragment.this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KeysUtil.ALBUM_PAGE_NUMBER_KEY, mCurrentAlbumPage);
        if(!mCountryAlbumAdapter.getPhotos().isEmpty()){
              if(!mCountryAlbumAdapter.isLoading()) ((OnlinePhotosLoader) getLoaderManager().getLoader(KeysUtil.ONLINE_PHOTOS_LOADER_ID)).setSavedPhotos(mCountryAlbumAdapter.getPhotos());
        }
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object album) {
        super.onLoadFinished(loader, album);
        if (loader.getId() == KeysUtil.ONLINE_PHOTOS_LOADER_ID){

            if(((List<Photo>)album).isEmpty()){
                if(mCountryAlbumAdapter.isLoading())  mCountryAlbumAdapter.setAdapterLoadingState(false); else mSwipeRefreshLayout.setRefreshing(false);
                if(mCountryAlbumAdapter.getPhotos().isEmpty()){
                    if(ApiUtils.isOnline(getContext())) setEmptyListBitmap(true,R.string.no_photos,R.drawable.no_photo);else setEmptyListBitmap(true,R.string.no_signal,R.drawable.no_signal);
                }else{
                    Toast.makeText(getContext(), R.string.no_photos, Toast.LENGTH_SHORT).show();
                }
            }else{
                if (mCountryAlbumAdapter.isLoading()) {
                    Log.d(TAG, "onLoadFinished: load more end");
                    mCountryAlbumAdapter.setAdapterLoadingState(false);
                    mCountryAlbumAdapter.addPhotosToBottom((List<Photo>)album);
                }else{
                    mSwipeRefreshLayout.setRefreshing(false);
                    mCountryAlbumAdapter.addPhotosToTop((List<Photo>)album);
                }

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
