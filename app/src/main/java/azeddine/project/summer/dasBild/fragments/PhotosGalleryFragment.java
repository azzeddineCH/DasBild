package azeddine.project.summer.dasBild.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorRes;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import azeddine.project.summer.dasBild.ApiUtils;
import azeddine.project.summer.dasBild.R;
import azeddine.project.summer.dasBild.adapters.CountryAlbumAdapter;
import azeddine.project.summer.dasBild.costumComponents.WrapContentGridLayoutManager;
import azeddine.project.summer.dasBild.loaders.BookmarkedPhotosLoader;
import azeddine.project.summer.dasBild.loaders.OnlinePhotosLoader;
import azeddine.project.summer.dasBild.objectsUtils.KeysUtil;
import azeddine.project.summer.dasBild.objectsUtils.Photo;

/**
 * Created by azeddine on 10/23/17.
 */

public abstract class PhotosGalleryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Object>{
    private static final String TAG = "PhotosGalleryFragment";
    protected RecyclerView mAlbumRecyclerView;
    protected ImageView mErrorImageView ;
    protected TextView mErrorText;
    protected CountryAlbumAdapter mCountryAlbumAdapter;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected String mAlbumName;
    protected String mCategoryName;
    protected View mErrorDisplayLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_counrty_album, container, false);

        mAlbumRecyclerView = view.findViewById(R.id.country_album);
        mErrorImageView = view.findViewById(R.id.error_image);
        mErrorText = view.findViewById(R.id.error_text);
        mSwipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        mErrorDisplayLayout = view.findViewById(R.id.error_frame);
        return view;
    }

    protected void setEmptyListBitmap(boolean state, @StringRes int errorText, @DrawableRes int drawableId){
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

    public void resetAlbumScroll(){
        mAlbumRecyclerView.smoothScrollToPosition(0);
    }

    public void setAlbumOnRefreshListner(SwipeRefreshLayout.OnRefreshListener refreshListener, @ColorRes int circleColor){
        this.mSwipeRefreshLayout.setOnRefreshListener(refreshListener);
        this.mSwipeRefreshLayout.setColorSchemeResources(circleColor);

    }

    public  CountryAlbumAdapter initAlbumAdapter(Context context,int columnNumber) throws Exception {
        if(columnNumber < 1) throw new Exception("the column number can't be less then one!");
        mAlbumRecyclerView.setLayoutManager(new WrapContentGridLayoutManager(context,columnNumber));
        return new CountryAlbumAdapter(context, mAlbumRecyclerView,columnNumber);
    }


    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: ");
        switch (id) {
            case KeysUtil.ONLINE_PHOTOS_LOADER_ID:
                return new OnlinePhotosLoader(getContext(),mAlbumName,mCategoryName);
            case KeysUtil.BOOKMARKED_COUNTRY_DETAILS_LOADER_ID:
                return new BookmarkedPhotosLoader(getContext());
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Object> loader,Object album ) {
        }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }


}
