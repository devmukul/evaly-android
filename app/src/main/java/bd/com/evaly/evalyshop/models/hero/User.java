package bd.com.evaly.evalyshop.models.hero;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {

    @SerializedName("user_status")
    private String userStatus;

    @SerializedName("is_superuser")
    private boolean isSuperuser;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("address")
    private String address;

    @SerializedName("is_staff")
    private boolean isStaff;

    @SerializedName("gender")
    private String gender;

    @SerializedName("devices")
    private String devices;

    @SerializedName("last_login")
    private String lastLogin;

    @SerializedName("facebook")
    private String facebook;

    @SerializedName("verified")
    private boolean verified;

    @SerializedName("groups")
    private List<String> groups;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("google")
    private String google;

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("contact")
    private String contact;

    @SerializedName("modified_by")
    private String modifiedBy;

    @SerializedName("modified_at")
    private String modifiedAt;

    @SerializedName("image_sm")
    private String imageSm;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("profile_pic_url")
    private String profilePicUrl;

    @SerializedName("email")
    private String email;

    @SerializedName("username")
    private String username;

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public boolean isIsSuperuser() {
        return isSuperuser;
    }

    public void setIsSuperuser(boolean isSuperuser) {
        this.isSuperuser = isSuperuser;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isIsStaff() {
        return isStaff;
    }

    public void setIsStaff(boolean isStaff) {
        this.isStaff = isStaff;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDevices() {
        return devices;
    }

    public void setDevices(String devices) {
        this.devices = devices;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public String getLastName() {
        if (lastName == null)
            return "";
        else
            return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getGoogle() {
        return google;
    }

    public void setGoogle(String google) {
        this.google = google;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getImageSm() {
        return imageSm;
    }

    public void setImageSm(String imageSm) {
        this.imageSm = imageSm;
    }

    public String getFirstName() {
        if (firstName == null)
            return "";
        else
            return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
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
                "User{" +
                        "user_status = '" + userStatus + '\'' +
                        ",is_superuser = '" + isSuperuser + '\'' +
                        ",is_active = '" + isActive + '\'' +
                        ",address = '" + address + '\'' +
                        ",is_staff = '" + isStaff + '\'' +
                        ",gender = '" + gender + '\'' +
                        ",devices = '" + devices + '\'' +
                        ",last_login = '" + lastLogin + '\'' +
                        ",facebook = '" + facebook + '\'' +
                        ",verified = '" + verified + '\'' +
                        ",groups = '" + groups + '\'' +
                        ",last_name = '" + lastName + '\'' +
                        ",created_at = '" + createdAt + '\'' +
                        ",google = '" + google + '\'' +
                        ",created_by = '" + createdBy + '\'' +
                        ",contact = '" + contact + '\'' +
                        ",modified_by = '" + modifiedBy + '\'' +
                        ",modified_at = '" + modifiedAt + '\'' +
                        ",image_sm = '" + imageSm + '\'' +
                        ",first_name = '" + firstName + '\'' +
                        ",profile_pic_url = '" + profilePicUrl + '\'' +
                        ",email = '" + email + '\'' +
                        ",username = '" + username + '\'' +
                        "}";
    }
}