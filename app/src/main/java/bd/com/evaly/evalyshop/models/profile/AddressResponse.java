package bd.com.evaly.evalyshop.models.profile;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "address_list_table")
public class AddressResponse {

    @PrimaryKey
    @SerializedName("id")
    @NonNull
    private String id;

    @SerializedName("area")
    private String area;

    @SerializedName("address")
    private String address;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("city")
    private String city;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("region")
    private String region;

    @SerializedName("status")
    private String status;

    @SerializedName("is_primary")
    private boolean primary;

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