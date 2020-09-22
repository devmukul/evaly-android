package bd.com.evaly.evalyshop.models.campaign.products;

import com.google.gson.annotations.SerializedName;

public class CampaignProductResponse{

	@SerializedName("cashback_text")
	private String cashbackText;

	@SerializedName("image")
	private String image;

	@SerializedName("badge_text")
	private String badgeText;

	@SerializedName("bottom_text")
	private String bottomText;

	@SerializedName("price")
	private double price;

	@SerializedName("name")
	private String name;

	@SerializedName("slug")
	private String slug;

	@SerializedName("discounted_price")
	private double discountedPrice;

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

	public void setBottomText(String bottomText){
		this.bottomText = bottomText;
	}

	public String getBottomText(){
		return bottomText;
	}

	public void setPrice(double price){
		this.price = price;
	}

	public double getPrice(){
		return price;
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

	public void setDiscountedPrice(double discountedPrice){
		this.discountedPrice = discountedPrice;
	}

	public double getDiscountedPrice(){
		return discountedPrice;
	}

	@Override
 	public String toString(){
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