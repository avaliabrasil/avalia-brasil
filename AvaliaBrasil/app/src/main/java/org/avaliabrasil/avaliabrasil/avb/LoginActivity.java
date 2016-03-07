package org.avaliabrasil.avaliabrasil.avb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.avaliabrasil.avaliabrasil.R;

public class LoginActivity extends  AppCompatActivity {
    public final String LOG_TAG = this.getClass().getSimpleName();

    //TODO: Implementar variável userId ou outra corretamente!
    public static String USRID = "userId";

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        // TODO: Setando a variável userId
        userId = getUserId();

    }

    public void startMainActivity(View view){

        Intent intent_main_activity = new Intent(this,MainActivity.class);
        intent_main_activity.putExtra(USRID, userId);
        startActivity(intent_main_activity);
    }

    // TODO: Login do Usuário..
    private int getUserId () {
        return 1;
    }

}
