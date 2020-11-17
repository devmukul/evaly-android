package bd.com.evaly.evalyshop.models.profile;

import com.google.gson.annotations.SerializedName;

public class AddressRequest {

    @SerializedName("id")
    private Integer id;

    @SerializedName("area")
    private String area;

    @SerializedName("address")
    private String address;

    @SerializedName("city")
    private String city;

    @SerializedName("region")
    private String region;

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