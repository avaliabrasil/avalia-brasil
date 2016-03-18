package org.avaliabrasil.avaliabrasil.avb;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.fragments.PlacesListFragmentWithProvider;
import org.avaliabrasil.avaliabrasil.avb.fragments.PlacesMapFragmentWithProvider;
import org.avaliabrasil.avaliabrasil.data.AvBContract;
import org.avaliabrasil.avaliabrasil.data.AvBProviderSearch;
import org.avaliabrasil.avaliabrasil.data.AvBProviderTest;
import org.avaliabrasil.avaliabrasil.rest.GooglePlacesAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceSearch;
import org.avaliabrasil.avaliabrasil.rest.javabeans.ResultPlaceSearch;
import org.avaliabrasil.avaliabrasil.sync.Observer;

import java.util.Stack;

public class MainActivityWithProvider extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,LoaderManager.LoaderCallbacks<Cursor>{

    static final String URI = "URI";

    //TODO: Implementar variável userId ou outra corretamente!
    private static String USRID = "userId";
    private int userId = 0;
    private static String GOOGLEPLACESID = "googlePlacesId";

    // Definindo o Google Place Id que será passado para o Place Activity
    public String googlePlacesId = "asdasgd8218hdddDdsSAD";

    // Variáveis para o ViewPager e Tabs!
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    public static Location location;

    public Stack<Observer> observerStack = new Stack<Observer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "onCreate: ");

        // Add Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Instruções para criar o Section Page!!
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.view_page_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.search_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fetchDataFromGoogleAPI();

        getSupportLoaderManager().initLoader(0, null, MainActivityWithProvider.this);

    }

    private void fetchDataFromGoogleAPI(){
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


            if (!locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                showGPSDisabledAlertToUser();
            }

            location = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location == null) {
                location = new Location("");
                location.setLatitude(0);
                location.setLongitude(0);
            }

            if (MainActivity.placeSearch == null) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, GooglePlacesAPIClient.getNearlyPlaces(location.getLatitude(), location.getLongitude(), 500, null, "AIzaSyCBq-qetL_jdUUhM0TepfVZ5EYxJvw6ct0"),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Gson gson = new Gson();
                                PlaceSearch placeSearch = gson.fromJson(response, PlaceSearch.class);

                                ContentValues[] values = new ContentValues[placeSearch.getResults().size()];

                                ContentValues value = null;

                                for (int i = 0; i < values.length; i++) {
                                    value = new ContentValues();
                                    value.put("place_id",placeSearch.getResults().get(i).getPlaceId());
                                    value.put("name",placeSearch.getResults().get(i).getName());
                                    value.put("vicinity",placeSearch.getResults().get(i).getVicinity());
                                    value.put("latitude",placeSearch.getResults().get(i).getGeometry().getLocation().getLat());
                                    value.put("longitude",placeSearch.getResults().get(i).getGeometry().getLocation().getLng());
                                    values[i] = value;
                                }

                                int adicionados = getContentResolver().bulkInsert(
                                        AvBProviderTest.PLACE_CONTENT_URI, values);

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

                Volley.newRequestQueue(MainActivityWithProvider.this).add(stringRequest);
            }
        } catch (SecurityException e){
            e.printStackTrace();
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivityWithProvider.this);
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

    @SuppressWarnings("StatementWithEmptyBody")
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
    protected void onDestroy() {
        super.onDestroy();
    }

    // TODO: Tirar este menu!
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search_places).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);

        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery("",false);
                getSupportLoaderManager().restartLoader(0,null,MainActivityWithProvider.this);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle bundle = new Bundle();
                bundle.putString("query","%"+query+"%");
                getSupportLoaderManager().restartLoader(0,bundle,MainActivityWithProvider.this);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    PlacesListFragmentWithProvider placesListFragmentWithProvider = PlacesListFragmentWithProvider.newInstance(position + 1);

                    observerStack.add(placesListFragmentWithProvider);

                    return placesListFragmentWithProvider;

                case 1:

                    PlacesMapFragmentWithProvider placesMapFragmentWithProvider = PlacesMapFragmentWithProvider.newInstance(position + 1);

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

    public void startPlaceActivity (View view) {
        Intent intent_place_activity = new Intent(MainActivityWithProvider.this, PlaceActivity.class);
        intent_place_activity.putExtra(USRID, userId);
        intent_place_activity.putExtra(GOOGLEPLACESID, googlePlacesId);
        startActivity(intent_place_activity);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(args != null){
            Uri uri = AvBProviderTest.PLACE_CONTENT_URI;
            return new CursorLoader(MainActivityWithProvider.this, uri, null, null, new String[]{args.getString("query","")}, null);
        }else {
            Uri uri = AvBProviderTest.PLACE_CONTENT_URI;
            return new CursorLoader(MainActivityWithProvider.this, uri, null, null, null, null);
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
}
