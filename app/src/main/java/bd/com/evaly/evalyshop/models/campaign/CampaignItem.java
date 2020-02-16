package bd.com.evaly.evalyshop.models.campaign;

import com.google.gson.annotations.SerializedName;

public class CampaignItem {

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("image")
    private String image;

    @SerializedName("cashback_percentage")
    private String cashbackPercentage;

    @SerializedName("name")
    private String name;

    @SerializedName("cashback_date")
    private String cashbackDate;

    @SerializedName("modified_by")
    private String modifiedBy;

    @SerializedName("banner_image")
    private String bannerImage;

    @SerializedName("modified_at")
    private String modifiedAt;

    @SerializedName("slug")
    private String slug;

    @SerializedName("start_date")
    private String startDate;

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCashbackPercentage() {
        return cashbackPercentage;
    }

    public void setCashbackPercentage(String cashbackPercentage) {
        this.cashbackPercentage = cashbackPercentage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCashbackDate() {
        return cashbackDate;
    }

    public void setCashbackDate(String cashbackDate) {
        this.cashbackDate = cashbackDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return
                "CampaignItem{" +
                        "end_date = '" + endDate + '\'' +
                        ",image = '" + image + '\'' +
                        ",cashback_percentage = '" + cashbackPercentage + '\'' +
                        ",name = '" + name + '\'' +
                        ",cashback_date = '" + cashbackDate + '\'' +
                        ",modified_by = '" + modifiedBy + '\'' +
                        ",banner_image = '" + bannerImage + '\'' +
                        ",modified_at = '" + modifiedAt + '\'' +
                        ",slug = '" + slug + '\'' +
                        ",start_date = '" + startDate + '\'' +
                        "}";
    }
}