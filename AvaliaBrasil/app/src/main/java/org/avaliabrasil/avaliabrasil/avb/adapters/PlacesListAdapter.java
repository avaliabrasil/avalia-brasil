package org.avaliabrasil.avaliabrasil.avb.adapters;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil.R;

import java.util.Comparator;

/**
 * {@link PlacesListAdapter} exposes a list of Places
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class PlacesListAdapter extends CursorAdapter {
    public final String LOG_TAG = this.getClass().getSimpleName();

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        //TODO adicionar margin na box.
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

    private Location location;

    public PlacesListAdapter(Context context, Cursor c, int flags, Location location) {
        super(context, c, flags);
        this.location = location;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_place_info, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Ler Place Name do Cursor
        viewHolder.nameView.setText(cursor.getString(cursor.getColumnIndex("name")));

        // Ler Place Address
        viewHolder.addressView.setText(cursor.getString(cursor.getColumnIndex("vicinity")));


        Location placeLocation = new Location("");
        placeLocation.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
        placeLocation.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
        // Ler Place Distance
        viewHolder.distanceView.setText((int)(location== null  ? 0 : location.distanceTo(placeLocation)) + "m");

    }

}