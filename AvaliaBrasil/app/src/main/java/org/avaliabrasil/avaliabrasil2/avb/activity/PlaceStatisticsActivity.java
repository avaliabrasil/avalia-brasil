package org.avaliabrasil.avaliabrasil2.avb.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

import org.avaliabrasil.avaliabrasil2.R;
import org.avaliabrasil.avaliabrasil2.avb.adapters.CommentAdapter;
import org.avaliabrasil.avaliabrasil2.avb.adapters.DividerItemDecoration;
import org.avaliabrasil.avaliabrasil2.avb.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.PlaceStatistics;
import org.avaliabrasil.avaliabrasil2.avb.util.Utils;
import org.avaliabrasil.avaliabrasil2.avb.view.RankingView;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class PlaceStatisticsActivity extends AppCompatActivity implements View.OnClickListener {
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

    /**
     *
     */
    private String name;

    /**
     *
     */
    private PlaceStatistics placeStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        placeId = getIntent().getExtras().getString("placeid", "");
        name = getIntent().getExtras().getString("name", "");

        new Loading().execute();

    }

    private void fetchData(String response) {
        Gson gson = new Gson();

        placeStats = gson.fromJson(new String((Utils.normalizeAvaliaBrasilResponse(response)).replace("\\\\", "\\").getBytes(Charset.defaultCharset())), PlaceStatistics.class);

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

        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);

        rvComments = (RecyclerView) findViewById(R.id.rvComments);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PlaceStatisticsActivity.this);
        rvComments.setLayoutManager(mLayoutManager);
        rvComments.setItemAnimator(new DefaultItemAnimator());
        rvComments.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

        toolbar.setTitle(placeStats.getName());

        tvPlace.setText(placeStats.getCity() + "," + placeStats.getState());

        tvCategory.setText(placeStats.getCategory());

        tvPlaceType.setText(placeStats.getType());

        tvQualityIndice.setText(placeStats.getQualityIndex().size() > 0 ? String.valueOf(placeStats.getQualityIndex().get(placeStats.getQualityIndex().size() - 1).getValue()) : "0");

        rvNacional.setUpView("Nacional", placeStats.getRankingPosition().getNational(), placeStats.getRankingStatus().getNational());

        rvNacional.setOnClickListener(this);

        rvRegional.setUpView("Regional", placeStats.getRankingPosition().getRegional(), placeStats.getRankingStatus().getRegional());

        rvRegional.setOnClickListener(this);

        rvEstadual.setUpView("Estadual", placeStats.getRankingPosition().getState(), placeStats.getRankingStatus().getState());

        rvEstadual.setOnClickListener(this);

        rvMunicipal.setUpView("Municipal", placeStats.getRankingPosition().getMunicipal(), placeStats.getRankingStatus().getMunicipal());

        rvMunicipal.setOnClickListener(this);

        tvNumberOfAvaliation.setText(String.valueOf(placeStats.getLastWeekSurveys()));

        graph.removeAllSeries();

        DataPoint[] points = new DataPoint[placeStats.getQualityIndex().size()];

        for (int i = 0; i < placeStats.getQualityIndex().size(); i++) {
            points[i] = new DataPoint(i, placeStats.getQualityIndex().get(i).getValue());
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(points);

        graph.addSeries(series);

        rvComments.setAdapter(new CommentAdapter(PlaceStatisticsActivity.this, placeStats.getComments()));

        rvComments.setHasFixedSize(true);
    }

    @Override
    public void onClick(View v) {

        Intent rankingIntent = new Intent(PlaceStatisticsActivity.this, RankingActivity.class);

        if (placeStats != null) {
            rankingIntent.putExtra("name", placeStats.getName());
            rankingIntent.putExtra("city", placeStats.getCity());
            rankingIntent.putExtra("state", placeStats.getState());
            rankingIntent.putExtra("category", placeStats.getCategory());
            rankingIntent.putExtra("type", placeStats.getType());
        }

        switch (v.getId()) {

            case R.id.rvNacional:
                rankingIntent.putExtra("rankingType", "nacional");

                break;
            case R.id.rvRegional:
                rankingIntent.putExtra("rankingType", "regional");

                break;
            case R.id.rvEstadual:
                rankingIntent.putExtra("rankingType", "estadual");

                break;
            case R.id.rvMunicipal:
                rankingIntent.putExtra("rankingType", "municipal");

                break;

        }

        startActivity(rankingIntent);
    }

    private class Loading extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            try {
                TextView view = new TextView(PlaceStatisticsActivity.this);
                view.setText(getResources().getString(R.string.progress_dialog_message));
                view.setGravity(Gravity.CENTER);
                view.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
                view.setHeight(ViewGroup.LayoutParams.FILL_PARENT);
                setContentView(view);
            } catch (Exception e) {
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
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    return params;
                }
            };
            Volley.newRequestQueue(PlaceStatisticsActivity.this).add(stringRequest);
            return "Executed";
        }
    }

}
