package org.avaliabrasil.avaliabrasil.avb;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.fragments.PlacesListFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.PlacesMapFragment;
import org.avaliabrasil.avaliabrasil.data.AvBProvider;
import org.avaliabrasil.avaliabrasil.data.AvaliaBrasilApplication;
import org.avaliabrasil.avaliabrasil.rest.GooglePlacesAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.User;
import org.avaliabrasil.avaliabrasil.sync.Constant;
import org.avaliabrasil.avaliabrasil.sync.Observer;

import java.io.IOException;
import java.util.Stack;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,LoaderManager.LoaderCallbacks<Cursor>, LocationListener{

    static final String URI = "URI";

    /**
     * Constant for the {@link Intent}
     */
    private static final String USRID = "userId";

    /**
     * Constant for the {@link Intent}
     */
    private static final String GOOGLEPLACESID = "googlePlacesId";

    /**
     * The user id that comes with the {@link LoginActivity}
     */
    private int userId = 0;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    /**
     * User location
     */
    public Location location;

    /**
     * For notify the fragment children's.
     */
    public Stack<Observer> observerStack = new Stack<Observer>();

    /**
     * User defined to {@link org.avaliabrasil.avaliabrasil.sync.AvbAuthenticator}
     */
    private User user;

    /**
     * The {@link AccountManager} for getting user informations.
     */
    private AccountManager manager;

    /**
     * The {@link LocationManager} for get the user place/location or, if there is not, search for it.
     */
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            new Loading().execute();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    /**
     * Method use to fetch the data from the google api every time that the activity is started.
     */
    private void fetchDataFromGoogleAPI(){
        if(location == null){
            return;
        }
        GooglePlacesAPIClient.getNearlyPlaces(MainActivity.this, location);
    }


    /**
     * Show the user a message if the GPS is turned off.
     */
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder
                .setMessage(
                        "GPS está desativado em seu dispositivo. Deseja ativa-lo?")
                .setCancelable(false)
                .setPositiveButton("Configurações de ativação do GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search_places).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery("",false);
                getSupportLoaderManager().restartLoader(0,null,MainActivity.this);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                GooglePlacesAPIClient.getPlacesByName(MainActivity.this,MainActivity.this, location,query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO tirar fora atualização de localização.
    @Override
    public void onLocationChanged(Location providerLocation) {

        Toast.makeText(MainActivity.this,"Buscando novo local",Toast.LENGTH_LONG).show();

        if(providerLocation != null){
            if(isBetterLocation(providerLocation,location)){
                location = providerLocation;
            }
        }

        this.location = providerLocation;

        fetchDataFromGoogleAPI();

        getSupportLoaderManager().restartLoader(0,null,MainActivity.this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    PlacesListFragment placesListFragmentWithProvider = PlacesListFragment.newInstance(position + 1,location);

                    observerStack.add(placesListFragmentWithProvider);

                    return placesListFragmentWithProvider;

                case 1:

                    PlacesMapFragment placesMapFragmentWithProvider = PlacesMapFragment.newInstance(position + 1,location);

                    observerStack.add(placesMapFragmentWithProvider);

                    return placesMapFragmentWithProvider;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.places_list);
                case 1:
                    return getString(R.string.places_map);
            }
            return null;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(args != null){
            Uri uri = AvBProvider.PLACE_CONTENT_URI;
            return new CursorLoader(MainActivity.this, uri, null, null, new String[]{args.getString("query","")}, null);
        }else {
            Uri uri = AvBProvider.PLACE_CONTENT_URI;
            return new CursorLoader(MainActivity.this, uri, null, null, null, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        for(Observer ob : observerStack){
            ob.update(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        for(Observer ob : observerStack){
            ob.update(null);
        }
    }

    /**
     * Constant for 2 minutes.
     */
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /**
     * Check if the last user place is better than the last one.
     *
     * @param location
     * @param currentBestLocation
     * @return if the new location is better than the older.
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * {@link AsyncTask} class to act has a splash screen
     */
    private class Loading extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {



            user = ((AvaliaBrasilApplication)getApplication()).getUser();

            manager = AccountManager.get(MainActivity.this);

            Log.d("MainActivity", "onCreate: manager.getAccounts().length: " + manager.getAccounts().length);

            if(manager.getAccounts().length == 0){
                manager.addAccount(Constant.ACCOUNT_TYPE, Constant.ACCOUNT_TOKEN_TYPE_USER, null, null, MainActivity.this, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle bundle = future.getResult();

                        } catch (OperationCanceledException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (AuthenticatorException e) {
                            e.printStackTrace();
                        }

                    }
                }, null);
            }else{
                setContentView(R.layout.activity_main);

                // Add Toolbar
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                // Add Drawer
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        MainActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.setDrawerListener(toggle);
                toggle.syncState();

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(MainActivity.this);

                // Instruções para criar o Section Page!!
                // Create the adapter that will return a fragment for each of the three
                // primary sections of the activity.

                try {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    if (!locationManager
                            .isProviderEnabled(LocationManager.GPS_PROVIDER)&&!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                        showGPSDisabledAlertToUser();
                    }else {

                        Location providerLocation;

                        for(String provider : locationManager.getAllProviders()){

                            providerLocation = locationManager.getLastKnownLocation(provider);

                            Log.e("Location", "location "+ provider +" in provider is " + (providerLocation == null ? "null" : "not null"));

                            if(providerLocation != null){
                                if(isBetterLocation(providerLocation,location)){
                                    location = providerLocation;
                                }
                            }
                        }
                    }

                    if(location == null){
                        Log.e("Location", "location is null");
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,MainActivity.this);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,MainActivity.this);
                        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,0,0,MainActivity.this);
                    }else{
                        Log.e("Location", "location isn't null");
                        Log.e("Location", "lat: " + location.getLatitude());
                        Log.e("Location", "long: " + location.getLongitude());
                    }
                }catch(SecurityException e) {
                    e.printStackTrace();
                }

                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


                // Set up the ViewPager with the sections adapter.
                mViewPager = (ViewPager) findViewById(R.id.view_page_container);
                mViewPager.setAdapter(mSectionsPagerAdapter);

                TabLayout tabLayout = (TabLayout) findViewById(R.id.search_tabs);
                tabLayout.setupWithViewPager(mViewPager);

                fetchDataFromGoogleAPI();

                getSupportLoaderManager().initLoader(0, null, MainActivity.this);
            }
        }

        @Override
        protected void onPreExecute() {
            ImageView view = new ImageView(MainActivity.this);
            view.setImageDrawable(getDrawable(R.drawable.retangular_logo));
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT
                    ,ViewGroup.LayoutParams.FILL_PARENT);
            view.setLayoutParams(layoutParams);
            setContentView(view);
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
