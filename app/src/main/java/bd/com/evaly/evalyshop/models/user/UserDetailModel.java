package bd.com.evaly.evalyshop.models.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserDetailModel {
    @SerializedName("user")
    private User user;
    @SerializedName("verified")
    private Boolean verified;
    @SerializedName("addresses")
    private List<Address> addresses = null;
    @SerializedName("profile_pic_url")
    private String profilePicUrl;

    public User getUser() {
        return user;
    }

    public String getFullName() {
        return this.user.getFirstName() + " " + this.user.getLastName();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }


    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}
