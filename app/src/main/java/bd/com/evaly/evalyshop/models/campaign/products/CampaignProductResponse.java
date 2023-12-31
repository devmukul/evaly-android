package bd.com.evaly.evalyshop.models.campaign.products;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import bd.com.evaly.evalyshop.models.campaign.CampaignParentModel;

public class CampaignProductResponse extends CampaignParentModel implements Serializable {

    @SerializedName(value = "cashback_text", alternate = "badge_text_2")
    private String cashbackText;

    @SerializedName("image")
    private String image;

    @SerializedName(value = "badge_text", alternate = "badge_text_1")
    private String badgeText;

    @SerializedName(value = "bottom_text", alternate = "shop_name")
    private String bottomText;

    @SerializedName(value = "price", alternate = "max_price")
    private double price;

    @SerializedName("name")
    private String name;

    @SerializedName("slug")
    private String slug;

    @SerializedName(value = "discounted_price", alternate = "min_discounted_price")
    private double discountedPrice;

    @SerializedName("shop_slug")
    private String shopSlug;

    public String getShopSlug() {
        return shopSlug;
    }

    public void setShopSlug(String shopSlug) {
        this.shopSlug = shopSlug;
    }

    public String getCashbackText() {
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
        return badgeText;
    }

    public void setBadgeText(String badgeText) {
        this.badgeText = badgeText;
    }

    public String getBottomText() {
        return bottomText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    @Override
    public String toString() {
        return
                "CampaignProductResponse{" +
                        "cashback_text = '" + cashbackText + '\'' +
                        ",image = '" + image + '\'' +
                        ",badge_text = '" + badgeText + '\'' +
                        ",bottom_text = '" + bottomText + '\'' +
                        ",price = '" + price + '\'' +
                        ",name = '" + name + '\'' +
                        ",slug = '" + slug + '\'' +
                        ",discounted_price = '" + discountedPrice + '\'' +
                        "}";
    }
}