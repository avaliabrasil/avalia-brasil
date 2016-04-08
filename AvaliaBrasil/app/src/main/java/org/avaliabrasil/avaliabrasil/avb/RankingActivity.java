package org.avaliabrasil.avaliabrasil.avb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.adapters.DividerItemDecoration;
import org.avaliabrasil.avaliabrasil.avb.adapters.PlaceRankingAdapter;
import org.avaliabrasil.avaliabrasil.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceRankingSearch;

import java.util.HashMap;
import java.util.Map;

public class RankingActivity extends AppCompatActivity {

    /**
     *
     */
    private RecyclerView rvRankingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Classificação de Instituições");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvRankingList = (RecyclerView) findViewById(R.id.rvRankingList);

        rvRankingList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(RankingActivity.this);

        rvRankingList.setLayoutManager(mLayoutManager);

        rvRankingList.setItemAnimator(new DefaultItemAnimator());

         /* -------------------------------------------------------------------------------------------------------------- */

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AvaliaBrasilAPIClient.getPlacesRanking(),
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
        PlaceRankingSearch placeRanking = gson.fromJson("{" +
                "\"places\":" +
                "[" +
                "{" +
                "\"rankingPosition\": 1," +
                "\"name\": \"Nome do Lugar 1\"," +
                "\"address\": \"Endereço do Lugar 1\"," +
                "\"qualityIndex\": 3.8" +
                "}," +
                "{" +
                "\"rankingPosition\": 2," +
                "\"name\": \"Nome do Lugar 2\"," +
                "\"address\": \"Endereço do Lugar 2\"," +
                "\"qualityIndex\": 3.6" +
                "}," +
                "{" +
                "\"rankingPosition\": 3," +
                "\"name\": \"Nome do Lugar 3\"," +
                "\"address\": \"Endereço do Lugar 3\"," +
                "\"qualityIndex\": 3.4" +
                "}," +
                "{" +
                "\"rankingPosition\": 4," +
                "\"name\": \"Nome do Lugar 4\"," +
                "\"address\": \"Endereço do Lugar 4\"," +
                "\"qualityIndex\": 3.0" +
                "}," +
                "{" +
                "\"rankingPosition\": 5," +
                "\"name\": \"Nome do Lugar 5\"," +
                "\"address\": \"Endereço do Lugar 5\"," +
                "\"qualityIndex\": 2.8" +
                "}" +
                "]" +
                "}", PlaceRankingSearch.class);


        rvRankingList.setAdapter(new PlaceRankingAdapter(RankingActivity.this,placeRanking.getPlaceRankings()));

        rvRankingList.setHasFixedSize(true);
    }

}
