package org.avaliabrasil.avaliabrasil.avb;

import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import com.facebook.CallbackManager;
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
import org.avaliabrasil.avaliabrasil.data.AvBContract;
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

    /**
     *
     */
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        if (getIntent().getExtras() == null || getIntent().getExtras().getString("name") == null) {
            finish();
        }

        callbackManager = CallbackManager.Factory.create();

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

            if (getIntent().getExtras().getBoolean("pendingSurvey", false)) {

                preparePendingSurvey();

                newFragment = getNextQuestionFragment();
            } else if (holder.isNewPlace()) {
                newFragment = new NewPlaceFragment();
                args.putSerializable("question", new Question("Você é o primeiro a avaliar este local, por favor nos de algumas informações adicionais"));
                args.putSerializable("categoriess", (Serializable) holder.getCategories());
                args.putSerializable("types", (Serializable) holder.getPlaceTypes());

                newFragment.setArguments(args);
            } else {
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


    private void preparePendingSurvey() {
        Cursor c = getContentResolver().query(AvBContract.SurveyEntry.SURVEY_URI,null,AvBContract.SurveyEntry.SURVEY_FINISHED + " = ?",new String[]{"false"},"_id desc");

        c.moveToNext();

        String lastInstrument_id = c.getString(c.getColumnIndex("instrument_id"));
        String lastGroup_id = c.getString(c.getColumnIndex("group_id"));
        String lastQuestion_id = c.getString(c.getColumnIndex("question_id"));

        for (int i = 0; i < holder.getInstruments().size(); i++, instrumentCursor++) {
            if (holder.getInstruments().get(i).getId().contains(lastInstrument_id)) {
                break;
            }
        }

        for (int i = 0; i < holder.getInstruments().get(instrumentCursor).getData().getGroups().size(); i++, groupCursor++) {
            if (holder.getInstruments().get(instrumentCursor).getData().getGroups().get(i).getId().contains(lastGroup_id)) {
                break;
            }
        }

        for (int i = 0; i < holder.getInstruments().get(instrumentCursor).getData().getGroups().get(groupCursor).getQuestions().size() ; i++, questionCursor++) {
            if (holder.getInstruments().get(instrumentCursor).getData().getGroups().get(groupCursor).getQuestions().get(i).getId().contains(lastQuestion_id)) {
                questionCursor++;
                break;
            }
        }
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

    private void insertAnwser(Anwser awn) {

        ContentValues cv = new ContentValues();

        cv.put(AvBContract.SurveyEntry.PLACE_ID, place_id);
        cv.put(AvBContract.SurveyEntry.INSTRUMENT_ID, holder.getInstruments().get(instrumentCursor).getId());
        cv.put(AvBContract.SurveyEntry.GROUP_ID, holder.getInstruments().get(instrumentCursor).getData().getGroups().get(groupCursor).getId());

        cv.put(AvBContract.SurveyEntry.QUESTION_ID, holder.getInstruments().get(instrumentCursor).getData().getGroups().get(groupCursor).getQuestions().get(questionCursor-1).getId());
        cv.put(AvBContract.SurveyEntry.QUESTION_TYPE, !awn.getNumber().isEmpty() ? "number" : !awn.getComment().isEmpty() ? "comment" : "likert");
        cv.put(AvBContract.SurveyEntry.ANWSER, !awn.getNumber().isEmpty() ? awn.getNumber() : !awn.getComment().isEmpty() ? awn.getComment() : awn.getLikert());

        getContentResolver().insert(AvBContract.SurveyEntry.SURVEY_URI, cv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                if (newFragment.isAnwser()) {
                    Anwser anw;
                    if (newFragment instanceof LikertFragment) {
                        anw =new Anwser("0", (String) newFragment.getAnwser(), null, null);
                        anwsers.add(anw);
                        insertAnwser(anw);
                    } else if (newFragment instanceof CommentFragment) {
                        anw = new Anwser("0", null, (String) newFragment.getAnwser(), null);
                        anwsers.add(anw);
                        insertAnwser(anw);
                    } else if (newFragment instanceof NumberFragment) {
                        anw = new Anwser("0", null, null, (String) newFragment.getAnwser());
                        anwsers.add(anw);
                        insertAnwser(anw);
                    } else if (newFragment instanceof NewPlaceFragment) {

                        Integer[] responses = (Integer[]) newFragment.getAnwser();

                        response.addProperty("categoryId", responses[0]);
                        response.addProperty("placeTypeId", responses[1]);

                        ContentValues cv = new ContentValues();

                        cv.put(AvBContract.NewPlaceEntry.PLACE_ID, place_id);
                        cv.put(AvBContract.NewPlaceEntry.CATEGORY_ID, responses[0]);
                        cv.put(AvBContract.NewPlaceEntry.PLACE_TYPE_ID, responses[1]);

                        getContentResolver().insert(AvBContract.NewPlaceEntry.NEWPLACE_URI, cv);
                    }

                    newFragment = getNextQuestionFragment();

                    if (newFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, newFragment).commit();
                    } else {
                        sendAnwserBack();
                    }
                } else {
                    Toast.makeText(EvaluationActivity.this, "Você não respondeu a questão", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tvSkip:
                Intent intent = new Intent(EvaluationActivity.this, PlaceStatisticsActivity.class);
                intent.putExtra("placeid", place_id);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void setSurveyAsCompleted(){
        ContentValues cv = new ContentValues();

        cv.put(AvBContract.SurveyEntry.SURVEY_FINISHED, true);

        getContentResolver().update(AvBContract.SurveyEntry.SURVEY_URI, cv,AvBContract.SurveyEntry.SURVEY_FINISHED + " = ? " ,new String[]{"false"});
    }

    //TODO Still need to be test with the API
    private void sendAnwserBack() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AvaliaBrasilAPIClient.postAnwsers(place_id),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ShareEvaluateFragment share = new ShareEvaluateFragment();

                        args.putString("shareString", "blablabla");

                        share.setArguments(args);

                        getContentResolver().delete(AvBContract.NewPlaceEntry.NEWPLACE_URI, AvBContract.NewPlaceEntry.PLACE_ID + " = ?", new String[]{place_id});
                        getContentResolver().delete(AvBContract.SurveyEntry.SURVEY_URI, AvBContract.SurveyEntry.SURVEY_FINISHED + " = ?",new String[]{"false"});

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, share).commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setSurveyAsCompleted();

                error.printStackTrace();
                //TODO show a screen if there is no internet

                ShareEvaluateFragment share = new ShareEvaluateFragment();

                args.putString("shareString", "blablabla");

                share.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, share).commit();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //TODO ADD THE USER TOKEN
                response.addProperty("userID", "");

                JsonArray anwserArray = new JsonArray();

                if(getIntent().getExtras().getBoolean("pendingSurvey", false)){
                    for (Anwser awn : anwsers) {
                        JsonObject obj = new JsonObject();

                        obj.addProperty("question_id", awn.getQuestion_id());

                        JsonArray anwsers = new JsonArray();

                        JsonObject anwser = new JsonObject();

                        anwser.addProperty("number",awn.getNumber().isEmpty() ? "": awn.getNumber());
                        anwser.addProperty("likert",awn.getComment().isEmpty() ? "": awn.getComment());
                        anwser.addProperty("comment",awn.getLikert().isEmpty() ? "": awn.getLikert());

                        anwsers.add(anwser);

                        obj.add("answer", anwsers);

                        anwserArray.add(obj);
                    }

                    response.add("answers", anwserArray);
                }else{
                    Cursor c = getContentResolver().query(AvBContract.SurveyEntry.SURVEY_URI,null,AvBContract.SurveyEntry.SURVEY_FINISHED + " = ?",new String[]{"false"},"_id asc");

                    while(c.moveToNext()){
                        JsonObject obj = new JsonObject();

                        obj.addProperty("question_id", c.getString(c.getColumnIndex(AvBContract.SurveyEntry.QUESTION_ID)));

                        JsonArray anwsers = new JsonArray();

                        String type = c.getString(c.getColumnIndex(AvBContract.SurveyEntry.QUESTION_TYPE));

                        JsonObject anwser = new JsonObject();
                        
                        if(type.contains("number")){
                            anwser.addProperty("number",c.getString(c.getColumnIndex(AvBContract.SurveyEntry.ANWSER)));
                            anwser.addProperty("likert","");
                            anwser.addProperty("comment","");

                        }else if(type.contains("comment")){
                            anwser.addProperty("comment",c.getString(c.getColumnIndex(AvBContract.SurveyEntry.ANWSER)));
                            anwser.addProperty("likert","");
                            anwser.addProperty("number","");

                        }else if(type.contains("likert")){
                            anwser.addProperty("likert",c.getString(c.getColumnIndex(AvBContract.SurveyEntry.ANWSER)));
                            anwser.addProperty("number","");
                            anwser.addProperty("comment","");
                        }

                        anwsers.add(anwser);
                        
                        obj.add("answer", anwsers);

                        anwserArray.add(obj);
                    }
                }

                return params;
            }
        };
        Volley.newRequestQueue(EvaluationActivity.this).add(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public TransactionFragment getNextQuestionFragment() {
        TransactionFragment nextFragment = null;

        if (holder.getInstruments().size() > instrumentCursor) {
            if (holder.getInstruments().get(instrumentCursor).getData().getGroups().size() > groupCursor) {
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
                } else {
                    questionCursor = 0;
                    groupCursor++;
                    return getNextQuestionFragment();
                }
            } else {
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
