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

/**
 * {@link CategoryCursorAdapter} exposes a list of Places
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class CategoryCursorAdapter extends CursorAdapter {
    public final String LOG_TAG = this.getClass().getSimpleName();

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final TextView text1;

        public ViewHolder(View view) {
            text1 = (TextView) view.findViewById(android.R.id.text1);
        }
    }

    public CategoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.text1.setText(cursor.getString(cursor.getColumnIndex("name")));
    }
}
