package bd.com.evaly.evalyshop.models.campaign.campaign;

import com.google.gson.annotations.SerializedName;

import bd.com.evaly.evalyshop.models.campaign.CampaignParentModel;

public class SubCampaignResponse extends CampaignParentModel {

    @SerializedName(value = "cashback_text", alternate = "badge_text_1")
    private String cashbackText;

    @SerializedName("image")
    private String image;

    @SerializedName(value = "badge_text", alternate = "badge_text_2")
    private String badgeText;

    @SerializedName("name")
    private String name;

    @SerializedName("slug")
    private String slug;

    public String getCashbackText() {
        if (cashbackText == null)
            return "";
        return cashbackText;
    }

    public void setCashbackText(String cashbackText) {
        this.cashbackText = cashbackText;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBadgeText() {
        if (badgeText == null)
            return "";
        return badgeText;
    }

    public void setBadgeText(String badgeText) {
        this.badgeText = badgeText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public String toString() {
        return
                "SubCampaignResponse{" +
                        "cashback_text = '" + cashbackText + '\'' +
                        ",image = '" + image + '\'' +
                        ",badge_text = '" + badgeText + '\'' +
                        ",name = '" + name + '\'' +
                        ",slug = '" + slug + '\'' +
                        "}";
    }
}