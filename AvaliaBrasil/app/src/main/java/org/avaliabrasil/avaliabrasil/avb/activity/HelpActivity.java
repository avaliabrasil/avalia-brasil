package org.avaliabrasil.avaliabrasil.avb.activity;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil.R;

/**
 * Created by Developer on 11/04/2016.
 */
public class HelpActivity extends AppCompatActivity {
    /**
     * The {@link AccountManager} for getting user informations.
     */
    private AccountManager manager;

    private TextView tvhelpText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        tvhelpText = (TextView) findViewById(R.id.tvHelpText);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Ajuda");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
