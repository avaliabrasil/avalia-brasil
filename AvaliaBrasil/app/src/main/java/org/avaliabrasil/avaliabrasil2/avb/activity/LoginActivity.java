package org.avaliabrasil.avaliabrasil2.avb.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.avaliabrasil.avaliabrasil2.R;
import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil2.avb.util.AvaliaBrasilApplication;
import org.avaliabrasil.avaliabrasil2.avb.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.etc.User;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.etc.UserToken;
import org.avaliabrasil.avaliabrasil2.avb.sync.Constant;
import org.avaliabrasil.avaliabrasil2.avb.util.LocationPermission;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.GraphRequest.TAG;
import static org.avaliabrasil.avaliabrasil2.avb.sync.Constant.ACCOUNT_TOKEN;

public class LoginActivity extends AccountAuthenticatorActivity {
    public final String LOG_TAG = this.getClass().getSimpleName();

    /**
     * The {@link AccountManager} for check the users and/or add it to the {@link Account}
     */
    private AccountManager accountManager;

    /**
     * Default user class for get the email/name and photo.
     */
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

    /**
     *
     */
    private LocationPermission locationPermission;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountManager = AccountManager.get(this);

        locationPermission = new LocationPermission(this);

        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(LoginActivity.this);
        }

        setContentView(R.layout.activity_login);

        user = ((AvaliaBrasilApplication) getApplication()).getUser();

        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton = (LoginButton) findViewById(R.id.login_button);
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        /** Prevent user to log out via the login page */
        if (accessToken != null) {
            facebookLoginButton.setVisibility(View.GONE);
        }

        /** Callback for the facebook API to get user details */
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    final String email = object.getString("email");
                                    final String name = object.getString("name");
                                    final String userID = object.getString("id");

                                    user.setEmail(email);
                                    user.setName(name);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                URL url = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
                                                String fileName = url.getFile();

                                                InputStream is = url.openStream();

                                                final Bitmap bit = BitmapFactory.decodeStream(is);

                                                FileOutputStream out;
                                                try {
                                                    out = LoginActivity.this.openFileOutput("profile.jpg", Context.MODE_PRIVATE);
                                                    bit.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                                    out.close();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            checkForPermissions();
                                        }
                                    }).start();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

    public void startMainActivity(View view) {
        if (accountManager.getAccountsByType(Constant.ACCOUNT_TYPE).length > 0) {
            Snackbar
                    .make(findViewById(R.id.layout), getResources().getString(R.string.user_already_logged), Snackbar.LENGTH_LONG)
                    .show();
        } else {
            checkForPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LocationPermission.INITIAL_REQUEST:
                if (locationPermission.canAccessFineLocation() && locationPermission.canAccessCoarseLocation()) {
                    getUserToken(LoginActivity.this);
                } else {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.location_access_needed), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * Get the user token and add it to the {@link Account}
     *
     * @param context
     */
    public void getUserToken(final Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AvaliaBrasilAPIClient.getUserTokenURL(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("LoginActivity", "onResponse: " + response);
                        Gson gson = new Gson();
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jo = (JsonObject) jsonParser.parse(response);

                        if(jo.has("Error")){
                            Toast.makeText(context, "Ocorreu um erro com o servidor, entre em contato com o suporte!", Toast.LENGTH_SHORT).show();
                        }else if(jo.has("authorized")){
                            if(jo.get("authorized").getAsBoolean()){
                                Intent intent_main_activity = new Intent(context, MainActivity.class);
                                intent_main_activity.putExtra("userId", user.getAndroid_id());

                                Account account = new Account(user.getName() == null ? "Anonimo" : user.getName(), Constant.ACCOUNT_TYPE);

                                Bundle bundle = new Bundle();

                                bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, Constant.ACCOUNT_TYPE);
                                bundle.putString(AccountManager.KEY_ACCOUNT_NAME, user.getName() == null ? "Anonimo" : user.getName());
                                bundle.putString(AccountManager.KEY_AUTHTOKEN, user.getToken());
                                bundle.putString(Constant.ACCOUNT_EMAIL, user.getEmail() == null ? "avaliabrasil@gmail.com" : user.getEmail());
                                bundle.putString(Constant.ACCOUNT_EMAIL, user.getEmail() == null ? "avaliabrasil@gmail.com" : user.getEmail());
                                bundle.putString(Constant.ACCOUNT_TOKEN, jo.get("userId").getAsString());
                                bundle.putString(Constant.ARG_ACCOUNT_TYPE, user.getName() == null ? Constant.ACCOUNT_TYPE_USER : Constant.ACCOUNT_TYPE_FACEBOOK);

                                accountManager.addAccountExplicitly(account, null, bundle);

                                setAccountAuthenticatorResult(bundle);

                                //TODO DYNAMIC GET THE CATEGORY AND THE PLACE TYPE

                                if (accountManager.getAccountsByType(Constant.ACCOUNT_TYPE).length > 0) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("showSplash", false);
                                    startActivity(intent);
                                }
                            }else{
                                Toast.makeText(context, "Não foi possível autoriza-lo!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                String android_id = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                Log.d("AndroidID", "onResponse: " + android_id);
                user.setAndroid_id(android_id);
                JsonObject jsonObject = new JsonObject();
                jsonObject.add("deviceId", new JsonPrimitive(android_id));
                Log.d("AndroidID", "onResponse: " + jsonObject.toString());
                try {
                    return jsonObject.toString() == null ? null : jsonObject.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", jsonObject.toString(), "utf-8");
                    return null;
                }
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);
    }

    /**
     * Check the user permissions to make sure the app work property in the phone
     */
    public void checkForPermissions() {
        if (!locationPermission.canAccessCoarseLocation() || !locationPermission.canAccessFineLocation()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(LocationPermission.INITIAL_PERMS, LocationPermission.INITIAL_REQUEST);
            }
        } else {
            getUserToken(LoginActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this, ExitActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
