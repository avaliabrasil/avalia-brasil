package org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object;

import android.database.Cursor;

import org.avaliabrasil.avaliabrasil.avb.data.AvBContract;

/**
 * Created by Developer on 31/03/2016.
 */
public class Anwser {

    private String question_id;
    private String placeId;
    private String likert = "";
    private String comment = "";
    private String number = "";
    private String instrumentId;
    private String groupId;

    public Anwser(String placeId, String instrumentId ,String groupId ,String question_id, String likert, String comment, String number) {
        this.question_id = question_id;
        this.placeId = placeId;
        this.instrumentId = instrumentId;
        this.groupId = groupId;

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

    public Anwser(Cursor c){
        question_id = c.getString(c.getColumnIndex(AvBContract.SurveyEntry.QUESTION_ID));
        String type = c.getString(c.getColumnIndex(AvBContract.SurveyEntry.QUESTION_TYPE));

        if (type.contentEquals(Question.QuestionTypes.IS_COMMENT.getType())) {
            comment =
                    c.getString(c.getColumnIndex(AvBContract.SurveyEntry.ANWSER));
        } else if (type.contentEquals(Question.QuestionTypes.IS_LIKERT.getType())) {
            likert =
                    c.getString(c.getColumnIndex(AvBContract.SurveyEntry.ANWSER));

        } else if (type.contentEquals(Question.QuestionTypes.IS_NUMBER.getType())) {
            number =
                    c.getString(c.getColumnIndex(AvBContract.SurveyEntry.ANWSER));

        }

    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

}
