package org.avaliabrasil.avaliabrasil.avb;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.adapters.PlacesListAdapterTeste;
import org.avaliabrasil.avaliabrasil.rest.GooglePlacesAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceDetails;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceSearch;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaceActivityTeste extends AppCompatActivity {

    private CollapsingToolbarLayout toolbarLayout;
    private LinearLayout placesInfo;
    private String distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().getExtras().getString("placeid") == null || getIntent().getExtras().getString("distance") == null){
            finish();
        }

        String placeid = getIntent().getExtras().getString("placeid");
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
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(PlaceActivityTeste.this).add(stringRequest);


        setContentView(R.layout.activity_place);

        // Definindo o título da Toolbar
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle("Buscando lugar...");

        // Ativando a opção voltar da Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        placesInfo = (LinearLayout) findViewById(R.id.placesInfo);

    }
}