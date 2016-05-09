package org.avaliabrasil.avaliabrasil.avb.fragments.evaluate;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.EvaluationActivity;

/**
 * Created by Developer on 04/04/2016.
 */
public class ShareEvaluateFragment extends Fragment {

    /**
     *
     */
    private ShareButton btnShare;

    /**
     *
     */
    private TextView tvSkip;

    /**
     *
     */
    private ShareLinkContent shareLinkContent;

    /**
     *
     */
    private CallbackManager callbackManager;

    private String shareString;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(getContext());
        }

        shareString = getArguments().getString("shareString", "");

        View rootView = inflater.inflate(R.layout.fragment_share_evaluate, container, false);

        btnShare = (ShareButton) rootView.findViewById(R.id.btnShare);

        tvSkip = (TextView) rootView.findViewById(R.id.tvSkip);

        tvSkip.setOnClickListener((EvaluationActivity) getActivity());

        shareLinkContent = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://www.facebook.com/avaliabrasil.org/"))
                .setContentDescription(shareString)
                .build();

        btnShare.setShareContent(shareLinkContent);

        callbackManager = ((EvaluationActivity) getActivity()).getCallbackManager();

        btnShare.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result shareResult) {
                Toast.makeText(getContext(), getResources().getString(R.string.evaluate_shared), Toast.LENGTH_SHORT).show();
                tvSkip.performClick();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getContext(), getResources().getString(R.string.evaluate_shared_error), Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
