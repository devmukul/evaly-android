package bd.com.evaly.evalyshop.models.profile;

import com.google.gson.annotations.SerializedName;

public class AddressRequest {

    @SerializedName("id")
    private Integer id;

    @SerializedName("address")
    private String address;

    @SerializedName("area")
    private String area;

    @SerializedName("area_slug")
    private String areaSlug;

    @SerializedName("city")
    private String city;

    @SerializedName("city_slug")
    private String citySlug;

    @SerializedName("region")
    private String region;

    @SerializedName("region_slug")
    private String regionSlug;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("phone_number")
    private String phoneNumber;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }


    public String getFullAddress() {
        return address + ", " + area + ", " + city + ", " + region;
    }

    public String getFullAddressLine() {
        return address + ", " + area + "\n" + city + ", " + region;
    }

    @Override
    public String toString() {
        return "AddressRequest{" +
                "id=" + id +
                ", area='" + area + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}