package org.avaliabrasil.avaliabrasil.rest.javabeans;


/**
 * @author <a href="https://github.com/Klauswk/">Klaus Klein</a>
 *
 * Utility class that define all API status that google api can return, each one has its own mean.
 *
 * @see <a href="https://developers.google.com/places/web-service/details?hl=eng#PlaceDetailsStatusCodes">API Status</a>.
 * @since 1.0
 * @version 1.0
 */
public enum APIStatus {

    /**
     * Indicates that no errors occurred; the place was successfully detected and at least one result was returned.
     * @see <a href="https://developers.google.com/places/web-service/details?hl=eng#PlaceDetailsStatusCodes">API Status</a>.
     * @since 1.0
     * @version 1.0
     */
    OK("OK"),
    /**
     * Indicates a server-side error; trying again may be successful.
     * @see <a href="https://developers.google.com/places/web-service/details?hl=eng#PlaceDetailsStatusCodes">API Status</a>.
     * @since 1.0
     * @version 1.0
     */
    UNKNOWN_ERROR("UNKNOWN_ERROR"),
    /**
     * Indicates that the reference was valid but no longer refers to a valid result. This may occur if the establishment is no longer in business.
     * @see <a href="https://developers.google.com/places/web-service/details?hl=eng#PlaceDetailsStatusCodes">API Status</a>.
     * @since 1.0
     * @version 1.0
     */
    ZERO_RESULTS("ZERO_RESULTS"),
    /**
     * Indicates that you are over your quota.
     * @see <a href="https://developers.google.com/places/web-service/details?hl=eng#PlaceDetailsStatusCodes">API Status</a>.
     * @since 1.0
     * @version 1.0
     */
    OVER_QUERY_LIMIT("OVER_QUERY_LIMIT"),
    /**
     * Indicates that your request was denied, generally because of lack of an invalid key parameter.
     * @see <a href="https://developers.google.com/places/web-service/details?hl=eng#PlaceDetailsStatusCodes">API Status</a>.
     * @since 1.0
     * @version 1.0
     */
    REQUEST_DENIED("REQUEST_DENIED"),
    /**
     * Generally indicates that the query (reference) is missing.
     * @see <a href="https://developers.google.com/places/web-service/details?hl=eng#PlaceDetailsStatusCodes">API Status</a>.
     * @since 1.0
     * @version 1.0
     */
    INVALID_REQUEST("INVALID_REQUEST"),
    /**
     * Indicates that the referenced location was not found in the Places database.
     * @see <a href="https://developers.google.com/places/web-service/details?hl=eng#PlaceDetailsStatusCodes">API Status</a>.
     * @since 1.0
     * @version 1.0
     */
    NOT_FOUND("NOT_FOUND");
        
   private String status;

    private APIStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
