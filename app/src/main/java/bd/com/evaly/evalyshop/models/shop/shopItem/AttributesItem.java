package bd.com.evaly.evalyshop.models.shop.shopItem;

import com.google.gson.annotations.SerializedName;

public class AttributesItem{


	@SerializedName(value="name")
	private String name;

	@SerializedName(value="value")
	private String value;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}



}