package org.avaliabrasil.avaliabrasil2.avb.fragments.main;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil2.R;
import org.avaliabrasil.avaliabrasil2.avb.activity.PlaceActivity;
import org.avaliabrasil.avaliabrasil2.avb.adapters.PlacesListAdapter;
import org.avaliabrasil.avaliabrasil2.avb.util.AvaliaBrasilApplication;
import org.avaliabrasil.avaliabrasil2.avb.sync.Observer;
import org.avaliabrasil.avaliabrasil2.avb.util.Utils;

/**
 * Created by Pedro on 29/02/2016.
 */
public class PlacesListFragment extends Fragment implements Observer {
    private PlacesListAdapter mPlacesListAdapter;

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ListView mListView;

    private int mPosition = ListView.INVALID_POSITION;

    private static final String SELECTED_place = "selected_place";

    private TextView tvEmpty;


    public static PlacesListFragment newInstance(int sectionNumber, Location location) {
        PlacesListFragment fragment = new PlacesListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PlacesListFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setRetainInstance(true);

        View rootView = inflater.inflate(R.layout.fragment_places_list, container, false);

        tvEmpty = (TextView) rootView.findViewById(R.id.tvEmpty);

        mListView = (ListView) rootView.findViewById(R.id.listview_places);

        mPlacesListAdapter = new PlacesListAdapter(getContext(), null, 0, ((AvaliaBrasilApplication)getActivity().getApplication()).getLocation());

        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        /** Aqui abaixo
         *
         * if (Utils.isGPSAvailable(getContext())) {
         *
         * **/
        if (Utils.isGPSAvailable(getContext())) {
            tvEmpty.setText(getContext().getString(R.string.gps_off));
        }else if(!Utils.isNetworkAvailable(getContext())){
            tvEmpty.setText(getContext().getString(R.string.internet_connection_error));
        }

        mListView.setEmptyView(tvEmpty);

        mListView.setAdapter(mPlacesListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cur = (Cursor) mPlacesListAdapter.getItem(position);
                cur.moveToPosition(position);

                Location placeLocation = new Location("");
                Intent intent = new Intent(getContext(), PlaceActivity.class);

                placeLocation.setLatitude(cur.getDouble(cur.getColumnIndex("latitude")));
                placeLocation.setLongitude(cur.getDouble(cur.getColumnIndex("longitude")));

                intent.putExtra("placeid", cur.getString(cur.getColumnIndex("place_id")));
                intent.putExtra("distance", cur.getInt(cur.getColumnIndex("distance")) + "m");
                intent.putExtra("name", cur.getString(cur.getColumnIndex("name")));

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
    public synchronized void update(Cursor cursor) {
        Log.d("PlacesListFragment", "updating list fragment");
        mPlacesListAdapter.swapCursor(cursor);
    }
}
