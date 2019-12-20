package bd.com.evaly.evalyshop.models.brand;

import com.google.gson.annotations.SerializedName;

public class BrandDetails{

	@SerializedName("approved")
	private boolean approved;

	@SerializedName("image_url")
	private String imageUrl;

	@SerializedName("name")
	private String name;

	@SerializedName("description")
	private String description;

	@SerializedName("slug")
	private String slug;

	@SerializedName("brand_type")
	private String brandType;

	public void setApproved(boolean approved){
		this.approved = approved;
	}

	public boolean isApproved(){
		return approved;
	}

	public void setImageUrl(String imageUrl){
		this.imageUrl = imageUrl;
	}

	public String getImageUrl(){
		return imageUrl;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}

	public void setBrandType(String brandType){
		this.brandType = brandType;
	}

	public String getBrandType(){
		return brandType;
	}

	@Override
 	public String toString(){
		return 
			"BrandDetails{" + 
			"approved = '" + approved + '\'' + 
			",image_url = '" + imageUrl + '\'' + 
			",name = '" + name + '\'' + 
			",description = '" + description + '\'' + 
			",slug = '" + slug + '\'' + 
			",brand_type = '" + brandType + '\'' + 
			"}";
		}
}