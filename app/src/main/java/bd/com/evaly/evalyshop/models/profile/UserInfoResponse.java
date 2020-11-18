package bd.com.evaly.evalyshop.models.profile;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserInfoResponse implements Serializable {

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("occupation")
    private String occupation;

    @SerializedName("other_email")
    private String otherEmail;

    @SerializedName("father_name")
    private String fatherName;

    @SerializedName("father_phone_number")
    private String fatherPhoneNumber;

    @SerializedName("organization")
    private String organization;

    @SerializedName("mother_name")
    private String motherName;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("primary_email")
    private String primaryEmail;

    @SerializedName("mother_phone_number")
    private String motherPhoneNumber;

    @SerializedName("gender")
    private String gender;

    @SerializedName("birth_date")
    private String birthDate;

    @SerializedName("profile_pic_url")
    private String profilePicUrl;

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getGender() {
        if (gender == null)
            return "";
        else
            return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFirstName() {
        if (fullName == null)
            return "";
        if (!fullName.contains(" "))
            return fullName;
        String[] names = fullName.split(" ");
        if (names.length > 0)
            return names[0];
        else
            return "";
    }

    public String getLastName() {
        if (fullName == null || !fullName.contains(" "))
            return "";
        String[] names = fullName.split(" ");
        if (names.length > 1)
            return names[1];
        else
            return "";
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getOtherEmail() {
        return otherEmail;
    }

    public void setOtherEmail(String otherEmail) {
        this.otherEmail = otherEmail;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getFatherPhoneNumber() {
        return fatherPhoneNumber;
    }

    public void setFatherPhoneNumber(String fatherPhoneNumber) {
        this.fatherPhoneNumber = fatherPhoneNumber;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public String getMotherPhoneNumber() {
        return motherPhoneNumber;
    }

    public void setMotherPhoneNumber(String motherPhoneNumber) {
        this.motherPhoneNumber = motherPhoneNumber;
    }

    @Override
    public String toString() {
        return
                "UserInfoResponse{" +
                        "full_name = '" + fullName + '\'' +
                        ",occupation = '" + occupation + '\'' +
                        ",other_email = '" + otherEmail + '\'' +
                        ",father_name = '" + fatherName + '\'' +
                        ",father_phone_number = '" + fatherPhoneNumber + '\'' +
                        ",organization = '" + organization + '\'' +
                        ",mother_name = '" + motherName + '\'' +
                        ",phone_number = '" + phoneNumber + '\'' +
                        ",primary_email = '" + primaryEmail + '\'' +
                        ",mother_phone_number = '" + motherPhoneNumber + '\'' +
                        "}";
    }
}