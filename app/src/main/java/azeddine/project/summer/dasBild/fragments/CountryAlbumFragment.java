package azeddine.project.summer.dasBild.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private ImageView mErrorImageView ;
    private TextView mErrorText;
    private CountryAlbumAdapter mCountryAlbumAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mAlbumName;
    private String mCategoryName;
    private View mErrorDisplayLayout;
    private int mCurrentAlbumPage = CountryAlbumLoader.DEFAULT_ALBUM_PAGE;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_counrty_album, container, false);
        mAlbumRecyclerView = view.findViewById(R.id.country_album);
        mErrorImageView = view.findViewById(R.id.error_image);
        mErrorText = view.findViewById(R.id.error_text);
        mSwipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        mErrorDisplayLayout = view.findViewById(R.id.error_frame);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!mCountryAlbumAdapter.isLoading())  ((CountryAlbumLoader) getLoaderManager().getLoader(KeysUtil.ALBUM_LOADER_ID)).forceLoad(1);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mAlbumRecyclerView.setLayoutManager(new WrapContentGridLayoutManager(getContext(), 3));
        mCountryAlbumAdapter = new CountryAlbumAdapter(getContext(), mAlbumRecyclerView,3);

        mCountryAlbumAdapter.setOnPhotoClickedListener((CountryAlbumAdapter.OnPhotoClickedListener) getContext());
        mCountryAlbumAdapter.setOnLoadMoreListener(new CountryAlbumAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (ApiUtils.isOnline(getContext())) {
                    mCurrentAlbumPage++;
                    ((CountryAlbumLoader) getLoaderManager().getLoader(KeysUtil.ALBUM_LOADER_ID)).forceLoad(mCurrentAlbumPage);
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
            CountryAlbumLoader loader = ((CountryAlbumLoader) getLoaderManager().getLoader(KeysUtil.ALBUM_LOADER_ID));
            if(loader != null){
                mCountryAlbumAdapter.addPhotosToBottom(((CountryAlbumLoader) getLoaderManager().getLoader(KeysUtil.ALBUM_LOADER_ID)).getSavedPhotos());
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
        outState.putInt(KeysUtil.ALBUM_PAGE_NUMBER_KEY, mCurrentAlbumPage);
        if(!mCountryAlbumAdapter.getPhotos().isEmpty()){
              if(!mCountryAlbumAdapter.isLoading()) ((CountryAlbumLoader) getLoaderManager().getLoader(KeysUtil.ALBUM_LOADER_ID)).setSavedPhotos(mCountryAlbumAdapter.getPhotos());
        }
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
        if (loader.getId() == KeysUtil.ALBUM_LOADER_ID){

            if(((ArrayList<Photo>) album).isEmpty()){
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
                    mCountryAlbumAdapter.addPhotosToBottom((ArrayList<Photo>) album);
                }else{
                    mSwipeRefreshLayout.setRefreshing(false);
                    mCountryAlbumAdapter.addPhotosToTop((ArrayList<Photo>) album);
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

    private void setEmptyListBitmap(boolean state, @StringRes int errorText, @DrawableRes int drawableId){
        if(state){
            mSwipeRefreshLayout.setVisibility(View.GONE);
            mErrorDisplayLayout.setVisibility(View.VISIBLE);
            mErrorImageView.setImageResource(drawableId);
            mErrorText.setText(errorText);
        }else{
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            mErrorDisplayLayout.setVisibility(View.GONE);
        }
    }
    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }

    public void resetAlbumScroll(){
        mAlbumRecyclerView.smoothScrollToPosition(0);
    }


}
