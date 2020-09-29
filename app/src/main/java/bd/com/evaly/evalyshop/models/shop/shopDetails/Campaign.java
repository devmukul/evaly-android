package bd.com.evaly.evalyshop.models.shop.shopDetails;

import com.google.gson.annotations.SerializedName;

public class Campaign{

	@SerializedName("end_date")
	private String endDate;

	@SerializedName("image")
	private String image;

	@SerializedName("category_name")
	private String categoryName;

	@SerializedName("cashback_percentage")
	private String cashbackPercentage;

	@SerializedName("cashback_date")
	private String cashbackDate;

	@SerializedName("description")
	private String description;

	@SerializedName("name")
	private String name;

	@SerializedName("modified_by")
	private String modifiedBy;

	@SerializedName("banner_image")
	private String bannerImage;

	@SerializedName("modified_at")
	private String modifiedAt;

	@SerializedName("slug")
	private String slug;

	@SerializedName("category_slug")
	private String categorySlug;

	@SerializedName("start_date")
	private String startDate;

	@SerializedName("cashback_backoff_period")
	private String cashbackBackoffPeriod;


	public void setEndDate(String endDate){
		this.endDate = endDate;
	}

	public String getEndDate(){
		return endDate;
	}

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setCategoryName(String categoryName){
		this.categoryName = categoryName;
	}

	public String getCategoryName(){
		return categoryName;
	}

	public void setCashbackPercentage(String cashbackPercentage){
		this.cashbackPercentage = cashbackPercentage;
	}

	public String getCashbackPercentage(){
		return cashbackPercentage;
	}

	public void setCashbackDate(String cashbackDate){
		this.cashbackDate = cashbackDate;
	}

	public String getCashbackDate(){
		return cashbackDate;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setModifiedBy(String modifiedBy){
		this.modifiedBy = modifiedBy;
	}

	public String getModifiedBy(){
		return modifiedBy;
	}

	public void setBannerImage(String bannerImage){
		this.bannerImage = bannerImage;
	}

	public String getBannerImage(){
		return bannerImage;
	}

	public void setModifiedAt(String modifiedAt){
		this.modifiedAt = modifiedAt;
	}

	public String getModifiedAt(){
		return modifiedAt;
	}

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}

	public void setCategorySlug(String categorySlug){
		this.categorySlug = categorySlug;
	}

	public String getCategorySlug(){
		return categorySlug;
	}

	public void setStartDate(String startDate){
		this.startDate = startDate;
	}

	public String getStartDate(){
		return startDate;
	}

	public void setCashbackBackoffPeriod(String cashbackBackoffPeriod){
		this.cashbackBackoffPeriod = cashbackBackoffPeriod;
	}

	public String getCashbackBackoffPeriod(){
		return cashbackBackoffPeriod;
	}

	@Override
 	public String toString(){
		return 
			"Campaign{" + 
			"end_date = '" + endDate + '\'' + 
			",image = '" + image + '\'' + 
			",category_name = '" + categoryName + '\'' + 
			",cashback_percentage = '" + cashbackPercentage + '\'' + 
			",cashback_date = '" + cashbackDate + '\'' + 
			",description = '" + description + '\'' + 
			",name = '" + name + '\'' + 
			",modified_by = '" + modifiedBy + '\'' + 
			",banner_image = '" + bannerImage + '\'' + 
			",modified_at = '" + modifiedAt + '\'' + 
			",slug = '" + slug + '\'' + 
			",category_slug = '" + categorySlug + '\'' + 
			",start_date = '" + startDate + '\'' + 
			",cashback_backoff_period = '" + cashbackBackoffPeriod + '\'' + 
			"}";
		}
}