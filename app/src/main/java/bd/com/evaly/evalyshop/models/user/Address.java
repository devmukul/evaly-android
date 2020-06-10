package bd.com.evaly.evalyshop.models.user;

import com.google.gson.annotations.SerializedName;

public class Address {

    @SerializedName("address")
    private String address;
    @SerializedName("id")
    private Integer id;

    public Address() {
    }

    public Address(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
