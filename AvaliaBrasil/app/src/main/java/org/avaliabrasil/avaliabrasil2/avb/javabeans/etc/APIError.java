package org.avaliabrasil.avaliabrasil2.avb.javabeans.etc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class APIError {

    @SerializedName("response")
    @Expose
    private APIErrorResponse response;

    @SerializedName("status")
    @Expose
    private int status;

    public APIErrorResponse getResponse() {
        return this.response;
    }

    public void setResponse(APIErrorResponse response) {
        this.response = response;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
