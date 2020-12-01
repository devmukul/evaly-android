package bd.com.evaly.evalyshop.models.user;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserModel {

    @SerializedName("addresses")
    private Addresses addresses;

    @SerializedName("occupation")
    private String occupation;

    @SerializedName("gender")
    private String gender;

    @SerializedName("birth_date")
    private String birthDate;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("password")
    private String password;

    @SerializedName("user_type")
    private String userType;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("other_email")
    private String otherEmail;

    @SerializedName("contact")
    private String contact;

    @SerializedName("date_joined")
    private String dateJoined;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("profile_pic_url")
    private String profilePicUrl;

    @SerializedName("email")
    private String email;

    @SerializedName("user_status")
    private String userStatus;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("is_superuser")
    private boolean isSuperuser;

    @SerializedName("address")
    private String address;

    @SerializedName("is_staff")
    private boolean isStaff;

    @SerializedName("last_login")
    private String lastLogin;

    @SerializedName("facebook")
    private String facebook;

    @SerializedName("verified")
    private boolean verified;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("groups")
    private List<String> groups;

    @SerializedName("google")
    private String google;

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("parents_info")
    private ParentsInfo parentsInfo;

    @SerializedName("block_history")
    private List<BlockHistoryItem> blockHistory;

    @SerializedName("organization")
    private String organization;

    @SerializedName("updated_by")
    private String updatedBy;

    @SerializedName("primary_email")
    private String primaryEmail;

    @SerializedName("image_sm")
    private String imageSm;

    @SerializedName("username")
    private String username;

    public void setAddresses(Addresses addresses) {
        this.addresses = addresses;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Addresses getAddresses() {
        return addresses;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserType() {
        return userType;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setOtherEmail(String otherEmail) {
        this.otherEmail = otherEmail;
    }

    public String getOtherEmail() {
        return otherEmail;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }

    public String getContacts() {
        return contact;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsSuperuser(boolean isSuperuser) {
        this.isSuperuser = isSuperuser;
    }

    public boolean isIsSuperuser() {
        return isSuperuser;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setIsStaff(boolean isStaff) {
        this.isStaff = isStaff;
    }

    public boolean isIsStaff() {
        return isStaff;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGoogle(String google) {
        this.google = google;
    }

    public String getGoogle() {
        return google;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setParentsInfo(ParentsInfo parentsInfo) {
        this.parentsInfo = parentsInfo;
    }

    public ParentsInfo getParentsInfo() {
        return parentsInfo;
    }

    public void setBlockHistory(List<BlockHistoryItem> blockHistory) {
        this.blockHistory = blockHistory;
    }

    public List<BlockHistoryItem> getBlockHistory() {
        return blockHistory;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrganization() {
        return organization;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setImageSm(String imageSm) {
        this.imageSm = imageSm;
    }

    public String getImageSm() {
        return imageSm;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return
                "UserModel{" +
                        "addresses = '" + addresses + '\'' +
                        ",occupation = '" + occupation + '\'' +
                        ",gender = '" + gender + '\'' +
                        ",birth_date = '" + birthDate + '\'' +
                        ",created_at = '" + createdAt + '\'' +
                        ",password = '" + password + '\'' +
                        ",user_type = '" + userType + '\'' +
                        ",updated_at = '" + updatedAt + '\'' +
                        ",other_email = '" + otherEmail + '\'' +
                        ",contact = '" + contact + '\'' +
                        ",date_joined = '" + dateJoined + '\'' +
                        ",first_name = '" + firstName + '\'' +
                        ",profile_pic_url = '" + profilePicUrl + '\'' +
                        ",email = '" + email + '\'' +
                        ",user_status = '" + userStatus + '\'' +
                        ",is_active = '" + isActive + '\'' +
                        ",is_superuser = '" + isSuperuser + '\'' +
                        ",address = '" + address + '\'' +
                        ",is_staff = '" + isStaff + '\'' +
                        ",last_login = '" + lastLogin + '\'' +
                        ",facebook = '" + facebook + '\'' +
                        ",verified = '" + verified + '\'' +
                        ",last_name = '" + lastName + '\'' +
                        ",groups = '" + groups + '\'' +
                        ",google = '" + google + '\'' +
                        ",created_by = '" + createdBy + '\'' +
                        ",parents_info = '" + parentsInfo + '\'' +
                        ",block_history = '" + blockHistory + '\'' +
                        ",organization = '" + organization + '\'' +
                        ",updated_by = '" + updatedBy + '\'' +
                        ",primary_email = '" + primaryEmail + '\'' +
                        ",image_sm = '" + imageSm + '\'' +
                        ",username = '" + username + '\'' +
                        "}";
    }
}