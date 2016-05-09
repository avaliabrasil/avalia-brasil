package org.avaliabrasil.avaliabrasil.rest.javabeans;

import android.util.Log;

import com.google.gson.JsonObject;

/**
 * Created by Developer on 31/03/2016.
 */
public class Anwser {

    private String question_id;

    private String likert = "";
    private String comment = "";
    private String number = "";

    public Anwser(String question_id, String likert, String comment, String number) {
        this.question_id = question_id;
        if (likert != null) {
            this.likert = likert;
        }
        if (comment != null) {
            this.comment = comment;
        }
        if (number != null) {
            this.number = number;
        }
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getLikert() {
        return likert;
    }

    public void setLikert(String likert) {
        this.likert = likert;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();

        obj.addProperty("question_id", question_id);

        JsonObject anwsers = new JsonObject();

        anwsers.addProperty("likert", getLikert());
        anwsers.addProperty("comment", getComment());
        anwsers.addProperty("number", getNumber());

        obj.add("anwsers", anwsers);

        Log.d("Anwser", "toJson: ".concat(obj.toString()));

        return obj;
    }
}
