package org.avaliabrasil.avaliabrasil.avb.adapters;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.rest.javabeans.PlaceSearch;
import org.avaliabrasil.avaliabrasil.rest.javabeans.ResultPlaceSearch;

/**
 * {@link PlacesListAdapterTeste} exposes a list of Places
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */

public class PlacesListAdapterTeste extends BaseAdapter{
    public final String LOG_TAG = this.getClass().getSimpleName();

    private final Context context;

    private PlaceSearch places;

    private Location location;

    public PlacesListAdapterTeste(Context context , PlaceSearch places, Location location){
        this.places = places;
        this.context = context;
        this.location = location;
    }

    @Override
    public int getCount() {
        if(places.getResults() == null){
            return 0;
        }
        return places.getResults().size();
    }

    @Override
    public ResultPlaceSearch getItem(int position) {
        return places.getResults().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_place_info, parent, false);
        }else{
            view = (View) convertView;
        }

        //ImageView iconView = (ImageView) view.findViewById(R.id.place_icon);

        TextView nameView = (TextView) view.findViewById(R.id.place_name_text_view);
        TextView addressView = (TextView) view.findViewById(R.id.place_address_text_view);


        Location placeLocation = new Location("");
        placeLocation.setLatitude(places.getResults().get(position).getGeometry().getLocation().getLat());
        placeLocation.setLongitude(places.getResults().get(position).getGeometry().getLocation().getLng());

        TextView distanceView = (TextView) view.findViewById(R.id.place_distance_text_view);
        distanceView.setText((int)location.distanceTo(placeLocation) + "m");

        nameView.setText(places.getResults().get(position).getName());
        addressView.setText(places.getResults().get(position).getVicinity());

        return view;
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView nameView;
        public final TextView addressView;
        public final TextView distanceView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.place_icon);
            nameView = (TextView) view.findViewById(R.id.place_name_text_view);
            addressView = (TextView) view.findViewById(R.id.place_address_text_view);
            distanceView = (TextView) view.findViewById(R.id.place_distance_text_view);
        }
    }

}