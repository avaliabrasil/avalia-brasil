package org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Developer on 06/04/2016.
 */
public class AvaliaBrasilPlaceType implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("idCategory")
    @Expose
    private String idCategory;

    @SerializedName("name")
    @Expose
    private String category;

    public String getIdCategory() {
        return idCategory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
