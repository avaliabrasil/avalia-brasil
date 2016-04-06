package org.avaliabrasil.avaliabrasil.avb.fragments.evaluate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.EvaluationActivity;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 * @version 1.0
 * @since 1.0
 */
public class CommentFragment extends TransactionFragment {

    private EditText etComment;

    private TextView tvQuestion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setRetainInstance(true);

        View rootView = inflater.inflate(R.layout.fragment_comment, container, false);

        etComment = (EditText) rootView.findViewById(R.id.etComment);

        tvQuestion = (TextView) rootView.findViewById(R.id.tvQuestion);

        tvQuestion.setText(getQuestion().getTitle());

        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener((EvaluationActivity)getActivity());

        return rootView;
    }

    @Override
    public boolean isAnwser() {

        if(TextUtils.isEmpty(etComment.getText().toString())){
            return false;
        }
        return true;
    }

    @Override
    public String getAnwser() {
        if(!isAnwser()){
            return null;
        }
        return etComment.getText().toString();
    }
}
