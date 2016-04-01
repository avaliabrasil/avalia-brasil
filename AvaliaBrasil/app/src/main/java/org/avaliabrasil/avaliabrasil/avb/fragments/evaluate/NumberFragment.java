package org.avaliabrasil.avaliabrasil.avb.fragments.evaluate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil.R;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 * @version 1.0
 * @since 1.0
 */
public class NumberFragment extends TransactionFragment {

    /**
     *
     */
    private SeekBar likertScale;

    /**
     *
     */
    private TextView tvLegend;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setRetainInstance(true);

        View rootView = inflater.inflate(R.layout.fragment_number, container, false);

        likertScale = (SeekBar) rootView.findViewById(R.id.likertScale);

        tvLegend = (TextView) rootView.findViewById(R.id.tvLegend);

        tvLegend.setText("3");

        likertScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch(progress){
                    case 0:
                        tvLegend.setText("1");
                        break;

                    case 1:
                        tvLegend.setText("2");
                        break;

                    case 2:
                        tvLegend.setText("3");
                        break;

                    case 3:
                        tvLegend.setText("4");
                        break;

                    case 4:
                        tvLegend.setText("5");
                        break;

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return rootView;
    }

    @Override
    public boolean isAnwser() {
        return true;
    }

    @Override
    public String getAnwser() {
        return tvLegend.getText().toString();
    }
}
