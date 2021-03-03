package bd.com.evaly.evalyshop.models.search.product;

import com.google.gson.annotations.SerializedName;

public class SortItem{

	@SerializedName("field_name")
	private String fieldName;

	@SerializedName("order")
	private String order;

	public void setFieldName(String fieldName){
		this.fieldName = fieldName;
	}

	public String getFieldName(){
		return fieldName;
	}

	public void setOrder(String order){
		this.order = order;
	}

	public String getOrder(){
		return order;
	}

	@Override
 	public String toString(){
		return 
			"SortItem{" + 
			"field_name = '" + fieldName + '\'' + 
			",order = '" + order + '\'' + 
			"}";
		}
}