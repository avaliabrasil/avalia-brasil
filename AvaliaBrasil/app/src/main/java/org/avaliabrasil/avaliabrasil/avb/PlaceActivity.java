package org.avaliabrasil.avaliabrasil.avb;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.data.AvBContract;
import org.avaliabrasil.avaliabrasil.data.AvBProvider;
import org.avaliabrasil.avaliabrasil.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil.rest.GooglePlacesAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Data;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Holder;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Instrument;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceDetails;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Question;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PlaceActivity extends AppCompatActivity {

    private CollapsingToolbarLayout toolbarLayout;
    private LinearLayout placesInfo;
    private String distance;
    private MapView mMapView;
    private GoogleMap googleMap;
    private String place_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if((getIntent() == null)||(getIntent().getExtras()==null)||(getIntent().getExtras().getString("placeid") == null) || (getIntent().getExtras().getString("name") == null)
                || (getIntent().getExtras().getString("distance") == null)){
            finish();
        }else{
            setContentView(R.layout.activity_place);

            place_id = getIntent().getExtras().getString("placeid");

            toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

            toolbarLayout.setTitle(getIntent().getExtras().getString("name"));

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

            distance = getIntent().getExtras().getString("distance");

            if(cursor.getCount() <= 0){
                final String placeid = getIntent().getExtras().getString("placeid");


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
                        View view = null;
                        view =  getLayoutInflater().inflate(R.layout.image_and_text, null);
                        ((TextView)view.findViewById(R.id.text)).setText("Não foi possível buscar as informações deste local, verifique sua internet");
                        ((ImageView)view.findViewById(R.id.icon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_search_white_24dp));

                        placesInfo.addView(view);
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

                googleMap.addMarker(marker);
            }
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

    public void fetchData(String response){
        Gson gson = new Gson();
        Holder data = gson.fromJson("{ \"instruments\":[ " +
                "                                    { " +
                "                                      \"id\":1, " +
                "                                        \"data\":{ " +
                "                                           \"groups\":[ " +
                "                                               { " +
                "                                                    \"id\":1, " +
                "                                                    \"order\":1, " +
                "                                                    \"questions\":[ " +
                "                                                        { " +
                "                                                            \"id\":1, " +
                "                                                            \"title\":\"Question 1 \", " +
                "                                                            \"questionType\":\"is_comment\" " +
                "                                                        }, " +
                "                                                        { " +
                "                                                            \"id\":2, " +
                "                                                            \"title\":\"Question 2 \"," +
                "                                                            \"questionType\":\"is_number\" " +
                "                                                        }, " +
                "                                                        { " +
                "                                                            \"id\":3," +
                "                                                            \"title\":\"Question 3\", " +
                "                                                            \"questionType\":\"is_number\" " +
                "                                                        }, " +
                "                                                        { " +
                "                                                            \"id\":4, " +
                "                                                            \"title\":\"Question 4 \"," +
                "                                                            \"questionType\":\"is_likert\"" +
                "                                                        } " +
                "                                                    ] " +
                "                                                }, " +
                "                                                { " +
                "                                                    \"id\":2, " +
                "                                                    \"order\":2," +
                "                                                    \"questions\":[ " +
                "                                                        { " +
                "                                                            \"id\":5," +
                "                                                            \"title\":\" Question 5 \"," +
                "                                                            \"questionType\":\"is_number\" " +
                "                                                        }, " +
                "                                                        { " +
                "                                                            \"id\":6," +
                "                                                            \"title\":\" Question 6 \", " +
                "                                                            \"questionType\":\"is_comment\" " +
                "                                                        }, " +
                "                                                        { " +
                "                                                            \"id\":7," +
                "                                                            \"title\":\" Question 7 \", " +
                "                                                            \"questionType\":\"is_likert\" " +
                "                                                        }, " +
                "                                                        { " +
                "                                                            \"id\":8," +
                "                                                            \"title\":\" Question 8 \"," +
                "                                                            \"questionType\":\"is_number\" " +
                "                                                        } " +
                "                                                    ] " +
                "                                                } " +
                "                                            ] " +
                "                                        } " +
                "                                    }, " +
                "                                    { " +
                "                                        \"id\":2, " +
                "                                        \"data\":{ " +
                "                                        } " +
                "                                    } " +
                "                                ], " +
                "                            \"newPlace\": true, " +
                "                            \"categories\": [ " +
                "                            { \"id\":1, \"name\": \"cat1\" }, " +
                "                            { \"id\":2, \"name\": \"cat2\" }, " +
                "                            { \"id\":3, \"name\": \"cat3\" }, " +
                "                            { \"id\":4, \"name\": \"cat4\" } " +
                "                            ]," +
                "                            \"placeTypes\": [" +
                "                            {" +
                "                            \"id_category\": 1, " +
                "                            \"name\": \"TIPO1\" " +
                "                            }," +
                "                            {" +
                "                            \"id_category\": 2, " +
                "                            \"name\": \"TIPO2\" " +
                "                            }, " +
                "                            { " +
                "                            \"id_category\": 3," +
                "                            \"name\": \"TIPO3\" " +
                "                            }, " +
                "                            {" +
                "                            \"id_category\": 4," +
                "                            \"name\": \"TIPO4\" " +
                "                            } " +
                "                            ] " +
                "                            }", Holder.class);

        ContentValues[] values = new ContentValues[data.getInstruments().size()];
        ContentValues value = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd");

        Date dateAtual = new Date(System.currentTimeMillis());
        for (int i = 0; i < values.length; i++) {
            if(data.getInstruments().get(i).getData().getGroups().size() != 0) {
                value = new ContentValues();
                value.put(AvBContract.InstrumentEntry.INSTRUMENT_ID, data.getInstruments().get(i).getId());
                value.put(AvBContract.InstrumentEntry.UPDATED_AT, dateFormat.format(dateAtual));
                values[i] = value;
            }
        }

        getContentResolver().bulkInsert(
                AvBContract.InstrumentEntry.INSTRUMENT_URI, values);

        for (int i = 0; i < values.length; i++) {
            if(data.getInstruments().get(i).getData().getGroups().size() != 0) {
                value = new ContentValues();
                value.put(AvBContract.InstrumentPlaceEntry.INSTRUMENT_ID, data.getInstruments().get(i).getId());
                value.put(AvBContract.InstrumentPlaceEntry.PLACE_ID, place_id);
                values[i] = value;
            }
        }

        getContentResolver().bulkInsert(
                AvBContract.InstrumentPlaceEntry.INSTRUMENTPLACE_URI, values);

        for (int i = 0; i < data.getInstruments().size(); i++) {
            values = new ContentValues[data.getInstruments().get(i).getData().getGroups().size()];
            for (int j = 0; j < data.getInstruments().get(i).getData().getGroups().size(); j++) {
                value = new ContentValues();
                value.put(AvBContract.GroupQuestionEntry.INSTRUMENT_ID, data.getInstruments().get(i).getId());
                value.put(AvBContract.GroupQuestionEntry.GROUP_ID, data.getInstruments().get(i).getData().getGroups().get(j).getId());
                value.put(AvBContract.GroupQuestionEntry.ORDER_QUESTION, data.getInstruments().get(i).getData().getGroups().get(j).getOrder());
                values[j] = value;
            }

            getContentResolver().bulkInsert(
                    AvBContract.GroupQuestionEntry.GROUP_URI, values);
        }

        for (int k = 0; k < data.getInstruments().size(); k++) {
            Log.d("PlaceActivity", "instrument ID: " + data.getInstruments().get(k).getId());
            for (int i = 0; i < data.getInstruments().get(k).getData().getGroups().size(); i++) {

                values = new ContentValues[data.getInstruments().get(k).getData().getGroups().get(i).getQuestions().size()];
                Log.d("PlaceActivity", "Group ID: " + data.getInstruments().get(k).getData().getGroups().get(i).getId());
                Log.d("PlaceActivity", "order: " + data.getInstruments().get(k).getData().getGroups().get(i).getOrder());
                Log.d("PlaceActivity", "order: " + data.getInstruments().get(k).getData().getGroups().get(i).getOrder());

                for (int j = 0; j < data.getInstruments().get(k).getData().getGroups().get(i).getQuestions().size(); j++) {

                    Log.d("PlaceActivity", "Question type: " + data.getInstruments().get(k).getData().getGroups().get(i).getQuestions().get(j).getQuestionType());
                    Log.d("PlaceActivity", "Question: " + data.getInstruments().get(k).getData().getGroups().get(i).getQuestions().get(j).getTitle());
                    Log.d("PlaceActivity", "Question ID: " + data.getInstruments().get(k).getData().getGroups().get(i).getQuestions().get(j).getId());

                    value = new ContentValues();
                    value.put(AvBContract.QuestionEntry.QUESTION, data.getInstruments().get(k).getData().getGroups().get(i).getQuestions().get(j).getTitle());
                    value.put(AvBContract.QuestionEntry.GROUP_ID, data.getInstruments().get(k).getData().getGroups().get(i).getId());
                    value.put(AvBContract.QuestionEntry.QUESTION_ID, data.getInstruments().get(k).getData().getGroups().get(i).getQuestions().get(j).getId());
                    value.put(AvBContract.QuestionEntry.QUESTION_TYPE, data.getInstruments().get(k).getData().getGroups().get(i).getQuestions().get(j).getQuestionType());
                    values[j] = value;
                }

                getContentResolver().bulkInsert(
                        AvBContract.QuestionEntry.QUESTION_URI, values);
            }
        }

        prepareHolder(data);
    }

    public void startEvaluationActivity(View view){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AvaliaBrasilAPIClient.getSurveyURL(place_id),
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
                protected Map<String, String> getParams () {
                    Map<String, String> params = new HashMap<String, String>();

                    JsonObject jsonObject = new JsonObject();
                    JsonArray availableInstruments = new JsonArray();
                    Cursor c = getContentResolver().query(AvBContract.InstrumentEntry.buildInstrumentUri(place_id),null,null,null,null);

                    JsonObject objs = null;
                    while (c.moveToNext()){

                        objs = new JsonObject();

                        objs.addProperty("id",c.getString(c.getColumnIndex(AvBContract.InstrumentEntry.INSTRUMENT_ID)));
                        objs.addProperty("updated_at",c.getString(c.getColumnIndex(AvBContract.InstrumentEntry.UPDATED_AT)));

                        availableInstruments.add(objs);
                    }
                    jsonObject.add("availableInstruments",availableInstruments);

                    //TODO get the user token to send into the request
                    jsonObject.addProperty("userID","");

                    params.put("",jsonObject.toString());
                    return params;
                }
            };
            Volley.newRequestQueue(PlaceActivity.this).add(stringRequest);

    }


    private void prepareHolder(Holder holder){

        if(holder == null){
            holder = new Holder();

            ArrayList<String> ids = new ArrayList<String>();

            Cursor c = getContentResolver().query(AvBContract.InstrumentEntry.buildInstrumentUri(place_id),null,null,null,null);

            List<Instrument> instruments = new ArrayList<Instrument>();

            //Log.e("PlaceActivity", DatabaseUtils.dumpCursorToString(c));

            while(c.moveToNext()){
                Log.d("PlaceActivity", "startEvaluationActivity: id: " + c.getString(c.getColumnIndex(AvBContract.InstrumentEntry.INSTRUMENT_ID)));
                ids.add(c.getString(c.getColumnIndex(AvBContract.InstrumentEntry.INSTRUMENT_ID)));
            }

            for(String id : ids){
                c = getContentResolver().query(AvBContract.GroupQuestionEntry.buildGroupQuestionsUri(id),null,null,null,null);

                Log.e("PlaceActivity", DatabaseUtils.dumpCursorToString(c));

                instruments.add(new Instrument(id,c));
            }

            holder.setInstruments(instruments);
        }


        Intent intent = new Intent(PlaceActivity.this,EvaluationActivity.class);
        intent.putExtra("holder", (Serializable) holder);
        intent.putExtra("name",getIntent().getExtras().getString("name"));
        intent.putExtra("placeid",place_id);
        startActivity(intent);
        finish();
    }

    public void startStatisticsActivity(View view){

    }
}