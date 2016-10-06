package org.avaliabrasil.avaliabrasil2.avb.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil2.avb.dao.LocationDAO;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationAdapter extends ArrayAdapter<Location> {
    private List<Location> items;
    private LocationDAO locationDAO;

    public LocationAdapter(Context context,LocationDAO locationDAO) {
        super(context, android.R.layout.simple_dropdown_item_1line,locationDAO.findLocationByName(""));
        this.locationDAO = locationDAO;
        items = new ArrayList<Location>();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(android.R.layout.simple_dropdown_item_1line, null);
        }

        if(!items.isEmpty()){
            Location location = items.get(position);
            if (location != null) {
                TextView locationLabel = (TextView) v.findViewById(android.R.id.text1);
                if (locationLabel != null) {
                    locationLabel.setText(location.toString());
                }
            }
        }
        return v;
    }

    @Nullable
    public Location getLocation(int position) {
        if(!items.isEmpty()){
            return items.get(position);
        }
        return null;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return resultValue.toString();
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                FilterResults filterResults = new FilterResults();
                items.clear();
                items.addAll(locationDAO.findLocationByName(constraint.toString()));
                filterResults.values = items;
                filterResults.count = items.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            ArrayList<Location> filteredList = (ArrayList<Location>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (Location c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

}