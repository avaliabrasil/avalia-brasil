package org.avaliabrasil.avaliabrasil2.avb.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.avaliabrasil.avaliabrasil2.R;
import org.avaliabrasil.avaliabrasil2.avb.adapters.CommentAdapter;
import org.avaliabrasil.avaliabrasil2.avb.adapters.DividerItemDecoration;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.PlaceStatistics;
import org.avaliabrasil.avaliabrasil2.avb.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil2.avb.util.DateAsXAxisLabel;
import org.avaliabrasil.avaliabrasil2.avb.util.Utils;
import org.avaliabrasil.avaliabrasil2.avb.view.RankingView;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
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

        Log.d("PlaceStatisticsActivity", "fetchData: " + response);

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

        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);

        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabel(PlaceStatisticsActivity.this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(6);
        graph.getGridLabelRenderer().setHumanRounding(false);

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

        prepareGraphData();


        rvComments.setAdapter(new CommentAdapter(PlaceStatisticsActivity.this, placeStats.getComments()));

        rvComments.setHasFixedSize(true);
    }

    private void prepareGraphData() {
        DataPoint[] points = new DataPoint[6];
        int month;
        int year;
        Date firstMonth = null;
        Date lastMonth = null;

        for (int i = 0; i < points.length; i++) {
            Calendar calendar = Calendar.getInstance();
            month = calendar.get(Calendar.MONTH) + i - 6;
            year = calendar.get(Calendar.YEAR);
            calendar.set(Calendar.DATE , 1);
            calendar.set(Calendar.HOUR , 1);
            calendar.set(Calendar.MINUTE , 1);
            calendar.set(Calendar.SECOND , 1);

            Log.d("PlaceStatisticsActivity", "B Month/year: " + month + "/" + year);
            if(month <= 0){
                calendar.set(Calendar.YEAR,year - 1);
                calendar.set(Calendar.MONTH,11 + month);
                month = calendar.get(Calendar.MONTH);
                year = calendar.get(Calendar.YEAR);
            }else if(month > 11){
                calendar.set(Calendar.YEAR,year + 1);
                calendar.set(Calendar.MONTH,(month - 12));
                month = calendar.get(Calendar.MONTH);
                year = calendar.get(Calendar.YEAR);
            }else{
                calendar.set(Calendar.MONTH,month + 1);
                month = calendar.get(Calendar.MONTH);
            }
            Log.d("PlaceStatisticsActivity", "A Month/year: " + month + "/" + year + "\n");
            points[i] = new DataPoint(calendar.getTime(), 0);
            firstMonth = checkIfIsBeforeDate(firstMonth,calendar.getTime());
            lastMonth = checkIfIsAfterDate(lastMonth,calendar.getTime());
        }

        DataPoint newDp;
        for (int i = 0; i < placeStats.getQualityIndex().size(); i++) {
            Calendar calendar = Calendar.getInstance();

            String requestMonth = placeStats.getQualityIndex().get(i).getMonth();

            Log.d("PlaceStatistics", "fetchData: " + requestMonth);

            String requestYear = requestMonth.substring(0,4);
            Log.d("PlaceStatistics", "fetchData: " + requestYear);
            requestMonth = requestMonth.substring(5);

            calendar.set(Calendar.MONTH,Integer.valueOf(requestMonth)-1);
            calendar.set(Calendar.YEAR,Integer.valueOf(requestYear));
            calendar.set(Calendar.DATE , 1);
            calendar.set(Calendar.HOUR , 1);
            calendar.set(Calendar.MINUTE , 1);
            calendar.set(Calendar.SECOND , 1);

            for (int j = 0; j < points.length; j++) {
                DataPoint dp = points[j];
                newDp =  new DataPoint(calendar.getTime(), placeStats.getQualityIndex().get(i).getValue());

                Calendar c2 = Calendar.getInstance();
                c2.setTimeInMillis((long) dp.getX());

                Log.d("PlaceStatistics", "m1: " + calendar.get(Calendar.MONTH) + " | m2: " + c2.get(Calendar.MONTH));

                if(calendar.get(Calendar.MONTH) == c2.get(Calendar.MONTH)){
                    Log.d("PlaceStatistics", c2.getTime().toString());
                    points[j] = newDp;
                    break;
                }
            }
            Log.d("PlaceStatistics", "value: " + points[i].getY());
        }

        Calendar c = Calendar.getInstance();
        c.setTime(firstMonth);
        c.add(Calendar.MONTH,-1);
        graph.getViewport().setMinX(c.getTime().getTime());
        c.setTime(lastMonth);
        c.add(Calendar.MONTH,1);
        graph.getViewport().setMaxX(c.getTime().getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(points);
        PointsGraphSeries<DataPoint> series2 = new PointsGraphSeries<DataPoint>(points);
        series2.setSize(7);
        graph.addSeries(series);
        graph.addSeries(series2);
    }

    private Date checkIfIsBeforeDate(
            Date firstMonth,
            Date date){

        if(firstMonth == null){
            return date;
        }else{
            if(firstMonth.after(date)){
                return date;
            }
        }
        return  firstMonth;
    }

    private Date checkIfIsAfterDate(
            Date lastMonth,
            Date date){

        if(lastMonth == null){
            return date;
        }else{
            if(lastMonth.before(date)){
                return date;
            }
        }
        return lastMonth;
    }

    @Override
    public void onClick(View v) {

        Intent rankingIntent = new Intent(PlaceStatisticsActivity.this, RankingActivity.class);

        StringBuilder builder = new StringBuilder();
        if (placeStats != null) {
            rankingIntent.putExtra("name", placeStats.getName());
            rankingIntent.putExtra("city", placeStats.getCity());
            rankingIntent.putExtra("state", placeStats.getState());
            rankingIntent.putExtra("category", placeStats.getCategory());
            rankingIntent.putExtra("type", placeStats.getType());

            builder.append("name" + placeStats.getName());
            builder.append("\ncity" + placeStats.getCity());
            builder.append("\nstate" + placeStats.getState());
            builder.append("\ncategory" + placeStats.getCategory());
            builder.append("\ntype" + placeStats.getType());

        }

        switch (v.getId()) {

            case R.id.rvNacional:
                rankingIntent.putExtra("rankingType", "nacional");

                break;
            case R.id.rvRegional:
                rankingIntent.putExtra("rankingType", "regional");
                rankingIntent.putExtra("webId",placeStats.getId_region());

                break;
            case R.id.rvEstadual:
                rankingIntent.putExtra("rankingType", "estadual");
                rankingIntent.putExtra("webId",placeStats.getId_state());

                break;
            case R.id.rvMunicipal:
                rankingIntent.putExtra("rankingType", "municipal");
                rankingIntent.putExtra("webId",placeStats.getId_city());

                break;

        }
        builder.append("\nrankingType"+rankingIntent.getExtras().getString("rankingType"));
        Log.d("PlaceStatisticsActivity", "onClick: " + builder.toString());

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
            }){
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
