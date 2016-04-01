package org.avaliabrasil.avaliabrasil.rest.javabeans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer on 31/03/2016.
 */
public class Group {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("order")
    @Expose
    private String order;

    @SerializedName("questions")
    @Expose
    private List<Question> questions = new ArrayList<Question>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
