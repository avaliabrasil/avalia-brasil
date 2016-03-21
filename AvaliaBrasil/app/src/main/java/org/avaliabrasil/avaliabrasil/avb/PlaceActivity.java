package org.avaliabrasil.avaliabrasil.avb;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.data.AvBProvider;
import org.avaliabrasil.avaliabrasil.rest.GooglePlacesAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceDetails;

import java.util.ArrayList;

public class PlaceActivity extends AppCompatActivity {

    private CollapsingToolbarLayout toolbarLayout;
    private LinearLayout placesInfo;
    private String distance;
    private MapView mMapView;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().getExtras().getString("placeid") == null || getIntent().getExtras().getString("distance") == null){
            finish();
        }

        setContentView(R.layout.activity_place);

        // Definindo o título da Toolbar
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        //TODO Passar diretamente pelo bundle o nome do lugar
        toolbarLayout.setTitle("Buscando lugar...");

        // Ativando a opção voltar da Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();

        placesInfo = (LinearLayout) findViewById(R.id.placesInfo);

        Cursor cursor = getContentResolver().query(
                AvBProvider.getPlaceDetails(getIntent().getExtras().getString("placeid")), null,null,null,null);

        if(cursor.getCount() <= 0){
            final String placeid = getIntent().getExtras().getString("placeid");
            distance = getIntent().getExtras().getString("distance");

            StringRequest stringRequest = new StringRequest(Request.Method.GET, GooglePlacesAPIClient.getPlaceDetails(placeid,"AIzaSyCBq-qetL_jdUUhM0TepfVZ5EYxJvw6ct0"),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Teste", "onResponse: " + response);
                            Gson gson = new Gson();
                            PlaceDetails placeDetails = gson.fromJson(response, PlaceDetails.class);

                            ArrayList<String> placeInfoList = new ArrayList<String>();

                            placeInfoList.add("Address: " + placeDetails.getResult().getVicinity());
                            View view = null;

                            ContentValues value = new ContentValues();
                            value.put("place_id",placeid);
                            value.put("website",placeDetails.getResult().getWebsite());
                            value.put("formattedPhoneNumber",placeDetails.getResult().getFormattedPhoneNumber());

                            getContentResolver().insert(
                                    AvBProvider.PLACE_DETAILS_CONTENT_URI, value);

                            if(placeDetails.getResult().getVicinity() != null){
                                View place = getLayoutInflater().inflate(R.layout.list_item_place_info, null);
                                ((TextView)place.findViewById(R.id.place_name_text_view)).setText(placeDetails.getResult().getName());
                                ((TextView)place.findViewById(R.id.place_address_text_view)).setText(placeDetails.getResult().getVicinity());
                                ((TextView)place.findViewById(R.id.place_distance_text_view)).setText(distance);
                                placesInfo.addView(place);
                            }

                            if(placeDetails.getResult().getFormattedPhoneNumber() != null){
                                view =  getLayoutInflater().inflate(R.layout.image_and_text, null);

                                ((TextView)view.findViewById(R.id.text)).setText(placeDetails.getResult().getFormattedPhoneNumber());
                                ((ImageView)view.findViewById(R.id.icon)).setImageDrawable(getResources().getDrawable(R.mipmap.ic_call_black_24dp));

                                placesInfo.addView(view);
                            }

                            if(placeDetails.getResult().getWebsite() != null){
                                view =  getLayoutInflater().inflate(R.layout.image_and_text, null);

                                ((TextView)view.findViewById(R.id.text)).setText(placeDetails.getResult().getWebsite());
                                ((ImageView)view.findViewById(R.id.icon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_http_black_24dp));

                                placesInfo.addView(view);
                            }

                            toolbarLayout.setTitle(placeDetails.getResult().getName());


                            MarkerOptions marker;

                            marker = new MarkerOptions().position(
                                    new LatLng(placeDetails.getResult().getGeometry().getLocation().getLat(),placeDetails.getResult().getGeometry().getLocation().getLng())).title(placeDetails.getResult().getName());

                            // Changing marker icon
                            marker.icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));


                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(placeDetails.getResult().getGeometry().getLocation().getLat(), placeDetails.getResult().getGeometry().getLocation().getLng())).zoom(16).build();
                            googleMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(cameraPosition));
                            // adding marker
                            googleMap.addMarker(marker);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            Volley.newRequestQueue(PlaceActivity.this).add(stringRequest);
        }else{
            cursor.moveToFirst();

            View view = null;

            if(cursor.getString(cursor.getColumnIndex("vicinity")) != null){
                View place = getLayoutInflater().inflate(R.layout.list_item_place_info, null);
                ((TextView)place.findViewById(R.id.place_name_text_view)).setText(cursor.getString(cursor.getColumnIndex("name")));
                ((TextView)place.findViewById(R.id.place_address_text_view)).setText(cursor.getString(cursor.getColumnIndex("vicinity")));
                ((TextView)place.findViewById(R.id.place_distance_text_view)).setText(distance);
                placesInfo.addView(place);
            }

            if(cursor.getString(cursor.getColumnIndex("formattedPhoneNumber")) != null){
                view =  getLayoutInflater().inflate(R.layout.image_and_text, null);

                ((TextView)view.findViewById(R.id.text)).setText(cursor.getString(cursor.getColumnIndex("formattedPhoneNumber")));
                ((ImageView)view.findViewById(R.id.icon)).setImageDrawable(getResources().getDrawable(R.mipmap.ic_call_black_24dp));

                placesInfo.addView(view);
            }

            if(cursor.getString(cursor.getColumnIndex("website")) != null){
                view =  getLayoutInflater().inflate(R.layout.image_and_text, null);

                ((TextView)view.findViewById(R.id.text)).setText(cursor.getString(cursor.getColumnIndex("website")));
                ((ImageView)view.findViewById(R.id.icon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_http_black_24dp));

                placesInfo.addView(view);
            }

            toolbarLayout.setTitle(cursor.getString(cursor.getColumnIndex("name")));


            MarkerOptions marker;

            marker = new MarkerOptions().position(
                    new LatLng(cursor.getDouble(cursor.getColumnIndex("latitude")),cursor.getDouble(cursor.getColumnIndex("longitude")))).title(cursor.getString(cursor.getColumnIndex("name")));

            // Changing marker icon
            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(cursor.getDouble(cursor.getColumnIndex("latitude")),cursor.getDouble(cursor.getColumnIndex("longitude")))).zoom(16).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            // adding marker
            googleMap.addMarker(marker);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}