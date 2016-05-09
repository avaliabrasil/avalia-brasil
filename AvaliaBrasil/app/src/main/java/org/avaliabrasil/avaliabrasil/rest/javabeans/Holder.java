package org.avaliabrasil.avaliabrasil.rest.javabeans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Klaus Klein
 */
public class Holder implements Serializable {

    @SerializedName("instruments")
    @Expose
    private List<Instrument> instruments = new ArrayList<Instrument>();

    @SerializedName("newPlace")
    @Expose
    private Boolean newPlace = false;

    @SerializedName("categories")
    @Expose
    private List<AvaliaBrasilCategory> categories = new ArrayList<AvaliaBrasilCategory>();


    @SerializedName("placeTypes")
    @Expose
    private List<AvaliaBrasilPlaceType> placeTypes = new ArrayList<AvaliaBrasilPlaceType>();


    public List<Instrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(List<Instrument> instruments) {
        this.instruments = instruments;
    }

    public Boolean isNewPlace() {
        return newPlace;
    }

    public void setNewPlace(Boolean newPlace) {
        this.newPlace = newPlace;
    }

    public List<AvaliaBrasilCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<AvaliaBrasilCategory> categories) {
        this.categories = categories;
    }

    public List<AvaliaBrasilPlaceType> getPlaceTypes() {
        return placeTypes;
    }

    public void setPlaceTypes(List<AvaliaBrasilPlaceType> placeTypes) {
        this.placeTypes = placeTypes;
    }
}
