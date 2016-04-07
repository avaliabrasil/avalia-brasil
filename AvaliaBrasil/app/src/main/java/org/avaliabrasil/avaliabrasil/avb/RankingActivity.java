package org.avaliabrasil.avaliabrasil.avb;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.data.AvBContract;
import org.avaliabrasil.avaliabrasil.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Holder;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceRanking;
import org.avaliabrasil.avaliabrasil.view.RankingView;

import java.util.HashMap;
import java.util.Map;

public class RankingActivity extends AppCompatActivity {
    public final String LOG_TAG = this.getClass().getSimpleName();

    /**
     *
     */
    private Toolbar toolbar;

    /**
     *
     */
    private RankingView rvNacional;

    /**
     *
     */
    private RankingView rvRegional;

    /**
     *
     */
    private RankingView rvEstadual;

    /**
     *
     */
    private RankingView rvMunicipal;

    /**
     *
     */
    private TextView tvNumberOfAvaliation;

    /**
     *
     */
    private GraphView graph;

    /**
     *
     */
    private TextView tvQualityIndice;

    /**
     *
     */
    private TextView tvPlace;

    /**
     *
     */
    private TextView tvCategory;

    /**
     *
     */
    private TextView tvPlaceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvNacional = (RankingView) findViewById(R.id.rvNacional);

        rvRegional = (RankingView) findViewById(R.id.rvRegional);

        rvEstadual = (RankingView) findViewById(R.id.rvEstadual);

        rvMunicipal = (RankingView) findViewById(R.id.rvMunicipal);

        tvNumberOfAvaliation = (TextView) findViewById(R.id.tvNumberOfAvaliation);

        tvQualityIndice = (TextView) findViewById(R.id.tvQualityIndice);

        tvPlaceType = (TextView) findViewById(R.id.tvPlaceType);

        tvCategory = (TextView) findViewById(R.id.tvCategory);

        tvPlace = (TextView) findViewById(R.id.tvPlace);

        graph = (GraphView) findViewById(R.id.graph);

        graph.getGridLabelRenderer().setGridStyle( GridLabelRenderer.GridStyle.NONE );


        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 0),
                new DataPoint(1, 0),
                new DataPoint(2, 0),
                new DataPoint(3, 0),
                new DataPoint(4, 0)
        });
        graph.addSeries(series);

        /* -------------------------------------------------------------------------------------------------------------- */

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AvaliaBrasilAPIClient.getPlaceRanking(""),
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

                return params;
            }
        };
        Volley.newRequestQueue(RankingActivity.this).add(stringRequest);
    }

    private void fetchData(String response){
        Gson gson = new Gson();
        PlaceRanking placeRanking = gson.fromJson("{" +
                " \"id\":3," +
                " \"name\":\"UPA Outra\"," +
                " \"city\":\"Porto Alegre\"," +
                " \"state\":\"RS\"," +
                " \"category\":\"Saude\"," +
                " \"type\":\"Pronto Atendimento\"," +
                " \"qualityIndex\":[3.8, 3.8, 3.8, 2.5]," +
                " \"rankingPosition\":{" +
                "  \"national\":2," +
                "  \"regional\":2," +
                "  \"state\":2," +
                "  \"municipal\":2" +
                " }," +
                " \"rankingStatus\":{" +
                "  \"national\":\"up\"," +
                "  \"regional\":\"up\"," +
                "  \"state\":\"down\"," +
                "  \"municipal\":\"none\"" +
                " }," +
                " \"lastWeekSurveys\":212," +
                " \"comments\":[" +
                "  {\"uid\":1,\"description\":\"teste de comentario\"}," +
                "  {\"uid\":1,\"description\":\"teste de comentario\"}," +
                "  {\"uid\":1,\"description\":\"teste de comentario\"}" +
                " ]" +
                "}", PlaceRanking.class);


        toolbar.setTitle(placeRanking.getName());

        tvPlace.setText(placeRanking.getCity() + "," + placeRanking.getState());

        tvCategory.setText(placeRanking.getCategory());

        tvPlaceType.setText(placeRanking.getType());

        tvQualityIndice.setText(placeRanking.getQualityIndex().size() > 0 ? String.valueOf(placeRanking.getQualityIndex().get(placeRanking.getQualityIndex().size() - 1)) :"0");

        rvNacional.setUpView("Nacional",placeRanking.getRankingPosition().getNational(),placeRanking.getRankingStatus().getNational());

        rvRegional.setUpView("Regional",placeRanking.getRankingPosition().getRegional(),placeRanking.getRankingStatus().getRegional());

        rvEstadual.setUpView("Estadual",placeRanking.getRankingPosition().getState(),placeRanking.getRankingStatus().getState());

        rvMunicipal.setUpView("Municipal",placeRanking.getRankingPosition().getMunicipal(),placeRanking.getRankingStatus().getMunicipal());

        tvNumberOfAvaliation.setText(String.valueOf(placeRanking.getLastWeekSurveys()));

        graph.removeAllSeries();

        DataPoint[] points = new DataPoint[placeRanking.getQualityIndex().size()];

        for(int i = 0 ; i < placeRanking.getQualityIndex().size() ; i++){
            points[i] = new DataPoint(i,placeRanking.getQualityIndex().get(i));
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(points);

        graph.addSeries(series);
    }

}
