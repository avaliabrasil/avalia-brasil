package org.avaliabrasil.avaliabrasil2.avb.activity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.avaliabrasil.avaliabrasil2.R;
import org.avaliabrasil.avaliabrasil2.avb.dao.InstrumentDAO;
import org.avaliabrasil.avaliabrasil2.avb.dao.PlaceDetailsDAO;
import org.avaliabrasil.avaliabrasil2.avb.dao.PlacePeriodDAO;
import org.avaliabrasil.avaliabrasil2.avb.impl.AnwserDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.GroupQuestionDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.InstrumentDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.NewPlaceDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.PlaceCategoryDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.PlaceDetailsDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.PlacePeriodDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.PlaceTypeDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.QuestionDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.SurveyDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.google.places.Period;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.place.placedetail.PlaceDetails;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.place.placedetail.ResultDetails;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.PlaceStatistics;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.Instrument;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.Survey;
import org.avaliabrasil.avaliabrasil2.avb.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil2.avb.rest.GooglePlacesAPIClient;
import org.avaliabrasil.avaliabrasil2.avb.util.Utils;

import java.io.Serializable;
import java.sql.SQLException;
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
    private PlaceDetailsDAO placeDetailsDAO;
    private PlacePeriodDAO placePeriodDAO;
    private SurveyDAOImpl surveyDAO;
    private InstrumentDAO instrumentDAO;
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

            placeDetailsDAO = new PlaceDetailsDAOImpl(PlaceActivity.this);
            placePeriodDAO = new PlacePeriodDAOImpl(PlaceActivity.this);
            surveyDAO = new SurveyDAOImpl(PlaceActivity.this,new InstrumentDAOImpl(PlaceActivity.this,
                    new GroupQuestionDAOImpl(PlaceActivity.this)),
                    new GroupQuestionDAOImpl(PlaceActivity.this),
                    new QuestionDAOImpl(PlaceActivity.this),
                    new NewPlaceDAOImpl(PlaceActivity.this),
                    new AnwserDAOImpl(PlaceActivity.this));
            instrumentDAO = new InstrumentDAOImpl(PlaceActivity.this, new GroupQuestionDAOImpl(PlaceActivity.this));

            try {
                MapsInitializer.initialize(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            googleMap = mMapView.getMap();

            placesInfo = (LinearLayout) findViewById(R.id.placesInfo);

            ResultDetails details = placeDetailsDAO.getPlaceDetailsByPlaceId(place_id);

            distance = getIntent().getExtras().getString("distance");

            if (details == null) {
                getPlaceDetails(true);
            } else {
                getPlaceDetails(false);
                toolbarLayout.setTitle(details.getName());
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
                                quality_index.setText("-");
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
                    quality_index.setText("-");
                    ivRankingStatus.setImageResource(R.drawable.ic_remove_black_24dp);

                }
            });
            Volley.newRequestQueue(PlaceActivity.this).add(stringRequest);
        }
    }

    private void prepareDependencyUI(ResultDetails details) {
        View view = null;

        if(details == null){
            return;
        }

        if (details.getVicinity() != null) {
            View place = getLayoutInflater().inflate(R.layout.list_item_place_info, null);
            ((TextView) place.findViewById(R.id.place_name_text_view)).setText(details.getName());
            ((TextView) place.findViewById(R.id.place_address_text_view)).setText(details.getVicinity());
            ((TextView) place.findViewById(R.id.place_distance_text_view)).setText(distance);
            placesInfo.addView(place);
        }

        if (details.getFormattedPhoneNumber() != null) {
            view = getLayoutInflater().inflate(R.layout.image_and_text, null);

            ((TextView) view.findViewById(R.id.text)).setText(details.getFormattedPhoneNumber());
            ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(getResources().getDrawable(R.mipmap.ic_call_black_24dp));

            placesInfo.addView(view);
        }

        if (details.getWebsite() != null) {
            view = getLayoutInflater().inflate(R.layout.image_and_text, null);

            ((TextView) view.findViewById(R.id.text)).setText(details.getWebsite());
            ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_http_black_24dp));

            placesInfo.addView(view);
        }

        Period p = placePeriodDAO.getTodayPeriod(place_id);

        if (p != null) {
            view = getLayoutInflater().inflate(R.layout.image_and_text, null);
            ((TextView) view.findViewById(R.id.text)).setText("Aberto hoje " + Utils.formatTime(p.getIsOpen().getTime()) + " - " + Utils.formatTime(p.getIsClose().getTime()));
            ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(getResources().getDrawable(R.drawable.ic_schedule_black_24dp));
            placesInfo.addView(view);
        }

        MarkerOptions marker;
        marker = new MarkerOptions().position(
                details.getLatlng()).title(details.getName());

        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(details.getLatlng()).zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        googleMap.addMarker(marker);
    }

    private void getPlaceDetails(final boolean insert) {
        progress = ProgressDialog.show(this, getResources().getString(R.string.progress_dialog_title),
                getResources().getString(R.string.progress_dialog_message), true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GooglePlacesAPIClient.getPlaceDetails(place_id, "AIzaSyCBq-qetL_jdUUhM0TepfVZ5EYxJvw6ct0"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Teste", "onResponse: " + response);
                        Gson gson = new Gson();
                        PlaceDetails placeDetails = gson.fromJson(response, PlaceDetails.class);
                        View view = null;

                        placeDetailsDAO.insertOrUpdatePlaceDetails(place_id,placeDetails.getResult(),insert);
                        placePeriodDAO.bulkInsertPeriods(place_id,placeDetails.getResult().getOpeningHour() == null ? null : placeDetails.getResult().getOpeningHour().getPeriods());
                        ResultDetails details = placeDetailsDAO.getPlaceDetailsByPlaceId(place_id);
                        prepareDependencyUI(details);
                        progress.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                View view = null;
                ResultDetails details = placeDetailsDAO.getPlaceDetailsByPlaceId(place_id);
                prepareDependencyUI(details);
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

    public void fetchData(String response) throws SQLException {
        Survey survey = null;

        if (response == null) {
            survey = surveyDAO.findSurveyByPlaceId(place_id);
            if (survey.getInstruments().size() > 0) {
                prepareHolder(survey);
            } else {
                throw new SQLException(getResources().getString(R.string.internet_connection_error));
            }
        } else {
            Gson gson = new Gson();

            survey = gson.fromJson(Utils.normalizeAvaliaBrasilResponse(response), Survey.class);

            surveyDAO.bulkAddInstrument(survey);

            surveyDAO.boundPlaceAndInstrument(place_id, survey);

            surveyDAO.bulkAddQuestionGroup(survey);

            surveyDAO.bulkAddQuestion(survey);

            PlaceCategoryDAOImpl placeCategoryDAO = new PlaceCategoryDAOImpl(PlaceActivity.this);

            PlaceTypeDAOImpl placeTypeDAO = new PlaceTypeDAOImpl(PlaceActivity.this);

            placeCategoryDAO.bulkInsertPlaceCategory(survey.getCategories());

            placeTypeDAO.bulkInsertPlaceType(survey.getPlaceTypes());

            prepareHolder(survey);
        }
    }

    public void startEvaluationActivity(View view) {

        progress = ProgressDialog.show(this, getResources().getString(R.string.progress_dialog_title),
                getResources().getString(R.string.progress_dialog_message), true);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, AvaliaBrasilAPIClient.getSurveyURL(place_id),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            fetchData(response);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Toast.makeText(PlaceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progress.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progress.dismiss();
                try {
                    fetchData(null);
                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(PlaceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                JsonObject jsonObject = new JsonObject();
                JsonArray availableInstruments = new JsonArray();

                try {
                    List<Instrument> instruments = instrumentDAO.getInstrumentByPlace(place_id);
                    for(Instrument instrument : instruments){
                        availableInstruments.add(instrument.instrumentLastUpdated());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
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


    private void prepareHolder(Survey survey) {
        Intent intent = new Intent(PlaceActivity.this, EvaluationActivity.class);
        intent.putExtra("holder", (Serializable) survey);
        intent.putExtra("name", getIntent().getExtras().getString("name"));
        intent.putExtra("placeid", place_id);
        startActivity(intent);
        finish();
    }

    public void startStatisticsActivity(View view) {
        if (placeStats == null) {
            Toast.makeText(PlaceActivity.this, getResources().getString(R.string.no_statistics_error), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(PlaceActivity.this, PlaceStatisticsActivity.class);
        intent.putExtra("placeid", place_id);
        startActivity(intent);
    }
}