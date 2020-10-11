package bd.com.evaly.evalyshop.models.campaign.carousel;

import com.google.gson.annotations.SerializedName;

public class CampaignsItem{

	@SerializedName("image")
	private String image;

	@SerializedName("name")
	private String name;

	@SerializedName("badge_text_1")
	private Object badgeText1;

	@SerializedName("slug")
	private String slug;

	@SerializedName("badge_text_2")
	private String badgeText2;

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setBadgeText1(Object badgeText1){
		this.badgeText1 = badgeText1;
	}

	public Object getBadgeText1(){
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
		return badgeText2;
	}

	@Override
 	public String toString(){
		return 
			"CampaignsItem{" + 
			"image = '" + image + '\'' + 
			",name = '" + name + '\'' + 
			",badge_text_1 = '" + badgeText1 + '\'' + 
			",slug = '" + slug + '\'' + 
			",badge_text_2 = '" + badgeText2 + '\'' + 
			"}";
		}
}