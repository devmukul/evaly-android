package bd.com.evaly.evalyshop.models.product.productDetails;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AttributesItem{

	@SerializedName("attribute_values")
	private List<AttributeValuesItem> attributeValues;

	@SerializedName("attribute_slug")
	private String attributeSlug;

	@SerializedName("attribute_name")
	private String attributeName;

	public void setAttributeValues(List<AttributeValuesItem> attributeValues){
		this.attributeValues = attributeValues;
	}

	public List<AttributeValuesItem> getAttributeValues(){
		return attributeValues;
	}

	public void setAttributeSlug(String attributeSlug){
		this.attributeSlug = attributeSlug;
	}

	public String getAttributeSlug(){
		return attributeSlug;
	}

	public void setAttributeName(String attributeName){
		this.attributeName = attributeName;
	}

	public String getAttributeName(){
		return attributeName;
	}

	@Override
 	public String toString(){
		return 
			"AttributesItem{" + 
			"attribute_values = '" + attributeValues + '\'' + 
			",attribute_slug = '" + attributeSlug + '\'' + 
			",attribute_name = '" + attributeName + '\'' + 
			"}";
		}
}