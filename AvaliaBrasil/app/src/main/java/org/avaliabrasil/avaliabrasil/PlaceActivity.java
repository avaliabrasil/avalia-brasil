package org.avaliabrasil.avaliabrasil;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class PlaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

       // toolbar.setTitle("UPA Zona Norte");

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        //setSupportActionBar();
        toolbarLayout.setTitle(getString(R.string.temp_place_name));

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Comentado vamos ver no que dá
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
