package bd.com.evaly.evalyshop.models.profile;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddressWholeResponse {

    @SerializedName("addresses")
    private List<AddressResponse> addresses;

    @SerializedName("username")
    private String username;

    public List<AddressResponse> getAddresses() {
        return addresses;
    }

    public String getUsername() {
        return username;
    }
}