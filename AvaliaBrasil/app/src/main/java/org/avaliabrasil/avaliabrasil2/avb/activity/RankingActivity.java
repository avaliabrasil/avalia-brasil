package org.avaliabrasil.avaliabrasil2.avb.activity;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.avaliabrasil.avaliabrasil2.R;
import org.avaliabrasil.avaliabrasil2.avb.adapters.CategoryCursorAdapter;
import org.avaliabrasil.avaliabrasil2.avb.adapters.DividerItemDecoration;
import org.avaliabrasil.avaliabrasil2.avb.adapters.LocationAdapter;
import org.avaliabrasil.avaliabrasil2.avb.dao.LocationDAO;
import org.avaliabrasil.avaliabrasil2.avb.factory.LocationFactory;
import org.avaliabrasil.avaliabrasil2.avb.factory.LocationFactoryImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.LocationDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.LocationDAOTestImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.NavigatorViewImpl;
import org.avaliabrasil.avaliabrasil2.avb.adapters.PlaceRankingAdapter;
import org.avaliabrasil.avaliabrasil2.avb.adapters.PlaceTypeCursorAdapter;
import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.Location;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.LocationType;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.PlaceRanking;
import org.avaliabrasil.avaliabrasil2.avb.mvp.RankingActivityPresenter;
import org.avaliabrasil.avaliabrasil2.avb.util.AvaliaBrasilApplication;
import org.avaliabrasil.avaliabrasil2.avb.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.PlaceRankingSearch;
import org.avaliabrasil.avaliabrasil2.avb.sync.Constant;
import org.avaliabrasil.avaliabrasil2.avb.util.CircleTransform;
import org.avaliabrasil.avaliabrasil2.avb.util.Utils;
import org.avaliabrasil.avaliabrasil2.avb.view.DelayAutoCompleteTextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RankingActivity extends AppCompatActivity implements RankingActivityPresenter {

    /**
     *
     */
    private RecyclerView rvRankingList;

    /**
     * The {@link AccountManager} for getting user informations.
     */
    private AccountManager manager;

    /**
     *
     */
    private DelayAutoCompleteTextView actvPlace;

    /**
     *
     */
    private Spinner spCategory;

    /**
     *
     */
    private Spinner spPlaceType;

    /**
     *
     */
    private CategoryCursorAdapter categoryCursorAdapter;

    /**
     *
     */
    private PlaceTypeCursorAdapter placeTypeCursorAdapter;

    /**
     *
     */
    private Geocoder geocoder;

    /**
     *
     */
    private ProgressDialog progress;

    private LocationAdapter locationAdapter;


    private LocationDAO locationDAO;
    private LocationFactory locationFactory;

    boolean isFirstSelected = true;

    private Location loc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ranking);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Classificação de Instituições");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        locationFactory = new LocationFactoryImpl(RankingActivity.this);
        locationDAO = new LocationDAOImpl(RankingActivity.this,locationFactory);

        manager = AccountManager.get(RankingActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                RankingActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigatorViewImpl(RankingActivity.this,(AvaliaBrasilApplication)getApplication(),manager));

        String name = manager.getUserData(manager.getAccountsByType(Constant.ACCOUNT_TYPE)[0], AccountManager.KEY_ACCOUNT_NAME);
        Bitmap photo = Utils.getImageBitmap(RankingActivity.this);

        TextView tvName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvName);
        ImageView ivProfilePhoto = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ivProfilePhoto);
        tvName.setText(name);

        if (photo != null) {
            ivProfilePhoto.setImageBitmap(new CircleTransform().transform(photo));
        }

        actvPlace = (DelayAutoCompleteTextView) findViewById(R.id.actvPlace);

        locationAdapter = new LocationAdapter(RankingActivity.this,locationDAO);

        actvPlace.setAdapter(locationAdapter);

        actvPlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (actvPlace != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(actvPlace.getWindowToken(), 0);
                }
                sendFilter(position);
             }
        });

        rvRankingList = (RecyclerView) findViewById(R.id.rvRankingList);

        rvRankingList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(RankingActivity.this);

        rvRankingList.setLayoutManager(mLayoutManager);

        rvRankingList.setItemAnimator(new DefaultItemAnimator());

        spCategory = (Spinner) findViewById(R.id.spCategory);

        Cursor query = getContentResolver().query(AvBContract.PlaceCategoryEntry.PLACE_CATEGORY_URI, null, null, null, null);
        categoryCursorAdapter = new CategoryCursorAdapter(RankingActivity.this, query);
        query.moveToFirst();
        spCategory.setAdapter(categoryCursorAdapter);

        spPlaceType = (Spinner) findViewById(R.id.spPlaceType);

        actvPlace.setText("Brasil");

        if (getIntent().hasExtra("rankingType")) {
            getIntentInfo();
        } else {
            if (getIntent().getExtras().getDouble("latitude") != 0) {
                try {
                    List<Address> addresses = null;
                    geocoder = new Geocoder(this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(getIntent().getExtras().getDouble("latitude"), getIntent().getExtras().getDouble("longitude"), 5);
                    if(addresses.size() > 0){
                        List<Location> locations = locationDAO.findLocationByName(addresses.get(0).getAdminArea());

                        if(locations.size() > 0){
                            loc = locations.get(0);
                            actvPlace.setText(loc.toString());
                            sendFilter(-1);
                        }else{
                            requestRankingUpdate(null);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                requestRankingUpdate(null);
            }
        }
    }

    private void sendFilter(int position) {
        if(position >= 0){
            loc = locationAdapter.getLocation(position);
        }

        HashMap<String, String> params = new HashMap<>();

        if(loc != null){
            switch(loc.getLocationType()){
                case COUNTRY:
                    params.put("idCountry", loc.getId());
                    break;
                case REGION:
                    params.put("idRegion", loc.getId());
                    break;
                case STATE:
                    params.put("idState", loc.getId());
                    break;
                case CITY:
                    params.put("idCity", loc.getId());
                    break;
            }
        }

        String category = ((TextView)spCategory.getSelectedView()).getText().toString();
        String placeType = "Todos";

        if(!(spPlaceType.getSelectedView() == null)){
            placeType = ((TextView)spPlaceType.getSelectedView()).getText().toString();
        }

        if(!(category.contains("Todos"))){
            Cursor cur = (Cursor) categoryCursorAdapter.getItem(spCategory.getSelectedItemPosition());
            params.put("idCategory",cur.getString(cur.getColumnIndex("category_id")));
            if(!placeType.contains(("Todos"))){
                cur = (Cursor) placeTypeCursorAdapter.getItem(spPlaceType.getSelectedItemPosition());
                params.put("idType",cur.getString(cur.getColumnIndex("_id")));
            }
        }
        requestRankingUpdate(params);
    }

    @Override
    protected void onResume() {
        super.onResume();

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if(!Utils.checkIfIsNullOrEmpty(lastSettedOption)){
                    if(selectedItemView == null){
                        Cursor cur = (Cursor) categoryCursorAdapter.getItem(position);
                        cur.moveToPosition(position);
                        placeTypeCursorAdapter = new PlaceTypeCursorAdapter(RankingActivity.this, getContentResolver().query(AvBContract.PlaceTypeEntry.buildPlaceTypeUri(cur.getString(cur.getColumnIndex(AvBContract.PlaceCategoryEntry.CATEGORY_ID))), null, null, null, null));
                        spPlaceType.setAdapter(placeTypeCursorAdapter);
                        sendFilter(-1);
                    }else if(!((TextView)selectedItemView).getText().toString().contains(lastSettedOption)){
                        Cursor cur = (Cursor) categoryCursorAdapter.getItem(position);
                        cur.moveToPosition(position);
                        placeTypeCursorAdapter = new PlaceTypeCursorAdapter(RankingActivity.this, getContentResolver().query(AvBContract.PlaceTypeEntry.buildPlaceTypeUri(cur.getString(cur.getColumnIndex(AvBContract.PlaceCategoryEntry.CATEGORY_ID))), null, null, null, null));
                        spPlaceType.setAdapter(placeTypeCursorAdapter);
                    }
                }else{
                    Cursor cur = (Cursor) categoryCursorAdapter.getItem(position);
                    cur.moveToPosition(position);
                    placeTypeCursorAdapter = new PlaceTypeCursorAdapter(RankingActivity.this, getContentResolver().query(AvBContract.PlaceTypeEntry.buildPlaceTypeUri(cur.getString(cur.getColumnIndex(AvBContract.PlaceCategoryEntry.CATEGORY_ID))), null, null, null, null));
                    spPlaceType.setAdapter(placeTypeCursorAdapter);
                    sendFilter(-1);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        spPlaceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                sendFilter(-1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
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

    private String lastSettedOption = "";

    private void getIntentInfo() {
        /*String name = getIntent().getExtras().getString("name", "");
        String city = getIntent().getExtras().getString("city", "");
        String state = getIntent().getExtras().getString("state", "");*/

        String category = getIntent().getExtras().getString("category", "");
        String placeType = getIntent().getExtras().getString("type", "");
        String rankingType = getIntent().getExtras().getString("rankingType", "");
        String webId = getIntent().getExtras().getString("webId", "");

        Cursor cur = categoryCursorAdapter.getCursor();
        final Cursor placeTypeCursor;

        HashMap<String, String> params = new HashMap<>();

        while (cur.moveToNext()) {
            if (cur.getString(cur.getColumnIndex(AvBContract.PlaceCategoryEntry.NAME)).contains(category)) {
                Log.d("RankingActivity", "setted category: " + cur.getString(cur.getColumnIndex(AvBContract.PlaceCategoryEntry.NAME)));
                params.put("idCategory",  cur.getString(cur.getColumnIndex(AvBContract.PlaceCategoryEntry.CATEGORY_ID)));
                lastSettedOption = cur.getString(cur.getColumnIndex(AvBContract.PlaceCategoryEntry.NAME));
                spCategory.setSelection(cur.getPosition());

                placeTypeCursor = getContentResolver().query(AvBContract.PlaceTypeEntry.buildPlaceTypeUri(cur.getString(cur.getColumnIndex(AvBContract.PlaceCategoryEntry.CATEGORY_ID))), null, null, null, null);

                placeTypeCursorAdapter = new PlaceTypeCursorAdapter(RankingActivity.this,placeTypeCursor );

                spPlaceType.setAdapter(placeTypeCursorAdapter);

                Log.d("RankingActivity", "PlaceType: " + placeType);
                while (placeTypeCursor.moveToNext()) {
                    Log.d("RankingActivity", "setted placeType: " + placeTypeCursor.getString(placeTypeCursor.getColumnIndex(AvBContract.PlaceCategoryEntry.NAME)));


                    if (placeTypeCursor.getString(placeTypeCursor.getColumnIndex(AvBContract.PlaceCategoryEntry.NAME)).contains(placeType)) {
                        Log.d("RankingActivity", "position cursor: " + placeTypeCursor.getPosition());
                        spPlaceType.setSelection(placeTypeCursor.getPosition());
                        break;
                    }
                }
                break;
            }
        };

            switch (rankingType) {

                case "nacional":
                    loc = locationDAO.findLocationByWebID(webId, LocationType.COUNTRY);
                    params.put("idCountry", loc.getId());
                   actvPlace.setText("Brasil");
                    break;
                case "regional":
                    loc = locationDAO.findLocationByWebID(webId, LocationType.REGION);
                    params.put("idRegion", loc.getId());
                    actvPlace.setText(loc.getLocation());
                    break;
                case "estadual":
                    loc = locationDAO.findLocationByWebID(webId, LocationType.STATE);
                    params.put("idState", loc.getId());
                    actvPlace.setText(loc.getLocation());
                    break;
                case "municipal":
                    loc = locationDAO.findLocationByWebID(webId, LocationType.CITY);
                    params.put("idCity", loc.getId());
                    actvPlace.setText(loc.getLocation());

                    break;

            }


        requestRankingUpdate(params);
    }

    private void requestRankingUpdate(final HashMap<String, String> params) {
         /* -------------------------------------------------------------------------------------------------------------- */

        Log.d("RankingActivity", "Sending data....");

        if(progress != null){
            if(!progress.isShowing()){
                fetchFromDb(params);

            }
        }else{
            fetchFromDb(params);
        }
    }

    private void fetchFromDb(final HashMap<String, String> params) {
        progress = ProgressDialog.show(this, getResources().getString(R.string.progress_dialog_title),
                getResources().getString(R.string.progress_dialog_message), true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AvaliaBrasilAPIClient.getPlacesRanking(params),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fetchData(response);
                        progress.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                fetchData("[]");
                progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> body;
                if (params == null) {
                    body = new HashMap<String, String>();
                } else {
                    body = params;
                }
                return body;
            }
        };

        Volley.newRequestQueue(RankingActivity.this).add(stringRequest);
    }

    private void fetchData(String response) {
        Gson gson = new Gson();
        Log.d("RankingActivity", "Response: " + response);
        if(!response.contentEquals("[]")){
            PlaceRankingSearch placeRanking = gson.fromJson(response, PlaceRankingSearch.class);
            rvRankingList.setAdapter(new PlaceRankingAdapter(rvRankingList,RankingActivity.this, placeRanking.getPlaceRankings(),RankingActivity.this));
            rvRankingList.setHasFixedSize(true);
        }else{
            rvRankingList.setAdapter(new PlaceRankingAdapter(rvRankingList,RankingActivity.this, null,RankingActivity.this));
            rvRankingList.setHasFixedSize(true);
        }
    }

    @Override
    public void OnRankingClickListener(PlaceRanking placeRanking) {
        Intent intent = new Intent(this,PlaceStatisticsActivity.class);
        intent.putExtra("placeid",placeRanking.getPlaceId());
        intent.putExtra("name",placeRanking.getName());
        startActivity(intent);
    }
}
