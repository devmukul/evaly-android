package bd.com.evaly.evalyshop.models.issue;

import com.google.gson.annotations.SerializedName;

public class ReplyBy {

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("id")
    private int id;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("username")
    private String username;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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
                "ReplyBy{" +
                        "last_name = '" + lastName + '\'' +
                        ",id = '" + id + '\'' +
                        ",first_name = '" + firstName + '\'' +
                        ",username = '" + username + '\'' +
                        "}";
    }
}