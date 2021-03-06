package org.avaliabrasil.avaliabrasil2.avb.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.AvaliaBrasilCategory;

import java.util.List;

/**
 * Created by Developer on 06/04/2016.
 */
public class PlaceClassificationAdapter extends RecyclerView.Adapter<PlaceClassificationAdapter.MyViewHolder> {

    List<AvaliaBrasilCategory> items;
    Context context;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, null);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AvaliaBrasilCategory category = items.get(position);
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
            title = (TextView) view.findViewById(android.R.id.text1);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelectedListener.onItemSelected(null, v, getPosition(), 1);
                }
            });
        }
    }

    public PlaceClassificationAdapter(Context context, List<AvaliaBrasilCategory> items, AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.items = items;
        this.context = context;
        this.onItemSelectedListener = onItemSelectedListener;
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener;
}
