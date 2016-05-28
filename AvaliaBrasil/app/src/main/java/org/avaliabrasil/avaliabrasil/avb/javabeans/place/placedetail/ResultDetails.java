package org.avaliabrasil.avaliabrasil.avb.javabeans.place.placedetail;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.avaliabrasil.avaliabrasil.avb.javabeans.google.places.AddressComponent;
import org.avaliabrasil.avaliabrasil.avb.javabeans.google.places.Geometry;
import org.avaliabrasil.avaliabrasil.avb.javabeans.google.places.OpeningHour;
import org.avaliabrasil.avaliabrasil.avb.javabeans.etc.Photo;

import java.util.ArrayList;
import java.util.List;

public class ResultDetails {

    @SerializedName("address_components")
    @Expose
    private List<AddressComponent> addressComponents = new ArrayList<AddressComponent>();
    @SerializedName("adr_address")
    @Expose
    private String adrAddress;
    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;
    @SerializedName("formatted_phone_number")
    @Expose
    private String formattedPhoneNumber;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("photos")
    @Expose
    private List<Photo> photos = new ArrayList<Photo>();
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("scope")
    @Expose
    private String scope;
    @SerializedName("types")
    @Expose
    private List<String> types = new ArrayList<String>();
    @SerializedName("vicinity")
    @Expose
    private String vicinity;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHour openingHour;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    private LatLng latlng;

    private String cityName;

    private String stateLetter;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStateLetter() {
        return stateLetter;
    }

    public void setStateLetter(String stateLetter) {
        this.stateLetter = stateLetter;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    /**
     * @return The addressComponents
     */
    public List<AddressComponent> getAddressComponents() {
        return addressComponents;
    }

    /**
     * @param addressComponents The address_components
     */
    public void setAddressComponents(List<AddressComponent> addressComponents) {
        this.addressComponents = addressComponents;
    }

    /**
     * @return The adrAddress
     */
    public String getAdrAddress() {
        return adrAddress;
    }

    /**
     * @param adrAddress The adr_address
     */
    public void setAdrAddress(String adrAddress) {
        this.adrAddress = adrAddress;
    }

    /**
     * @return The formattedAddress
     */
    public String getFormattedAddress() {
        return formattedAddress;
    }

    /**
     * @param formattedAddress The formatted_address
     */
    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    /**
     * @return The formattedPhoneNumber
     */
    public String getFormattedPhoneNumber() {
        return formattedPhoneNumber;
    }

    /**
     * @param formattedPhoneNumber The formatted_phone_number
     */
    public void setFormattedPhoneNumber(String formattedPhoneNumber) {
        this.formattedPhoneNumber = formattedPhoneNumber;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The photos
     */
    public List<Photo> getPhotos() {
        return photos;
    }

    /**
     * @param photos The photos
     */
    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    /**
     * @return The placeId
     */
    public String getPlaceId() {
        return placeId;
    }

    /**
     * @param placeId The place_id
     */
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }


    /**
     * @return The scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * @param scope The scope
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * @return The types
     */
    public List<String> getTypes() {
        return types;
    }

    /**
     * @param types The types
     */
    public void setTypes(List<String> types) {
        this.types = types;
    }

    /**
     * @return The vicinity
     */
    public String getVicinity() {
        return vicinity;
    }

    /**
     * @param vicinity The vicinity
     */
    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    /**
     * @return The website
     */
    public String getWebsite() {
        return website;
    }

    /**
     * @param website The website
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCity(){
        for(AddressComponent addressComponent : addressComponents){
            for(String type : addressComponent.getTypes()){
                if(type.contains("locality")){
                    setCityName(addressComponent.getLongName());
                    return addressComponent.getLongName();
                }
            }
        }
        return "";
    }

    public String getState(){
        for(AddressComponent addressComponent : addressComponents){
            for(String type : addressComponent.getTypes()){
                if(type.contains("administrative_area_level_1")){
                    return addressComponent.getShortName();
                }
            }
        }
        return "";
    }


    public String getCountry(){
        for(AddressComponent addressComponent : addressComponents){
            for(String type : addressComponent.getTypes()){
                if(type.contains("country")){
                    return addressComponent.getShortName();
                }
            }
        }
        return "";
    }

    public OpeningHour getOpeningHour() {
        return openingHour;
    }

    public void setOpeningHour(OpeningHour openingHour) {
        this.openingHour = openingHour;
    }
}
