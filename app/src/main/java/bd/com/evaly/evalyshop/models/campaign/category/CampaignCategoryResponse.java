package bd.com.evaly.evalyshop.models.campaign.category;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.campaign.campaign.SubCampaignResponse;

public class CampaignCategoryResponse implements Serializable {

    @SerializedName("image")
    private String image;

    @SerializedName("banner_sub_text")
    private String bannerSubText;

    @SerializedName("name")
    private String name;

    @SerializedName("banner_secondary_bg_color")
    private String bannerSecondaryBgColor;

    @SerializedName("banner_header_text")
    private String bannerHeaderText;

    @SerializedName("banner_header_text_color")
    private String bannerHeaderTextColor;

    @SerializedName("banner_image")
    private String bannerImage;

    @SerializedName("banner_sub_text_color")
    private String bannerSubTextColor;

    @SerializedName("slug")
    private String slug;

    @SerializedName("banner_primary_bg_color")
    private String bannerPrimaryBgColor;

    @SerializedName("campaigns")
    private List<SubCampaignResponse> campaigns;

    public void setCampaigns(List<SubCampaignResponse> campaigns) {
        this.campaigns = campaigns;
    }

    public List<SubCampaignResponse> getCampaigns() {
        if (campaigns == null)
            return new ArrayList<>();
        return campaigns;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setBannerSubText(String bannerSubText) {
        this.bannerSubText = bannerSubText;
    }

    public String getBannerSubText() {
        return bannerSubText;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setBannerSecondaryBgColor(String bannerSecondaryBgColor) {
        this.bannerSecondaryBgColor = bannerSecondaryBgColor;
    }

    public String getBannerSecondaryBgColor() {
        return bannerSecondaryBgColor;
    }

    public void setBannerHeaderText(String bannerHeaderText) {
        this.bannerHeaderText = bannerHeaderText;
    }

    public String getBannerHeaderText() {
        return bannerHeaderText;
    }

    public void setBannerHeaderTextColor(String bannerHeaderTextColor) {
        this.bannerHeaderTextColor = bannerHeaderTextColor;
    }

    public String getBannerHeaderTextColor() {
        return bannerHeaderTextColor;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerSubTextColor(String bannerSubTextColor) {
        this.bannerSubTextColor = bannerSubTextColor;
    }

    public String getBannerSubTextColor() {
        return bannerSubTextColor;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
    }

    public void setBannerPrimaryBgColor(String bannerPrimaryBgColor) {
        this.bannerPrimaryBgColor = bannerPrimaryBgColor;
    }

    public String getBannerPrimaryBgColor() {
        return bannerPrimaryBgColor;
    }

    @Override
    public String toString() {
        return
                "CampaignCategoryResponse{" +
                        "image = '" + image + '\'' +
                        ",banner_sub_text = '" + bannerSubText + '\'' +
                        ",name = '" + name + '\'' +
                        ",banner_secondary_bg_color = '" + bannerSecondaryBgColor + '\'' +
                        ",banner_header_text = '" + bannerHeaderText + '\'' +
                        ",banner_header_text_color = '" + bannerHeaderTextColor + '\'' +
                        ",banner_image = '" + bannerImage + '\'' +
                        ",banner_sub_text_color = '" + bannerSubTextColor + '\'' +
                        ",slug = '" + slug + '\'' +
                        ",banner_primary_bg_color = '" + bannerPrimaryBgColor + '\'' +
                        "}";
    }
}