package bd.com.evaly.evalyshop.models.appointment;

import com.google.gson.annotations.SerializedName;

public class AppointmentCategoryResponse{

	@SerializedName("is_active")
	private boolean isActive;

	@SerializedName("name")
	private String name;

	@SerializedName("slug")
	private String slug;

	public void setIsActive(boolean isActive){
		this.isActive = isActive;
	}

	public boolean isIsActive(){
		return isActive;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}

	@Override
 	public String toString(){
		return 
			"AppointmentCategoryResponse{" + 
			"is_active = '" + isActive + '\'' + 
			",name = '" + name + '\'' + 
			",slug = '" + slug + '\'' + 
			"}";
		}
}