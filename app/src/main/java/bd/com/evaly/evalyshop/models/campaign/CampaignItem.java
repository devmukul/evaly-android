package bd.com.evaly.evalyshop.models.campaign;

import com.google.gson.annotations.SerializedName;


public class CampaignItem{

	@SerializedName("end_date")
	private String endDate;

	@SerializedName("name")
	private String name;

	@SerializedName("modified_by")
	private String modifiedBy;

	@SerializedName("modified_at")
	private String modifiedAt;

	@SerializedName("slug")
	private String slug;

	@SerializedName("start_date")
	private String startDate;

	@SerializedName("image")
	private String image;

	public void setEndDate(String endDate){
		this.endDate = endDate;
	}

	public String getEndDate(){
		return endDate;
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

	public void setStartDate(String startDate){
		this.startDate = startDate;
	}

	public String getStartDate(){
		return startDate;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
 	public String toString(){
		return 
			"CampaignItem{" + 
			"end_date = '" + endDate + '\'' + 
			",name = '" + name + '\'' + 
			",modified_by = '" + modifiedBy + '\'' + 
			",modified_at = '" + modifiedAt + '\'' + 
			",slug = '" + slug + '\'' + 
			",start_date = '" + startDate + '\'' + 
			"}";
		}
}