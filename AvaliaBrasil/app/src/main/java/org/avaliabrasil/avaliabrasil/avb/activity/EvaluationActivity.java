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
import org.avaliabrasil.avaliabrasil.avb.dao.SurveyDAO;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.CommentFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.LikertFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.NewPlaceFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.NumberFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.ShareEvaluateFragment;
import org.avaliabrasil.avaliabrasil.avb.fragments.evaluate.TransactionFragment;
import org.avaliabrasil.avaliabrasil.avb.dao.AnwserDAO;
import org.avaliabrasil.avaliabrasil.avb.dao.AnwserService;
import org.avaliabrasil.avaliabrasil.avb.dao.NewPlaceDAO;
import org.avaliabrasil.avaliabrasil.avb.dao.SurveyService;
import org.avaliabrasil.avaliabrasil.avb.impl.GroupQuestionDAOImpl;
import org.avaliabrasil.avaliabrasil.avb.impl.InstrumentDAOImpl;
import org.avaliabrasil.avaliabrasil.avb.impl.PlaceDetailsDAOImpl;
import org.avaliabrasil.avaliabrasil.avb.impl.QuestionDAOImpl;
import org.avaliabrasil.avaliabrasil.avb.impl.SurveyDAOImpl;
import org.avaliabrasil.avaliabrasil.avb.javabeans.place.placedetail.ResultDetails;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.NewPlace;
import org.avaliabrasil.avaliabrasil.avb.impl.AnwserDAOImpl;
import org.avaliabrasil.avaliabrasil.avb.impl.AnwserServiceImpl;
import org.avaliabrasil.avaliabrasil.avb.impl.NewPlaceDAOImpl;
import org.avaliabrasil.avaliabrasil.avb.impl.SurveyCursor;
import org.avaliabrasil.avaliabrasil.avb.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.Anwser;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.Survey;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.Question;
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

    private SurveyDAO surveyDAO;

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

            anwserDAO = new AnwserDAOImpl(EvaluationActivity.this);

            surveyService = new SurveyCursor(EvaluationActivity.this,(Survey) getIntent().getSerializableExtra("holder"),anwserDAO);

            newPlaceDAO = new NewPlaceDAOImpl(EvaluationActivity.this);

            place_id = getIntent().getExtras().getString("placeid");

            anwserService = new AnwserServiceImpl(newPlaceDAO,anwserDAO,new PlaceDetailsDAOImpl(EvaluationActivity.this));

            surveyDAO = new SurveyDAOImpl(EvaluationActivity.this,new InstrumentDAOImpl(EvaluationActivity.this,
                    new GroupQuestionDAOImpl(EvaluationActivity.this)),
                    new GroupQuestionDAOImpl(EvaluationActivity.this),
                    new QuestionDAOImpl(EvaluationActivity.this),
                    new NewPlaceDAOImpl(EvaluationActivity.this),
                    new AnwserDAOImpl(EvaluationActivity.this));

            surveyService.getSurvey().setPlaceId(place_id);

            ResultDetails details = new PlaceDetailsDAOImpl(EvaluationActivity.this).getPlaceDetailsByPlaceId(place_id);

            if (getIntent().getExtras().getBoolean("pendingSurvey", false)) {
                surveyService.preparePendingSurvey();
                newFragment = getNextQuestionFragment();
            } else if (surveyService.getSurvey().isNewPlace()) {
                surveyDAO.addSurvey(surveyService.getSurvey());

                newFragment = new NewPlaceFragment();
                args.putSerializable("question", new Question(getResources().getString(R.string.new_place_dialog)));
                args.putSerializable("categoriess", (Serializable) surveyService.getSurvey().getCategories());
                args.putSerializable("types", (Serializable) surveyService.getSurvey().getPlaceTypes());
                args.putString("placeId", place_id);
                args.putString("city", details.getCityName());
                args.putString("state", details.getStateLetter());


                newFragment.setArguments(args);
            } else {
                surveyDAO.addSurvey(surveyService.getSurvey());
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
    public void onBackPressed() {
        super.onBackPressed();
        surveyDAO.removeSurvey(surveyService.getSurvey());
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
                        anw = new Anwser(surveyService.getSurvey().getSurveyId(),surveyService.peekInstrument().getId(), surveyService.peekGroup().getId(),newFragment.getQuestion().getId(), (String) newFragment.getAnwser(), null, null);
                        anwserDAO.insertAnwser(anw);
                    } else if (newFragment instanceof CommentFragment) {
                        anw = new Anwser(surveyService.getSurvey().getSurveyId(),surveyService.peekInstrument().getId(), surveyService.peekGroup().getId(),newFragment.getQuestion().getId(), null, (String) newFragment.getAnwser(), null);
                        anwserDAO.insertAnwser(anw);
                    } else if (newFragment instanceof NumberFragment) {
                        anw = new Anwser(surveyService.getSurvey().getSurveyId(),surveyService.peekInstrument().getId(), surveyService.peekGroup().getId(),newFragment.getQuestion().getId(), null, null, (String) newFragment.getAnwser());
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

                finish();
            }
        }) {
            @Override
            public byte[] getBody() {

                JsonObject response = anwserService.prepareForSendAnwser("1",place_id,surveyService.getSurvey().getSurveyId());

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
