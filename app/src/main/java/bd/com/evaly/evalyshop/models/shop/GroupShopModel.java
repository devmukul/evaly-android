package bd.com.evaly.evalyshop.models.shop;

import com.google.gson.annotations.SerializedName;

public class GroupShopModel{

	@SerializedName("image")
	private String image;

	@SerializedName("approved")
	private int approved;

	@SerializedName(value = "shop_name", alternate = "name")
	private String shopName;

	@SerializedName("logo_image")
	private String logoImage;

	@SerializedName("shop_address")
	private String shopAddress;

	@SerializedName("contact_number")
	private String contactNumber;

	@SerializedName(value = "shop_slug", alternate = "slug")
	private String shopSlug;

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setApproved(int approved){
		this.approved = approved;
	}

	public int getApproved(){
		return approved;
	}

	public void setShopName(String shopName){
		this.shopName = shopName;
	}

	public String getShopName(){
		return shopName;
	}

	public void setLogoImage(String logoImage){
		this.logoImage = logoImage;
	}

	public String getLogoImage(){
		return logoImage;
	}

	public void setShopAddress(String shopAddress){
		this.shopAddress = shopAddress;
	}

	public String getShopAddress(){
		return shopAddress;
	}

	public void setContactNumber(String contactNumber){
		this.contactNumber = contactNumber;
	}

	public String getContactNumber(){
		return contactNumber;
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
			"GroupShopModel{" + 
			"image = '" + image + '\'' + 
			",approved = '" + approved + '\'' + 
			",shop_name = '" + shopName + '\'' + 
			",logo_image = '" + logoImage + '\'' + 
			",shop_address = '" + shopAddress + '\'' + 
			",contact_number = '" + contactNumber + '\'' + 
			",shop_slug = '" + shopSlug + '\'' + 
			"}";
		}
}