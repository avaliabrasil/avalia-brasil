package org.avaliabrasil.avaliabrasil.avb;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.data.AvaliaBrasilApplication;
import org.avaliabrasil.avaliabrasil.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.User;
import org.avaliabrasil.avaliabrasil.rest.javabeans.UserToken;
import org.avaliabrasil.avaliabrasil.sync.Constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AccountAuthenticatorActivity {
    public final String LOG_TAG = this.getClass().getSimpleName();

    /**
     *  TODO refatorar para classe única
     */
    private static final int INITIAL_REQUEST=1337;
    private static final int LOCATION_REQUEST=INITIAL_REQUEST+3;
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private AccountManager accountManager;


    private User user;
    /**
     * Facebook login button
     *
     * @see <a href="https://developers.facebook.com/docs/facebook-login/android"> Login Tutorial</a>
     */
    private LoginButton facebookLoginButton;

    /**
     * Facebook callBackManager for login
     *
     * @see <a href="https://developers.facebook.com/docs/facebook-login/android"> Login Tutorial</a>
     */
    private CallbackManager callbackManager;

    //Manter logon mesmo com usuario logado.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountManager =  AccountManager.get(this);

        FacebookSdk.sdkInitialize(LoginActivity.this);

        setContentView(R.layout.activity_login);

        user = ((AvaliaBrasilApplication)getApplication()).getUser();

        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton = (LoginButton) findViewById(R.id.login_button);
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));

        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Profile profile = Profile.getCurrentProfile();
                user.setName(profile.getName());
                checkForPermissions();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void startMainActivity(View view){

        checkForPermissions();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case INITIAL_REQUEST:
                if (canAccessFineLocation()&&canAccessCoarseLocation()) {
                    getUserToken(LoginActivity.this);
                }
                else {
                    Toast.makeText(LoginActivity.this,"This application need the access location to work property",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    private boolean canAccessFineLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean canAccessCoarseLocation() {
        return(hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION));
    }


    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
        }
        return true;
    }

    public void getUserToken(final Context context){


        StringRequest stringRequest = new StringRequest(Request.Method.POST, AvaliaBrasilAPIClient.getUserTokenURL(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jo = (JsonObject)jsonParser.parse(response);

                        UserToken userToken = gson.fromJson(jo.get("data").getAsJsonObject(), UserToken.class);

                        user.setToken(userToken.getToken());

                        Intent intent_main_activity = new Intent(context,MainActivity.class);
                        intent_main_activity.putExtra("userId", user.getAndroid_id());

                        Account account = new Account(user.getName() == null ? "Anonimo" : user.getName(), Constant.ACCOUNT_TYPE);


                        Bundle bundle = new Bundle();

                        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE,Constant.ACCOUNT_TYPE);
                        bundle.putString(AccountManager.KEY_ACCOUNT_NAME,user.getName() == null ? "Anonimo" : user.getName());
                        bundle.putString(AccountManager.KEY_AUTHTOKEN,user.getToken());

                        accountManager.addAccountExplicitly(account,null,bundle);
                        accountManager.setAuthToken(account,Constant.ACCOUNT_TOKEN_TYPE_USER,user.getToken());


                        setAccountAuthenticatorResult(bundle);

                        if(accountManager.getAccountsByType(Constant.ACCOUNT_TYPE).length > 0){
                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(LoginActivity.this,"Não foi possível realizar o login, verifique sua internet!",Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams () {
                String android_id = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                user.setAndroid_id(android_id);
                Map<String, String> params = new HashMap<String, String>();
                JsonObject jsonObject = new JsonObject();
                jsonObject.add("userId",new JsonPrimitive(android_id));
                return params;
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);
    }

    public void checkForPermissions(){
         if (!canAccessCoarseLocation()||!canAccessFineLocation()) {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                 requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
             }
         }else{
             getUserToken(LoginActivity.this);
         }
    }
}
