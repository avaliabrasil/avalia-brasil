package org.avaliabrasil.avaliabrasil2.avb.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil2.R;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.ranking.PlaceRanking;
import org.avaliabrasil.avaliabrasil2.avb.mvp.RankingActivityPresenter;

import java.util.ArrayList;
import java.util.List;


public class PlaceRankingAdapter extends RecyclerView.Adapter<PlaceRankingAdapter.MyViewHolder> {

    /**
     *
     */
    private List<PlaceRanking> items = new ArrayList<>();

    /**
     *
     */
    private Context context;

    private RankingActivityPresenter rankingActivityPresenter;
    private RecyclerView recyclerView;

    public PlaceRankingAdapter(RecyclerView recyclerView,Context context, List<PlaceRanking> items , RankingActivityPresenter rankingActivityPresenter) {

        this.recyclerView = recyclerView;
        this.items = items;
        this.context = context;
        this.rankingActivityPresenter = rankingActivityPresenter;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_place_ranking_card_view, null);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = recyclerView.getChildLayoutPosition(v);
                final PlaceRanking placeRanking = items.get(itemPosition);
                rankingActivityPresenter.OnRankingClickListener(placeRanking);
            }
        });
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final PlaceRanking placeRanking = items.get(position);

        holder.tvRankingPosition.setText(String.valueOf(placeRanking.getRankingPosition()));
        holder.tvName.setText(placeRanking.getName());
        holder.tvAdress.setText(placeRanking.getAddress());
        holder.tvQualityIndex.setText(String.valueOf(placeRanking.getQualityIndex()));



    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvRankingPosition;
        public TextView tvName;
        public TextView tvAdress;
        public TextView tvQualityIndex;

        public MyViewHolder(View view) {
            super(view);

            tvRankingPosition = (TextView) view.findViewById(R.id.tvRankingPosition);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvAdress = (TextView) view.findViewById(R.id.tvAdress);
            tvQualityIndex = (TextView) view.findViewById(R.id.tvQualityIndex);

        }
    }

}