package org.avaliabrasil.avaliabrasil.avb;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.adapters.CategoryCursorAdapter;
import org.avaliabrasil.avaliabrasil.avb.adapters.DividerItemDecoration;
import org.avaliabrasil.avaliabrasil.avb.adapters.PlaceRankingAdapter;
import org.avaliabrasil.avaliabrasil.avb.adapters.PlaceTypeCursorAdapter;
import org.avaliabrasil.avaliabrasil.data.AvBContract;
import org.avaliabrasil.avaliabrasil.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil.rest.GooglePlacesAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.GoogleGeoCodeReverse;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceRankingSearch;
import org.avaliabrasil.avaliabrasil.sync.Constant;
import org.avaliabrasil.avaliabrasil.util.CircleTransform;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RankingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ranking);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Classificação de Instituições");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        manager = AccountManager.get(RankingActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                RankingActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(RankingActivity.this);

        String name = manager.getUserData(manager.getAccountsByType(Constant.ACCOUNT_TYPE)[0], AccountManager.KEY_ACCOUNT_NAME);
        Bitmap photo = getImageBitmap(RankingActivity.this);

        TextView tvName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvName);
        ImageView ivProfilePhoto = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ivProfilePhoto);
        tvName.setText(name);
        if (photo != null) {
            ivProfilePhoto.setImageBitmap(new CircleTransform().transform(photo));
        }

        actvPlace = (AutoCompleteTextView) findViewById(R.id.actvPlace);

        rvRankingList = (RecyclerView) findViewById(R.id.rvRankingList);

        rvRankingList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(RankingActivity.this);

        rvRankingList.setLayoutManager(mLayoutManager);

        rvRankingList.setItemAnimator(new DefaultItemAnimator());

        spCategory = (Spinner) findViewById(R.id.spCategory);

        categoryCursorAdapter = new CategoryCursorAdapter(RankingActivity.this,getContentResolver().query(AvBContract.PlaceCategoryEntry.PLACE_CATEGORY_URI,null,null,null,null));

        spCategory.setAdapter(categoryCursorAdapter);

        spPlaceType = (Spinner) findViewById(R.id.spPlaceType);

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Cursor cur = (Cursor) categoryCursorAdapter.getItem(position);
                cur.moveToPosition(position);

                placeTypeCursorAdapter = new PlaceTypeCursorAdapter(RankingActivity.this,getContentResolver().query(AvBContract.PlaceTypeEntry.buildPlaceTypeUri(cur.getString(cur.getColumnIndex(AvBContract.PlaceCategoryEntry.CATEGORY_ID))),null,null,null,null));

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
                List<Address> addresses = geocoder.getFromLocation(getIntent().getExtras().getDouble("latitude"),getIntent().getExtras().getDouble("longitude"),5);

                actvPlace.setText(
                        addresses.get(0).getLocality() + "," + addresses.get(0).getCountryName() + " " + addresses.get(0).getAdminArea());
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        while(cur.moveToNext()){
            if(cur.getString(cur.getColumnIndex(AvBContract.PlaceCategoryEntry.NAME)).contains(category)){
                spCategory.setSelection(cur.getPosition());

                placeTypeCursorAdapter = new PlaceTypeCursorAdapter(RankingActivity.this,getContentResolver().query(AvBContract.PlaceTypeEntry.buildPlaceTypeUri(cur.getString(cur.getColumnIndex(AvBContract.PlaceCategoryEntry.CATEGORY_ID))),null,null,null,null));

                spPlaceType.setAdapter(placeTypeCursorAdapter);

                while(cur.moveToNext()){
                    if(cur.getString(cur.getColumnIndex(AvBContract.PlaceCategoryEntry.NAME)).contains(placeType)){
                        spPlaceType.setSelection(cur.getPosition());
                        break;
                    }
                }
                break;
            }
        }

        //TODO send the rankingType to the query.
        Map<String, String> params = new HashMap<>();

        params.put("name", name);

        params.put("city", city);
        params.put("state", state);
        params.put("category", category);
        params.put("type", placeType);
        params.put("rankingType", rankingType);

        requestRankingUpdate(params);
    }

    private void requestRankingUpdate(final Map<String, String> params) {
         /* -------------------------------------------------------------------------------------------------------------- */

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AvaliaBrasilAPIClient.getPlacesRanking(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fetchData(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                fetchData("");
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

    public Bitmap getImageBitmap(Context context) {
        try {
            FileInputStream fis = context.openFileInput("profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(fis);
            fis.close();
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void fetchData(String response) {
        Gson gson = new Gson();
        PlaceRankingSearch placeRanking = gson.fromJson("{" +
                "\"places\":" +
                "[" +
                "{" +
                "\"rankingPosition\": 1," +
                "\"name\": \"Nome do Lugar 1\"," +
                "\"address\": \"Endereço do Lugar 1\"," +
                "\"qualityIndex\": 3.8" +
                "}," +
                "{" +
                "\"rankingPosition\": 2," +
                "\"name\": \"Nome do Lugar 2\"," +
                "\"address\": \"Endereço do Lugar 2\"," +
                "\"qualityIndex\": 3.6" +
                "}," +
                "{" +
                "\"rankingPosition\": 3," +
                "\"name\": \"Nome do Lugar 3\"," +
                "\"address\": \"Endereço do Lugar 3\"," +
                "\"qualityIndex\": 3.4" +
                "}," +
                "{" +
                "\"rankingPosition\": 4," +
                "\"name\": \"Nome do Lugar 4\"," +
                "\"address\": \"Endereço do Lugar 4\"," +
                "\"qualityIndex\": 3.0" +
                "}," +
                "{" +
                "\"rankingPosition\": 5," +
                "\"name\": \"Nome do Lugar 5\"," +
                "\"address\": \"Endereço do Lugar 5\"," +
                "\"qualityIndex\": 2.8" +
                "}" +
                "]" +
                "}", PlaceRankingSearch.class);


        rvRankingList.setAdapter(new PlaceRankingAdapter(RankingActivity.this, placeRanking.getPlaceRankings()));

        rvRankingList.setHasFixedSize(true);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.btnSearchInstitution:
                startActivity(new Intent(RankingActivity.this, MainActivity.class));
                break;
            case R.id.btnLogout:

                if (!FacebookSdk.isInitialized()) {
                    FacebookSdk.sdkInitialize(RankingActivity.this);
                }

                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut();
                }
                for (Account c : manager.getAccountsByType(Constant.ACCOUNT_TYPE)) {
                    manager.removeAccount(c, null, null);
                }

                manager.addAccount(Constant.ACCOUNT_TYPE, Constant.ACCOUNT_TOKEN_TYPE_USER, null, null, RankingActivity.this, new AccountManagerCallback<Bundle>() {
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
                startActivity(new Intent(RankingActivity.this,HelpActivity.class));
                break;
            case R.id.btnTermsOfUse:
                startActivity(new Intent(RankingActivity.this,TermsOfUseActivity.class));
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
