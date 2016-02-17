package org.avaliabrasil.avaliabrasil;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaceActivity extends AppCompatActivity {

    public static String GOOGLEPLACEID = "googlePlacesId";
    private ArrayAdapter<String> PlaceInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        // Definindo o título da Toolbar
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(getPlaceName());

        // Ativando a opção voltar da Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String PlaceDataStringArray [] = {
                "Praça Ernest Ludwig Herman",
                "(51) 3212-2015",
                "e-mail@site.com.br",
                "Aberto hoje 08:00 - 21:00"
        };
        // Criando List
        List<String> placeInfoList = new ArrayList<String>(Arrays.asList(PlaceDataStringArray));

        // Criando ArrayAdapter:
        ArrayAdapter<String> placeListAdapter =
                new ArrayAdapter<String>(
                        this, // Context
                        R.layout.list_item_place_info, // LayoutId
                        R.id.list_item_forecast_textview, // Id of Textview
                        placeInfoList // ListName
                );
        // Referenciando o ListView e ligando o Adapter a ele
        ListView placeInfoListView = (ListView) findViewById(R.id.listview_place_info);
        placeInfoListView.setAdapter(placeListAdapter);

        // Chamando a classe assíncrona!
        FetchPlaceData placeDataTask = new FetchPlaceData();
        String googlePlaceId = getIntent().getExtras().getString(GOOGLEPLACEID);
        placeDataTask.execute(googlePlaceId);

    }

    public String getPlaceName(){
        // Temporário, enquanto não implementamos um template provider

        // Temporário, apenas para testar intent:
        return getIntent().getExtras().getString(GOOGLEPLACEID);
    }

    public void startEvaluationActivity (View view) {
        Intent intent_evaluation_activity = new Intent(this, EvaluationActivity.class);
        startActivity(intent_evaluation_activity);

    }

    public void startStatisticsActivity (View view) {
        Intent intent_statistics_activity = new Intent(this, PlaceStatisticsActivity.class);
        startActivity(intent_statistics_activity);

    }

    public void startNewPlaceActivity (View view) {
        Intent intent_new_place_activity = new Intent(this, NewPlaceActivity.class);
        startActivity(intent_new_place_activity);

    }

    // Returns an JSONObject with
    public class FetchPlaceData extends AsyncTask<String, Void, JSONObject> {

        private final String LOG_TAG = FetchPlaceData.class.getSimpleName();

        // Returns a Place Json String with AvB place info
        protected String fetchAvBPlaceJsonStringInfo(String googlePlaceId) {

            if (googlePlaceId.length() == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String placeJsonStr = null;

            try {
                // Mudar isso depois!
                final String API_BASE_URL = "http://avaliabrasil.siteseguro.ws/fakeapi/Place/ID/file.json";
                final String OBJECT_PARAM = "";
                // TODO: Aqui a URI deveria ser construída de modo correto
                Uri builtUri = Uri.parse(API_BASE_URL).buildUpon().build();
                        //appendQueryParameter(OBJECT_PARAM, variableName) . Inserir antes do Build
                    URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URL "+ builtUri.toString());

                // Create Request to AvB Api:
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read Input stream to a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null){
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                // Breaking lines
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                // Checking if buffer is nul
                if (buffer.length() == 0) {
                    return null;
                }

                placeJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Place String: " + placeJsonStr);
            }

            catch (IOException error) {
                Log.e(LOG_TAG, "Error ", error);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            }

            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return placeJsonStr;

            // Transforming String into JSON Object
        }

        // Returns a Place Json Object of 
        @Override
        protected JSONObject doInBackground (String... params) {

            String googlePlaceId = params[0];

            if (googlePlaceId.length()==0){
                return null;
            }

            String placeJsonStr = fetchAvBPlaceJsonStringInfo(googlePlaceId);
            final String AB_PLACE = "place";

            try {
                JSONObject placeInfoJson = new JSONObject(placeJsonStr);
                return placeInfoJson.getJSONObject(AB_PLACE);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject placeObject) {

            final String AB_QUALITYINDEX = "qualityIndex";
            final String AB_RANKINGPOSITION = "rankingPosition";
            final String AB_RANKINGSTATUS = "rankingStatus";

            try {
                double qualityIndex = (placeObject.getDouble(AB_QUALITYINDEX));
                int rankingPosition = placeObject.getInt(AB_RANKINGPOSITION);
                String rankingStatus = placeObject.getString(AB_RANKINGSTATUS);

                Button qualityIndexButton = (Button) findViewById(R.id.quality_index);
                qualityIndexButton.setText(String.valueOf(qualityIndex));

                Button rankingPositionButton = (Button) findViewById(R.id.ranking_position);
                rankingPositionButton.setText(String.valueOf(rankingPosition));


            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

        }


        }
    }
