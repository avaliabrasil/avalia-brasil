package org.avaliabrasil.avaliabrasil.avb;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.CommentFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.LikertFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.NewPlaceFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.NumberFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.ShareEvaluateFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.TransactionFragment;
import org.avaliabrasil.avaliabrasil.data.AvBContract;
import org.avaliabrasil.avaliabrasil.data.AvaliaBrasilApplication;
import org.avaliabrasil.avaliabrasil.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Anwser;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Holder;
import org.avaliabrasil.avaliabrasil.rest.javabeans.Question;
import org.avaliabrasil.avaliabrasil.util.AvaliaBrasilGeocoderService;
import org.avaliabrasil.avaliabrasil.util.ImageLoader;
import org.avaliabrasil.avaliabrasil.util.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

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

        getSupportActionBar().setTitle(getIntent().getExtras().getString("name"));

        if (savedInstanceState == null) {
            holder = (Holder) getIntent().getSerializableExtra("holder");

            place_id = getIntent().getExtras().getString("placeid");

            if (getIntent().getExtras().getBoolean("pendingSurvey", false)) {
                preparePendingSurvey();
                newFragment = getNextQuestionFragment();
            } else if (holder.isNewPlace()) {
                newFragment = new NewPlaceFragment();
                args.putSerializable("question", new Question(getResources().getString(R.string.new_place_dialog)));
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

    @Override
    protected void onResume() {
        super.onResume();
        String ref = getIntent().getExtras().getString("photo_reference","");
        if(!ref.isEmpty()){
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            new ImageLoader(EvaluationActivity.this, metrics.widthPixels,ivPlace.getDrawable().getIntrinsicHeight(),ivPlace).execute(place_id);
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

        for (int i = 0; i < holder.getInstruments().get(instrumentCursor).getGroups().size(); i++, groupCursor++) {
            if (holder.getInstruments().get(instrumentCursor).getGroups().get(i).getId().contains(lastGroup_id)) {
                break;
            }
        }

        for (int i = 0; i < holder.getInstruments().get(instrumentCursor).getGroups().get(groupCursor).getQuestions().size() ; i++, questionCursor++) {
            if (holder.getInstruments().get(instrumentCursor).getGroups().get(groupCursor).getQuestions().get(i).getId().contains(lastQuestion_id)) {
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
        cv.put(AvBContract.SurveyEntry.GROUP_ID, holder.getInstruments().get(instrumentCursor).getGroups().get(groupCursor).getId());

        cv.put(AvBContract.SurveyEntry.QUESTION_ID, awn.getQuestion_id());
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
                        anw = new Anwser(newFragment.getQuestion().getId(), (String) newFragment.getAnwser(), null, null);
                        insertAnwser(anw);
                    } else if (newFragment instanceof CommentFragment) {
                        anw = new Anwser(newFragment.getQuestion().getId(), null, (String) newFragment.getAnwser(), null);
                        insertAnwser(anw);
                    } else if (newFragment instanceof NumberFragment) {
                        anw = new Anwser(newFragment.getQuestion().getId(), null, null, (String) newFragment.getAnwser());
                        insertAnwser(anw);
                    } else if (newFragment instanceof NewPlaceFragment) {
                        Integer[] responses = (Integer[]) newFragment.getAnwser();

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
                    Toast.makeText(EvaluationActivity.this, getResources().getString(R.string.question_not_anwsered), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tvSkip:
                Intent intent = new Intent(EvaluationActivity.this, PlaceStatisticsActivity.class);
                intent.putExtra("placeid", place_id);
                intent.putExtra("name", getIntent().getExtras().getString("name"));
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
    /**
     *
     */
    private ProgressDialog progress;

    private void sendAnwserBack() {
        progress = ProgressDialog.show(this, getResources().getString(R.string.anwser_dialog_title),
                getResources().getString(R.string.anwser_dialog_message), true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AvaliaBrasilAPIClient.postAnwsers(place_id),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ShareEvaluateFragment share = new ShareEvaluateFragment();

                        JsonParser parser = new JsonParser();
                        JsonObject o = parser.parse(Utils.normalizeAvaliaBrasilResponse(response)).getAsJsonObject();

                        Log.d(TAG, "onResponse: " + o.get("response").getAsJsonObject().get("fbShareText").getAsString() );

                        args.putString("shareString", o.get("response").getAsJsonObject().get("fbShareText").getAsString());

                        share.setArguments(args);

                        getContentResolver().delete(AvBContract.NewPlaceEntry.NEWPLACE_URI, AvBContract.NewPlaceEntry.PLACE_ID + " = ?", new String[]{place_id});
                        getContentResolver().delete(AvBContract.SurveyEntry.SURVEY_URI, AvBContract.SurveyEntry.SURVEY_FINISHED + " = ?",new String[]{"false"});

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, share).commit();

                        progress.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setSurveyAsCompleted();

                error.printStackTrace();

                Toast.makeText(EvaluationActivity.this, getResources().getString(R.string.internet_connection_error) , Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }) {
            @Override
            public byte[] getBody() {
                //TODO ADD THE USER TOKEN
                Cursor c = getContentResolver().query(AvBContract.NewPlaceEntry.NEWPLACE_URI,null,null,null,null);



                response.addProperty("userId", "1");

                if(c.getCount() > 0){
                    c.moveToFirst();

                    AvaliaBrasilGeocoderService service = new AvaliaBrasilGeocoderService(EvaluationActivity.this,new Geocoder(EvaluationActivity.this, Locale.getDefault()),((AvaliaBrasilApplication)getApplication()).getLocation());

                    try {
                        service.fetchAddress();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //response.addProperty("categoryId", c.getString(c.getColumnIndex(AvBContract.NewPlaceEntry.CATEGORY_ID)));
                    response.addProperty("placeTypeId", c.getString(c.getColumnIndex(AvBContract.NewPlaceEntry.PLACE_TYPE_ID)));
                    response.addProperty("newPlace", true);

                    c = getContentResolver().query(
                            AvBContract.PlaceEntry.getPlaceDetails(getIntent().getExtras().getString("placeid")), null, null, null, null);

                    c.moveToNext();

                    response.addProperty("address", c.getString(c.getColumnIndex(AvBContract.PlaceEntry.VICINITY)));
                    response.addProperty("name", c.getString(c.getColumnIndex(AvBContract.PlaceEntry.NAME)));
                    response.addProperty("cityName",service.getLocality());
                    response.addProperty("stateLetter", Utils.getStateAbbreviation(service.getAdminArea()));

                }

                JsonArray anwserArray = new JsonArray();

                c = getContentResolver().query(AvBContract.SurveyEntry.SURVEY_URI,null,AvBContract.SurveyEntry.SURVEY_FINISHED + " = ?",new String[]{"false"},"_id asc");

                while(c.moveToNext()){
                    JsonObject obj = new JsonObject();

                    obj.addProperty("questionId", c.getString(c.getColumnIndex(AvBContract.SurveyEntry.QUESTION_ID)));
                    obj.addProperty("questionType",c.getString(c.getColumnIndex(AvBContract.SurveyEntry.QUESTION_TYPE)));
                    obj.addProperty("answer",c.getString(c.getColumnIndex(AvBContract.SurveyEntry.ANWSER)));

                    anwserArray.add(obj);
                }

                response.add("answers",anwserArray);

                Log.d(TAG, "getBody: " + response.toString());

                return response == null ? null : response.toString().getBytes(Charset.forName("UTF-8"));

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
            if (holder.getInstruments().get(instrumentCursor).getGroups().size() > groupCursor) {
                if (holder.getInstruments().get(instrumentCursor).getGroups().get(groupCursor).getQuestions().size() > questionCursor) {
                    String type = holder.getInstruments().get(instrumentCursor).getGroups().get(groupCursor).getQuestions().get(questionCursor).getQuestionType();

                    if (type.contentEquals(Question.QuestionTypes.IS_COMMENT.getType())) {
                        nextFragment = new CommentFragment();
                    } else if (type.contentEquals(Question.QuestionTypes.IS_LIKERT.getType())) {
                        nextFragment = new LikertFragment();
                    } else if (type.contentEquals(Question.QuestionTypes.IS_NUMBER.getType())) {
                        nextFragment = new NumberFragment();
                    }
                    args.putSerializable("question", holder.getInstruments().get(instrumentCursor).getGroups().get(groupCursor).getQuestions().get(questionCursor));

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
