package bd.com.evaly.evalyshop.models.issueNew.list;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AssignedTo implements Serializable {

    @SerializedName("last_name")
    private Object lastName;

    @SerializedName("first_name")
    private Object firstName;

    @SerializedName("email")
    private Object email;

    @SerializedName("username")
    private String username;

    public Object getLastName() {
        return lastName;
    }

    public void setLastName(Object lastName) {
        this.lastName = lastName;
    }

    public Object getFirstName() {
        return firstName;
    }

    public void setFirstName(Object firstName) {
        this.firstName = firstName;
    }

    public Object getEmail() {
        return email;
    }

    public void setEmail(Object email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return
                "AssignedTo{" +
                        "last_name = '" + lastName + '\'' +
                        ",first_name = '" + firstName + '\'' +
                        ",email = '" + email + '\'' +
                        ",username = '" + username + '\'' +
                        "}";
    }
}