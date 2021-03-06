package org.avaliabrasil.avaliabrasil2.avb.fragments.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import com.google.maps.android.ui.IconGenerator;

import org.avaliabrasil.avaliabrasil2.R;
import org.avaliabrasil.avaliabrasil2.avb.activity.PlaceActivity;
import org.avaliabrasil.avaliabrasil2.avb.util.AvaliaBrasilApplication;
import org.avaliabrasil.avaliabrasil2.avb.sync.Observer;

import static com.facebook.GraphRequest.TAG;

/**
 * Created by Pedro on 29/02/2016.
 */
public class PlacesMapFragment extends Fragment implements GoogleMap.OnMarkerClickListener,
        Observer {
    public final String LOG_TAG = this.getClass().getSimpleName();

    private static final String ARG_SECTION_NUMBER = "section_number";

    private MapView mMapView;
    private GoogleMap googleMap;

    private AvaliaBrasilApplication avaliaBrasilApplication;

    public PlacesMapFragment() {
    }

    public static PlacesMapFragment newInstance(int sectionNumber, Location location) {
        PlacesMapFragment fragment = new PlacesMapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        avaliaBrasilApplication = (AvaliaBrasilApplication) getActivity().getApplication();

        View rootView = inflater.inflate(R.layout.fragment_places_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();


        double latitude = avaliaBrasilApplication.getLocation() == null ? 0 : avaliaBrasilApplication.getLocation() .getLatitude();
        double longitude = avaliaBrasilApplication.getLocation()  == null ? 0 : avaliaBrasilApplication.getLocation() .getLongitude();

        if (!(latitude == 0 || longitude == 0)) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude)).zoom(16).build();

            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }


        googleMap.setOnMarkerClickListener(this);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return new View(getContext());
        }
        googleMap.setMyLocationEnabled(true);

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
    public synchronized void update(Cursor cursor) {
        Log.d("PlaceMapFragment", "updating map fragment");
        googleMap.clear();

        double latitude = avaliaBrasilApplication.getLocation()  == null ? 0 : avaliaBrasilApplication.getLocation() .getLatitude();
        double longitude = avaliaBrasilApplication.getLocation() == null ? 0 : avaliaBrasilApplication.getLocation() .getLongitude();

        if (!(latitude == 0 || longitude == 0)) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude)).zoom(16).build();

            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }

        MarkerOptions marker;

        if (cursor == null) {
            return;
        }

        if (cursor.isLast()) {
            cursor.moveToPosition(-1);
        }

        IconGenerator iconGenerator = new IconGenerator(getContext());
        while (cursor.moveToNext()) {
            iconGenerator.setStyle(getStyle());

            Bitmap image = iconGenerator.makeIcon(cursor.getString(cursor.getColumnIndex("name")));

            marker = new MarkerOptions().position(
                    new LatLng(cursor.getDouble(cursor.getColumnIndex("latitude")), cursor.getDouble(cursor.getColumnIndex("longitude")))).title(cursor.getString(cursor.getColumnIndex("place_id"))).snippet(cursor.getString(cursor.getColumnIndex("name")));

            marker.icon(BitmapDescriptorFactory
                    .fromBitmap(image));

            googleMap.addMarker(marker);
        }
    }


    private static int counter = 0;

    private int getStyle() {
        counter++;
        if (counter > 7) {
            counter = 0;
        }
        return counter;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(getContext(), PlaceActivity.class);

        Location placeLocation = new Location("");
        placeLocation.setLatitude(marker.getPosition().latitude);
        placeLocation.setLongitude(marker.getPosition().longitude);

        intent.putExtra("placeid", marker.getTitle());
        intent.putExtra("distance", (int) (avaliaBrasilApplication.getLocation()  == null ? 0 : avaliaBrasilApplication.getLocation() .distanceTo(placeLocation)) + "m");
        intent.putExtra("name", marker.getSnippet());

        startActivity(intent);
        return true;
    }
}
