package org.avaliabrasil.avaliabrasil.avb;

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
public class TermsOfUseActivity extends AppCompatActivity {
    /**
     * The {@link AccountManager} for getting user informations.
     */

    private TextView tvTermsOfUse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);

        tvTermsOfUse = (TextView) findViewById(R.id.tvHelpText);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Termos de Uso");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
