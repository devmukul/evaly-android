package bd.com.evaly.evalyshop.models.campaign.brand;

import com.google.gson.annotations.SerializedName;

import bd.com.evaly.evalyshop.models.campaign.CampaignParentModel;

public class CampaignBrandResponse extends CampaignParentModel {

	@SerializedName("image")
	private String image;

	@SerializedName("campaign_slug")
	private String campaignSlug;

	@SerializedName("name")
	private String name;

	@SerializedName("badge_text_1")
	private String badgeText1;

	@SerializedName("slug")
	private String slug;

	@SerializedName("badge_text_2")
	private String badgeText2;

	@SerializedName("shop_slug")
	private String shopSlug;

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setCampaignSlug(String campaignSlug){
		this.campaignSlug = campaignSlug;
	}

	public String getCampaignSlug(){
		return campaignSlug;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setBadgeText1(String badgeText1){
		this.badgeText1 = badgeText1;
	}

	public String getBadgeText1(){
		return badgeText1;
	}

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}

	public void setBadgeText2(String badgeText2){
		this.badgeText2 = badgeText2;
	}

	public String getBadgeText2(){
		if (badgeText2 == null)
			return "";
		return badgeText2;
	}

	public void setShopSlug(String shopSlug){
		this.shopSlug = shopSlug;
	}

	public String getShopSlug(){
		return shopSlug;
	}

	@Override
 	public String toString(){
		return 
			"CampaignBrandResponse{" + 
			"image = '" + image + '\'' + 
			",campaign_slug = '" + campaignSlug + '\'' + 
			",name = '" + name + '\'' + 
			",badge_text_1 = '" + badgeText1 + '\'' + 
			",slug = '" + slug + '\'' + 
			",badge_text_2 = '" + badgeText2 + '\'' + 
			",shop_slug = '" + shopSlug + '\'' + 
			"}";
		}
}