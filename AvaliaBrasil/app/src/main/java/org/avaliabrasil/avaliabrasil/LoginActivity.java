package org.avaliabrasil.avaliabrasil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LoginActivity extends  AppCompatActivity {

    //TODO: Implementar variável userId ou outra corretamente!
    private static String USRID = "userId";
    private String userId = "12351 {Pedro Nascimento de Lima!}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
    }

    public void startMainActivity(View view){
        Intent intent_main_activity = new Intent(this,MainActivity.class);
        intent_main_activity.putExtra(USRID, userId);
        startActivity(intent_main_activity);
    }

}
