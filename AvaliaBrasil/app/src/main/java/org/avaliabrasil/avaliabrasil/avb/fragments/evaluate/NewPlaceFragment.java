package org.avaliabrasil.avaliabrasil.avb.fragments.evaluate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.activity.EvaluationActivity;
import org.avaliabrasil.avaliabrasil.avb.adapters.CategoryAdapter;
import org.avaliabrasil.avaliabrasil.avb.adapters.DividerItemDecoration;
import org.avaliabrasil.avaliabrasil.avb.adapters.PlaceTypeAdapter;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.AvaliaBrasilCategory;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.AvaliaBrasilPlaceType;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.NewPlace;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setRetainInstance(true);

        View rootView = inflater.inflate(R.layout.fragment_new_place, container, false);

        tvQuestion = (TextView) rootView.findViewById(R.id.tvQuestion);

        tvCategory = (TextView) rootView.findViewById(R.id.tvCategory);

        tvType = (TextView) rootView.findViewById(R.id.tvType);

        rcCategory = (RecyclerView) rootView.findViewById(R.id.rcCategory);

        rcType = (RecyclerView) rootView.findViewById(R.id.rcType);

        tvQuestion.setText(getQuestion().getTitle());

        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener((EvaluationActivity) getActivity());

        category = (ArrayList<AvaliaBrasilCategory>) getArguments().getSerializable("categoriess");

        types = (ArrayList<AvaliaBrasilPlaceType>) getArguments().getSerializable("types");

        placeId = getArguments().getString("placeId");

        btnSubmit.setVisibility(View.GONE);

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
        return isCategorySelected && isPlaceSelected;
    }

    @Override
    public NewPlace getAnwser() {
        if (!isAnwser()) {
            return null;
        }

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
