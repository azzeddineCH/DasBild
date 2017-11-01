package azeddine.project.summer.dasBild.loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import azeddine.project.summer.dasBild.objectsUtils.Photo;

/**
 * Created by azeddine on 10/25/17.
 */

public abstract class PhotosLoader extends AsyncTaskLoader<Object> {
    private static final String TAG = "OnlinePhotosLoader";


    public static final int ALBUM_PAGE_IMAGE_NUM = 30;
    public static final int DEFAULT_ALBUM_PAGE = 1;

    private String mCountryName;
    private String mCategoryName;
    private int mAlbumPageNumber = DEFAULT_ALBUM_PAGE;
    private List<Photo> mSavedPhotos;

    public String getCountryName() {
        return mCountryName;
    }

    public void setCountryName(String mCountryName) {
        this.mCountryName = mCountryName;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public void setCategoryName(String mCategoryName) {
        this.mCategoryName = mCategoryName;
    }

    public int getAlbumPageNumber() {
        return mAlbumPageNumber;
    }

    public void setAlbumPageNumber(int mAlbumPageNumber) {
        this.mAlbumPageNumber = mAlbumPageNumber;
    }


    public PhotosLoader(Context context) {
        super(context);
    }



    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    protected void forceLoad(int pageNumber) {
        mAlbumPageNumber = pageNumber;
        super.onForceLoad();
    }

    public List<Photo> getSavedPhotos() {
        return mSavedPhotos;
    }

    public void setSavedPhotos(List<Photo> savedPhotos) {
        this.mSavedPhotos = savedPhotos;
    }


}
