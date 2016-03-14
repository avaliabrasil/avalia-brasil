package org.avaliabrasil.avaliabrasil.avb;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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

    private ArrayAdapter<String> PlaceInfoAdapter;
    private ListView placeInfoListView;
    private CollapsingToolbarLayout toolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String placeid = getIntent().getExtras().getString("placeid");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GooglePlacesAPIClient.getPlaceDetails(placeid,"AIzaSyCBq-qetL_jdUUhM0TepfVZ5EYxJvw6ct0"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Teste", "onResponse: " + response);
                        Gson gson = new Gson();
                        PlaceDetails placeDetails = gson.fromJson(response, PlaceDetails.class);

                        ArrayList<String> placeInfoList = new ArrayList<String>();

                        placeInfoList.add("Address: " + placeDetails.getResult().getVicinity());

                        ArrayAdapter<String> placeListAdapter =
                        new ArrayAdapter<String>(
                                PlaceActivityTeste.this, // Context
                                R.layout.list_item_place_info, // LayoutId
                                R.id.place_name_text_view, // Id of Textview
                                placeInfoList // ListName
                        );

                        placeInfoListView.setAdapter(placeListAdapter);
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

        placeInfoListView = (ListView) findViewById(R.id.listview_place_info);
    }
}