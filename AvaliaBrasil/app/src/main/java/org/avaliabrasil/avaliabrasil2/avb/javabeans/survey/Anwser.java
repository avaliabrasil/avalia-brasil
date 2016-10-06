package org.avaliabrasil.avaliabrasil2.avb.javabeans.survey;

import android.database.Cursor;

import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;

/**
 * Created by Developer on 31/03/2016.
 */
public class Anwser {

    private String question_id;
    private String likert = "";
    private String comment = "";
    private String number = "";
    private String surveyId = "";
    private String instrumentId;
    private String groupId;

    public Anwser(String surveyId, String instrumentId ,String groupId ,String question_id, String likert, String comment, String number) {
        this.surveyId = surveyId;
        this.question_id = question_id;
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
        this.question_id = c.getString(c.getColumnIndex(AvBContract.AnwserEntry.QUESTION_ID));
        this.instrumentId = c.getString(c.getColumnIndex(AvBContract.AnwserEntry.INSTRUMENT_ID));
        this.groupId = c.getString(c.getColumnIndex(AvBContract.AnwserEntry.GROUP_ID));

        String type = c.getString(c.getColumnIndex(AvBContract.AnwserEntry.QUESTION_TYPE));

        if (type.contentEquals(Question.QuestionTypes.IS_COMMENT.getType())) {
            comment =
                    c.getString(c.getColumnIndex(AvBContract.AnwserEntry.ANWSER));
        } else if (type.contentEquals(Question.QuestionTypes.IS_LIKERT.getType())) {
            likert =
                    c.getString(c.getColumnIndex(AvBContract.AnwserEntry.ANWSER));

        } else if (type.contentEquals(Question.QuestionTypes.IS_NUMBER.getType())) {
            number =
                    c.getString(c.getColumnIndex(AvBContract.AnwserEntry.ANWSER));

        }

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

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }
}
