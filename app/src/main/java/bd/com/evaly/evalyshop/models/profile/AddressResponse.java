package bd.com.evaly.evalyshop.models.profile;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "address_list_table")
public class AddressResponse implements Serializable {

    @PrimaryKey
    @SerializedName("id")
    @NonNull
    private String id;

    @SerializedName("area")
    private String area;

    @SerializedName("area_slug")
    private String areaSlug;

    @SerializedName("address")
    private String address;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("city")
    private String city;

    @SerializedName("city_slug")
    private String citySlug;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("region")
    private String region;

    @SerializedName("region_slug")
    private String regionSlug;

    @SerializedName("status")
    private String status;

    @SerializedName("is_primary")
    private boolean primary;

    public String getAreaSlug() {
        return areaSlug;
    }

    public void setAreaSlug(String areaSlug) {
        this.areaSlug = areaSlug;
    }

    public String getCitySlug() {
        return citySlug;
    }

    public void setCitySlug(String citySlug) {
        this.citySlug = citySlug;
    }

    public String getRegionSlug() {
        return regionSlug;
    }

    public void setRegionSlug(String regionSlug) {
        this.regionSlug = regionSlug;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getFullAddress() {
        return address + ", " + area + ", " + city + ", " + region;
    }

    public String getFullAddressLine() {
        return address + ", " + area + "\n" + city + ", " + region;
    }

    @Override
    public String toString() {
        return
                "AddressResponse{" +
                        "area = '" + area + '\'' +
                        ",address = '" + address + '\'' +
                        ",full_name = '" + fullName + '\'' +
                        ",city = '" + city + '\'' +
                        ",phone_number = '" + phoneNumber + '\'' +
                        ",id = '" + id + '\'' +
                        ",region = '" + region + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }

}