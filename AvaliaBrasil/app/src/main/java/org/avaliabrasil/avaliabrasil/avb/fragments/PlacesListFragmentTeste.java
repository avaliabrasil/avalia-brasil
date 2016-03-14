package org.avaliabrasil.avaliabrasil.avb.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.PlaceActivityTeste;
import org.avaliabrasil.avaliabrasil.avb.adapters.PlacesListAdapterTeste;
import org.avaliabrasil.avaliabrasil.rest.GooglePlacesAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceSearch;

/**
 * Created by Pedro on 29/02/2016.
 */
public class PlacesListFragmentTeste extends Fragment implements android.location.LocationListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private PlacesListAdapterTeste mPlacesListAdapter;

    private static final String ARG_SECTION_NUMBER = "section_number";

    private Location location;

    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;

    private  static final String SELECTED_place = "selected_place";

    public static PlacesListFragmentTeste newInstance(int sectionNumber) {
        PlacesListFragmentTeste fragment = new PlacesListFragmentTeste();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PlacesListFragmentTeste(){

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        try{
        location = locationManager
                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        Log.d("Places list", "Lat: " + location.getLatitude() + " and long: " + location.getLongitude());

            StringRequest stringRequest = new StringRequest(Request.Method.GET, GooglePlacesAPIClient.getNearlyPlaces(location.getLatitude(),location.getLongitude(),500,null,"AIzaSyCBq-qetL_jdUUhM0TepfVZ5EYxJvw6ct0"),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Teste", "onResponse: " + response);
                            Gson gson = new Gson();
                            PlaceSearch placeSearch = gson.fromJson(response, PlaceSearch.class);
                            mPlacesListAdapter = new PlacesListAdapterTeste(getContext(),placeSearch);
                            mListView.setAdapter(mPlacesListAdapter);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            Volley.newRequestQueue(getContext()).add(stringRequest);

        }catch(SecurityException e){
            e.printStackTrace();
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_places_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listview_places);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getContext(), PlaceActivityTeste.class);
                intent.putExtra("placeid",mPlacesListAdapter.getItem(position).getPlaceId());
                startActivity(intent);
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_place)) {
            mPosition = savedInstanceState.getInt(SELECTED_place);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_place, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
