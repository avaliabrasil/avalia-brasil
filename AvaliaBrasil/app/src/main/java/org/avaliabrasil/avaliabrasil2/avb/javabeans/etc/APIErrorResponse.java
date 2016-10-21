package org.avaliabrasil.avaliabrasil2.avb.javabeans.etc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class APIErrorResponse {

    @SerializedName("authorized")
    @Expose
    private boolean authorized;

    @SerializedName("error")
    @Expose
    private String error;

    public boolean getAuthorized() {
        return this.authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
