package bd.com.evaly.evalyshop.models.auth.user;

import com.google.gson.annotations.SerializedName;

public class Group{

	@SerializedName("name")
	private String name;

	@SerializedName("_id")
	private String id;

	@SerializedName("slug")
	private String slug;

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
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
			"Group{" + 
			"name = '" + name + '\'' + 
			",_id = '" + id + '\'' + 
			",slug = '" + slug + '\'' + 
			"}";
		}
}