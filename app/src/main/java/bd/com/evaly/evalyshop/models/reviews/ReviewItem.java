package bd.com.evaly.evalyshop.models.reviews;

import com.google.gson.annotations.SerializedName;

public class ReviewItem {


    @SerializedName("id")
    private int id;

    private String sku;

    @SerializedName(value = "user_name", alternate = "full_name")
    private String user_name;

    @SerializedName(value = "rating_value", alternate = "rating")
    private int rating_value;

    @SerializedName(value = "rating_text", alternate = "review")
    private String rating_text;

    @SerializedName("time")
    private String time;

    @SerializedName("is_approved")
    private int is_approved;

    @SerializedName(value = "profile_image", alternate = "profile_pic_url")
    private String profileImage;

    @SerializedName("phone")
    private String phone;

    @SerializedName("created_at")
    private String createdAt;

    public ReviewItem(){


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setProfileImage(String profileImage){
        this.profileImage = profileImage;
    }

    public String getProfileImage(){
        return profileImage;
    }

    public void setCreatedAt(String createdAt){
        this.createdAt = createdAt;
    }

    public String getCreatedAt(){
        return createdAt;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getPhone(){
        return phone;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getRating_value() {
        return rating_value;
    }

    public void setRating_value(int rating_value) {
        this.rating_value = rating_value;
    }

    public String getRating_text() {
        return rating_text;
    }

    public void setRating_text(String rating_text) {
        this.rating_text = rating_text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIs_approved() {
        return is_approved;
    }

    public void setIs_approved(int is_approved) {
        this.is_approved = is_approved;
    }
}
