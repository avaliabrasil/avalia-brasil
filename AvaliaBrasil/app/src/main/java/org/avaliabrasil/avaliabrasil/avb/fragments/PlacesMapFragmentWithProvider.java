package org.avaliabrasil.avaliabrasil.avb.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import org.avaliabrasil.avaliabrasil.avb.MainActivityWithProvider;
import org.avaliabrasil.avaliabrasil.avb.PlaceActivityTeste;
import org.avaliabrasil.avaliabrasil.data.AvBProviderTest;
import org.avaliabrasil.avaliabrasil.rest.javabeans.ResultPlaceSearch;

/**
 * Created by Pedro on 29/02/2016.
 */
// PlacesMapFragment: Map of nearby found places
public class PlacesMapFragmentWithProvider extends Fragment implements GoogleMap.OnMarkerClickListener,LoaderManager.LoaderCallbacks<Cursor>{
    public final String LOG_TAG = this.getClass().getSimpleName();

    private static final String ARG_SECTION_NUMBER = "section_number";

    private MapView mMapView;
    private GoogleMap googleMap;

    public PlacesMapFragmentWithProvider() {
    }

    public static PlacesMapFragmentWithProvider newInstance(int sectionNumber) {
        PlacesMapFragmentWithProvider fragment = new PlacesMapFragmentWithProvider();
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
                .target(new LatLng(MainActivityWithProvider.location.getLatitude(), MainActivityWithProvider.location.getLongitude())).zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        getLoaderManager().initLoader(0, null, this);

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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = AvBProviderTest.PLACE_CONTENT_URI;
        return new CursorLoader(getContext(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        MarkerOptions marker;

        data.moveToFirst();

        while(data.moveToNext()){
            marker = new MarkerOptions().position(
                    new LatLng(data.getDouble(data.getColumnIndex("latitude")), data.getDouble(data.getColumnIndex("longitude")))).title(data.getString(data.getColumnIndex("place_id")));

            // Changing marker icon
            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));


            // adding marker
            googleMap.addMarker(marker);
        }

        googleMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
       googleMap.clear();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
                Location placeLocation = new Location("");
                Intent intent = new Intent(getContext(), PlaceActivityTeste.class);
                placeLocation.setLatitude(marker.getPosition().latitude);
                placeLocation.setLongitude(marker.getPosition().longitude);

                intent.putExtra("placeid",marker.getTitle());
                intent.putExtra("distance",(int)MainActivityWithProvider.location.distanceTo(placeLocation) + "m");
                startActivity(intent);
        return true;
    }
}
