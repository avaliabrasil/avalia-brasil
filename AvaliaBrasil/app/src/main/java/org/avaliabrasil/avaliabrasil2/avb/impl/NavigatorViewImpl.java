package org.avaliabrasil.avaliabrasil2.avb.impl;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import org.avaliabrasil.avaliabrasil2.R;
import org.avaliabrasil.avaliabrasil2.avb.activity.HelpActivity;
import org.avaliabrasil.avaliabrasil2.avb.activity.MainActivity;
import org.avaliabrasil.avaliabrasil2.avb.activity.RankingActivity;
import org.avaliabrasil.avaliabrasil2.avb.activity.TermsOfUseActivity;
import org.avaliabrasil.avaliabrasil2.avb.dao.AvBDBHelper;
import org.avaliabrasil.avaliabrasil2.avb.util.AvaliaBrasilApplication;
import org.avaliabrasil.avaliabrasil2.avb.sync.Constant;
import org.avaliabrasil.avaliabrasil2.avb.util.Utils;

import java.io.IOException;

/**
 * Created by Developer on 17/05/2016.
 */
public class NavigatorViewImpl implements NavigationView.OnNavigationItemSelectedListener {

    private Activity activity;

    private AvaliaBrasilApplication avaliaBrasilApplication;

    private ProgressDialog progress;

    /**
     * The {@link AccountManager} for getting user informations.
     */
    private AccountManager manager;

    public NavigatorViewImpl(Activity activity , AvaliaBrasilApplication avaliaBrasilApplication , AccountManager manager){
        this.activity = activity;
        this.avaliaBrasilApplication = avaliaBrasilApplication;
        this.manager = manager;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.btnSearchInstitution:
                if(!(activity instanceof MainActivity)){
                    activity.startActivity(new Intent(activity, MainActivity.class));
                }
                break;
            case R.id.btnClassification:
                if(!(activity instanceof RankingActivity)){
                    Intent rankingIntent = new Intent(activity, RankingActivity.class);
                    rankingIntent.putExtra("latitude", avaliaBrasilApplication.getLocation() != null ? avaliaBrasilApplication.getLocation().getLatitude() : 0);
                    rankingIntent.putExtra("longitude", avaliaBrasilApplication.getLocation() != null ? avaliaBrasilApplication.getLocation().getLongitude() : 0);

                    activity.startActivity(rankingIntent);
                }
                break;
            case R.id.btnLogout:

                progress = ProgressDialog.show(activity, activity.getResources().getString(R.string.progress_dialog_title),
                        activity.getResources().getString(R.string.progress_dialog_message), false);

                progress.show();

                if (!FacebookSdk.isInitialized()) {
                    FacebookSdk.sdkInitialize(activity);
                }

                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut();
                }

                for (Account c : manager.getAccountsByType(Constant.ACCOUNT_TYPE)) {
                    manager.removeAccount(c, null, null);
                }

                AvBDBHelper helper = new AvBDBHelper(activity);
                helper.clearAllData(helper.getWritableDatabase());

                Utils.removeProfilePhoto(activity);


                manager.addAccount(Constant.ACCOUNT_TYPE, Constant.ACCOUNT_TOKEN_TYPE_USER, null, null,activity, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle bundle = future.getResult();

                        } catch (OperationCanceledException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (AuthenticatorException e) {
                            e.printStackTrace();
                        }
                    }
                }, null);

                progress.dismiss();

                break;
            case R.id.btnHelp:
                activity.startActivity(new Intent(activity, HelpActivity.class));
                break;
            /** Retirando termos de uso do app. Vai estar no site.
            case R.id.btnTermsOfUse:
                activity.startActivity(new Intent(activity, TermsOfUseActivity.class));
                break;
             **/
        }

        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
