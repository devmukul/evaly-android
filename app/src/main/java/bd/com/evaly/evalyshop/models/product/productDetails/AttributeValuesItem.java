package bd.com.evaly.evalyshop.models.product.productDetails;


import com.google.gson.annotations.SerializedName;

public class AttributeValuesItem{

	@SerializedName("value")
	private String value;

	@SerializedName("key")
	private int key;


	private String color_image;
	private boolean isSelected;

	public String getColor_image() {
		return color_image;
	}

	public void setColor_image(String color_image) {
		this.color_image = color_image;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	public void setValue(String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}

	public void setKey(int key){
		this.key = key;
	}

	public int getKey(){
		return key;
	}

	@Override
 	public String toString(){
		return 
			"AttributeValuesItem{" + 
			"value = '" + value + '\'' + 
			",key = '" + key + '\'' + 
			"}";
		}
}