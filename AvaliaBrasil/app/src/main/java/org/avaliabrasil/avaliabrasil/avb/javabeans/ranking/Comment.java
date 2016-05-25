package org.avaliabrasil.avaliabrasil.avb.javabeans.ranking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment {

    @SerializedName("uid")
    @Expose
    private Integer uid;
    @SerializedName("description")
    @Expose
    private String description;

    /**
     * @return The uid
     */
    public Integer getUid() {
        return uid;
    }

    /**
     * @param uid The uid
     */
    public void setUid(Integer uid) {
        this.uid = uid;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
