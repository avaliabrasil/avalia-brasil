package org.avaliabrasil.avaliabrasil2.avb.sync;

import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.avaliabrasil.avaliabrasil2.R;
import org.avaliabrasil.avaliabrasil2.avb.activity.EvaluationActivity;
import org.avaliabrasil.avaliabrasil2.avb.activity.MainActivity;
import org.avaliabrasil.avaliabrasil2.avb.dao.AnwserDAO;
import org.avaliabrasil.avaliabrasil2.avb.dao.AnwserService;
import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil2.avb.dao.NewPlaceDAO;
import org.avaliabrasil.avaliabrasil2.avb.dao.SurveyDAO;
import org.avaliabrasil.avaliabrasil2.avb.fragments.evaluate.ShareEvaluateFragment;
import org.avaliabrasil.avaliabrasil2.avb.impl.AnwserDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.AnwserServiceImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.GroupQuestionDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.InstrumentDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.NewPlaceDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.PlaceDetailsDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.QuestionDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.impl.SurveyDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.Survey;
import org.avaliabrasil.avaliabrasil2.avb.rest.AvaliaBrasilAPIClient;
import org.avaliabrasil.avaliabrasil2.avb.util.Utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Developer on 19/05/2016.
 */
public class ServiceAnwserSync extends Service {

    private String TAG = this.getClass().getSimpleName();
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
    /**
     *
     */
    private SurveyDAO surveyDAO;

    /**
     * The {@link AccountManager} for getting user informations.
     */
    private AccountManager manager;

    private String token;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        manager = AccountManager.get(ServiceAnwserSync.this);
        token = manager.getUserData(manager.getAccountsByType(Constant.ACCOUNT_TYPE)[0], Constant.ACCOUNT_TOKEN);
        Log.d(TAG, "onCreate: Starting ServiceAnwserSync");

        surveyDAO = new SurveyDAOImpl(ServiceAnwserSync.this,new InstrumentDAOImpl(ServiceAnwserSync.this,
                new GroupQuestionDAOImpl(ServiceAnwserSync.this)),
                new GroupQuestionDAOImpl(ServiceAnwserSync.this),
                new QuestionDAOImpl(ServiceAnwserSync.this),
                new NewPlaceDAOImpl(ServiceAnwserSync.this),
                new AnwserDAOImpl(ServiceAnwserSync.this));

        newPlaceDAO = new NewPlaceDAOImpl(ServiceAnwserSync.this);

        anwserDAO = new AnwserDAOImpl(ServiceAnwserSync.this);

        anwserService = new AnwserServiceImpl(newPlaceDAO,anwserDAO,new PlaceDetailsDAOImpl(ServiceAnwserSync.this));

        List<Survey> unsendedSurveyList = surveyDAO.getAllUnsendedSurvey();

        for(final Survey survey : unsendedSurveyList){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AvaliaBrasilAPIClient.postAnwsers(survey.getPlaceId()),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            surveyDAO.removeSurvey(survey);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    anwserDAO.setSurveyAsCompleted();

                    error.printStackTrace();
                }
            }) {
                @Override
                public byte[] getBody() {
                    JsonObject response = anwserService.prepareForSendAnwser("1",survey.getPlaceId(),survey.getSurveyId());

                    Log.d(TAG, "body: " + response.toString());

                    return response == null ? null : response.toString().getBytes(Charset.forName("UTF-8"));
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<String,String>();
                    header.put("userId",token);
                    return header;
                }
            };
            Log.d(TAG, "Sending survey: " + survey.getSurveyId());
            Volley.newRequestQueue(ServiceAnwserSync.this).add(stringRequest);
        }
        unsendedSurveyList.clear();
        stopSelf();
    }
}
