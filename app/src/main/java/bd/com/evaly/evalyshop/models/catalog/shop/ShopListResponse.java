package bd.com.evaly.evalyshop.models.catalog.shop;

import com.google.gson.annotations.SerializedName;

import bd.com.evaly.evalyshop.models.BaseModel;

public class ShopListResponse extends BaseModel {

    @SerializedName("owner_name")
    private String ownerName;

    @SerializedName("approval")
    private boolean approval;

    @SerializedName("owner_number")
    private String ownerNumber;

    @SerializedName("shop_name")
    private String shopName;

    @SerializedName("logo_image")
    private String shopImage;

    @SerializedName("slug")
    private String slug;

    @SerializedName("contact_number")
    private String contactNumber;

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setApproval(boolean approval) {
        this.approval = approval;
    }

    public boolean isApproval() {
        return approval;
    }

    public void setOwnerNumber(String ownerNumber) {
        this.ownerNumber = ownerNumber;
    }

    public String getOwnerNumber() {
        return ownerNumber;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopImage(String shopImage) {
        this.shopImage = shopImage;
    }

    public String getShopImage() {
        return shopImage;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    @Override
    public String toString() {
        return
                "ShopListResponse{" +
                        "owner_name = '" + ownerName + '\'' +
                        ",approval = '" + approval + '\'' +
                        ",owner_number = '" + ownerNumber + '\'' +
                        ",shop_name = '" + shopName + '\'' +
                        ",shop_image = '" + shopImage + '\'' +
                        ",slug = '" + slug + '\'' +
                        ",contact_number = '" + contactNumber + '\'' +
                        "}";
    }
}