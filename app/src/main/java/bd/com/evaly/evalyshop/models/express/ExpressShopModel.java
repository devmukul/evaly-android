package bd.com.evaly.evalyshop.models.express;

import com.google.gson.annotations.SerializedName;

public class ExpressShopModel {

    @SerializedName("image")
    private String image;

    @SerializedName("approved")
    private boolean approved;

    @SerializedName("name")
    private String name;

    @SerializedName("logo_image")
    private String logoImage;

    @SerializedName("slug")
    private String slug;

    @SerializedName("contact_number")
    private String contactNumber;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(String logoImage) {
        this.logoImage = logoImage;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public String toString() {
        return
                "ExpressShopModel{" +
                        "image = '" + image + '\'' +
                        ",approved = '" + approved + '\'' +
                        ",name = '" + name + '\'' +
                        ",logo_image = '" + logoImage + '\'' +
                        ",slug = '" + slug + '\'' +
                        ",contact_number = '" + contactNumber + '\'' +
                        "}";
    }
}