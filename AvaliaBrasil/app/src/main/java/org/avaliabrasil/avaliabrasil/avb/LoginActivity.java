package org.avaliabrasil.avaliabrasil.avb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.rest.AvaliaBrasilAPIClient;

import java.util.Arrays;

public class LoginActivity extends  AppCompatActivity {
    public final String LOG_TAG = this.getClass().getSimpleName();


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

    public void startMainActivity(View view){
        AvaliaBrasilAPIClient.getUserToken(LoginActivity.this);
    }
}
