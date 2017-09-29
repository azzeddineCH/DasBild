package azeddine.project.summer.dasBild.loaders;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import azeddine.project.summer.dasBild.ApiUtils;
import azeddine.project.summer.dasBild.objectsUtils.Photo;

/**
 * Created by azeddine on 31/07/17.
 */

public class CountryAlbumLoader extends AsyncTaskLoader<Object> {

    private static final String TAG = "CountryAlbumLoader";

    private static final String PHOTO_API_BASE_URL = "https://api.500px.com/v1/photos";
    private static final String API_CONSUMER_KEY = "8rESAvR28TpJTguNMbEabYUkDRBXK2ldh2H6Ypy0";
    private static final String API_CROPPED_IMAGE_SIZE = "200";
    private static final String API_UNCROPPED_IMAGE_SIZE = "1080";

    private static final int ALBUM_PAGE_IMAGE_NUM = 30;
    public static final int DEFAULT_ALBUM_PAGE = 1;


    private String mCountryName;
    private String mCategoryName;
    private int mAlbumPageNumber = DEFAULT_ALBUM_PAGE;
    private ArrayList<Photo> mSavedPhotos;


    public CountryAlbumLoader(Context context, String countryName, String categoryName) {
        super(context);
        this.mCountryName = countryName;
        this.mCategoryName = categoryName;
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public ArrayList<Photo> loadInBackground() {

        String responseBodyString;
        JSONArray photosJsonArray;
        ArrayList<Photo> photoArrayList = new ArrayList<>();

        Uri url = new Uri.Builder()
                .encodedPath(PHOTO_API_BASE_URL)
                .appendPath("search")
                .encodedQuery("image_size=" + API_CROPPED_IMAGE_SIZE + "," + API_UNCROPPED_IMAGE_SIZE)
                .appendQueryParameter("term", mCountryName)
                .appendQueryParameter("only", mCategoryName)
                .appendQueryParameter("exclude", "Nude")
                .appendQueryParameter("page", "" + mAlbumPageNumber)
                .appendQueryParameter("rpp", "" + ALBUM_PAGE_IMAGE_NUM)
                .appendQueryParameter("sort", "created_at")
                .appendQueryParameter("consumer_key", API_CONSUMER_KEY)
                .build();

        try {
            responseBodyString = ApiUtils.run(url);
            JSONObject jsonObject = new JSONObject(responseBodyString);
            photosJsonArray = jsonObject.getJSONArray("photos");

            for (int i = 0; i < photosJsonArray.length(); i++) {
                photoArrayList.add(getPhotoInstance(photosJsonArray.getJSONObject(i)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return photoArrayList;
    }

    public void forceLoad(int pageNumber) {
        mAlbumPageNumber = pageNumber;
        super.onForceLoad();
    }

    public ArrayList<Photo> getSavedPhotos() {
        return mSavedPhotos;
    }

    public void setSavedPhotos(ArrayList<Photo> savedPhotos) {
        this.mSavedPhotos = savedPhotos;
    }

    private Photo getPhotoInstance(JSONObject jsonObject) throws JSONException {
        Photo photo = new Photo();
        photo.setId(jsonObject.getString("id"));
        photo.setCroppedPhotoUrl(jsonObject.getJSONArray("images").getJSONObject(0).getString("url"));
        photo.setUnCroppedPhotoUrl(jsonObject.getJSONArray("images").getJSONObject(1).getString("url"));
        photo.setDescription(jsonObject.getString("description"));
        photo.setTitle(jsonObject.getString("name"));
        photo.setPhotographerImageUrl(jsonObject.getJSONObject("user").getString("userpic_url"));
        photo.setPhotographerUsername(jsonObject.getJSONObject("user").getString("fullname"));
        photo.setDateString(jsonObject.getString("taken_at").equals("null") ? jsonObject.getString("created_at") : jsonObject.getString("taken_at"));


        return photo;

    }


}
