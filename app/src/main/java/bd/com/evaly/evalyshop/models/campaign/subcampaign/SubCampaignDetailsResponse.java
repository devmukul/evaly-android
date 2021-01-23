package bd.com.evaly.evalyshop.models.campaign.subcampaign;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;

public class SubCampaignDetailsResponse {

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("image")
    private String image;

    @SerializedName("cashback_percentage")
    private String cashbackPercentage;

    @SerializedName("cashback_date")
    private String cashbackDate;

    @SerializedName("description")
    private String description;

    @SerializedName("cashback_destination")
    private String cashbackDestination;

    @SerializedName("allowed_payment_methods")
    private List<String> allowedPaymentMethods;

    @SerializedName("cashback_on_payment_by")
    private String cashbackOnPaymentBy;

    @SerializedName("promotion_end_date")
    private String promotionEndDate;

    @SerializedName("name")
    private String name;

    @SerializedName("banner_image")
    private String bannerImage;

    @SerializedName("promotion_start_date")
    private String promotionStartDate;

    @SerializedName("category")
    private CampaignCategoryResponse category;

    @SerializedName("cashback_type")
    private String cashbackType;

    @SerializedName("slug")
    private String slug;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("cashback_backoff_period")
    private int cashbackBackoffPeriod;

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

    public String getCashbackDate() {
        return cashbackDate;
    }

    public void setCashbackDate(String cashbackDate) {
        this.cashbackDate = cashbackDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCashbackDestination() {
        return cashbackDestination;
    }

    public void setCashbackDestination(String cashbackDestination) {
        this.cashbackDestination = cashbackDestination;
    }

    public List<String> getAllowedPaymentMethods() {
        return allowedPaymentMethods;
    }

    public void setAllowedPaymentMethods(List<String> allowedPaymentMethods) {
        this.allowedPaymentMethods = allowedPaymentMethods;
    }

    public String getCashbackOnPaymentBy() {
        return cashbackOnPaymentBy;
    }

    public void setCashbackOnPaymentBy(String cashbackOnPaymentBy) {
        this.cashbackOnPaymentBy = cashbackOnPaymentBy;
    }

    public String getPromotionEndDate() {
        return promotionEndDate;
    }

    public void setPromotionEndDate(String promotionEndDate) {
        this.promotionEndDate = promotionEndDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getPromotionStartDate() {
        return promotionStartDate;
    }

    public void setPromotionStartDate(String promotionStartDate) {
        this.promotionStartDate = promotionStartDate;
    }

    public CampaignCategoryResponse getCategory() {
        return category;
    }

    public void setCategory(CampaignCategoryResponse category) {
        this.category = category;
    }

    public String getCashbackType() {
        return cashbackType;
    }

    public void setCashbackType(String cashbackType) {
        this.cashbackType = cashbackType;
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

    public int getCashbackBackoffPeriod() {
        return cashbackBackoffPeriod;
    }

    public void setCashbackBackoffPeriod(int cashbackBackoffPeriod) {
        this.cashbackBackoffPeriod = cashbackBackoffPeriod;
    }

    @Override
    public String toString() {
        return
                "SubCampaignResponse{" +
                        "end_date = '" + endDate + '\'' +
                        ",image = '" + image + '\'' +
                        ",cashback_percentage = '" + cashbackPercentage + '\'' +
                        ",cashback_date = '" + cashbackDate + '\'' +
                        ",description = '" + description + '\'' +
                        ",cashback_destination = '" + cashbackDestination + '\'' +
                        ",allowed_payment_methods = '" + allowedPaymentMethods + '\'' +
                        ",cashback_on_payment_by = '" + cashbackOnPaymentBy + '\'' +
                        ",promotion_end_date = '" + promotionEndDate + '\'' +
                        ",name = '" + name + '\'' +
                        ",banner_image = '" + bannerImage + '\'' +
                        ",promotion_start_date = '" + promotionStartDate + '\'' +
                        ",category = '" + category + '\'' +
                        ",cashback_type = '" + cashbackType + '\'' +
                        ",slug = '" + slug + '\'' +
                        ",start_date = '" + startDate + '\'' +
                        ",cashback_backoff_period = '" + cashbackBackoffPeriod + '\'' +
                        "}";
    }
}