package org.avaliabrasil.avaliabrasil.rest.javabeans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 * @version 1.0
 * @since 1.0
 */
public class UserToken {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("expires")
    @Expose
    private String expires;

    /**
     * @return The token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token The token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return The expires
     */
    public String getExpires() {
        return expires;
    }

    /**
     * @param expires The expires
     */
    public void setExpires(String expires) {
        this.expires = expires;
    }
}