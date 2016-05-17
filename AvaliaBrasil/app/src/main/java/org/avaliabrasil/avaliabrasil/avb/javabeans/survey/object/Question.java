package org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object;

import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.avaliabrasil.avaliabrasil.avb.data.AvBContract;

import java.io.Serializable;

/**
 * Created by Developer on 31/03/2016.
 */
public class Question implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("questionType")
    @Expose
    private String questionType;

    public Question(Cursor cursor) {
        this.id = cursor.getString(cursor.getColumnIndex(AvBContract.QuestionEntry.QUESTION_ID));
        this.title = cursor.getString(cursor.getColumnIndex(AvBContract.QuestionEntry.QUESTION));
        this.questionType = cursor.getString(cursor.getColumnIndex(AvBContract.QuestionEntry.QUESTION_TYPE));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public Question() {
    }

    public Question(String title) {
        this.title = title;
        this.id = "newPlace";
        this.questionType = "newPlace";
    }


    public enum QuestionTypes {
        IS_LIKERT("likert"), IS_NUMBER("number"), IS_COMMENT("comment");

        private final String type;

        public String getType() {
            return type;
        }

        QuestionTypes(String type) {
            this.type = type;
        }
    }
}
