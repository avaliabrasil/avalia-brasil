package org.avaliabrasil.avaliabrasil.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.avaliabrasil.avaliabrasil.avb.LoginActivity;

/**
 * Created by Pedro on 29/02/2016.
 */
public class AvbAuthenticator extends AbstractAccountAuthenticator {
    public final String LOG_TAG = this.getClass().getSimpleName();

    private Context context;

    public AvbAuthenticator(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle editProperties(
            AccountAuthenticatorResponse r, String authTokenType) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Bundle addAccount(
            AccountAuthenticatorResponse r,
            String authTokenType,
            String s2,
            String[] requiredFeatures,
            Bundle options) throws NetworkErrorException {

        Log.d("Auth", "addAccount: ");

        Intent intent = new Intent(context, LoginActivity.class);

        intent.putExtra(Constant.ARG_ACCOUNT_TYPE, "");
        intent.putExtra(Constant.ARG_AUTH_TYPE, "");
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, r);

        Bundle bundle = new Bundle();

        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }


    @Override
    public Bundle confirmCredentials(
            AccountAuthenticatorResponse r,
            Account account,
            Bundle options) throws NetworkErrorException {

        Log.d("Auth", "confirmCredentials: ");
        return null;
    }

    @Override
    public Bundle getAuthToken(
            AccountAuthenticatorResponse r,
            Account account,
            String authTokenType,
            Bundle options) throws NetworkErrorException {

        Intent intent = new Intent(context, LoginActivity.class);

        intent.putExtra(Constant.ARG_ACCOUNT_TYPE, "");
        intent.putExtra(Constant.ARG_AUTH_TYPE, "");
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, r);

        Bundle bundle = new Bundle();

        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }


    @Override
    public String getAuthTokenLabel(String authTokenType) {

        Log.d("Auth", "getAuthTokenLabel: ");

        if (authTokenType.equals(Constant.ACCOUNT_TOKEN_TYPE_USER)) {
            return "user";
        }
        return "";
    }


    @Override
    public Bundle updateCredentials(
            AccountAuthenticatorResponse r,
            Account account,
            String authTokenType, Bundle options) throws NetworkErrorException {

        Log.d("Auth", "updateCredentials: ");

        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures(
            AccountAuthenticatorResponse r,
            Account account, String[] requiredFeatures) throws NetworkErrorException {

        Log.d("Auth", "hasFeatures: ");

        throw new UnsupportedOperationException();
    }
}