package bd.com.evaly.evalyshop.models.catalog.shop;

import com.google.gson.annotations.SerializedName;

public class ShopDetailsResponse {

    @SerializedName("owner_name")
    private String ownerName;

    @SerializedName("campaign_ends_at")
    private String campaignEndsAt;

    @SerializedName("approval")
    private boolean approval;

    @SerializedName("owner_number")
    private String ownerNumber;

    @SerializedName("shop_name")
    private String shopName;

    @SerializedName("logo_image")
    private String shopImage;

    @SerializedName("contact_number")
    private String contactNumber;

    @SerializedName("campaign_name")
    private String campaignName;

    @SerializedName("campaign_category_slug")
    private String campaignCategorySlug;

    @SerializedName("campaign_slug")
    private String campaignSlug;

    @SerializedName("campaign_category_name")
    private String campaignCategoryName;

    @SerializedName("cashback_percentage")
    private int cashbackPercentage;

    @SerializedName("slug")
    private String slug;

    @SerializedName("campaign_start_at")
    private String campaignStartAt;

    @SerializedName("subscribed")
    private boolean subscribed;

    @SerializedName("subscriber_count")
    private int subscriberCount;

    @SerializedName("shop_address")
    private String shopAddress;

    public int getCashbackPercentage() {
        return cashbackPercentage;
    }

    public void setCashbackPercentage(int cashbackPercentage) {
        this.cashbackPercentage = cashbackPercentage;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public int getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(int subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCampaignEndsAt() {
        return campaignEndsAt;
    }

    public void setCampaignEndsAt(String campaignEndsAt) {
        this.campaignEndsAt = campaignEndsAt;
    }

    public boolean isApproval() {
        return approval;
    }

    public void setApproval(boolean approval) {
        this.approval = approval;
    }

    public String getOwnerNumber() {
        return ownerNumber;
    }

    public void setOwnerNumber(String ownerNumber) {
        this.ownerNumber = ownerNumber;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopImage() {
        return shopImage;
    }

    public void setShopImage(String shopImage) {
        this.shopImage = shopImage;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getCampaignCategorySlug() {
        return campaignCategorySlug;
    }

    public void setCampaignCategorySlug(String campaignCategorySlug) {
        this.campaignCategorySlug = campaignCategorySlug;
    }

    public String getCampaignSlug() {
        return campaignSlug;
    }

    public void setCampaignSlug(String campaignSlug) {
        this.campaignSlug = campaignSlug;
    }

    public String getCampaignCategoryName() {
        return campaignCategoryName;
    }

    public void setCampaignCategoryName(String campaignCategoryName) {
        this.campaignCategoryName = campaignCategoryName;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCampaignStartAt() {
        return campaignStartAt;
    }

    public void setCampaignStartAt(String campaignStartAt) {
        this.campaignStartAt = campaignStartAt;
    }

    @Override
    public String toString() {
        return
                "ShopDetailsResponse{" +
                        "owner_name = '" + ownerName + '\'' +
                        ",campaign_ends_at = '" + campaignEndsAt + '\'' +
                        ",approval = '" + approval + '\'' +
                        ",owner_number = '" + ownerNumber + '\'' +
                        ",shop_name = '" + shopName + '\'' +
                        ",shop_image = '" + shopImage + '\'' +
                        ",contact_number = '" + contactNumber + '\'' +
                        ",campaign_name = '" + campaignName + '\'' +
                        ",campaign_category_slug = '" + campaignCategorySlug + '\'' +
                        ",campaign_slug = '" + campaignSlug + '\'' +
                        ",campaign_category_name = '" + campaignCategoryName + '\'' +
                        ",slug = '" + slug + '\'' +
                        ",campaign_start_at = '" + campaignStartAt + '\'' +
                        "}";
    }
}