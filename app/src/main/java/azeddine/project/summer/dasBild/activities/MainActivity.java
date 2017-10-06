package azeddine.project.summer.dasBild.activities;

import android.app.usage.NetworkStats;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Arrays;

import azeddine.project.summer.dasBild.ApiUtils;
import azeddine.project.summer.dasBild.R;
import azeddine.project.summer.dasBild.adapters.CountriesListAdapter;
import azeddine.project.summer.dasBild.adapters.CountryAlbumAdapter;
import azeddine.project.summer.dasBild.fragments.CountriesListFragment;
import azeddine.project.summer.dasBild.fragments.CountryAlbumFragment;
import azeddine.project.summer.dasBild.fragments.CountryDetailsDialogFragment;
import azeddine.project.summer.dasBild.fragments.PhotoProfileFragment;
import azeddine.project.summer.dasBild.objectsUtils.Country;
import azeddine.project.summer.dasBild.objectsUtils.KeysUtil;
import azeddine.project.summer.dasBild.objectsUtils.Photo;

public class MainActivity extends AppCompatActivity implements
        CountriesListAdapter.OnCountryItemClickedListener, CountriesListAdapter.OnCountryItemLongClickedListener,
        CountryAlbumAdapter.OnPhotoClickedListener, NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener {

    private static final String TAG = "MainActivity";
    public static final String DEFAULT_REGION_NAME = "Arab world";
    public static final String DEFAULT_CATEGORY = "Landscapes";

    private DrawerLayout mDrawer;
    private TabLayout mAlbumCategoriesTabLayout;

    private String currentCountryName;
    private String currentCategoryName = DEFAULT_CATEGORY;
    private String currentRegionName = DEFAULT_REGION_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_main);


        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.findViewById(R.id.toolbar_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fm = getSupportFragmentManager().findFragmentByTag(CountryAlbumFragment.TAG);
                if (fm != null) ((CountryAlbumFragment)fm).resetAlbumScroll();

            }
        });
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        mDrawer.setStatusBarBackground(R.color.colorPrimaryDark);
        toggle.syncState();

        NavigationView mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.arab_region);

        mAlbumCategoriesTabLayout = findViewById(R.id.tab_layout);
        ArrayList<String> albumCategories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.albums_categories)));
        for (String category : albumCategories) {
            mAlbumCategoriesTabLayout.addTab(mAlbumCategoriesTabLayout.newTab().setText(category));
        }
        mAlbumCategoriesTabLayout.addOnTabSelectedListener(this);

        if (savedInstanceState != null) {
            currentCategoryName = savedInstanceState.getString(KeysUtil.CATEGORY_NAME_KEY);
            currentCountryName = savedInstanceState.getString(KeysUtil.ALBUM_NAME_KEY);
            currentRegionName = savedInstanceState.getString(KeysUtil.REGION_NAME_KEY);
            mAlbumCategoriesTabLayout.getTabAt(albumCategories.indexOf(currentCategoryName)).select();
        }

    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");

        PhotoProfileFragment fm = (PhotoProfileFragment) getSupportFragmentManager().findFragmentByTag(PhotoProfileFragment.TAG);
        if (fm != null) {
            if (fm.isSlidingPanelOpen()) {
                fm.setSlidingPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                return;
            } else {
                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        } else {
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START);
                return;
            }
        }
        super.onBackPressed();
    }


    @Override
    protected void onStart() {
        super.onStart();
        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentByTag(CountriesListFragment.TAG) == null) startCountriesListFragment(DEFAULT_REGION_NAME);
        if (fm.findFragmentByTag(CountryAlbumFragment.TAG) == null)  startAlbumFragment(DEFAULT_REGION_NAME, DEFAULT_CATEGORY);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ");

        outState.putString(KeysUtil.CATEGORY_NAME_KEY, currentCategoryName);
        outState.putString(KeysUtil.ALBUM_NAME_KEY, currentCountryName);
        outState.putString(KeysUtil.REGION_NAME_KEY, currentRegionName);


    }

    @Override
    public void onCountryLongClicked(Country country, ImageView sharedImageView) {
        DialogFragment fragment = new CountryDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putString(KeysUtil.COUNTRY_NAME_KEY, country.getName());
        args.putString(KeysUtil.COUNTRY_FLAG_URL_KEY, country.getFlagURL());
        fragment.setArguments(args);
        fragment.show(getSupportFragmentManager(), "bd");
    }

    @Override
    public void onCountryClicked(String countryName) {
        Log.d(TAG, "onCountryClicked: ");
        currentCountryName = countryName;
        ((CountriesListFragment)getSupportFragmentManager().findFragmentByTag(CountriesListFragment.TAG)).setFocusedCountryName(currentCountryName);
        startAlbumFragment(currentCountryName, currentCategoryName);
    }

    @Override
    public void onPhotoClicked(Photo photo) {
        Log.d(TAG, "onPhotoClicked: ");
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


        Bundle args = new Bundle();
        args.putSerializable("Photo", photo);

        PhotoProfileFragment photoProfileFragment = new PhotoProfileFragment();
        photoProfileFragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.drawer_layout, photoProfileFragment, PhotoProfileFragment.TAG)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int groupId = item.getGroupId();
        int id = item.getItemId();
        String selectedRegionTitle;
        if (groupId == R.id.regions) {
            switch (id) {
                case R.id.arab_region:
                    selectedRegionTitle = getResources().getStringArray(R.array.regions_search_names)[0];
                    break;
                case R.id.europe_region:
                    selectedRegionTitle = getResources().getStringArray(R.array.regions_search_names)[1];
                    break;
                case R.id.africa_region:
                    selectedRegionTitle = getResources().getStringArray(R.array.regions_search_names)[2];
                    break;
                case R.id.asia_region:
                    selectedRegionTitle = getResources().getStringArray(R.array.regions_search_names)[3];
                    break;
                case R.id.america_region:
                    selectedRegionTitle = getResources().getStringArray(R.array.regions_search_names)[4];
                    break;
                case R.id.south_america_region:
                    selectedRegionTitle = getResources().getStringArray(R.array.regions_search_names)[5];
                    break;
                default:
                    selectedRegionTitle = getResources().getStringArray(R.array.regions_search_names)[0];
            }
            if (!selectedRegionTitle.equalsIgnoreCase(currentRegionName)) {
                currentRegionName = selectedRegionTitle;
                currentCountryName = currentRegionName;
                startCountriesListFragment(selectedRegionTitle);
                startAlbumFragment(selectedRegionTitle, currentCategoryName);
            }
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.d(TAG, "onTabSelected: ");
        currentCategoryName = tab.getText().toString();
        startAlbumFragment(currentCountryName, currentCategoryName);

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startAlbumFragment(String name, String category) {
        Log.d(TAG, "startAlbumFragment: ");

        Bundle args = new Bundle();
        args.putString(KeysUtil.ALBUM_NAME_KEY, name);
        args.putString(KeysUtil.CATEGORY_NAME_KEY, category);

        CountryAlbumFragment countryAlbumFragment = new CountryAlbumFragment();
        countryAlbumFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_country_album_container, countryAlbumFragment, CountryAlbumFragment.TAG)
                .commit();
    }

    private void startCountriesListFragment(String region) {
        Log.d(TAG, "startCountriesListFragment: ");
        Bundle args = new Bundle();
        args.putString(KeysUtil.REGION_NAME_KEY, region);
        CountriesListFragment countriesListFragment = new CountriesListFragment();
        countriesListFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_countries_list_container, countriesListFragment, CountriesListFragment.TAG)
                .commit();
    }

    public int getCountriesListScrollOrientation() {
        return findViewById(R.id.app_bar_layout).findViewById(R.id.fragment_countries_list_container) == null ? 1 :0;


    }

    public  static class NetworkStateBroadcastReceiver extends BroadcastReceiver{

        public NetworkStateBroadcastReceiver(){

        }
        @Override
        public void onReceive(Context context, Intent intent) {
           boolean connectionState = ApiUtils.isOnline(context);
           if(connectionState)  Toast.makeText(context, "Swipe to refresh", Toast.LENGTH_SHORT).show();

        }

    }
}
