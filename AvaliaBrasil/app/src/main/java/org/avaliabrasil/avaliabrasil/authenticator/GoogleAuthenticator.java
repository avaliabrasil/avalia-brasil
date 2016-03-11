package org.avaliabrasil.avaliabrasil.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by Developer on 11/03/2016.
 */
public class GoogleAuthenticator extends AccountManager {

    public final String LOG_TAG = this.getClass().getSimpleName();

    private GoogleSignInAccount account;

    private Context context;


    GoogleAuthenticator() {
        super();
    }
}
