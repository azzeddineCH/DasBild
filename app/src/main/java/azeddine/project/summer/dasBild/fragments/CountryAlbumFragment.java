package azeddine.project.summer.dasBild.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import azeddine.project.summer.dasBild.ApiUtils;
import azeddine.project.summer.dasBild.R;
import azeddine.project.summer.dasBild.adapters.CountryAlbumAdapter;
import azeddine.project.summer.dasBild.costumComponents.WrapContentGridLayoutManager;
import azeddine.project.summer.dasBild.loaders.CountryAlbumLoader;
import azeddine.project.summer.dasBild.objectsUtils.KeysUtil;
import azeddine.project.summer.dasBild.objectsUtils.Photo;

/**
 * Created by azeddine on 31/07/17.
 */

public class CountryAlbumFragment extends Fragment implements LoaderManager.LoaderCallbacks<Object> {
    public static final String TAG = "CountryAlbumFragment";

    private RecyclerView mAlbumRecyclerView;
    private CountryAlbumAdapter mCountryAlbumAdapter;
    private String mAlbumName;
    private String mCategoryName;
    private int mCurrentAlbumPage = CountryAlbumLoader.DEFAULT_ALBUM_PAGE;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_counrty_album, container, false);
        mAlbumRecyclerView = view.findViewById(R.id.country_album);

        mAlbumRecyclerView.setLayoutManager(new WrapContentGridLayoutManager(getContext(), 3));
        mCountryAlbumAdapter = new CountryAlbumAdapter(getContext(), mAlbumRecyclerView,3);

        mCountryAlbumAdapter.setOnPhotoClickedListener((CountryAlbumAdapter.OnPhotoClickedListener) getContext());
        mCountryAlbumAdapter.setOnLoadMoreListener(new CountryAlbumAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mCurrentAlbumPage++;
                Log.d(TAG, "onLoadMore: load page number "+mCurrentAlbumPage);
                ((CountryAlbumLoader) getLoaderManager().getLoader(KeysUtil.ALBUM_LOADER_ID)).forceLoad(mCurrentAlbumPage);
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
            CountryAlbumLoader loader = ((CountryAlbumLoader) getLoaderManager().getLoader(KeysUtil.ALBUM_LOADER_ID));
            if(loader != null){
                mCountryAlbumAdapter.updatePhotos(((CountryAlbumLoader) getLoaderManager().getLoader(KeysUtil.ALBUM_LOADER_ID)).getSavedPhotos());
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

        mAlbumRecyclerView.setAdapter(mCountryAlbumAdapter);

        mAlbumName = getArguments().getString(KeysUtil.ALBUM_NAME_KEY);
        mCategoryName = getArguments().getString(KeysUtil.CATEGORY_NAME_KEY);
        mCountryAlbumAdapter.setAdapterLoadingState(true);

        getLoaderManager().initLoader(KeysUtil.ALBUM_LOADER_ID, null, CountryAlbumFragment.this);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: page number="+mCurrentAlbumPage+" item number="+mCountryAlbumAdapter.getPhotos().size());
        outState.putInt(KeysUtil.ALBUM_PAGE_NUMBER_KEY, mCurrentAlbumPage);
        if(mCountryAlbumAdapter.getPhotos().get(0) != null) ((CountryAlbumLoader) getLoaderManager().getLoader(KeysUtil.ALBUM_LOADER_ID)).setSavedPhotos(mCountryAlbumAdapter.getPhotos());
    }


    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: ");
        switch (id) {
            case KeysUtil.ALBUM_LOADER_ID:
                return new CountryAlbumLoader(getContext(),mAlbumName,mCategoryName);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Object> loader,Object album ) {
        Log.d(TAG, "onLoadFinished: ");
        if (loader.getId() == KeysUtil.ALBUM_LOADER_ID) {
            if(mCountryAlbumAdapter.isLoading())  mCountryAlbumAdapter.setAdapterLoadingState(false);
                if(album != null){
                    mCountryAlbumAdapter.updatePhotos((ArrayList<Photo>) album);
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
    public void resetAlbumScroll(){
        mAlbumRecyclerView.smoothScrollToPosition(0);
    }
}
