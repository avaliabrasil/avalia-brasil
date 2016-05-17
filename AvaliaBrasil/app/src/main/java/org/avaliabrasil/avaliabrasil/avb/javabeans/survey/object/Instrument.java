package org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object;

import android.database.Cursor;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.avaliabrasil.avaliabrasil.avb.data.AvBContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer on 31/03/2016.
 */
public class Instrument implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("groups")
    @Expose
    private List<GroupQuestion> groupQuestions = new ArrayList<GroupQuestion>();

    public Instrument() {
    }

    public Instrument(String instrumentId, Cursor c) {
        this.id = instrumentId;

        ArrayList<GroupQuestion> groupQuestions = new ArrayList<GroupQuestion>();

        boolean foundGroup = false;

        while (c.moveToNext()) {
            for (GroupQuestion g : groupQuestions) {
                Log.d("Instrument", "Instrument id: " + g.getId());
                Log.d("Instrument", "cursor id: " + c.getString(c.getColumnIndex(AvBContract.GroupQuestionEntry.GROUP_ID)));
                if (g.getId().contentEquals(c.getString(c.getColumnIndex(AvBContract.GroupQuestionEntry.GROUP_ID)))) {
                    g.addQuestion(new Question(c));
                    foundGroup = true;
                }
            }
            if (foundGroup) {
                foundGroup = false;
                continue;
            }
            groupQuestions.add(new GroupQuestion(c));
        }

        setGroupQuestions(groupQuestions);
    }

    public Instrument(String instrumentId, List<GroupQuestion> groupQuestions){
        this.id = instrumentId;
        this.groupQuestions = groupQuestions;
    }


    public List<GroupQuestion> getGroupQuestions() {
        return groupQuestions;
    }

    public void setGroupQuestions(List<GroupQuestion> groupQuestions) {
        this.groupQuestions = groupQuestions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
