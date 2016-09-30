package org.avaliabrasil.avaliabrasil.avb.activity;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
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
import android.view.View;
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

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.adapters.CategoryCursorAdapter;
import org.avaliabrasil.avaliabrasil.avb.adapters.DividerItemDecoration;
import org.avaliabrasil.avaliabrasil.avb.adapters.LocationAdapter;
import org.avaliabrasil.avaliabrasil.avb.dao.LocationDAO;
import org.avaliabrasil.avaliabrasil.avb.impl.LocationDAOTestImpl;
import org.avaliabrasil.avaliabrasil.avb.impl.NavigatorViewImpl;
import org.avaliabrasil.avaliabrasil.avb.adapters.PlaceRankingAdapter;
import org.avaliabrasil.avaliabrasil.avb.adapters.PlaceTypeCursorAdapter;
import org.avaliabrasil.avaliabrasil.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil.avb.javabeans.ranking.Location;
import org.avaliabrasil.avaliabrasil.avb.util.AvaliaBrasilApplication;
import org.avaliabrasil.avaliabrasil.avb.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil.avb.javabeans.ranking.PlaceRankingSearch;
import org.avaliabrasil.avaliabrasil.avb.sync.Constant;
import org.avaliabrasil.avaliabrasil.avb.util.CircleTransform;
import org.avaliabrasil.avaliabrasil.avb.util.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RankingActivity extends AppCompatActivity{

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
    private AutoCompleteTextView actvPlace;

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


    private LocationDAOTestImpl locationDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ranking);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Classificação de Instituições");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        locationDAO = new LocationDAOTestImpl();

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

        actvPlace = (AutoCompleteTextView) findViewById(R.id.actvPlace);

        locationAdapter = new LocationAdapter(RankingActivity.this,locationDAO);

        actvPlace.setAdapter(locationAdapter);

        actvPlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Location clickedLocation = locationAdapter.getLocation(position);

                System.out.println(clickedLocation);

                Toast.makeText(RankingActivity.this, "clicado em: " + (clickedLocation == null ? "nulo": clickedLocation.toString()), Toast.LENGTH_SHORT).show();
            }
        });

        rvRankingList = (RecyclerView) findViewById(R.id.rvRankingList);

        rvRankingList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(RankingActivity.this);

        rvRankingList.setLayoutManager(mLayoutManager);

        rvRankingList.setItemAnimator(new DefaultItemAnimator());

        spCategory = (Spinner) findViewById(R.id.spCategory);

        categoryCursorAdapter = new CategoryCursorAdapter(RankingActivity.this, getContentResolver().query(AvBContract.PlaceCategoryEntry.PLACE_CATEGORY_URI, null, null, null, null));

        spCategory.setAdapter(categoryCursorAdapter);

        spPlaceType = (Spinner) findViewById(R.id.spPlaceType);

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Cursor cur = (Cursor) categoryCursorAdapter.getItem(position);
                cur.moveToPosition(position);

                placeTypeCursorAdapter = new PlaceTypeCursorAdapter(RankingActivity.this, getContentResolver().query(AvBContract.PlaceTypeEntry.buildPlaceTypeUri(cur.getString(cur.getColumnIndex(AvBContract.PlaceCategoryEntry.CATEGORY_ID))), null, null, null, null));

                spPlaceType.setAdapter(placeTypeCursorAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        geocoder = new Geocoder(this, Locale.getDefault());

        if (getIntent().hasExtra("rankingType")) {
            getIntentInfo();
        } else {
            try {
                if (getIntent().getExtras().getDouble("latitude") != 0) {
                    List<Address> addresses = geocoder.getFromLocation(getIntent().getExtras().getDouble("latitude"), getIntent().getExtras().getDouble("longitude"), 5);
                    actvPlace.setText(
                            addresses.get(0).getLocality() + "," + addresses.get(0).getCountryName() + " " + addresses.get(0).getAdminArea());
                }
                requestRankingUpdate(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    private void getIntentInfo() {
        String name = getIntent().getExtras().getString("name", "");
        String city = getIntent().getExtras().getString("city", "");
        String state = getIntent().getExtras().getString("state", "");
        String category = getIntent().getExtras().getString("category", "");
        String placeType = getIntent().getExtras().getString("type", "");
        String rankingType = getIntent().getExtras().getString("rankingType", "");

        actvPlace.setText(name + "," + city + " " + state);

        //TODO set {#link spCategory} by category string
        Cursor cur = categoryCursorAdapter.getCursor();

        while (cur.moveToNext()) {
            if (cur.getString(cur.getColumnIndex(AvBContract.PlaceCategoryEntry.NAME)).contains(category)) {
                spCategory.setSelection(cur.getPosition());

                placeTypeCursorAdapter = new PlaceTypeCursorAdapter(RankingActivity.this, getContentResolver().query(AvBContract.PlaceTypeEntry.buildPlaceTypeUri(cur.getString(cur.getColumnIndex(AvBContract.PlaceCategoryEntry.CATEGORY_ID))), null, null, null, null));

                spPlaceType.setAdapter(placeTypeCursorAdapter);

                while (cur.moveToNext()) {
                    if (cur.getString(cur.getColumnIndex(AvBContract.PlaceCategoryEntry.NAME)).contains(placeType)) {
                        spPlaceType.setSelection(cur.getPosition());
                        break;
                    }
                }
                break;
            }
        }

        //TODO send the rankingType to the query.
        HashMap<String, String> params = new HashMap<>();

        params.put("name", name);

        params.put("city", city);
        params.put("state", state);
        params.put("category", category);
        params.put("type", placeType);
        params.put("rankingType", rankingType);

        requestRankingUpdate(params);
    }

    private void requestRankingUpdate(final HashMap<String, String> params) {
         /* -------------------------------------------------------------------------------------------------------------- */

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
                fetchData("");
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
        PlaceRankingSearch placeRanking = gson.fromJson(response, PlaceRankingSearch.class);

        rvRankingList.setAdapter(new PlaceRankingAdapter(RankingActivity.this, placeRanking.getPlaceRankings()));

        rvRankingList.setHasFixedSize(true);
    }
}
