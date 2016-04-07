package org.avaliabrasil.avaliabrasil.avb;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.CommentFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.LikertFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.NewPlaceFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.NumberFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.ShareEvaluateFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.TransactionFragment;
import org.avaliabrasil.avaliabrasil.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Anwser;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Holder;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Instrument;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Question;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluationActivity extends AppCompatActivity implements View.OnClickListener {
    public final String TAG = this.getClass().getSimpleName();

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
    private Holder holder;

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
    private int instrumentCursor = 0;

    /**
     *
     */
    private int groupCursor = 0;

    /**
     *
     */
    private String place_id;

    /**
     *
     */
    private ImageView ivPlace;

    /**
     *
     */
    private JsonObject response = new JsonObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        if (getIntent().getExtras() == null || getIntent().getExtras().getString("name") == null) {
            finish();
        }

        // Definindo a Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivPlace = (ImageView) findViewById(R.id.ivPlace);

        //Picasso.with(EvaluationActivity.this).load(new Uri()).into(ivPlace);

        //TODO: Alterar com o título dos dados reais
        getSupportActionBar().setTitle(getIntent().getExtras().getString("name"));

        if (savedInstanceState == null) {
            holder = (Holder) getIntent().getSerializableExtra("holder");

            place_id = getIntent().getExtras().getString("placeid");

            if(holder.isNewPlace()){
                newFragment = new NewPlaceFragment();
                args.putSerializable("question", new Question("Você é o primeiro a avaliar este local, por favor nos de algumas informações adicionais"));
                args.putSerializable("categoriess",(Serializable) holder.getCategories());
                args.putSerializable("types", (Serializable) holder.getPlaceTypes());

                newFragment.setArguments(args);
            }else{
                newFragment = getNextQuestionFragment();
            }

            ft = getFragmentManager().beginTransaction();

            if (newFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, newFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new ShareEvaluateFragment()).commit();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                if (newFragment.isAnwser()) {
                    if (newFragment instanceof LikertFragment) {
                        anwsers.add(new Anwser("0", (String)newFragment.getAnwser(), null, null));
                    } else if (newFragment instanceof CommentFragment) {
                        anwsers.add(new Anwser("0", null, (String)newFragment.getAnwser(), null));
                    } else if (newFragment instanceof NumberFragment) {
                        anwsers.add(new Anwser("0", null, null, (String)newFragment.getAnwser()));
                    } else if (newFragment instanceof NewPlaceFragment) {
                        response.add("newPlace",(JsonObject)newFragment.getAnwser());
                    }

                    newFragment = getNextQuestionFragment();

                    if (newFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, newFragment).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new ShareEvaluateFragment()).commit();
                    }
                } else {
                    Toast.makeText(EvaluationActivity.this, "Você não respondeu a questão", Toast.LENGTH_LONG).show();
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
    private void sendAnwserBack() {
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //TODO ADD THE USER TOKEN
                response.addProperty("userID", "");

                JsonArray anwserArray = new JsonArray();

                for (Anwser awn : anwsers) {
                    JsonObject obj = new JsonObject();

                    obj.addProperty("question_id", awn.getQuestion_id());

                    JsonArray anwser = new JsonArray();

                    anwser.add(awn.getNumber());
                    anwser.add(awn.getComment());
                    anwser.add(awn.getLikert());

                    obj.add("answer", anwser);

                    anwserArray.add(obj);
                }

                response.add("answers", anwserArray);

                return params;
            }
        };
        Volley.newRequestQueue(EvaluationActivity.this).add(stringRequest);
    }

    public TransactionFragment getNextQuestionFragment() {
        TransactionFragment nextFragment = null;

        Log.d(TAG, "holder.getInstruments() size: " + holder.getInstruments().size());

        if (holder.getInstruments().size() > instrumentCursor) {
            if (holder.getInstruments().get(instrumentCursor).getData().getGroups().size() > groupCursor) {
                Log.d(TAG, "group size: " + holder.getInstruments().get(instrumentCursor).getData().getGroups().size());
                if (holder.getInstruments().get(instrumentCursor).getData().getGroups().get(groupCursor).getQuestions().size() > questionCursor) {
                    String type = holder.getInstruments().get(instrumentCursor).getData().getGroups().get(groupCursor).getQuestions().get(questionCursor).getQuestionType();

                    if (type.contentEquals(Question.QuestionTypes.IS_COMMENT.getType())) {
                        nextFragment = new CommentFragment();
                    } else if (type.contentEquals(Question.QuestionTypes.IS_LIKERT.getType())) {
                        nextFragment = new LikertFragment();
                    } else if (type.contentEquals(Question.QuestionTypes.IS_NUMBER.getType())) {
                        nextFragment = new NumberFragment();
                    }
                    args.putSerializable("question", holder.getInstruments().get(instrumentCursor).getData().getGroups().get(groupCursor).getQuestions().get(questionCursor));

                    nextFragment.setArguments(args);

                    questionCursor++;

                    return nextFragment;
                }else{
                    questionCursor = 0;
                    groupCursor++;
                    return getNextQuestionFragment();
                }
            }else{
                questionCursor = 0;
                groupCursor = 0;
                instrumentCursor++;
                return getNextQuestionFragment();
            }
        } else {
            return null;
        }
    }
}
