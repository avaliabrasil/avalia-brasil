package org.avaliabrasil.avaliabrasil.avb;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.adapters.CommentAdapter;
import org.avaliabrasil.avaliabrasil.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceStatistics;
import org.avaliabrasil.avaliabrasil.view.RankingView;

import java.util.HashMap;
import java.util.Map;

public class PlaceStatisticsActivity extends AppCompatActivity {
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


    /**
     *
     */
    private RecyclerView rvComments;

    /**
     *
     */
    private String placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        placeId = getIntent().getExtras().getString("placeid","");

        new Loading().execute();

    }

    private void fetchData(String response){
        Gson gson = new Gson();
        PlaceStatistics placeRanking = gson.fromJson("{" +
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
                "  {\"uid\":1,\"description\":\"1 - teste de comentario\"}," +
                "  {\"uid\":2,\"description\":\"2 - teste de comentario\"}," +
                "  {\"uid\":3,\"description\":\"3 - teste de comentario\"}" +
                " ]" +
                "}", PlaceStatistics.class);

        setContentView(R.layout.activity_place_statistics);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

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

        rvComments = (RecyclerView) findViewById(R.id.rvComments);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PlaceStatisticsActivity.this,LinearLayoutManager.HORIZONTAL, false);
        rvComments.setLayoutManager(mLayoutManager);
        rvComments.setItemAnimator(new DefaultItemAnimator());

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

        rvComments.setAdapter(new CommentAdapter(PlaceStatisticsActivity.this,placeRanking.getComments()));

        rvComments.setHasFixedSize(true);
    }


    private class Loading extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            try{
                TextView view = new TextView(PlaceStatisticsActivity.this);
                view.setText("Aguarde enquanto buscamos os dados...");
                view.setGravity(Gravity.CENTER);
                view.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
                view.setHeight(ViewGroup.LayoutParams.FILL_PARENT);
                setContentView(view);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, AvaliaBrasilAPIClient.getPlaceStatistics(placeId),
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
            Volley.newRequestQueue(PlaceStatisticsActivity.this).add(stringRequest);
            return "Executed";
        }


    }

}
