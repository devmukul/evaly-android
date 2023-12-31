package bd.com.evaly.evalyshop.models.user;

import com.google.gson.annotations.SerializedName;

public class AddressItem {

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

    @SerializedName(value = "is_Primary", alternate = "is_primary")
    private boolean isPrimary;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullAddress() {
        return address + ", " + area + ", " + city + ", " + region;
    }

    public String getFullAddressLine() {
        return address + ", " + area + "\n" + city + ", " + region;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    @Override
    public String toString() {
        return
                "AddressItem{" +
                        "area = '" + area + '\'' +
                        ",address = '" + address + '\'' +
                        ",full_name = '" + fullName + '\'' +
                        ",city = '" + city + '\'' +
                        ",phone_number = '" + phoneNumber + '\'' +
                        ",region = '" + region + '\'' +
                        ",status = '" + status + '\'' +
                        ",is_Primary = '" + isPrimary + '\'' +
                        "}";
    }
}