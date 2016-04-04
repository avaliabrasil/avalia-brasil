package org.avaliabrasil.avaliabrasil.avb.fragments.evaluate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.EvaluationActivity;

/**
 * Created by Developer on 04/04/2016.
 */
public class ShareEvaluateFragment extends Fragment {

    /**
     *
     */
    private Button btnShare;

    /**
     *
     */
    private TextView tvSkip;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        View rootView = inflater.inflate(R.layout.fragment_share_evaluate, container, false);

        btnShare = (Button) rootView.findViewById(R.id.btnShare);

        tvSkip = (TextView) rootView.findViewById(R.id.tvSkip);

        btnShare.setOnClickListener((EvaluationActivity)getActivity());

        tvSkip.setOnClickListener((EvaluationActivity)getActivity());

        return rootView;
    }
}
