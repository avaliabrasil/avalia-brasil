package org.avaliabrasil.avaliabrasil.avb;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.CommentFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.LikertFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.NumberFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.ShareEvaluateFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.TransactionFragment;
import org.avaliabrasil.avaliabrasil.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Anwser;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EvaluationActivity extends AppCompatActivity implements View.OnClickListener{
    public final String LOG_TAG = this.getClass().getSimpleName();

    /**
     *
     */
    private Toolbar toolbar;

    /**
     *
     */
    private TransactionFragment newFragment;

    /**
     *
     */
    private FragmentTransaction ft;

    /**
     *
     */
    private List<Question> questions = new ArrayList<Question>();

    /**
     *
     */
    private List<Anwser> anwsers = new ArrayList<Anwser>();

    /**
     *
     */
    private Bundle args = new Bundle();

    /**
     *
     */
    private int questionCursor = 0;

    /**
     *
     */
    private String place_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        if(getIntent().getExtras() == null || getIntent().getExtras().getString("name") == null){
            finish();
        }

        // Definindo a Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TODO: Alterar com o título dos dados reais
        getSupportActionBar().setTitle(getIntent().getExtras().getString("name"));

        if (savedInstanceState == null) {
            questions = (List<Question>) getIntent().getSerializableExtra("questions");

            place_id = getIntent().getExtras().getString("placeid");

            newFragment = getNextQuestionFragment();

            ft = getFragmentManager().beginTransaction();

            if(newFragment != null){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, newFragment).commit();
            }else{
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new ShareEvaluateFragment()).commit();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnSubmit:
                if(newFragment.isAnwser()){
                   if(newFragment instanceof LikertFragment){
                       anwsers.add(new Anwser("0",newFragment.getAnwser(),null,null));
                    } else if(newFragment instanceof CommentFragment){
                       anwsers.add(new Anwser("0",null,newFragment.getAnwser(),null));
                    } else if(newFragment instanceof NumberFragment){
                       anwsers.add(new Anwser("0",null,null,newFragment.getAnwser()));
                    }

                    newFragment = getNextQuestionFragment();

                    if(newFragment != null){
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, newFragment).commit();
                    }else{
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new ShareEvaluateFragment()).commit();
                    }
                }else{
                    Toast.makeText(EvaluationActivity.this,"Você não respondeu a questão",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnShare:
                finish();
                break;
            case R.id.tvSkip:
                finish();
                break;
        }
    }

    //TODO Still need to be test with the API
    private void sendAnwserBack(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AvaliaBrasilAPIClient.postAnwsers(place_id),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> params = new HashMap<String, String>();

                JsonObject response = new JsonObject();

                //TODO ADD THE USER TOKEN
                response.addProperty("userID","");

                JsonArray anwserArray = new JsonArray();

                for(Anwser awn : anwsers){
                    JsonObject obj = new JsonObject();

                    obj.addProperty("question_id",awn.getQuestion_id());

                    JsonArray anwser = new JsonArray();

                    anwser.add(awn.getNumber());
                    anwser.add(awn.getComment());
                    anwser.add(awn.getLikert());

                    obj.add("answer",anwser);

                    anwserArray.add(obj);
                }

                response.add("answers",anwserArray);

                return params;
            }
        };
        Volley.newRequestQueue(EvaluationActivity.this).add(stringRequest);
    }

    public TransactionFragment getNextQuestionFragment(){
        TransactionFragment nextFragment = null;

        if(questions.size() > questionCursor){
            String type = questions.get(questionCursor).getQuestionType();

            if(type.contentEquals(Question.QuestionTypes.IS_COMMENT.getType())){
                nextFragment = new CommentFragment();
            }else if(type.contentEquals(Question.QuestionTypes.IS_LIKERT.getType())){
                nextFragment = new LikertFragment();
            }else if(type.contentEquals(Question.QuestionTypes.IS_NUMBER.getType())){
                nextFragment = new NumberFragment();
            }
            args.putSerializable("question",questions.get(questionCursor));

            nextFragment.setArguments(args);

            questionCursor++;

            return nextFragment;
        }else{
            return null;
        }
    }
}
