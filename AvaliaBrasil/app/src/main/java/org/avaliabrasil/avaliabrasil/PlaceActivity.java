package org.avaliabrasil.avaliabrasil;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class PlaceActivity extends AppCompatActivity {

    public static String GOOGLEPLACEID = "googlePlacesId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        // Definindo o título da Toolbar
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(getPlaceName());

        // Ativando a opção voltar da Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public String getPlaceName(){
        // Temporário, enquanto não implementamos um template provider

        // Temporário, apenas para testar intent:
        return getIntent().getExtras().getString(GOOGLEPLACEID);
    }

    public void startEvaluationActivity (View view) {
        Intent intent_evaluation_activity = new Intent(this, EvaluationActivity.class);
        startActivity(intent_evaluation_activity);

    }

    public void startStatisticsActivity (View view) {
        Intent intent_statistics_activity = new Intent(this, PlaceStatisticsActivity.class);
        startActivity(intent_statistics_activity);

    }

    public void startNewPlaceActivity (View view) {
        Intent intent_new_place_activity = new Intent(this, NewPlaceActivity.class);
        startActivity(intent_new_place_activity);

    }
}
