package org.avaliabrasil.avaliabrasil.rest.javabeans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Developer on 31/03/2016.
 */
public class Instrument {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("data")
    @Expose
    private Data data;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
