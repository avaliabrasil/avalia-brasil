package org.avaliabrasil.avaliabrasil2.avb.fragments.evaluate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil2.R;
import org.avaliabrasil.avaliabrasil2.avb.activity.EvaluationActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 * @version 1.0
 * @since 1.0
 */
public class LikertFragment extends TransactionFragment {

    /**
     *
     */
    private List<RadioButton> options = new ArrayList<RadioButton>();


    private TextView tvQuestion;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setRetainInstance(true);

        View rootView = inflater.inflate(R.layout.fragment_likert, container, false);

        options.add((RadioButton) rootView.findViewById(R.id.option1));
        options.add((RadioButton) rootView.findViewById(R.id.option2));
        options.add((RadioButton) rootView.findViewById(R.id.option3));
        options.add((RadioButton) rootView.findViewById(R.id.option4));
        options.add((RadioButton) rootView.findViewById(R.id.option5));

        tvQuestion = (TextView) rootView.findViewById(R.id.tvQuestion);

        tvQuestion.setText(getQuestion().getTitle());

        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener((EvaluationActivity) getActivity());

        return rootView;
    }

    @Override
    public boolean isAnwser() {
        for (RadioButton rb : options) {
            if (rb.isChecked()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getAnwser() {
        if (!isAnwser()) {
            return null;
        }
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).isChecked()) {
                return String.valueOf((5 - i));
            }
        }
        return null;
    }
}
