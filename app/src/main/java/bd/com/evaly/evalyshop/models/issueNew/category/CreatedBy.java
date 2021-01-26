package bd.com.evaly.evalyshop.models.issueNew.category;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CreatedBy implements Serializable {

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("email")
    private String email;

    @SerializedName("username")
    private String username;

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}