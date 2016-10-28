package org.avaliabrasil.avaliabrasil2.avb.fragments.evaluate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil2.R;
import org.avaliabrasil.avaliabrasil2.avb.activity.EvaluationActivity;
import org.avaliabrasil.avaliabrasil2.avb.adapters.CategoryAdapter;
import org.avaliabrasil.avaliabrasil2.avb.adapters.DividerItemDecoration;
import org.avaliabrasil.avaliabrasil2.avb.adapters.PlaceTypeAdapter;
import org.avaliabrasil.avaliabrasil2.avb.dao.PlaceDetailsDAO;
import org.avaliabrasil.avaliabrasil2.avb.impl.PlaceDetailsDAOImpl;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.AvaliaBrasilCategory;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.AvaliaBrasilPlaceType;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.NewPlace;

import java.util.ArrayList;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 * @version 1.0
 * @since 1.0
 */
public class NewPlaceFragment extends TransactionFragment implements AdapterView.OnItemSelectedListener {

    /**
     *
     */
    private TextView tvQuestion;


    /**
     *
     */
    private TextView tvCategory;

    /**
     *
     */
    private TextView tvType;

    private RelativeLayout rlPlaceCityAndState;

    private EditText etCity;

    private EditText etState;

    /**
     *
     */
    private RecyclerView rcCategory;

    /**
     *
     */
    private RecyclerView rcType;

    /**
     *
     */
    private ArrayList<AvaliaBrasilCategory> category;

    /**
     *
     */
    private ArrayList<AvaliaBrasilPlaceType> types;

    /**
     *
     */
    private String placeId;

    private PlaceDetailsDAO placeDetailsDAO;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setRetainInstance(true);

        View rootView = inflater.inflate(R.layout.fragment_new_place, container, false);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        tvQuestion = (TextView) rootView.findViewById(R.id.tvQuestion);

        tvCategory = (TextView) rootView.findViewById(R.id.tvCategory);

        tvType = (TextView) rootView.findViewById(R.id.tvType);

        rlPlaceCityAndState = (RelativeLayout) rootView.findViewById(R.id.rlPlaceCityAndState);

        etCity = (EditText) rootView.findViewById(R.id.etCity);

        etState = (EditText) rootView.findViewById(R.id.etState);

        rcCategory = (RecyclerView) rootView.findViewById(R.id.rcCategory);

        rcType = (RecyclerView) rootView.findViewById(R.id.rcType);

        tvQuestion.setText(getQuestion().getTitle());

        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener((EvaluationActivity) getActivity());

        category = (ArrayList<AvaliaBrasilCategory>) getArguments().getSerializable("categoriess");

        types = (ArrayList<AvaliaBrasilPlaceType>) getArguments().getSerializable("types");

        placeId = getArguments().getString("placeId");

        etCity.setText(getArguments().getString("city",""));

        etState.setText(getArguments().getString("state",""));

        etCity.setEnabled(etCity.getText().toString().isEmpty());

        etState.setEnabled(etState.getText().toString().isEmpty());

        btnSubmit.setVisibility(View.GONE);

        placeDetailsDAO = new PlaceDetailsDAOImpl(getContext());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rcCategory.setLayoutManager(mLayoutManager);
        rcCategory.setItemAnimator(new DefaultItemAnimator());
        rcCategory.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

        rcCategory.setAdapter(new CategoryAdapter(getContext(), category, this));
        rcCategory.setHasFixedSize(true);

        rcType.setItemAnimator(new DefaultItemAnimator());
        rcType.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

        return rootView;
    }

    @Override
    public boolean isAnwser() {
        return isCategorySelected && isPlaceSelected && !etCity.getText().toString().isEmpty() && !etState.getText().toString().isEmpty();
    }

    @Override
    public NewPlace getAnwser() {
        if (!isAnwser()) {
            return null;
        }

        placeDetailsDAO.updateCityAndState(placeId,etCity.getText().toString(),etState.getText().toString());

        NewPlace newPlace = new NewPlace(placeId,category_pos,type_pos);

        return newPlace;
    }

    boolean isCategorySelected = false;
    boolean isPlaceSelected = false;
    String category_pos;
    String type_pos;


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (id == 1) {
            tvCategory.setText(tvCategory.getText() + " " + category.get(position).getCategory());
            rcCategory.setVisibility(View.GONE);
            rcType.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            rcType.setLayoutManager(mLayoutManager);
            isCategorySelected = true;
            category_pos = category.get(position).getIdCategory();

            rcType.setAdapter(new PlaceTypeAdapter(getContext(), types, this, category.get(position).getIdCategory()));
            rcType.setHasFixedSize(true);
        } else {
            tvType.setText(tvType.getText() + " " + types.get(position).getCategory());
            rcType.setVisibility(View.GONE);
            isPlaceSelected = true;
            type_pos = types.get(position).getId();

            btnSubmit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
