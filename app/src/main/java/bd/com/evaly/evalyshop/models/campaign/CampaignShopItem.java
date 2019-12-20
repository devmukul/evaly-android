package bd.com.evaly.evalyshop.models.campaign;

import com.google.gson.annotations.SerializedName;

public class CampaignShopItem{

	@SerializedName("owner_name")
	private String ownerName;

	@SerializedName("approval")
	private int approval;

	@SerializedName("shop_name")
	private String shopName;

	@SerializedName("shop_image")
	private String shopImage;

	@SerializedName("slug")
	private String slug;

	@SerializedName("contact_number")
	private String contactNumber;

	public void setOwnerName(String ownerName){
		this.ownerName = ownerName;
	}

	public String getOwnerName(){
		return ownerName;
	}

	public void setApproval(int approval){
		this.approval = approval;
	}

	public int getApproval(){
		return approval;
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

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}

	public void setContactNumber(String contactNumber){
		this.contactNumber = contactNumber;
	}

	public String getContactNumber(){
		return contactNumber;
	}

	@Override
 	public String toString(){
		return 
			"CampaignShopItem{" + 
			"owner_name = '" + ownerName + '\'' + 
			",approval = '" + approval + '\'' + 
			",shop_name = '" + shopName + '\'' + 
			",shop_image = '" + shopImage + '\'' + 
			",slug = '" + slug + '\'' + 
			",contact_number = '" + contactNumber + '\'' + 
			"}";
		}
}