package org.avaliabrasil.avaliabrasil.avb;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.fragments.main.PlacesListFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.main.PlacesMapFragment;
import org.avaliabrasil.avaliabrasil.data.AvBContract;
import org.avaliabrasil.avaliabrasil.data.AvBDBHelper;
import org.avaliabrasil.avaliabrasil.data.AvaliaBrasilApplication;
import org.avaliabrasil.avaliabrasil.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil.rest.GooglePlacesAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Holder;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Instrument;
import org.avaliabrasil.avaliabrasil.rest.javabeans.User;
import org.avaliabrasil.avaliabrasil.sync.Constant;
import org.avaliabrasil.avaliabrasil.sync.Observer;
import org.avaliabrasil.avaliabrasil.util.CircleTransform;
import org.avaliabrasil.avaliabrasil.util.LocationPermission;
import org.avaliabrasil.avaliabrasil.util.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>, LocationListener {

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

    /**
     *
     */
    private LocationPermission locationPermission;

    /**
     *
     */
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            locationPermission = new LocationPermission(this);
            new Loading().execute();
        }
    }

    /**
     * Method use to fetch the data from the google api every time that the activity is started.
     */
    private void fetchDataFromGoogleAPI() {
        if (location == null) {
            return;
        }

        progress = ProgressDialog.show(this, getResources().getString(R.string.progress_dialog_title),
                getResources().getString(R.string.progress_dialog_message), true);

        GooglePlacesAPIClient.getNearlyPlaces(MainActivity.this, location);

        progress.dismiss();
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
        int id = item.getItemId();

        switch (id) {
            case R.id.btnClassification:

                Intent rankingIntent = new Intent(MainActivity.this, RankingActivity.class);
                rankingIntent.putExtra("latitude", location != null ? location.getLatitude() : 0);
                rankingIntent.putExtra("longitude", location != null ? location.getLongitude() : 0);


                startActivity(rankingIntent);
                break;
            case R.id.btnLogout:

                progress = ProgressDialog.show(this, getResources().getString(R.string.progress_dialog_title),
                        getResources().getString(R.string.progress_dialog_message), true);

                progress.show();

                if (!FacebookSdk.isInitialized()) {
                    FacebookSdk.sdkInitialize(MainActivity.this);
                }

                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut();
                }
                for (Account c : manager.getAccountsByType(Constant.ACCOUNT_TYPE)) {
                    manager.removeAccount(c, null, null);
                }

                AvBDBHelper helper = new AvBDBHelper(MainActivity.this);
                helper.clearAllData(helper.getWritableDatabase());

                Bitmap photo = Utils.getImageBitmap(MainActivity.this);

                progress.dismiss();

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


                break;
            case R.id.btnHelp:
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
                break;
            case R.id.btnTermsOfUse:
                startActivity(new Intent(MainActivity.this, TermsOfUseActivity.class));
                break;
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

        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery("", false);
                getSupportLoaderManager().restartLoader(0, null, MainActivity.this);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                GooglePlacesAPIClient.getPlacesByName(MainActivity.this, MainActivity.this, location, query);

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

    @Override
    public void onLocationChanged(Location providerLocation) {

        Toast.makeText(MainActivity.this, getResources().getString(R.string.fetching_provider_location), Toast.LENGTH_LONG).show();

        if (providerLocation != null) {
            if (Utils.isBetterLocation(providerLocation, location)) {
                location = providerLocation;

                this.location = providerLocation;

                ((AvaliaBrasilApplication) getApplication()).setLocation(location);

                fetchDataFromGoogleAPI();

                getSupportLoaderManager().restartLoader(0, null, MainActivity.this);

                try {
                    locationManager.removeUpdates(MainActivity.this);
                } catch (SecurityException e) {

                }
            }
        }
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
                    PlacesListFragment placesListFragmentWithProvider = PlacesListFragment.newInstance(position + 1, location);

                    observerStack.add(placesListFragmentWithProvider);

                    return placesListFragmentWithProvider;

                case 1:

                    PlacesMapFragment placesMapFragmentWithProvider = PlacesMapFragment.newInstance(position + 1, location);

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
        if (args != null) {
            Uri uri = AvBContract.PlaceEntry.PLACE_URI;
            return new CursorLoader(MainActivity.this, uri, null, null, new String[]{args.getString("query", "")}, null);
        } else {
            Uri uri = AvBContract.PlaceEntry.PLACE_URI;
            return new CursorLoader(MainActivity.this, uri, null, null, null, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        for (Observer ob : observerStack) {
            ob.update(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        for (Observer ob : observerStack) {
            ob.update(null);
        }
    }

    private boolean checkIfThereIsPendingSurvey() {
        Cursor c = getContentResolver().query(AvBContract.SurveyEntry.SURVEY_URI, null, AvBContract.SurveyEntry.SURVEY_FINISHED + " = ?", new String[]{"false"}, "_id desc");
        return c.getCount() > 0;
    }

    private String getPlaceNameByPlaceId(String place_id) {
        Cursor c = getContentResolver().query(AvBContract.PlaceEntry.getPlaceDetails(place_id), null, null, null, null);
        c.moveToNext();
        return c.getString(c.getColumnIndex("name"));
    }


    private void syncAnwsers() {
        Cursor c = getContentResolver().query(AvBContract.SurveyEntry.SURVEY_URI, new String[]{AvBContract.SurveyEntry.PLACE_ID}, null, null, null);

        ArrayList<String> ids = new ArrayList<>();

        while (c.moveToNext()) {
            if (!ids.contains(c.getString(c.getColumnIndex(AvBContract.SurveyEntry.PLACE_ID)))) {
                ids.add(c.getString(c.getColumnIndex(AvBContract.SurveyEntry.PLACE_ID)));
            }
        }

        for (final String place_id : ids) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AvaliaBrasilAPIClient.postAnwsers(place_id),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            getContentResolver().delete(AvBContract.NewPlaceEntry.NEWPLACE_URI, AvBContract.NewPlaceEntry.PLACE_ID + " = ?", new String[]{place_id});
                            getContentResolver().delete(AvBContract.SurveyEntry.SURVEY_URI, AvBContract.SurveyEntry.PLACE_ID + " = ?", new String[]{place_id});

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    //TODO ADD THE USER TOKEN
                    JsonObject response = new JsonObject();

                    response.addProperty("userID", "");

                    JsonArray anwserArray = new JsonArray();

                    Cursor c = getContentResolver().query(AvBContract.SurveyEntry.SURVEY_URI, null, AvBContract.SurveyEntry.PLACE_ID + " = ?", new String[]{place_id}, "_id asc");

                    while (c.moveToNext()) {
                        JsonObject obj = new JsonObject();

                        obj.addProperty("question_id", c.getString(c.getColumnIndex(AvBContract.SurveyEntry.QUESTION_ID)));

                        JsonArray anwsers = new JsonArray();

                        String type = c.getString(c.getColumnIndex(AvBContract.SurveyEntry.QUESTION_TYPE));

                        JsonObject anwser = new JsonObject();

                        if (type.contains("number")) {
                            anwser.addProperty("number", c.getString(c.getColumnIndex(AvBContract.SurveyEntry.ANWSER)));
                            anwser.addProperty("likert", "");
                            anwser.addProperty("comment", "");

                        } else if (type.contains("comment")) {
                            anwser.addProperty("comment", c.getString(c.getColumnIndex(AvBContract.SurveyEntry.ANWSER)));
                            anwser.addProperty("likert", "");
                            anwser.addProperty("number", "");

                        } else if (type.contains("likert")) {
                            anwser.addProperty("likert", c.getString(c.getColumnIndex(AvBContract.SurveyEntry.ANWSER)));
                            anwser.addProperty("number", "");
                            anwser.addProperty("comment", "");
                        }

                        anwsers.add(anwser);

                        obj.add("answer", anwsers);

                        anwserArray.add(obj);
                    }
                    return params;
                }
            };
            Volley.newRequestQueue(MainActivity.this).add(stringRequest);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LocationPermission.INITIAL_REQUEST:
                if (locationPermission.canAccessFineLocation() && locationPermission.canAccessCoarseLocation()) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("showSplash", false);
                    startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.location_access_needed), Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    /**
     * {@link AsyncTask} class to act has a splash screen
     */
    private class Loading extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                syncAnwsers();
                getContentResolver().delete(AvBContract.NewPlaceEntry.NEWPLACE_URI, null, null);
                //getContentResolver().delete(AvBContract.QuestionEntry.QUESTION_URI,null,null);

                if (getIntent().getExtras() != null) {
                    if (getIntent().getExtras().getBoolean("showSplash", true)) {
                        Thread.sleep(3000);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            if (locationPermission.canAccessFineLocation() && locationPermission.canAccessCoarseLocation()) {
                user = ((AvaliaBrasilApplication) getApplication()).getUser();
                manager = AccountManager.get(MainActivity.this);

                if (manager.getAccountsByType(Constant.ACCOUNT_TYPE).length == 0) {
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
                } else {
                    setContentView(R.layout.activity_main);

                    // Add Toolbar
                    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);

                    // Add Drawer
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                            MainActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                    drawer.addDrawerListener(toggle);
                    toggle.syncState();

                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    navigationView.setNavigationItemSelectedListener(MainActivity.this);

                    // Instruções para criar o Section Page!!
                    // Create the adapter that will return a fragment for each of the three
                    // primary sections of the activity.

                    try {
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                        if (!locationManager
                                .isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                            Utils.showGPSDisabledAlertToUser(MainActivity.this);
                        } else {

                            Location providerLocation;

                            for (String provider : locationManager.getAllProviders()) {

                                providerLocation = locationManager.getLastKnownLocation(provider);

                                Log.e("Location", "location " + provider + " in provider is " + (providerLocation == null ? "null" : "not null"));

                                if (providerLocation != null) {
                                    if (Utils.isBetterLocation(providerLocation, location)) {
                                        location = providerLocation;

                                        ((AvaliaBrasilApplication) getApplication()).setLocation(location);
                                    }
                                }
                            }
                        }

                        if (location == null) {
                            Log.e("Location", "location is null");
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, MainActivity.this);
                            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, MainActivity.this);
                        }
                    } catch (SecurityException e) {
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

                    String name = manager.getUserData(manager.getAccountsByType(Constant.ACCOUNT_TYPE)[0], AccountManager.KEY_ACCOUNT_NAME);
                    Bitmap photo = Utils.getImageBitmap(MainActivity.this);

                    TextView tvName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvName);
                    ImageView ivProfilePhoto = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ivProfilePhoto);
                    tvName.setText(name);
                    if (photo != null) {
                        ivProfilePhoto.setImageBitmap(new CircleTransform().transform(photo));
                    }

                    if (checkIfThereIsPendingSurvey()) {


                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Cursor c;
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:

                                        c = getContentResolver().query(AvBContract.SurveyEntry.SURVEY_URI, null, AvBContract.SurveyEntry.SURVEY_FINISHED + " = ?", new String[]{"false"}, "_id desc");

                                        c.moveToNext();

                                        String place_id = c.getString(c.getColumnIndex(AvBContract.SurveyEntry.PLACE_ID));

                                        Intent intent = new Intent(MainActivity.this, EvaluationActivity.class);

                                        intent.putExtra("placeid", place_id);

                                        intent.putExtra("name", getPlaceNameByPlaceId(place_id));

                                        Holder holder = new Holder();

                                        ArrayList<String> ids = new ArrayList<String>();

                                        c = getContentResolver().query(AvBContract.InstrumentEntry.buildInstrumentUri(place_id), null, null, null, null);

                                        List<Instrument> instruments = new ArrayList<Instrument>();

                                        //Log.e("PlaceActivity", DatabaseUtils.dumpCursorToString(c));

                                        while (c.moveToNext()) {
                                            ids.add(c.getString(c.getColumnIndex(AvBContract.InstrumentEntry.INSTRUMENT_ID)));
                                        }

                                        for (String id : ids) {
                                            c = getContentResolver().query(AvBContract.GroupQuestionEntry.buildGroupQuestionsUri(id), null, null, null, null);

                                            instruments.add(new Instrument(id, c));
                                        }

                                        holder.setInstruments(instruments);

                                        intent.putExtra("holder", (Serializable) holder);
                                        intent.putExtra("pendingSurvey", true);
                                        startActivity(intent);

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:

                                        c = getContentResolver().query(AvBContract.SurveyEntry.SURVEY_URI, null, AvBContract.SurveyEntry.SURVEY_FINISHED + " = ?", new String[]{"false"}, "_id desc");

                                        c.moveToNext();

                                        getContentResolver().delete(AvBContract.NewPlaceEntry.NEWPLACE_URI, AvBContract.NewPlaceEntry.PLACE_ID + " = ?", new String[]{c.getString(c.getColumnIndex(AvBContract.SurveyEntry.PLACE_ID))});
                                        getContentResolver().delete(AvBContract.SurveyEntry.SURVEY_URI, AvBContract.SurveyEntry.SURVEY_FINISHED + " = ?", new String[]{"false"});

                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(getResources().getString(R.string.evaluation_not_completed)).setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                                .setNegativeButton(getResources().getString(R.string.no), dialogClickListener).show();
                    }
                }
            } else {
                locationPermission.checkForPermissions(this.getClass());
            }
        }

        @Override
        protected void onPreExecute() {
            try {
                ImageView view = new ImageView(MainActivity.this);
                view.setImageResource(R.drawable.retangular_logo);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT
                        , ViewGroup.LayoutParams.FILL_PARENT);
                view.setLayoutParams(layoutParams);
                setContentView(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
