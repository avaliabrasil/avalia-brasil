package org.avaliabrasil.avaliabrasil.avb.sync;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.avaliabrasil.avaliabrasil.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil.avb.rest.AvaliaBrasilAPIClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Developer on 19/05/2016.
 */
public class ServiceAnwserSync extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //TODO FIXME: 09/05/2016
    @Override
    public void onCreate() {
        super.onCreate();

        Cursor c = getContentResolver().query(AvBContract.SurveyEntry.SURVEY_URI, new String[]{AvBContract.SurveyEntry.PLACE_ID}, null, null, null);

        ArrayList<String> ids = new ArrayList<>();

        while (c.moveToNext()) {
            if (!ids.contains(c.getString(c.getColumnIndex(AvBContract.SurveyEntry.PLACE_ID)))) {
                ids.add(c.getString(c.getColumnIndex(AvBContract.SurveyEntry.PLACE_ID)));
            }
        }

        for (final String place_id : ids) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AvaliaBrasilAPIClient.postAnwsers(place_id),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            getContentResolver().delete(AvBContract.NewPlaceEntry.NEWPLACE_URI, AvBContract.NewPlaceEntry.PLACE_ID + " = ?", new String[]{place_id});
                            getContentResolver().delete(AvBContract.SurveyEntry.SURVEY_URI, AvBContract.SurveyEntry.PLACE_ID + " = ?", new String[]{place_id});

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    //TODO ADD THE USER TOKEN
                    JsonObject response = new JsonObject();

                    response.addProperty("userID", "");

                    JsonArray anwserArray = new JsonArray();

                    Cursor c = getContentResolver().query(AvBContract.SurveyEntry.SURVEY_URI, null, AvBContract.SurveyEntry.PLACE_ID + " = ?", new String[]{place_id}, "_id asc");

                    while (c.moveToNext()) {
                        JsonObject obj = new JsonObject();

                        obj.addProperty("question_id", c.getString(c.getColumnIndex(AvBContract.SurveyEntry.QUESTION_ID)));

                        JsonArray anwsers = new JsonArray();

                        String type = c.getString(c.getColumnIndex(AvBContract.SurveyEntry.QUESTION_TYPE));

                        JsonObject anwser = new JsonObject();

                        if (type.contains("number")) {
                            anwser.addProperty("number", c.getString(c.getColumnIndex(AvBContract.SurveyEntry.ANWSER)));
                            anwser.addProperty("likert", "");
                            anwser.addProperty("comment", "");

                        } else if (type.contains("comment")) {
                            anwser.addProperty("comment", c.getString(c.getColumnIndex(AvBContract.SurveyEntry.ANWSER)));
                            anwser.addProperty("likert", "");
                            anwser.addProperty("number", "");

                        } else if (type.contains("likert")) {
                            anwser.addProperty("likert", c.getString(c.getColumnIndex(AvBContract.SurveyEntry.ANWSER)));
                            anwser.addProperty("number", "");
                            anwser.addProperty("comment", "");
                        }

                        anwsers.add(anwser);

                        obj.add("answer", anwsers);

                        anwserArray.add(obj);
                    }
                    return params;
                }
            };
            Volley.newRequestQueue(ServiceAnwserSync.this).add(stringRequest);
        }
        stopSelf();
    }
}
