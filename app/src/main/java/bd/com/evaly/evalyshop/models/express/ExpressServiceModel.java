package bd.com.evaly.evalyshop.models.express;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "express_service_list")
public class ExpressServiceModel{

	@SerializedName("image")
	private String image;

	@SerializedName("service_type")
	private String serviceType;

	@SerializedName("name")
	private String name;

	@SerializedName("modified_by")
	private String modifiedBy;

	@SerializedName("modified_at")
	private String modifiedAt;

	@NonNull
	@PrimaryKey
	@SerializedName("slug")
	private String slug;

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setServiceType(String serviceType){
		this.serviceType = serviceType;
	}

	public String getServiceType(){
		return serviceType;
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

	@Override
 	public String toString(){
		return 
			"ExpressServiceModel{" + 
			"image = '" + image + '\'' + 
			",service_type = '" + serviceType + '\'' + 
			",name = '" + name + '\'' + 
			",modified_by = '" + modifiedBy + '\'' + 
			",modified_at = '" + modifiedAt + '\'' + 
			",slug = '" + slug + '\'' + 
			"}";
		}
}