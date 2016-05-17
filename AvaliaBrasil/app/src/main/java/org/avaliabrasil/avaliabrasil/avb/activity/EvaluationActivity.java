package org.avaliabrasil.avaliabrasil.avb.activity;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.CommentFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.LikertFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.NewPlaceFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.NumberFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.ShareEvaluateFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.TransactionFragment;
import org.avaliabrasil.avaliabrasil.avb.data.AvBContract;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao.AnwserDAO;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao.AnwserService;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao.NewPlaceDAO;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao.SurveyService;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.NewPlace;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.service.AnwserDAOImpl;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.service.AnwserServiceImpl;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.service.NewPlaceDAOImpl;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.service.SurveyCursor;
import org.avaliabrasil.avaliabrasil.avb.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.Anwser;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.Survey;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.Question;
import org.avaliabrasil.avaliabrasil.avb.util.ImageLoader;
import org.avaliabrasil.avaliabrasil.avb.util.Utils;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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
    private SurveyService surveyService;

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
    /**
     *
     */
    private ProgressDialog progress;
    /**
     *
     */
    private NewPlaceDAO newPlaceDAO;

    /**
     *
     */
    private AnwserDAO anwserDAO;
    /**
     *
     */
    private AnwserService anwserService;


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
            surveyService = new SurveyCursor(EvaluationActivity.this,(Survey) getIntent().getSerializableExtra("holder"));
            anwserDAO = new AnwserDAOImpl(EvaluationActivity.this);
            newPlaceDAO = new NewPlaceDAOImpl(EvaluationActivity.this);
            place_id = getIntent().getExtras().getString("placeid");
            anwserService = new AnwserServiceImpl();

            if (getIntent().getExtras().getBoolean("pendingSurvey", false)) {
                surveyService.preparePendingSurvey();
                newFragment = getNextQuestionFragment();
            } else if (surveyService.getSurvey().isNewPlace()) {
                newFragment = new NewPlaceFragment();
                args.putSerializable("question", new Question(getResources().getString(R.string.new_place_dialog)));
                args.putSerializable("categoriess", (Serializable) surveyService.getSurvey().getCategories());
                args.putSerializable("types", (Serializable) surveyService.getSurvey().getPlaceTypes());
                args.putString("placeId", place_id);

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
        String ref = getIntent().getExtras().getString("photo_reference", "");
        if (!ref.isEmpty()) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            new ImageLoader(EvaluationActivity.this, 500, 500
                    , ivPlace).execute(place_id);
        }
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                if (newFragment.isAnwser()) {
                    Anwser anw;
                    if (newFragment instanceof LikertFragment) {
                        anw = new Anwser(place_id,surveyService.peekInstrument().getId(), surveyService.peekGroup().getId(),newFragment.getQuestion().getId(), (String) newFragment.getAnwser(), null, null);
                        anwserDAO.insertAnwser(anw);
                    } else if (newFragment instanceof CommentFragment) {
                        anw = new Anwser(place_id,surveyService.peekInstrument().getId(), surveyService.peekGroup().getId(),newFragment.getQuestion().getId(), null, (String) newFragment.getAnwser(), null);
                        anwserDAO.insertAnwser(anw);
                    } else if (newFragment instanceof NumberFragment) {
                        anw = new Anwser(place_id,surveyService.peekInstrument().getId(), surveyService.peekGroup().getId(),newFragment.getQuestion().getId(), null, null, (String) newFragment.getAnwser());
                        anwserDAO.insertAnwser(anw);
                    } else if (newFragment instanceof NewPlaceFragment) {
                        NewPlace newPlace = (NewPlace) newFragment.getAnwser();
                        newPlaceDAO.insertNewPlace(newPlace);
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

                        Log.d(TAG, "onResponse: " + o.get("response").getAsJsonObject().get("fbShareText").getAsString());

                        args.putString("shareString", o.get("response").getAsJsonObject().get("fbShareText").getAsString());

                        share.setArguments(args);

                        newPlaceDAO.deleteNewPlaceByPlaceId(place_id);
                        anwserDAO.deleteSendedSurvey();

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, share).commit();

                        progress.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                anwserDAO.setSurveyAsCompleted();

                error.printStackTrace();

                Toast.makeText(EvaluationActivity.this, getResources().getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }) {
            @Override
            public byte[] getBody() {
               response.addProperty("userId", "1");
                Cursor c;
                NewPlace newPlace = newPlaceDAO.findNewPlaceByPlaceId(place_id);

                if (newPlace != null) {
                    response.addProperty("newPlace", true);
                    response.addProperty("placeTypeId", newPlace.getPlaceType());

                    c = getContentResolver().query(
                            AvBContract.PlaceEntry.getPlaceDetails(getIntent().getExtras().getString("placeid")), null, null, null, null);

                    c.moveToNext();

                    response.addProperty("address", c.getString(c.getColumnIndex(AvBContract.PlaceEntry.VICINITY)));
                    response.addProperty("name", c.getString(c.getColumnIndex(AvBContract.PlaceEntry.NAME)));
                    response.addProperty("cityName", c.getString(c.getColumnIndex(AvBContract.PlaceDetailsEntry.CITY)));
                    response.addProperty("stateLetter", c.getString(c.getColumnIndex(AvBContract.PlaceDetailsEntry.STATE)));

                    c.close();

                } else {
                    response.addProperty("newPlace", false);
                }

                response.add("answers", anwserService.prepareForSendAnwser(anwserDAO));

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

        if(surveyService.getNextQuestion() != null){
            args.putSerializable("question", surveyService.peekQuestion());

            if (surveyService.peekQuestion().getQuestionType().contentEquals(Question.QuestionTypes.IS_COMMENT.getType())) {
                nextFragment = new CommentFragment();
            } else if (surveyService.peekQuestion().getQuestionType().contentEquals(Question.QuestionTypes.IS_LIKERT.getType())) {
                nextFragment = new LikertFragment();
            } else if (surveyService.peekQuestion().getQuestionType().contentEquals(Question.QuestionTypes.IS_NUMBER.getType())) {
                nextFragment = new NumberFragment();
            }else{
                return null;
            }

            nextFragment.setArguments(args);
            return nextFragment;
        } else {
            return null;
        }
    }
}
