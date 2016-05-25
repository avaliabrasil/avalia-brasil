package org.avaliabrasil.avaliabrasil.avb.javabeans.survey;

import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.avaliabrasil.avaliabrasil.avb.dao.AvBContract;
import org.json.JSONObject;

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

    private String updateAt = "";

    @SerializedName("groups")
    @Expose
    private List<GroupQuestion> groupQuestions = new ArrayList<GroupQuestion>();

    public Instrument() {
    }

    public Instrument(String instrumentId,String updateAt , List<GroupQuestion> groupQuestions){
        this.id = instrumentId;
        this.groupQuestions = groupQuestions;
        this.updateAt = updateAt == null ? "" : updateAt;
    }


    public List<GroupQuestion> getGroupQuestions() {
        return groupQuestions;
    }

    public void setGroupQuestions(List<GroupQuestion> groupQuestions) {
        this.groupQuestions = groupQuestions;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JsonObject instrumentLastUpdated(){
        JsonObject obj = new JsonObject();
        obj.addProperty("id", id);
        obj.addProperty("updated_at", updateAt);

        return obj;
    }
}
