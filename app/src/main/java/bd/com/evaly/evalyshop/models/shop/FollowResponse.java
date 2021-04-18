package bd.com.evaly.evalyshop.models.shop;

import com.google.gson.annotations.SerializedName;

public class FollowResponse{

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("shop_name")
	private String shopName;

	@SerializedName("first_name")
	private String firstName;

	@SerializedName("profile_pic_url")
	private String profilePicUrl;

	@SerializedName("username")
	private String username;

	@SerializedName("shop_slug")
	private String shopSlug;


	@SerializedName("shop_image_url")
	private String shopImageUrl;

	@SerializedName("status")
	private boolean status;

	public String getShopImageUrl() {
		return shopImageUrl;
	}

	public void setShopImageUrl(String shopImageUrl) {
		this.shopImageUrl = shopImageUrl;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public void setLastName(String lastName){
		this.lastName = lastName;
	}

	public String getLastName(){
		return lastName;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setShopName(String shopName){
		this.shopName = shopName;
	}

	public String getShopName(){
		return shopName;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public String getFirstName(){
		return firstName;
	}

	public void setProfilePicUrl(String profilePicUrl){
		this.profilePicUrl = profilePicUrl;
	}

	public String getProfilePicUrl(){
		return profilePicUrl;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public String getUsername(){
		return username;
	}

	public void setShopSlug(String shopSlug){
		this.shopSlug = shopSlug;
	}

	public String getShopSlug(){
		return shopSlug;
	}

	public void setStatus(boolean status){
		this.status = status;
	}

	public boolean isStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"FollowResponse{" + 
			"updated_at = '" + updatedAt + '\'' + 
			",last_name = '" + lastName + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",shop_name = '" + shopName + '\'' + 
			",first_name = '" + firstName + '\'' + 
			",profile_pic_url = '" + profilePicUrl + '\'' + 
			",username = '" + username + '\'' + 
			",shop_slug = '" + shopSlug + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}