package org.avaliabrasil.avaliabrasil.avb.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.MainActivity;
import org.avaliabrasil.avaliabrasil.avb.PlaceActivity;
import org.avaliabrasil.avaliabrasil.sync.Observer;

/**
 * Created by Pedro on 29/02/2016.
 */
public class PlacesMapFragment extends Fragment implements GoogleMap.OnMarkerClickListener,Observer/*LoaderManager.LoaderCallbacks<Cursor>*/{
    public final String LOG_TAG = this.getClass().getSimpleName();

    private static final String ARG_SECTION_NUMBER = "section_number";

    private MapView mMapView;
    private GoogleMap googleMap;

    public PlacesMapFragment() {
    }

    public static PlacesMapFragment newInstance(int sectionNumber) {
        PlacesMapFragment fragment = new PlacesMapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
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
                .target(new LatLng(MainActivity.location.getLatitude(), MainActivity.location.getLongitude())).zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        googleMap.setOnMarkerClickListener(this);

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

    @Override
    public void update(Cursor cursor) {
            googleMap.clear();

            MarkerOptions marker;

            if(cursor == null){
                return;
            }

            if(cursor.isLast()){
                cursor.moveToPosition(-1);
            }

            while(cursor.moveToNext()){
                marker = new MarkerOptions().position(
                        new LatLng(cursor.getDouble(cursor.getColumnIndex("latitude")), cursor.getDouble(cursor.getColumnIndex("longitude")))).title(cursor.getString(cursor.getColumnIndex("place_id")));

                marker.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                googleMap.addMarker(marker);
            }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Location placeLocation = new Location("");
        Intent intent = new Intent(getContext(), PlaceActivity.class);
        placeLocation.setLatitude(marker.getPosition().latitude);
        placeLocation.setLongitude(marker.getPosition().longitude);

        intent.putExtra("placeid",marker.getTitle());
        intent.putExtra("distance",(int)MainActivity.location.distanceTo(placeLocation) + "m");
        startActivity(intent);
        return true;
    }
}
