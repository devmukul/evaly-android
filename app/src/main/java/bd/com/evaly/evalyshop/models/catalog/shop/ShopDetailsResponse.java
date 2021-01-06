package bd.com.evaly.evalyshop.models.catalog.shop;

import com.google.gson.annotations.SerializedName;

public class ShopDetailsResponse{

	@SerializedName("owner_name")
	private String ownerName;

	@SerializedName("campaign_ends_at")
	private String campaignEndsAt;

	@SerializedName("approval")
	private boolean approval;

	@SerializedName("owner_number")
	private String ownerNumber;

	@SerializedName("shop_name")
	private String shopName;

	@SerializedName("logo_image")
	private String shopImage;

	@SerializedName("contact_number")
	private String contactNumber;

	@SerializedName("campaign_name")
	private String campaignName;

	@SerializedName("campaign_category_slug")
	private String campaignCategorySlug;

	@SerializedName("campaign_slug")
	private String campaignSlug;

	@SerializedName("campaign_category_name")
	private String campaignCategoryName;

	@SerializedName("slug")
	private String slug;

	@SerializedName("campaign_start_at")
	private String campaignStartAt;

	@SerializedName("subscribed")
	private boolean subscribed;

	@SerializedName("subscriber_count")
	private int subscriberCount;

	@SerializedName("shop_address")
	private String shopAddress;

	public String getShopAddress() {
		return shopAddress;
	}

	public void setShopAddress(String shopAddress) {
		this.shopAddress = shopAddress;
	}

	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}

	public int getSubscriberCount() {
		return subscriberCount;
	}

	public void setSubscriberCount(int subscriberCount) {
		this.subscriberCount = subscriberCount;
	}

	public void setOwnerName(String ownerName){
		this.ownerName = ownerName;
	}

	public String getOwnerName(){
		return ownerName;
	}

	public void setCampaignEndsAt(String campaignEndsAt){
		this.campaignEndsAt = campaignEndsAt;
	}

	public String getCampaignEndsAt(){
		return campaignEndsAt;
	}

	public void setApproval(boolean approval){
		this.approval = approval;
	}

	public boolean isApproval(){
		return approval;
	}

	public void setOwnerNumber(String ownerNumber){
		this.ownerNumber = ownerNumber;
	}

	public String getOwnerNumber(){
		return ownerNumber;
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

	public void setContactNumber(String contactNumber){
		this.contactNumber = contactNumber;
	}

	public String getContactNumber(){
		return contactNumber;
	}

	public void setCampaignName(String campaignName){
		this.campaignName = campaignName;
	}

	public String getCampaignName(){
		return campaignName;
	}

	public void setCampaignCategorySlug(String campaignCategorySlug){
		this.campaignCategorySlug = campaignCategorySlug;
	}

	public String getCampaignCategorySlug(){
		return campaignCategorySlug;
	}

	public void setCampaignSlug(String campaignSlug){
		this.campaignSlug = campaignSlug;
	}

	public String getCampaignSlug(){
		return campaignSlug;
	}

	public void setCampaignCategoryName(String campaignCategoryName){
		this.campaignCategoryName = campaignCategoryName;
	}

	public String getCampaignCategoryName(){
		return campaignCategoryName;
	}

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}

	public void setCampaignStartAt(String campaignStartAt){
		this.campaignStartAt = campaignStartAt;
	}

	public String getCampaignStartAt(){
		return campaignStartAt;
	}

	@Override
 	public String toString(){
		return 
			"ShopDetailsResponse{" + 
			"owner_name = '" + ownerName + '\'' + 
			",campaign_ends_at = '" + campaignEndsAt + '\'' + 
			",approval = '" + approval + '\'' + 
			",owner_number = '" + ownerNumber + '\'' + 
			",shop_name = '" + shopName + '\'' + 
			",shop_image = '" + shopImage + '\'' + 
			",contact_number = '" + contactNumber + '\'' + 
			",campaign_name = '" + campaignName + '\'' + 
			",campaign_category_slug = '" + campaignCategorySlug + '\'' + 
			",campaign_slug = '" + campaignSlug + '\'' + 
			",campaign_category_name = '" + campaignCategoryName + '\'' + 
			",slug = '" + slug + '\'' + 
			",campaign_start_at = '" + campaignStartAt + '\'' + 
			"}";
		}
}