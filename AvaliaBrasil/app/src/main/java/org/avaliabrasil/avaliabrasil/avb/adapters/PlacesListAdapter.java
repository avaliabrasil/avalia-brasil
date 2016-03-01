package org.avaliabrasil.avaliabrasil.avb.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.fragments.PlacesListFragment;

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

    public PlacesListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
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
        viewHolder.nameView.setText(cursor.getString(PlacesListFragment.COL_PLACE_NAME));

        // Ler Place Address
        viewHolder.addressView.setText(cursor.getString(PlacesListFragment.COL_PLACE_ADDRESS));

        // Ler Place Distance
        viewHolder.distanceView.setText(cursor.getString(PlacesListFragment.COL_PLACE_DISTANCE));

    }

//    @Override
//    public int getItemViewType(int position) {
//        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
//    }

//    @Override
//    public int getViewTypeCount() {
//        return VIEW_TYPE_COUNT;
//    }
}