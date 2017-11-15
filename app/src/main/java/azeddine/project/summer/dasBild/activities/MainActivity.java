package azeddine.project.summer.dasBild.activities;

import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import azeddine.project.summer.dasBild.DasBildDataBase;
import azeddine.project.summer.dasBild.R;
import azeddine.project.summer.dasBild.adapters.CountriesListAdapter;
import azeddine.project.summer.dasBild.adapters.CountryAlbumAdapter;
import azeddine.project.summer.dasBild.fragments.AboutUsFragment;
import azeddine.project.summer.dasBild.fragments.BookmarksAlbumFragment;
import azeddine.project.summer.dasBild.fragments.CountriesListFragment;
import azeddine.project.summer.dasBild.fragments.OnlineAlbumFragment;
import azeddine.project.summer.dasBild.fragments.CountryDetailsDialogFragment;
import azeddine.project.summer.dasBild.fragments.PhotoProfileFragment;
import azeddine.project.summer.dasBild.objectsUtils.Country;
import azeddine.project.summer.dasBild.objectsUtils.KeysUtil;
import azeddine.project.summer.dasBild.objectsUtils.Photo;

public class MainActivity extends AppCompatActivity implements
                                                    CountriesListAdapter.OnCountryItemClickedListener,
                                                    CountriesListAdapter.OnCountryItemLongClickedListener,
                                                    CountryAlbumAdapter.OnPhotoClickedListener,
                                                    NavigationView.OnNavigationItemSelectedListener,
                                                    TabLayout.OnTabSelectedListener {

    private static final String TAG = "MainActivity";
    public static final String DEFAULT_REGION_NAME = "Arab world";
    public static final String DEFAULT_CATEGORY = "Landscapes";
    public static DasBildDataBase dasBildDataBase;

    private DrawerLayout mDrawer;
    private TabLayout mAlbumCategoriesTabLayout;

    private String currentCountryName;
    private String currentCategoryName = DEFAULT_CATEGORY;
    private String currentRegionName = DEFAULT_REGION_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        introPageHandler();
        setContentView(R.layout.activity_main);

        // initializing the database
        dasBildDataBase = Room.databaseBuilder(this,DasBildDataBase.class,"db").allowMainThreadQueries()
                .build();

        // setting up the toolbar
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.findViewById(R.id.toolbar_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fm = getSupportFragmentManager().findFragmentByTag(OnlineAlbumFragment.TAG);
                if (fm != null) ((OnlineAlbumFragment)fm).resetAlbumScroll();

            }
        });
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);

        //setting up the navigation  drawer
        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        mDrawer.setStatusBarBackground(R.color.colorPrimaryDark);
        toggle.syncState();

        NavigationView mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.arab_region);

        //setting up the tabLayout
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
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        //intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        registerReceiver(new NetworkStateBroadcastReceiver(), intentFilter);


    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");

        PhotoProfileFragment fm = (PhotoProfileFragment) getSupportFragmentManager().findFragmentByTag(PhotoProfileFragment.TAG);

        //checking if the slide panel of the photo fragment is opened
        if (fm != null) {
            if (fm.isSlidingPanelOpen()) {
                fm.setSlidingPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                return;
            }
        } else {
            if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START);
                return;
            }

        }
        super.onBackPressed();
        if(getSupportFragmentManager().getBackStackEntryCount() == 0) mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FragmentManager fm = getSupportFragmentManager();
            if (fm.findFragmentByTag(CountriesListFragment.TAG) == null) startCountriesListFragment(DEFAULT_REGION_NAME);
            if (fm.findFragmentByTag(OnlineAlbumFragment.TAG) == null)  startAlbumFragment(DEFAULT_REGION_NAME, DEFAULT_CATEGORY);
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
        fragment.setArguments(args);
        fragment.show(getSupportFragmentManager(), CountryDetailsDialogFragment.TAG);
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
        }else if(groupId == R.id.bookmarks){
            currentCountryName = currentRegionName = null;
            switch (id){
                case R.id.bookmarked_countries_regions:
                    startCountriesListFragment(null);
                    Country country = dasBildDataBase.countryRoomDAO().selectLatestBookmarkedCountry();
                    startAlbumFragment((currentCountryName  = (country != null) ? country.getName() : null),currentCategoryName);
                    break;
                case R.id.bookmarked_photos:
                    startBookmarkedPhotosFragment();
                    break;
                default:
                    break;
            }
        }else if (groupId == R.id.more_info){
            switch (id){
                case R.id.about_the_app:
                    mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.drawer_layout,new AboutUsFragment())
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                    break;
            }
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        currentCategoryName = tab.getText().toString();
        if (getSupportFragmentManager().findFragmentByTag(OnlineAlbumFragment.TAG) != null){
                 startAlbumFragment(currentCountryName, currentCategoryName);
        }

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

    /**
     * start the Album fragment getting the selected country and album category
     * @param name the selected country name
     * @param category the category name
     */
    private void startAlbumFragment(String name, String category) {

        Bundle args = new Bundle();
        args.putString(KeysUtil.ALBUM_NAME_KEY, name);
        args.putString(KeysUtil.CATEGORY_NAME_KEY, category);

        OnlineAlbumFragment countryAlbumFragment = new OnlineAlbumFragment();
        countryAlbumFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_country_album_container, countryAlbumFragment, OnlineAlbumFragment.TAG)
                .commit();
    }

    /**
     * start the country list fragment getting the name of the selected region
     * @param region the slected region
     */
    private void startCountriesListFragment(String region) {
        CountriesListFragment countriesListFragment = new CountriesListFragment();
        if(region != null){
            Bundle args = new Bundle();
            args.putString(KeysUtil.REGION_NAME_KEY, region);
            countriesListFragment.setArguments(args);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_countries_list_container, countriesListFragment, CountriesListFragment.TAG)
                .commit();
    }


    /**
     * start the bookmarked photos fragment
     */
    private void startBookmarkedPhotosFragment(){
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        BookmarksAlbumFragment bookmarksAlbumFragment = new BookmarksAlbumFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.drawer_layout,bookmarksAlbumFragment,BookmarksAlbumFragment.TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    /**
     * a method that detect the layout orientation then return the type of scroll the countries list should get
     * @return 1 if landscape else 0
     */
    public int getCountriesListScrollOrientation() {
        return findViewById(R.id.app_bar_layout).findViewById(R.id.fragment_countries_list_container) == null ? 1 :0;


    }

    public void introPageHandler() {
        // this thread will start the intro activity if the app is being lunched for the first time
        // declare a new thread to do a preference check
        Thread intro = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
                Log.d(TAG, "run: this is the first start " + isFirstStart);
                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent introActivityIntent = new Intent(MainActivity.this,AppIntroActivity.class);
                    startActivity(introActivityIntent);

                    //  Make a new preferences editor
                    SharedPreferences.Editor preferenceEditor = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    preferenceEditor.putBoolean("firstStart", false);

                    //  Apply changes
                    preferenceEditor.apply();
                }
            }
        });
        intro.start();
    }


    public  static class NetworkStateBroadcastReceiver extends BroadcastReceiver{

        public NetworkStateBroadcastReceiver(){

        }
        @Override
        public void onReceive(Context context, Intent intent) {
           if(!ApiUtils.isOnline(context)) Toast.makeText(context,R.string.no_signal, Toast.LENGTH_SHORT).show();
        }

    }
}
