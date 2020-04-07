package bd.com.evaly.evalyshop.models.express;

import com.google.gson.annotations.SerializedName;

public class ExpressShopModel{

	@SerializedName("image")
	private String image;

	@SerializedName("approved")
	private boolean approved;

	@SerializedName("name")
	private String name;

	@SerializedName("logo_image")
	private String logoImage;

	@SerializedName("slug")
	private String slug;

	@SerializedName("contact_number")
	private String contactNumber;

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setApproved(boolean approved){
		this.approved = approved;
	}

	public boolean isApproved(){
		return approved;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setLogoImage(String logoImage){
		this.logoImage = logoImage;
	}

	public String getLogoImage(){
		return logoImage;
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
			"ExpressShopModel{" + 
			"image = '" + image + '\'' + 
			",approved = '" + approved + '\'' + 
			",name = '" + name + '\'' + 
			",logo_image = '" + logoImage + '\'' + 
			",slug = '" + slug + '\'' + 
			",contact_number = '" + contactNumber + '\'' + 
			"}";
		}
}