package azeddine.project.summer.dasBild.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import azeddine.project.summer.dasBild.R;
import azeddine.project.summer.dasBild.activities.MainActivity;
import azeddine.project.summer.dasBild.adapters.CountriesListAdapter;
import azeddine.project.summer.dasBild.loaders.BookmarkedCountriesLoader;
import azeddine.project.summer.dasBild.loaders.CountriesListLoader;
import azeddine.project.summer.dasBild.objectsUtils.Country;
import azeddine.project.summer.dasBild.objectsUtils.KeysUtil;

/**
 * Created by azeddine on 28/07/17.
 */

public class CountriesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Country>> {
    public static final String TAG = "CountriesListFragment";

    private RecyclerView mCountriesRecyclerView;
    private CountriesListAdapter mCountriesListAdapter;
    private String  mFocusedCountryName ;
    private String mRegionName;

    public CountriesListFragment() {}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        if(savedInstanceState != null) mFocusedCountryName = savedInstanceState.getString(KeysUtil.ALBUM_NAME_KEY);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView:");
        View view =  inflater.inflate(R.layout.fragment_countries_list,container,false);
        mCountriesRecyclerView =  view.findViewById(R.id.countries_list);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);
        mCountriesListAdapter = new CountriesListAdapter(getContext());
        int scrollOrientation = ((MainActivity)getContext()).getCountriesListScrollOrientation();
        mCountriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),scrollOrientation,false));
        mCountriesRecyclerView.getItemAnimator().setChangeDuration(0);
        mCountriesRecyclerView.setAdapter(mCountriesListAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
        Bundle fragmentArgs = getArguments();
        if(fragmentArgs != null){
            mRegionName =  fragmentArgs.getString(KeysUtil.REGION_NAME_KEY);
            Bundle args = new Bundle();
            args.putString(KeysUtil.REGION_NAME_KEY, mRegionName);
            Log.d(TAG, "onActivityCreated: loading args");
            getLoaderManager().initLoader(KeysUtil.COUNTRIES_LIST_LOADER_ID,args,this);
        }else {
            getLoaderManager().initLoader(KeysUtil.BOOKMARKED_COUNTRY_DETAILS_LOADER_ID,null,this);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: focused country "+mCountriesListAdapter.getFocusedCountryName());
        super.onSaveInstanceState(outState);
        outState.putString(KeysUtil.ALBUM_NAME_KEY,mCountriesListAdapter.getFocusedCountryName());
    }
    
    @Override
    public Loader<List<Country>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: ");
        switch (id){
            case KeysUtil.COUNTRIES_LIST_LOADER_ID:
                return new CountriesListLoader(getContext(),args.getString(KeysUtil.REGION_NAME_KEY));
            case KeysUtil.BOOKMARKED_COUNTRY_DETAILS_LOADER_ID:
                Log.d(TAG, "onCreateLoader: searching in the database");
                return new BookmarkedCountriesLoader(getContext());
            default:
                return null;
        }
    }
    @Override
    public void onLoadFinished(Loader<List<Country>> loader, List<Country> data) {
        int loaderId = loader.getId();
        if(loaderId == KeysUtil.COUNTRIES_LIST_LOADER_ID){
            if(data != null) {
                Country regionCountries = new Country(mRegionName,"ALL","ALL",null,mRegionName);
                if(!data.contains(regionCountries)) data.add(0,regionCountries);
            }
        }
        mCountriesListAdapter.setCountriesList(data,mFocusedCountryName);



    }
    @Override
    public void onLoaderReset(Loader<List<Country>> loader) {
        Log.d(TAG, "onLoaderReset: ");

    }

    public void setFocusedCountryName(String mFocusedCountryName) {
        this.mFocusedCountryName = mFocusedCountryName;
    }
}
