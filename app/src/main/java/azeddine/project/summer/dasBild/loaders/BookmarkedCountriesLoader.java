package azeddine.project.summer.dasBild.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import azeddine.project.summer.dasBild.activities.MainActivity;
import azeddine.project.summer.dasBild.objectsUtils.Country;

/**
 * Created by azeddine on 10/11/17.
 */

public class BookmarkedCountriesLoader extends AsyncTaskLoader<List<Country>>{
    public BookmarkedCountriesLoader(Context context) {
        super(context);
    }

    @Override
    public List<Country> loadInBackground() {
        return MainActivity.dasBildDataBase.countryRoomDAO().selectBookmarkedCountry();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
