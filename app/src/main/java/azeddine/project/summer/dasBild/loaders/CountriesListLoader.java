package azeddine.project.summer.dasBild.loaders;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import azeddine.project.summer.dasBild.ApiUtils;
import azeddine.project.summer.dasBild.R;
import azeddine.project.summer.dasBild.activities.MainActivity;
import azeddine.project.summer.dasBild.objectsUtils.Country;

/**
 * Created by azeddine on 29/07/17.
 */

public class CountriesListLoader extends AsyncTaskLoader<List<Country>> {
    private static final String TAG = "CountriesListLoader";
    private static final String REST_COUNTRY_API_V2 = "https://restcountries.eu/rest/v2";
    private static final String REST_COUNTRY_API_V1 = "https://restcountries.eu/rest/v1";
    private static final String FLAG_BASE_URL = "https://flagpedia.net/data/flags/normal/";
    private static final String API_ENDPOINT_REGION_BLOC = "regionalbloc";
    private static final String API_ENDPOINT_REGION = "region";

    private String regionKey;

    public CountriesListLoader(Context context, String regionName) {
        super(context);
        int i = Arrays.asList(context.getResources().getStringArray(R.array.regions_search_names)).indexOf(regionName);
        regionKey = context.getResources().getStringArray(R.array.regions_keys)[i];
    }

    @Override
    public List<Country> loadInBackground() {
        String responseBodyString;
        JSONArray countriesJsonArray;
        JSONObject countryJsonObject;
        List<Country> countriesList;
        countriesList = MainActivity.dasBildDataBase.countryRoomDAO().selectRegionCountries(regionKey);
        if (countriesList.isEmpty()){
            Uri url = new Uri.Builder()
                    .encodedPath(getApiStartPoint(regionKey))
                    .appendPath(getApiEndPoint(regionKey))
                    .appendPath(regionKey)
                    .encodedQuery("fields=name;alpha2Code;alpha3Code")
                    .build();
            try {
                responseBodyString = ApiUtils.run(url);
                countriesJsonArray = new JSONArray(responseBodyString);

                for (int i = 0; i < countriesJsonArray.length(); i++) {
                    countryJsonObject = countriesJsonArray.getJSONObject(i);
                    if (!countryJsonObject.getString("name").equalsIgnoreCase("israel"))
                        countriesList.add(getCountryInstance(countryJsonObject));
                }
                MainActivity.dasBildDataBase.countryRoomDAO().insertCountries(countriesList);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return countriesList;


    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    private Country getCountryInstance(JSONObject jsonObject) throws JSONException {
        String name;
        String twoAlphaCode;
        String threeAlphaCode;

        name = jsonObject.getString("name").split("\\(")[0].split(",")[0];
        twoAlphaCode = jsonObject.getString("alpha2Code");
        threeAlphaCode = jsonObject.getString("alpha3Code");
        String flagUrl = FLAG_BASE_URL + twoAlphaCode.toLowerCase() + ".png";
        return new Country(name,twoAlphaCode,threeAlphaCode,flagUrl,regionKey);

    }

    private String getApiStartPoint(String s) {
        String region = s.toLowerCase();
        switch (region) {
            case "asia":
                return REST_COUNTRY_API_V1;
            default:
                return REST_COUNTRY_API_V2;
        }
    }

    private String getApiEndPoint(String s) {
        String region = s.toLowerCase();
        switch (region) {
            case "asia":
                return API_ENDPOINT_REGION;
            default:
                return API_ENDPOINT_REGION_BLOC;
        }
    }


}
