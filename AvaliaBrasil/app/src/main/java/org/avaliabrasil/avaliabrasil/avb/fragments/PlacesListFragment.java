package org.avaliabrasil.avaliabrasil.avb.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.MainActivity;
import org.avaliabrasil.avaliabrasil.avb.adapters.PlacesListAdapter;
import org.avaliabrasil.avaliabrasil.data.AvBContract;

/**
 * Created by Pedro on 29/02/2016.
 */
public class PlacesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private PlacesListAdapter mPlacesListAdapter;

    private static final String ARG_SECTION_NUMBER = "section_number";


    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;

    private  static final String SELECTED_place = "selected_place";

    private static final int PLACE_LOADER = 0;

    // Projeção com as colunas que eu quero:

    private static final String[] PLACE_COLUMNS = {
            AvBContract.PlaceEntry.TABLE_NAME + "." + AvBContract.PlaceEntry._ID,
            AvBContract.PlaceEntry.COLUMN_PLACE_ID,
            AvBContract.PlaceEntry.COLUMN_NAME,
            AvBContract.PlaceEntry.COLUMN_ADRESS,
            AvBContract.PlaceEntry.COLUMN_OPEN_HOURS
    };

    public static final int COL_ID = 0;
    public static final int COL_PLACE_ID = 1;
    public static final int COL_PLACE_NAME = 2;
    public static final int COL_PLACE_ADDRESS = 3;
    public static final int COL_PLACE_DISTANCE = 4;

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri placeUri);
    }

    public PlacesListFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlacesListFragment newInstance(int sectionNumber) {
        PlacesListFragment fragment = new PlacesListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mPlacesListAdapter = new PlacesListAdapter(getActivity(),null,0,null);
        View rootView = inflater.inflate(R.layout.fragment_places_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.listview_places);
        mListView.setAdapter(mPlacesListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(AvBContract.PlaceEntry.buildGooglePlaceUri(
                                    cursor.getString(COL_PLACE_ID)
                            ));
                }
                mPosition = position;
            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_place)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_place);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PLACE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    // Não vou fazer isso, mas seria interessante caso a pesquisa mude, por exemplo:
    // since we read the location when we create the loader, all we need to do is restart things
//    void onQueryChanged(String query) {
//
//        Uri uri = placesSearchUri;
//
//        if (null!= uri)
//
//        updateWeather();
//        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
//    }

//    private void updatePlacesList() {
//        AvbSyncAdapter.syncImmediately(getActivity());
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_place, mPosition);
        }
        super.onSaveInstanceState(outState);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today.

        // Sort order:  Ascending, by Place Name.
        String sortOrder = AvBContract.PlaceEntry.COLUMN_NAME + " ASC";

        // String locationSetting = Utility.getPreferredLocation(getActivity());
        // Antes de 02/03, 18:10 Uri placesSearchUri = AvBContract.PlaceEntry.CONTENT_URI;
        Uri placesSearchUri = ((MainActivity)getActivity()).getSearchUri();

        //#parei aqui. tenho que implementar a nova uri no content provider

        return new CursorLoader(getActivity(),
                placesSearchUri,
                PLACE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPlacesListAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPlacesListAdapter.swapCursor(null);
    }

}
