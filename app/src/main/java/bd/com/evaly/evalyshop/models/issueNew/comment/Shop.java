package bd.com.evaly.evalyshop.models.issueNew.comment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Shop implements Serializable {

	@SerializedName("is_active")
	private boolean isActive;

	@SerializedName("updated_at")
	private Object updatedAt;

	@SerializedName("name")
	private String name;

	@SerializedName("updated_by")
	private Object updatedBy;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private int id;

	@SerializedName("created_by")
	private CreatedBy createdBy;

	@SerializedName("slug")
	private String slug;

	@SerializedName("kam_user")
	private Object kamUser;

	public void setIsActive(boolean isActive){
		this.isActive = isActive;
	}

	public boolean isIsActive(){
		return isActive;
	}

	public void setUpdatedAt(Object updatedAt){
		this.updatedAt = updatedAt;
	}

	public Object getUpdatedAt(){
		return updatedAt;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setUpdatedBy(Object updatedBy){
		this.updatedBy = updatedBy;
	}

	public Object getUpdatedBy(){
		return updatedBy;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setCreatedBy(CreatedBy createdBy){
		this.createdBy = createdBy;
	}

	public CreatedBy getCreatedBy(){
		return createdBy;
	}

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}

	public void setKamUser(Object kamUser){
		this.kamUser = kamUser;
	}

	public Object getKamUser(){
		return kamUser;
	}

	@Override
 	public String toString(){
		return 
			"Shop{" + 
			"is_active = '" + isActive + '\'' + 
			",updated_at = '" + updatedAt + '\'' + 
			",name = '" + name + '\'' + 
			",updated_by = '" + updatedBy + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",id = '" + id + '\'' + 
			",created_by = '" + createdBy + '\'' + 
			",slug = '" + slug + '\'' + 
			",kam_user = '" + kamUser + '\'' + 
			"}";
		}
}