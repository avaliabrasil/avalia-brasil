package org.avaliabrasil.avaliabrasil.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil.R;

/**
 * Created by Developer on 12/04/2016.
 */
public class EmptyView extends RelativeLayout {

    private TextView tvEmpty;

    public EmptyView(Context context) {
        super(context);
        init();
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.empty_text_view, this);
        this.tvEmpty = (TextView)findViewById(R.id.tvEmpty);
    }

    public void setEmptyText(String text){
        tvEmpty.setText(text);
    }

}
