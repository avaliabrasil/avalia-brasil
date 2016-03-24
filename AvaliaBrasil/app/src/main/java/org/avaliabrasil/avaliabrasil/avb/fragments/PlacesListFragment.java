package org.avaliabrasil.avaliabrasil.avb.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.PlaceActivity;
import org.avaliabrasil.avaliabrasil.avb.adapters.PlacesListAdapter;
import org.avaliabrasil.avaliabrasil.sync.Observer;

/**
 * Created by Pedro on 29/02/2016.
 */
public class PlacesListFragment extends Fragment implements Observer{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private PlacesListAdapter mPlacesListAdapter;

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ListView mListView;

    private int mPosition = ListView.INVALID_POSITION;

    private  static final String SELECTED_place = "selected_place";

    private Location location;

    public static PlacesListFragment newInstance(int sectionNumber, Location location) {
        PlacesListFragment fragment = new PlacesListFragment();
        fragment.setLocation(location);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PlacesListFragment(){

    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setRetainInstance(true);

        View rootView = inflater.inflate(R.layout.fragment_places_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listview_places);

        mPlacesListAdapter = new PlacesListAdapter(getContext(),null,0,location);

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

                intent.putExtra("placeid",cur.getString(cur.getColumnIndex("place_id")));
                intent.putExtra("distance",(location == null ? "Não foi possível calcular a distancia": (int)location.distanceTo(placeLocation) + "m"));
                intent.putExtra("name",cur.getString(cur.getColumnIndex("name")));

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
}
