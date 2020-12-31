package bd.com.evaly.evalyshop.models.catalog.brands;

import java.util.List;
import com.google.gson.annotations.SerializedName;

import bd.com.evaly.evalyshop.models.BaseModel;

public class BrandResponse extends BaseModel {

	@SerializedName("approved")
	private boolean approved;

	@SerializedName("brand_score")
	private int brandScore;

	@SerializedName("image_url")
	private String imageUrl;

	@SerializedName("name")
	private String name;

	@SerializedName("description")
	private String description;

	@SerializedName("id")
	private int id;

	@SerializedName("categories")
	private List<String> categories;

	@SerializedName("slug")
	private String slug;

	@SerializedName("brand_type")
	private String brandType;

	@SerializedName("status")
	private String status;

	public void setApproved(boolean approved){
		this.approved = approved;
	}

	public boolean isApproved(){
		return approved;
	}

	public void setBrandScore(int brandScore){
		this.brandScore = brandScore;
	}

	public int getBrandScore(){
		return brandScore;
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

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setCategories(List<String> categories){
		this.categories = categories;
	}

	public List<String> getCategories(){
		return categories;
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

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"BrandResponse{" + 
			"approved = '" + approved + '\'' + 
			",brand_score = '" + brandScore + '\'' + 
			",image_url = '" + imageUrl + '\'' + 
			",name = '" + name + '\'' + 
			",description = '" + description + '\'' + 
			",id = '" + id + '\'' + 
			",categories = '" + categories + '\'' + 
			",slug = '" + slug + '\'' + 
			",brand_type = '" + brandType + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}