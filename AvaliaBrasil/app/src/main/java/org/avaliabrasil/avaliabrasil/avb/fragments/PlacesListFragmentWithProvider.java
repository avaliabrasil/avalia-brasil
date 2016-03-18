package org.avaliabrasil.avaliabrasil.avb.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
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
import org.avaliabrasil.avaliabrasil.avb.MainActivity;
import org.avaliabrasil.avaliabrasil.avb.MainActivityWithProvider;
import org.avaliabrasil.avaliabrasil.avb.PlaceActivityTeste;
import org.avaliabrasil.avaliabrasil.avb.adapters.PlacesListAdapter;
import org.avaliabrasil.avaliabrasil.avb.adapters.PlacesListAdapterTeste;
import org.avaliabrasil.avaliabrasil.data.AvBProviderTest;
import org.avaliabrasil.avaliabrasil.rest.GooglePlacesAPIClient;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceSearch;
import org.avaliabrasil.avaliabrasil.sync.Observer;

/**
 * Created by Pedro on 29/02/2016.
 */
public class PlacesListFragmentWithProvider extends Fragment implements Observer/*LoaderManager.LoaderCallbacks<Cursor>*/{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private PlacesListAdapter mPlacesListAdapter;

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ListView mListView;

    private int mPosition = ListView.INVALID_POSITION;

    private  static final String SELECTED_place = "selected_place";

    public static PlacesListFragmentWithProvider newInstance(int sectionNumber) {
        PlacesListFragmentWithProvider fragment = new PlacesListFragmentWithProvider();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PlacesListFragmentWithProvider(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_places_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listview_places);

        mPlacesListAdapter = new PlacesListAdapter(getContext(),null,0, MainActivityWithProvider.location);

        mListView.setAdapter(mPlacesListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cur = (Cursor) mPlacesListAdapter.getItem(position);
                cur.moveToPosition(position);


               Location placeLocation = new Location("");
                Intent intent = new Intent(getContext(), PlaceActivityTeste.class);

                placeLocation.setLatitude(cur.getDouble(cur.getColumnIndex("latitude")));
                placeLocation.setLongitude(cur.getDouble(cur.getColumnIndex("longitude")));

                intent.putExtra("placeid",cur.getString(cur.getColumnIndex("place_id")));
                intent.putExtra("distance",(int)MainActivityWithProvider.location.distanceTo(placeLocation) + "m");
                startActivity(intent);
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_place)) {
            mPosition = savedInstanceState.getInt(SELECTED_place);
        }

       // getLoaderManager().initLoader(0, null, this);

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
    public void update(Cursor cursor) {
        mPlacesListAdapter.swapCursor(cursor);
    }

   /* @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = AvBProviderTest.PLACE_CONTENT_URI;
        return new CursorLoader(getContext(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPlacesListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPlacesListAdapter.swapCursor(null);
    }*/
}
