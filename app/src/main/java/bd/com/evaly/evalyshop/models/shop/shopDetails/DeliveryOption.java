package bd.com.evaly.evalyshop.models.shop.shopDetails;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DeliveryOption implements Serializable {

	@SerializedName("name")
	private String name;

	@SerializedName("description")
	private String description;

	@SerializedName("id")
	private int id;

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

	@Override
 	public String toString(){
		return 
			"DeliveryOption{" + 
			"name = '" + name + '\'' + 
			",description = '" + description + '\'' + 
			",id = '" + id + '\'' + 
			"}";
		}
}