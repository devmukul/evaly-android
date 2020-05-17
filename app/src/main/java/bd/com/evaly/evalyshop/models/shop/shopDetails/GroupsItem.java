package bd.com.evaly.evalyshop.models.shop.shopDetails;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GroupsItem implements Serializable {

	@SerializedName("name")
	private String name;

	@SerializedName("banner")
	private Object banner;

	@SerializedName("id")
	private int id;

	@SerializedName("slug")
	private String slug;

	@SerializedName("status")
	private String status;

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setBanner(Object banner){
		this.banner = banner;
	}

	public Object getBanner(){
		return banner;
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

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"GroupsItem{" + 
			"name = '" + name + '\'' + 
			",banner = '" + banner + '\'' + 
			",id = '" + id + '\'' + 
			",slug = '" + slug + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}