package org.avaliabrasil.avaliabrasil2.avb.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil2.R;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.AvaliaBrasilPlaceType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer on 06/04/2016.
 */
public class PlaceTypeAdapter extends RecyclerView.Adapter<PlaceTypeAdapter.MyViewHolder> {

    List<AvaliaBrasilPlaceType> items = new ArrayList<>();
    Context context;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_comment_card_view, null);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AvaliaBrasilPlaceType category = items.get(position);
        holder.title.setText(category.getCategory());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tvComment);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelectedListener.onItemSelected(null, v, getPosition(), 2);
                }
            });
        }
    }

    public PlaceTypeAdapter(Context context, List<AvaliaBrasilPlaceType> items, AdapterView.OnItemSelectedListener onItemSelectedListener, String idCategory) {
        //this.items = items;

        for (AvaliaBrasilPlaceType placeType : items) {
            if (placeType.getIdCategory().contains(idCategory)) {
                this.items.add(placeType);
            }
        }

        items.clear();
        items.addAll(this.items);

        this.context = context;
        this.onItemSelectedListener = onItemSelectedListener;
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener;
}
