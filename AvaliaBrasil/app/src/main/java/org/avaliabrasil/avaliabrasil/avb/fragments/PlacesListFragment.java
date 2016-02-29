package org.avaliabrasil.avaliabrasil.avb.fragments;

import android.app.LoaderManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil.R;

/**
 * Created by Pedro on 29/02/2016.
 */
public class PlacesListFragment extends Fragment implements LoaderManager<Cursor>{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_places_list, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.places_list_text_view);

        // Não vou alterar o texto do Places List!
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        return rootView;
    }

}
