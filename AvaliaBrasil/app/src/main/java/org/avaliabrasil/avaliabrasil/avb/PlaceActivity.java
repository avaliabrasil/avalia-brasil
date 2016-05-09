package org.avaliabrasil.avaliabrasil.avb;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import org.avaliabrasil.avaliabrasil.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil.rest.GooglePlacesAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Holder;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Instrument;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Period;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceDetails;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceStatistics;
import org.avaliabrasil.avaliabrasil.util.Utils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceActivity extends AppCompatActivity {

    private CollapsingToolbarLayout toolbarLayout;
    private LinearLayout placesInfo;
    private String distance;
    private MapView mMapView;
    private GoogleMap googleMap;
    private String place_id;
    private Button quality_index;
    private Button ranking_position;
    private ImageView ivRankingStatus;
    private PlaceStatistics placeStats;
    /**
     *
     */
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((getIntent() == null) || (getIntent().getExtras() == null) || (getIntent().getExtras().getString("placeid") == null) || (getIntent().getExtras().getString("name") == null)
                || (getIntent().getExtras().getString("distance") == null)) {
            finish();
        } else {
            setContentView(R.layout.activity_place);

            place_id = getIntent().getExtras().getString("placeid");

            toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

            toolbarLayout.setTitle(getIntent().getExtras().getString("name"));

            // Ativando a opção voltar da Toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            quality_index = (Button) findViewById(R.id.quality_index);
            ranking_position = (Button) findViewById(R.id.ranking_position);
            ivRankingStatus = (ImageView) findViewById(R.id.ivRankingStatus);

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
                    AvBContract.PlaceEntry.getPlaceDetails(getIntent().getExtras().getString("placeid")), null, null, null, null);

            distance = getIntent().getExtras().getString("distance");

            if (cursor.getCount() <= 0) {
                getPlaceDetails(true);
            } else {
                getPlaceDetails(false);
                cursor.moveToFirst();

                View view = null;

                if (cursor.getString(cursor.getColumnIndex("vicinity")) != null) {
                    View place = getLayoutInflater().inflate(R.layout.list_item_place_info, null);
                    ((TextView) place.findViewById(R.id.place_name_text_view)).setText(cursor.getString(cursor.getColumnIndex("name")));
                    ((TextView) place.findViewById(R.id.place_address_text_view)).setText(cursor.getString(cursor.getColumnIndex("vicinity")));
                    ((TextView) place.findViewById(R.id.place_distance_text_view)).setText(distance);
                    placesInfo.addView(place);
                }

                if (cursor.getString(cursor.getColumnIndex("formattedPhoneNumber")) != null) {
                    view = getLayoutInflater().inflate(R.layout.image_and_text, null);

                    ((TextView) view.findViewById(R.id.text)).setText(cursor.getString(cursor.getColumnIndex("formattedPhoneNumber")));
                    ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(getResources().getDrawable(R.mipmap.ic_call_black_24dp));

                    placesInfo.addView(view);
                }

                if (cursor.getString(cursor.getColumnIndex("website")) != null) {
                    view = getLayoutInflater().inflate(R.layout.image_and_text, null);

                    ((TextView) view.findViewById(R.id.text)).setText(cursor.getString(cursor.getColumnIndex("website")));
                    ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_http_black_24dp));

                    placesInfo.addView(view);
                }

                toolbarLayout.setTitle(cursor.getString(cursor.getColumnIndex("name")));

                MarkerOptions marker;

                marker = new MarkerOptions().position(
                        new LatLng(cursor.getDouble(cursor.getColumnIndex("latitude")), cursor.getDouble(cursor.getColumnIndex("longitude")))).title(cursor.getString(cursor.getColumnIndex("name")));

                // Changing marker icon
                marker.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));


                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(cursor.getDouble(cursor.getColumnIndex("latitude")), cursor.getDouble(cursor.getColumnIndex("longitude")))).zoom(16).build();
                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

                googleMap.addMarker(marker);

                cursor = getContentResolver().query(
                        AvBContract.PlacePeriodEntry.buildPlacePeriodUri(place_id), null, null, null, null);

                Log.d("Teste", DatabaseUtils.dumpCursorToString(cursor));

                if (cursor.moveToNext()) {

                    view = getLayoutInflater().inflate(R.layout.image_and_text, null);
                    HashMap<String, String> openAndClose = new HashMap<String, String>();
                    openAndClose.put("open", "");
                    openAndClose.put("close", "");
                    openAndClose.put(cursor.getString(cursor.getColumnIndex("status")), Utils.formatTime(cursor.getString(cursor.getColumnIndex("time"))));
                    if (cursor.moveToNext()) {
                        openAndClose.put(cursor.getString(cursor.getColumnIndex("status")), Utils.formatTime(cursor.getString(cursor.getColumnIndex("time"))));
                    }

                    ((TextView) view.findViewById(R.id.text)).setText("Aberto hoje " + openAndClose.get("open") + " - " + openAndClose.get("close"));
                    ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_schedule_black_24dp));
                    placesInfo.addView(view);
                }
            }

            StringRequest stringRequest = new StringRequest(Request.Method.GET, AvaliaBrasilAPIClient.getPlaceStatistics(place_id),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Teste", "onResponse: " + response);
                            Gson gson = new Gson();
                            placeStats = gson.fromJson(Utils.normalizeAvaliaBrasilResponse(response), PlaceStatistics.class);

                            if (placeStats == null) {
                                ranking_position.setText("-- º");
                                quality_index.setText("0");
                                ivRankingStatus.setImageResource(R.drawable.ic_remove_black_24dp);
                            } else {
                                ranking_position.setText(String.valueOf(placeStats.getRankingPosition().getNational()) + "º");

                                quality_index.setText(String.valueOf(placeStats.getQualityIndex().get(placeStats.getQualityIndex().size() - 1).getValue()));

                                switch (placeStats.getRankingStatus().getNational()) {
                                    case "up":
                                        ivRankingStatus.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                                        break;
                                    case "down":
                                        ivRankingStatus.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                                        break;
                                    case "none":
                                        ivRankingStatus.setImageResource(R.drawable.ic_remove_black_24dp);
                                        break;
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    ranking_position.setText("-- º");
                    quality_index.setText("0");
                    ivRankingStatus.setImageResource(R.drawable.ic_remove_black_24dp);

                }
            });
            Volley.newRequestQueue(PlaceActivity.this).add(stringRequest);
        }
    }

    private void getPlaceDetails(final boolean update) {
        progress = ProgressDialog.show(this, getResources().getString(R.string.progress_dialog_title),
                getResources().getString(R.string.progress_dialog_message), true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GooglePlacesAPIClient.getPlaceDetails(place_id, "AIzaSyCBq-qetL_jdUUhM0TepfVZ5EYxJvw6ct0"),
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
                        value.put("place_id", place_id);
                        value.put("website", placeDetails.getResult().getWebsite());
                        value.put("formattedPhoneNumber", placeDetails.getResult().getFormattedPhoneNumber());
                        value.put("photo_reference", placeDetails.getResult().getPhotos().size() > 0 ? placeDetails.getResult().getPhotos().get(0).getPhotoReference() : "");

                        value.put("city", placeDetails.getResult().getCity());
                        value.put("state", placeDetails.getResult().getState());
                        value.put("country", placeDetails.getResult().getCountry());

                        if (update) {
                            getContentResolver().insert(
                                    AvBContract.PlaceDetailsEntry.PLACE_DETAILS_URI, value);
                        } else {
                            getContentResolver().update(
                                    AvBContract.PlaceDetailsEntry.PLACE_DETAILS_URI, value, "place_id = ?", new String[]{place_id});
                        }


                        if (placeDetails.getResult().getVicinity() != null) {
                            View place = getLayoutInflater().inflate(R.layout.list_item_place_info, null);
                            ((TextView) place.findViewById(R.id.place_name_text_view)).setText(placeDetails.getResult().getName());
                            ((TextView) place.findViewById(R.id.place_address_text_view)).setText(placeDetails.getResult().getVicinity());
                            ((TextView) place.findViewById(R.id.place_distance_text_view)).setText(distance);
                            placesInfo.addView(place);
                        }

                        if (placeDetails.getResult().getFormattedPhoneNumber() != null) {
                            view = getLayoutInflater().inflate(R.layout.image_and_text, null);

                            ((TextView) view.findViewById(R.id.text)).setText(placeDetails.getResult().getFormattedPhoneNumber());
                            ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(getResources().getDrawable(R.mipmap.ic_call_black_24dp));

                            placesInfo.addView(view);
                        }

                        if (placeDetails.getResult().getWebsite() != null) {
                            view = getLayoutInflater().inflate(R.layout.image_and_text, null);

                            ((TextView) view.findViewById(R.id.text)).setText(placeDetails.getResult().getWebsite());
                            ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_http_black_24dp));

                            placesInfo.addView(view);
                        }

                        if (placeDetails.getResult().getOpeningHour() != null) {

                            Calendar c = Calendar.getInstance();

                            if (placeDetails.getResult().getOpeningHour().getPeriods().size() > 0) {

                                ContentValues[] values = new ContentValues[placeDetails.getResult().getOpeningHour().getPeriods().size() * 2];

                                for (int i = 0, j = 0; i < placeDetails.getResult().getOpeningHour().getPeriods().size(); i++) {
                                    value = null;
                                    Period p = placeDetails.getResult().getOpeningHour().getPeriods().get(i);
                                    if (p.getIsClose() != null) {
                                        value = new ContentValues();
                                        value.put("place_id", place_id);
                                        value.put("day", p.getIsClose().getDay());
                                        value.put("time", p.getIsClose().getTime());
                                        value.put("status", "close");
                                    }

                                    values[j++] = value;
                                    value = null;

                                    if (p.getIsOpen() != null) {
                                        value = new ContentValues();
                                        value.put("place_id", place_id);
                                        value.put("day", p.getIsOpen().getDay());
                                        value.put("time", p.getIsOpen().getTime());
                                        value.put("status", "open");

                                    }
                                    values[j++] = value;

                                    if (p.getIsClose() != null && p.getIsOpen() != null) {
                                        if (p.getIsOpen().getDay() == c.get(Calendar.DAY_OF_WEEK) && p.getIsClose().getDay() == c.get(Calendar.DAY_OF_WEEK)) {
                                            view = getLayoutInflater().inflate(R.layout.image_and_text, null);
                                            ((TextView) view.findViewById(R.id.text)).setText("Aberto hoje " + Utils.formatTime(p.getIsOpen().getTime()) + " - " + Utils.formatTime(p.getIsClose().getTime()));
                                            ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_schedule_black_24dp));
                                            placesInfo.addView(view);
                                        }
                                    }

                                }
                                getContentResolver().bulkInsert(
                                        AvBContract.PlacePeriodEntry.PLACE_PERIOD_URI, values);
                            }
                        }

                        toolbarLayout.setTitle(placeDetails.getResult().getName());


                        MarkerOptions marker;

                        marker = new MarkerOptions().position(
                                new LatLng(placeDetails.getResult().getGeometry().getLocation().getLat(), placeDetails.getResult().getGeometry().getLocation().getLng())).title(placeDetails.getResult().getName());

                        // Changing marker icon
                        marker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));


                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(placeDetails.getResult().getGeometry().getLocation().getLat(), placeDetails.getResult().getGeometry().getLocation().getLng())).zoom(16).build();
                        googleMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition));
                        // adding marker
                        googleMap.addMarker(marker);
                        progress.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                View view = null;
                view = getLayoutInflater().inflate(R.layout.image_and_text, null);
                ((TextView) view.findViewById(R.id.text)).setText("Não foi possível buscar as informações deste local, verifique sua internet");
                ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_search_white_24dp));

                placesInfo.addView(view);
                progress.dismiss();
            }
        });
        Volley.newRequestQueue(PlaceActivity.this).add(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void fetchData(String response) {
        Gson gson = new Gson();

        Holder data = gson.fromJson(Utils.normalizeAvaliaBrasilResponse(response), Holder.class);

        ContentValues[] values = new ContentValues[data.getInstruments().size()];
        ContentValues value = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd");

        Date dateAtual = new Date(System.currentTimeMillis());
        for (int i = 0; i < values.length; i++) {
            if (data.getInstruments().get(i).getGroups().size() != 0) {
                value = new ContentValues();
                value.put(AvBContract.InstrumentEntry.INSTRUMENT_ID, data.getInstruments().get(i).getId());
                value.put(AvBContract.InstrumentEntry.UPDATED_AT, dateFormat.format(dateAtual));
                values[i] = value;
            }
        }

        getContentResolver().bulkInsert(
                AvBContract.InstrumentEntry.INSTRUMENT_URI, values);

        for (int i = 0; i < values.length; i++) {
            if (data.getInstruments().get(i).getGroups().size() != 0) {
                value = new ContentValues();
                value.put(AvBContract.InstrumentPlaceEntry.INSTRUMENT_ID, data.getInstruments().get(i).getId());
                value.put(AvBContract.InstrumentPlaceEntry.PLACE_ID, place_id);
                values[i] = value;
            }
        }

        getContentResolver().bulkInsert(
                AvBContract.InstrumentPlaceEntry.INSTRUMENTPLACE_URI, values);

        for (int i = 0; i < data.getInstruments().size(); i++) {
            values = new ContentValues[data.getInstruments().get(i).getGroups().size()];
            for (int j = 0; j < data.getInstruments().get(i).getGroups().size(); j++) {
                value = new ContentValues();
                value.put(AvBContract.GroupQuestionEntry.INSTRUMENT_ID, data.getInstruments().get(i).getId());
                value.put(AvBContract.GroupQuestionEntry.GROUP_ID, data.getInstruments().get(i).getGroups().get(j).getId());
                value.put(AvBContract.GroupQuestionEntry.ORDER_QUESTION, data.getInstruments().get(i).getGroups().get(j).getOrder());
                values[j] = value;
            }

            getContentResolver().bulkInsert(
                    AvBContract.GroupQuestionEntry.GROUP_URI, values);
        }

        for (int k = 0; k < data.getInstruments().size(); k++) {
            Log.d("PlaceActivity", "instrument ID: " + data.getInstruments().get(k).getId());
            for (int i = 0; i < data.getInstruments().get(k).getGroups().size(); i++) {

                values = new ContentValues[data.getInstruments().get(k).getGroups().get(i).getQuestions().size()];
                Log.d("PlaceActivity", "Group ID: " + data.getInstruments().get(k).getGroups().get(i).getId());
                Log.d("PlaceActivity", "order: " + data.getInstruments().get(k).getGroups().get(i).getOrder());
                Log.d("PlaceActivity", "order: " + data.getInstruments().get(k).getGroups().get(i).getOrder());

                for (int j = 0; j < data.getInstruments().get(k).getGroups().get(i).getQuestions().size(); j++) {

                    Log.d("PlaceActivity", "Question type: " + data.getInstruments().get(k).getGroups().get(i).getQuestions().get(j).getQuestionType());
                    Log.d("PlaceActivity", "Question: " + data.getInstruments().get(k).getGroups().get(i).getQuestions().get(j).getTitle());
                    Log.d("PlaceActivity", "Question ID: " + data.getInstruments().get(k).getGroups().get(i).getQuestions().get(j).getId());

                    value = new ContentValues();
                    value.put(AvBContract.QuestionEntry.QUESTION, data.getInstruments().get(k).getGroups().get(i).getQuestions().get(j).getTitle());
                    value.put(AvBContract.QuestionEntry.GROUP_ID, data.getInstruments().get(k).getGroups().get(i).getId());
                    value.put(AvBContract.QuestionEntry.QUESTION_ID, data.getInstruments().get(k).getGroups().get(i).getQuestions().get(j).getId());
                    value.put(AvBContract.QuestionEntry.QUESTION_TYPE, data.getInstruments().get(k).getGroups().get(i).getQuestions().get(j).getQuestionType());
                    values[j] = value;
                }

                getContentResolver().bulkInsert(
                        AvBContract.QuestionEntry.QUESTION_URI, values);
            }
        }


        if (data.getCategories().size() > 0) {
            values = new ContentValues[data.getCategories().size()];

            for (int i = 0; i < data.getCategories().size(); i++) {
                value = new ContentValues();
                value.put(AvBContract.PlaceCategoryEntry.CATEGORY_ID, data.getCategories().get(i).getIdCategory());
                Log.d("PlaceAct", "id: " + data.getCategories().get(i).getIdCategory());
                value.put(AvBContract.PlaceCategoryEntry.NAME, data.getCategories().get(i).getCategory());
                values[i] = value;
            }
            getContentResolver().bulkInsert(
                    AvBContract.PlaceCategoryEntry.PLACE_CATEGORY_URI, values);
        }

        if (data.getPlaceTypes().size() > 0) {
            values = new ContentValues[data.getPlaceTypes().size()];

            for (int i = 0; i < data.getPlaceTypes().size(); i++) {
                value = new ContentValues();
                value.put(AvBContract.PlaceTypeEntry.CATEGORY_ID, data.getPlaceTypes().get(i).getIdCategory());
                Log.d("PlaceAct", "id: " + data.getPlaceTypes().get(i).getIdCategory());
                value.put(AvBContract.PlaceTypeEntry.NAME, data.getPlaceTypes().get(i).getCategory());
                values[i] = value;
            }
            getContentResolver().bulkInsert(
                    AvBContract.PlaceTypeEntry.PLACE_TYPE_URI, values);
        }

        prepareHolder(data);
    }

    public void startEvaluationActivity(View view) {

        progress = ProgressDialog.show(this, getResources().getString(R.string.progress_dialog_title),
                getResources().getString(R.string.progress_dialog_message), true);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, AvaliaBrasilAPIClient.getSurveyURL(place_id),
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
                progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                JsonObject jsonObject = new JsonObject();
                JsonArray availableInstruments = new JsonArray();
                Cursor c = getContentResolver().query(AvBContract.InstrumentEntry.buildInstrumentUri(place_id), null, null, null, null);

                JsonObject objs = null;
                while (c.moveToNext()) {

                    objs = new JsonObject();

                    objs.addProperty("id", c.getString(c.getColumnIndex(AvBContract.InstrumentEntry.INSTRUMENT_ID)));
                    objs.addProperty("updated_at", c.getString(c.getColumnIndex(AvBContract.InstrumentEntry.UPDATED_AT)));

                    availableInstruments.add(objs);
                }
                jsonObject.add("availableInstruments", availableInstruments);

                //TODO get the user token to send into the request
                jsonObject.addProperty("userID", "");

                params.put("", jsonObject.toString());
                return params;
            }
        };
        Volley.newRequestQueue(PlaceActivity.this).add(stringRequest);

    }


    private void prepareHolder(Holder holder) {


        Cursor c = getContentResolver().query(
                AvBContract.PlaceEntry.getPlaceDetails(place_id), null, null, null, null);


        Log.d("Place",DatabaseUtils.dumpCursorToString(c));

        if (c.moveToNext()) {
            String photo_reference = c.getString(c.getColumnIndex(AvBContract.PlaceDetailsEntry.PHOTO_REFERENCE));

            if (holder == null) {
                holder = new Holder();

                ArrayList<String> ids = new ArrayList<String>();

                c = getContentResolver().query(AvBContract.InstrumentEntry.buildInstrumentUri(place_id), null, null, null, null);

                List<Instrument> instruments = new ArrayList<Instrument>();

                while (c.moveToNext()) {
                    Log.d("PlaceActivity", "startEvaluationActivity: id: " + c.getString(c.getColumnIndex(AvBContract.InstrumentEntry.INSTRUMENT_ID)));
                    ids.add(c.getString(c.getColumnIndex(AvBContract.InstrumentEntry.INSTRUMENT_ID)));
                }

                for (String id : ids) {
                    c = getContentResolver().query(AvBContract.GroupQuestionEntry.buildGroupQuestionsUri(id), null, null, null, null);

                    Log.e("PlaceActivity", DatabaseUtils.dumpCursorToString(c));

                    instruments.add(new Instrument(id, c));
                }

                holder.setInstruments(instruments);
            }

            Intent intent = new Intent(PlaceActivity.this, EvaluationActivity.class);
            intent.putExtra("holder", (Serializable) holder);
            intent.putExtra("name", getIntent().getExtras().getString("name"));
            intent.putExtra("placeid", place_id);
            intent.putExtra("photo_reference", photo_reference);
            startActivity(intent);
            finish();
        }
    }

    public void startStatisticsActivity(View view) {
        if (placeStats == null) {
            Toast.makeText(PlaceActivity.this, getResources().getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(PlaceActivity.this, PlaceStatisticsActivity.class);
        intent.putExtra("placeid", place_id);
        startActivity(intent);
    }
}