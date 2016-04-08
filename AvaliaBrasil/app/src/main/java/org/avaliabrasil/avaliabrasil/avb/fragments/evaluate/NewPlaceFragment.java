package org.avaliabrasil.avaliabrasil.avb.fragments.evaluate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.avaliabrasil.avaliabrasil.R;
import org.avaliabrasil.avaliabrasil.avb.EvaluationActivity;
import org.avaliabrasil.avaliabrasil.avb.adapters.CategoryAdapter;
import org.avaliabrasil.avaliabrasil.avb.adapters.DividerItemDecoration;
import org.avaliabrasil.avaliabrasil.avb.adapters.PlaceTypeAdapter;
import org.avaliabrasil.avaliabrasil.rest.javabeans.AvaliaBrasilCategory;
import org.avaliabrasil.avaliabrasil.rest.javabeans.AvaliaBrasilPlaceType;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 * @version 1.0
 * @since 1.0
 */
public class NewPlaceFragment extends TransactionFragment implements AdapterView.OnItemSelectedListener{

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
    private RecyclerView lvCategory;

    /**
     *
     */
    private RecyclerView lvType;

    /**
     *
     */
    private ArrayList<AvaliaBrasilCategory> category;

    /**
     *
     */
    private ArrayList<AvaliaBrasilPlaceType> types;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setRetainInstance(true);

        View rootView = inflater.inflate(R.layout.fragment_new_place, container, false);

        tvQuestion = (TextView) rootView.findViewById(R.id.tvQuestion);

        tvCategory = (TextView) rootView.findViewById(R.id.tvCategory);

        tvType = (TextView) rootView.findViewById(R.id.tvType);

        lvCategory = (RecyclerView) rootView.findViewById(R.id.lvCategory);

        lvType = (RecyclerView) rootView.findViewById(R.id.lvType);

        tvQuestion.setText(getQuestion().getTitle());

        btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener((EvaluationActivity)getActivity());

        category =(ArrayList<AvaliaBrasilCategory>) getArguments().getSerializable("categoriess");

        types =(ArrayList<AvaliaBrasilPlaceType>) getArguments().getSerializable("types");

        btnSubmit.setVisibility(View.GONE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        lvCategory.setLayoutManager(mLayoutManager);
        lvCategory.setItemAnimator(new DefaultItemAnimator());
        lvCategory.setAdapter(new CategoryAdapter(getContext(),category,this));
        lvCategory.setHasFixedSize(true);
        lvCategory.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));
        lvType.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));
        lvType.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }

    @Override
    public boolean isAnwser() {
        return isCategorySelected&&isPlaceSelected;
    }

    @Override
    public Integer[] getAnwser() {
        if(!isAnwser()){
            return null;
        }

        Integer[] response = new Integer[]{Integer.valueOf(category.get(category_pos).getIdCategory()),Integer.valueOf(types.get(type_pos).getIdCategory())};

         return response;
    }

    boolean isCategorySelected= false;
    boolean isPlaceSelected = false;
    int category_pos = 0;
    int type_pos = 0;


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(id == 1){
            tvCategory.setText(tvCategory.getText() + category.get(position).getCategory());
            lvCategory.setVisibility(View.GONE);
            lvType.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            lvType.setLayoutManager(mLayoutManager);
            isCategorySelected = true;
            category_pos = position;

            lvType.setAdapter(new PlaceTypeAdapter(getContext(),types,this,category.get(position).getIdCategory()));
            lvType.setHasFixedSize(true);

        }else{
            tvType.setText(tvType.getText() + types.get(position).getCategory());
            lvType.setVisibility(View.GONE);
            isPlaceSelected = true;
            type_pos = position;

            btnSubmit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
