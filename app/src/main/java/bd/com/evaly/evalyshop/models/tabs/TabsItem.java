package bd.com.evaly.evalyshop.models.tabs;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import bd.com.evaly.evalyshop.models.BaseModel;

public class TabsItem extends BaseModel implements Serializable {

    private int type;

    @SerializedName(value = "name", alternate = "shop_name")
    private String title;

    @SerializedName(value = "image_url", alternate = "shop_image")
    private String image;

    @SerializedName("slug")
    private String slug;

    private String category;

    private String campaignSlug = "";

    private boolean isSelected;

    public TabsItem() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String previousCategory) {
        this.category = previousCategory;
    }

    public String getCampaignSlug() {
        return campaignSlug;
    }

    public void setCampaignSlug(String campaignSlug) {
        this.campaignSlug = campaignSlug;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
