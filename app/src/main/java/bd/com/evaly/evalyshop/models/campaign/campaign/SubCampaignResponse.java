package bd.com.evaly.evalyshop.models.campaign.campaign;

import com.google.gson.annotations.SerializedName;

import bd.com.evaly.evalyshop.models.campaign.CampaignParentModel;

public class SubCampaignResponse extends CampaignParentModel {

	@SerializedName("cashback_text")
	private String cashbackText;

	@SerializedName("image")
	private String image;

	@SerializedName("badge_text")
	private String badgeText;

	@SerializedName("name")
	private String name;

	@SerializedName("slug")
	private String slug;

	public void setCashbackText(String cashbackText){
		this.cashbackText = cashbackText;
	}

	public String getCashbackText(){
		return cashbackText;
	}

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setBadgeText(String badgeText){
		this.badgeText = badgeText;
	}

	public String getBadgeText(){
		return badgeText;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}

	@Override
 	public String toString(){
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