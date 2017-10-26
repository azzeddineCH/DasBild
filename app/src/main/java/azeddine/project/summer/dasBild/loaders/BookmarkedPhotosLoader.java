package azeddine.project.summer.dasBild.loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;


import java.util.ArrayList;
import java.util.List;

import azeddine.project.summer.dasBild.activities.MainActivity;
import azeddine.project.summer.dasBild.objectsUtils.Photo;

/**
 * Created by azeddine on 10/25/17.
 */

public class BookmarkedPhotosLoader extends PhotosLoader {


    public BookmarkedPhotosLoader(Context context) {
        super(context);
    }

    @Override
    public List<Photo> loadInBackground() {
        return MainActivity.dasBildDataBase.photoRoomDAO().selectPhotosRange(1,30);

    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        super.forceLoad();
    }

    public void forceLoad(int AlbumPageNumber) {
        super.forceLoad();
    }
}
