package org.avaliabrasil.avaliabrasil.avb.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.MainActivity;
import org.avaliabrasil.avaliabrasil.avb.PlaceActivityTeste;
import org.avaliabrasil.avaliabrasil.avb.adapters.PlacesListAdapterTeste;
import org.avaliabrasil.avaliabrasil.rest.GooglePlacesAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceSearch;
import org.avaliabrasil.avaliabrasil.rest.javabeans.ResultPlaceSearch;

/**
 * Created by Pedro on 29/02/2016.
 */
// PlacesMapFragment: Map of nearby found places
public class PlacesMapFragment extends Fragment implements GoogleMap.OnMarkerClickListener {
    public final String LOG_TAG = this.getClass().getSimpleName();
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    //TODO: Colocar outros parâmetros se necessário
    private static final String ARG_SECTION_NUMBER = "section_number";

    private Location location;
    private MapView mMapView;
    private GoogleMap googleMap;

    public PlacesMapFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlacesMapFragment newInstance(int sectionNumber) {
        PlacesMapFragment fragment = new PlacesMapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

            try{
                location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if(MainActivity.placeSearch == null){

                StringRequest stringRequest = new StringRequest(Request.Method.GET, GooglePlacesAPIClient.getNearlyPlaces(location.getLatitude(),location.getLongitude(),500,null,"AIzaSyCBq-qetL_jdUUhM0TepfVZ5EYxJvw6ct0"),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Gson gson = new Gson();
                                PlaceSearch placeSearch = gson.fromJson(response, PlaceSearch.class);
                                MainActivity.placeSearch = placeSearch;
                                fillMarks();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

                Volley.newRequestQueue(getContext()).add(stringRequest);
                }
            }catch(SecurityException e){
                e.printStackTrace();
            }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_places_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        if(MainActivity.placeSearch != null){
            fillMarks();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void fillMarks(){
        MarkerOptions marker;

        for(ResultPlaceSearch r : MainActivity.placeSearch.getResults()){
            marker = new MarkerOptions().position(
                    new LatLng(r.getGeometry().getLocation().getLat(), r.getGeometry().getLocation().getLng())).title(r.getPlaceId());

            // Changing marker icon
            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));


            // adding marker
            googleMap.addMarker(marker);
        }

        googleMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for(ResultPlaceSearch r : MainActivity.placeSearch.getResults()){
            if(marker.getTitle().contentEquals(r.getPlaceId())){
                Intent intent = new Intent(getContext(), PlaceActivityTeste.class);
                Location placeLocation = new Location("");

                placeLocation.setLatitude(r.getGeometry().getLocation().getLat());
                placeLocation.setLongitude(r.getGeometry().getLocation().getLng());

                intent.putExtra("placeid",r.getPlaceId());
                intent.putExtra("distance",(int)location.distanceTo(placeLocation) + "m");
                startActivity(intent);
                break;
            }
        }
        return true;
    }
}
