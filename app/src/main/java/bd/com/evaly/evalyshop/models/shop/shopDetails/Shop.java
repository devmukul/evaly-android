package bd.com.evaly.evalyshop.models.shop.shopDetails;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Shop{

	@SerializedName("image")
	private String image;

	@SerializedName("owner_name")
	private String ownerName;

	@SerializedName("address")
	private String address;

	@SerializedName("bank_info")
	private Object bankInfo;

	@SerializedName("latitude")
	private String latitude;

	@SerializedName("nid")
	private Object nid;

	@SerializedName("description")
	private Object description;

	@SerializedName("logo_image")
	private String logoImage;

	@SerializedName("contact_number")
	private String contactNumber;

	@SerializedName("shop_delivery_options")
	private List<Object> shopDeliveryOptions;

	@SerializedName("approved")
	private boolean approved;

	@SerializedName("trade_license")
	private Object tradeLicense;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	@SerializedName("slug")
	private String slug;

	@SerializedName("longitude")
	private String longitude;

	@SerializedName("status")
	private String status;

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setOwnerName(String ownerName){
		this.ownerName = ownerName;
	}

	public String getOwnerName(){
		return ownerName;
	}

	public void setAddress(String address){
		this.address = address;
	}

	public String getAddress(){
		return address;
	}

	public void setBankInfo(Object bankInfo){
		this.bankInfo = bankInfo;
	}

	public Object getBankInfo(){
		return bankInfo;
	}

	public void setLatitude(String latitude){
		this.latitude = latitude;
	}

	public String getLatitude(){
		return latitude;
	}

	public void setNid(Object nid){
		this.nid = nid;
	}

	public Object getNid(){
		return nid;
	}

	public void setDescription(Object description){
		this.description = description;
	}

	public Object getDescription(){
		return description;
	}

	public void setLogoImage(String logoImage){
		this.logoImage = logoImage;
	}

	public String getLogoImage(){
		return logoImage;
	}

	public void setContactNumber(String contactNumber){
		this.contactNumber = contactNumber;
	}

	public String getContactNumber(){
		return contactNumber;
	}

	public void setShopDeliveryOptions(List<Object> shopDeliveryOptions){
		this.shopDeliveryOptions = shopDeliveryOptions;
	}

	public List<Object> getShopDeliveryOptions(){
		return shopDeliveryOptions;
	}

	public void setApproved(boolean approved){
		this.approved = approved;
	}

	public boolean isApproved(){
		return approved;
	}

	public void setTradeLicense(Object tradeLicense){
		this.tradeLicense = tradeLicense;
	}

	public Object getTradeLicense(){
		return tradeLicense;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}

	public void setLongitude(String longitude){
		this.longitude = longitude;
	}

	public String getLongitude(){
		return longitude;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"Shop{" + 
			"image = '" + image + '\'' + 
			",owner_name = '" + ownerName + '\'' + 
			",address = '" + address + '\'' + 
			",bank_info = '" + bankInfo + '\'' + 
			",latitude = '" + latitude + '\'' + 
			",nid = '" + nid + '\'' + 
			",description = '" + description + '\'' + 
			",logo_image = '" + logoImage + '\'' + 
			",contact_number = '" + contactNumber + '\'' + 
			",shop_delivery_options = '" + shopDeliveryOptions + '\'' + 
			",approved = '" + approved + '\'' + 
			",trade_license = '" + tradeLicense + '\'' + 
			",name = '" + name + '\'' + 
			",id = '" + id + '\'' + 
			",slug = '" + slug + '\'' + 
			",longitude = '" + longitude + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}