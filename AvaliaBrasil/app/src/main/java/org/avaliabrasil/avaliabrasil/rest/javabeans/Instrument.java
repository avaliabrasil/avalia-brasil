package org.avaliabrasil.avaliabrasil.rest.javabeans;

import android.database.Cursor;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.avaliabrasil.avaliabrasil.data.AvBContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer on 31/03/2016.
 */
public class Instrument implements Serializable{

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("groups")
    @Expose
    private List<Group> groups = new ArrayList<Group>();

    public Instrument() {
    }

    public Instrument(String id , Cursor c) {
        this.id = id;

        ArrayList<Group> groups = new ArrayList<Group>();

        boolean foundGroup = false;

        while(c.moveToNext()){
            for(Group g : groups){
                Log.d("Instrument", "Instrument id: " + g.getId());
                Log.d("Instrument", "cursor id: " + c.getString(c.getColumnIndex(AvBContract.GroupQuestionEntry.GROUP_ID)));
                if(g.getId().contentEquals(c.getString(c.getColumnIndex(AvBContract.GroupQuestionEntry.GROUP_ID)))){
                    g.addQuestion(new Question(c));
                    foundGroup = true;
                }
            }
            if(foundGroup){
                foundGroup = false;
                continue;
            }
            groups.add(new Group(c));
        }

        setGroups(groups);
    }


    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
