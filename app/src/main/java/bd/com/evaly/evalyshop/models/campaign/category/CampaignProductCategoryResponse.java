package bd.com.evaly.evalyshop.models.campaign.category;

import com.google.gson.annotations.SerializedName;

public class CampaignProductCategoryResponse{

	@SerializedName("category_image")
	private String categoryImage;

	@SerializedName("category_name")
	private String categoryName;

	@SerializedName("shop_name")
	private String shopName;

	@SerializedName("shop_image")
	private String shopImage;

	@SerializedName("category_slug")
	private String categorySlug;

	@SerializedName("shop_slug")
	private String shopSlug;

	public void setCategoryImage(String categoryImage){
		this.categoryImage = categoryImage;
	}

	public String getCategoryImage(){
		return categoryImage;
	}

	public void setCategoryName(String categoryName){
		this.categoryName = categoryName;
	}

	public String getCategoryName(){
		return categoryName;
	}

	public void setShopName(String shopName){
		this.shopName = shopName;
	}

	public String getShopName(){
		return shopName;
	}

	public void setShopImage(String shopImage){
		this.shopImage = shopImage;
	}

	public String getShopImage(){
		return shopImage;
	}

	public void setCategorySlug(String categorySlug){
		this.categorySlug = categorySlug;
	}

	public String getCategorySlug(){
		return categorySlug;
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
			"CampaignProductCategoryResponse{" + 
			"category_image = '" + categoryImage + '\'' + 
			",category_name = '" + categoryName + '\'' + 
			",shop_name = '" + shopName + '\'' + 
			",shop_image = '" + shopImage + '\'' + 
			",category_slug = '" + categorySlug + '\'' + 
			",shop_slug = '" + shopSlug + '\'' + 
			"}";
		}
}