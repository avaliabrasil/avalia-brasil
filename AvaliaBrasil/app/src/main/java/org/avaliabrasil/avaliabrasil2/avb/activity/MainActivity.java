package org.avaliabrasil.avaliabrasil2.avb.activity;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.avaliabrasil.avaliabrasil2.R;
import org.avaliabrasil.avaliabrasil2.avb.dao.PlaceCategoryDAO;
import org.avaliabrasil.avaliabrasil2.avb.dao.PlaceTypeDAO;
import org.avaliabrasil.avaliabrasil2.avb.impl.NavigatorViewImpl;
import org.avaliabrasil.avaliabrasil2.avb.fragments.main.PlacesListFragment;
import org.avaliabrasil.avaliabrasil2.avb.fragments.main.PlacesMapFragment;
import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil2.avb.impl.GroupQuestionDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.InstrumentDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.PlaceCategoryDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.PlaceTypeDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.QuestionDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.AvaliaBrasilCategory;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.AvaliaBrasilPlaceType;
import org.avaliabrasil.avaliabrasil2.avb.sync.ServiceAnwserSync;
import org.avaliabrasil.avaliabrasil2.avb.util.AvaliaBrasilApplication;
import org.avaliabrasil.avaliabrasil2.avb.dao.PlaceDetailsDAO;
import org.avaliabrasil.avaliabrasil2.avb.impl.PlaceDetailsDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.dao.SurveyDAO;
import org.avaliabrasil.avaliabrasil2.avb.impl.AnwserDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.NewPlaceDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.SurveyDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.rest.GooglePlacesAPIClient;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.Survey;
import org.avaliabrasil.avaliabrasil2.avb.gps.GPSService;
import org.avaliabrasil.avaliabrasil2.avb.sync.Constant;
import org.avaliabrasil.avaliabrasil2.avb.sync.Observer;
import org.avaliabrasil.avaliabrasil2.avb.util.CircleTransform;
import org.avaliabrasil.avaliabrasil2.avb.util.LocationPermission;
import org.avaliabrasil.avaliabrasil2.avb.util.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    /**
     * For notify the fragment children's.
     */
    public Stack<Observer> observerStack = new Stack<Observer>();

    /**
     * The {@link AccountManager} for getting user informations.
     */
    private AccountManager manager;

    private LocationPermission locationPermission;

    private AvaliaBrasilApplication avaliaBrasilApplication;

    private SurveyDAO surveyDAO;

    private PlaceDetailsDAO placeDetailsDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            locationPermission = new LocationPermission(this);
            avaliaBrasilApplication = (AvaliaBrasilApplication)getApplication();
            surveyDAO = new SurveyDAOImpl(MainActivity.this,new InstrumentDAOImpl(MainActivity.this,
                    new GroupQuestionDAOImpl(MainActivity.this)),
                    new GroupQuestionDAOImpl(MainActivity.this),
                    new QuestionDAOImpl(MainActivity.this),
                    new NewPlaceDAOImpl(MainActivity.this),
                    new AnwserDAOImpl(MainActivity.this));
            placeDetailsDAO = new PlaceDetailsDAOImpl(MainActivity.this);
            new Loading().execute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationService();
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
                GooglePlacesAPIClient.getPlacesByName(MainActivity.this, MainActivity.this, avaliaBrasilApplication.getLocation(), query);

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

    private void createSyncAnwserService(){
        Intent startSyncAnwserService = new Intent(MainActivity.this, ServiceAnwserSync.class);
        startService(startSyncAnwserService);
    }

    private void createLocationService() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Utils.showGPSDisabledAlertToUser(this);
            }
        Intent startLocationService = new Intent(MainActivity.this, GPSService.class);
        startService(startLocationService);
    }

    private void stopLocationService() {
        Intent stopLocationService = new Intent(MainActivity.this, GPSService.class);
        stopService(stopLocationService);
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigatorViewImpl(MainActivity.this,(AvaliaBrasilApplication)getApplication(),manager));

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.view_page_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.search_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        getSupportLoaderManager().initLoader(0, null, MainActivity.this);

        String name = manager.getUserData(manager.getAccountsByType(Constant.ACCOUNT_TYPE)[0], AccountManager.KEY_ACCOUNT_NAME);
        Bitmap photo = Utils.getImageBitmap(MainActivity.this);

        TextView tvName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvName);
        ImageView ivProfilePhoto = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ivProfilePhoto);
        tvName.setText(name);
        if (photo != null) {
            ivProfilePhoto.setImageBitmap(new CircleTransform().transform(photo));
        }
    }

    private void checkIfThereIsPendingSurvey() {
        if (surveyDAO.checkIfThereIsPendingSurvey()) {
            final Survey survey = surveyDAO.findPendingSurvey();

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:

                            Intent intent = new Intent(MainActivity.this, EvaluationActivity.class);


                            if(survey == null){
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.evaluation_not_completed_error), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            intent.putExtra("placeid", survey.getPlaceId());
                            intent.putExtra("name", placeDetailsDAO.getNameByPlaceId(survey.getPlaceId()));
                            intent.putExtra("holder", (Serializable) survey);
                            intent.putExtra("pendingSurvey", true);
                            startActivity(intent);

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            surveyDAO.removeSurvey(survey);

                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(getResources().getString(R.string.evaluation_not_completed)).setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(getResources().getString(R.string.no), dialogClickListener).show();
        }
    }

    private List<AvaliaBrasilCategory> getPlaceCategories(){
        List<AvaliaBrasilCategory> avaliaBrasilCategoryList = new ArrayList<>();
        avaliaBrasilCategoryList.add(new AvaliaBrasilCategory("1","Saúde"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilCategory("2","Educação"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilCategory("3","Segurança"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilCategory("4","Transporte"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilCategory("5","Poder Judiciário"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilCategory("6","Poder Legislativo"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilCategory("7","Poder Executivo"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilCategory("8","Outro"));
        return avaliaBrasilCategoryList;
    }

    private List<AvaliaBrasilPlaceType> getAvaliaBrasilPlaceTypes(){
        List<AvaliaBrasilPlaceType> avaliaBrasilCategoryList = new ArrayList<>();
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("101","Posto de Saúde","1"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("102","Unidade Básica de Saúde","1"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("103","Policlínica","1"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("104","Hospital","1"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("105","Unidade Mista","1"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("106","Pronto Socorro","1"));

        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("107","Creche","2"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("108","Escola","2"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("109","Instituição de Ensino Superior","2"));

        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("110","Polícia Municipal","3"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("111","Polícia Militar","3"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("112","Polícia Civil","3"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("113","Polícia Federal","3"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("114","Quartel Militar","3"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("115","Exercíto Brasileiro","3"));

        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("116","Rodoviária","4"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("117","Aeroporto","4"));

        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("118","Tabelionato","5"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("119","Fórum Municipal","5"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("120","Fórum Estadual","5"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("121","Fórum Federal","5"));

        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("122","Câmara de Vereadores","6"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("123","Câmara de Deputados","6"));

        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("124","Prefeitura Municipal","7"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("125","Governo Estadual","7"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("126","Planalto Federal","7"));
        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("127","Ministério Federal","7"));

        avaliaBrasilCategoryList.add(new AvaliaBrasilPlaceType("128","Outro","8"));

        return avaliaBrasilCategoryList;
    }

    /**
     * {@link AsyncTask} class to act as an splash screen
     */
    private class Loading extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //getContentResolver().delete(AvBContract.NewPlaceEntry.NEWPLACE_URI, null, null);
                //getContentResolver().delete(AvBContract.QuestionEntry.QUESTION_URI,null,null);

                if (getIntent().getExtras() != null) {
                    if (getIntent().getExtras().getBoolean("showSplash", true)) {
                        Thread.sleep(3000);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            PlaceCategoryDAO placeCategoryDAO = new PlaceCategoryDAOImpl(MainActivity.this);
            PlaceTypeDAO placeTypeDAO = new PlaceTypeDAOImpl(MainActivity.this);
            placeCategoryDAO.bulkInsertPlaceCategory(getPlaceCategories());
            placeTypeDAO.bulkInsertPlaceType(getAvaliaBrasilPlaceTypes());
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            if (locationPermission.canAccessFineLocation() && locationPermission.canAccessCoarseLocation()) {
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

                    initUI();
                    createLocationService();
                    createSyncAnwserService();
                    checkIfThereIsPendingSurvey();
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

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    PlacesListFragment placesListFragmentWithProvider = PlacesListFragment.newInstance(position + 1, avaliaBrasilApplication.getLocation());

                    observerStack.add(placesListFragmentWithProvider);

                    return placesListFragmentWithProvider;

                case 1:

                    PlacesMapFragment placesMapFragmentWithProvider = PlacesMapFragment.newInstance(position + 1, avaliaBrasilApplication.getLocation());

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
}
