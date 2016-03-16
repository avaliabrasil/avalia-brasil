package org.avaliabrasil.avaliabrasil.avb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

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

import android.provider.Settings.Secure;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.UserToken;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends  AppCompatActivity {
    public final String LOG_TAG = this.getClass().getSimpleName();

    public static String USRID = "userId";

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


    private String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        FacebookSdk.sdkInitialize(LoginActivity.this);

        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        facebookLoginButton = (LoginButton) findViewById(R.id.login_button);

        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));

        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Profile profile = Profile.getCurrentProfile();
                startMainActivity(null);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //TODO send the info to the server, and fetch it too.
    public void startMainActivity(View view){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AvaliaBrasilAPIClient.getUserToken(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("LoginActivity", "onResponse: " + response);

                        Gson gson = new Gson();
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jo = (JsonObject)jsonParser.parse(response);

                        UserToken userToken = gson.fromJson(jo.get("data").getAsJsonObject(), UserToken.class);

                        Log.d("LoginActivity", userToken.getToken());
                        Log.d("LoginActivity", userToken.getExpires());

                        Log.d("LoginActivity", "id: " + android_id);

                        Intent intent_main_activity = new Intent(LoginActivity.this,MainActivity.class);
                        intent_main_activity.putExtra(USRID, android_id);
                        startActivity(intent_main_activity);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams () {
                android_id = Secure.getString(getBaseContext().getContentResolver(),
                        Secure.ANDROID_ID);

                Map<String, String> params = new HashMap<String, String>();
                JsonObject jsonObject = new JsonObject();
                jsonObject.add("userId",new JsonPrimitive(android_id));
                return params;
            }
        };
        Volley.newRequestQueue(LoginActivity.this).add(stringRequest);
    }
}
