package bd.com.evaly.evalyshop.models.catalog.location;

import com.google.gson.annotations.SerializedName;

public class LocationResponse{

	@SerializedName("parent")
	private String parent;

	@SerializedName("name_bn")
	private String nameBn;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	@SerializedName("type")
	private String type;

	@SerializedName("slug")
	private String slug;


	public LocationResponse(String name, String slug) {
		this.name = name;
		this.slug = slug;
	}

	public void setParent(String parent){
		this.parent = parent;
	}

	public String getParent(){
		return parent;
	}

	public void setNameBn(String nameBn){
		this.nameBn = nameBn;
	}

	public String getNameBn(){
		return nameBn;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
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
			"LocationResponse{" +
			"parent = '" + parent + '\'' +
			",name_bn = '" + nameBn + '\'' +
			",name = '" + name + '\'' +
			",id = '" + id + '\'' +
			",type = '" + type + '\'' +
			",slug = '" + slug + '\'' +
			"}";
		}
}