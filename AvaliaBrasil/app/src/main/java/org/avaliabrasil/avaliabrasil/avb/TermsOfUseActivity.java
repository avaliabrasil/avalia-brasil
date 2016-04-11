package org.avaliabrasil.avaliabrasil.avb;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.sync.Constant;
import org.avaliabrasil.avaliabrasil.util.CircleTransform;

import java.io.FileInputStream;
import java.io.IOException;

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
