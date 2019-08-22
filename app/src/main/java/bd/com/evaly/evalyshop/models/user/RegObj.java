package bd.com.evaly.evalyshop.models.user;

import com.google.gson.annotations.SerializedName;

public class RegObj {
    public RegObj(String firstName, String lastName, String phoneNumber, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    @SerializedName("first_name")
    public String firstName;
    @SerializedName("last_name")
    public String lastName;
    @SerializedName("phone_number")
    public String phoneNumber;
    public String password;
}
