package org.avaliabrasil.avaliabrasil.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil.R;

/**
 * Created by Developer on 07/04/2016.
 */
public class RankingView extends RelativeLayout {
    private TextView tvClassificationType;
    private TextView tvRank;
    private ImageView ivRankingStatus;

    public RankingView(Context context) {
        super(context);
        init();
    }

    public RankingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RankingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.content_ranking, this);
        this.tvClassificationType = (TextView)findViewById(R.id.tvClassificationType);
        this.tvRank = (TextView)findViewById(R.id.tvRank);
        this.ivRankingStatus = (ImageView)findViewById(R.id.ivRankingStatus);
    }

    public void setUpView(String classificationType , Integer rank , String status){
        tvClassificationType.setText(classificationType);

        tvRank.setText(rank + "º");

        switch(status){
            case "up":
                ivRankingStatus.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                break;
            case "down":
                ivRankingStatus.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                break;
            case "none":
                ivRankingStatus.setImageResource(R.drawable.ic_remove_black_24dp);
                break;
        }
    }
}
