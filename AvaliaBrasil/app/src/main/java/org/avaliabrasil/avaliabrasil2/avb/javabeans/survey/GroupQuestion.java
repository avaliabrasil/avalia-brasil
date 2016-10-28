package org.avaliabrasil.avaliabrasil2.avb.javabeans.survey;

import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer on 31/03/2016.
 */
public class GroupQuestion implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("order")
    @Expose
    private String order;

    @SerializedName("questions")
    @Expose
    private List<Question> questions = new ArrayList<Question>();

    public GroupQuestion() {
    }

    public GroupQuestion(Cursor c) {
        id = c.getString(c.getColumnIndex(AvBContract.GroupQuestionEntry.GROUP_ID));
        order = c.getString(c.getColumnIndex(AvBContract.GroupQuestionEntry.ORDER_QUESTION));
        addQuestion(new Question(c));
    }

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

    public void addQuestion(Question question) {
        questions.add(question);
    }
}
