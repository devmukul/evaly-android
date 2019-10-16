package bd.com.evaly.evalyshop.models.shopItem;

import com.google.gson.annotations.SerializedName;

public class AttributesItem{

	@SerializedName("Color")
	private String color;

	public void setColor(String color){
		this.color = color;
	}

	public String getColor(){
		return color;
	}

	@Override
 	public String toString(){
		return 
			"AttributesItem{" + 
			"color = '" + color + '\'' + 
			"}";
		}
}