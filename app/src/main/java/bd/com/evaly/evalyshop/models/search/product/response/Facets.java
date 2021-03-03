package bd.com.evaly.evalyshop.models.search.product.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import bd.com.evaly.evalyshop.models.search.filter.FilterSubItem;

public class Facets{

	@SerializedName("brands")
	private List<FilterSubItem> brands;

	@SerializedName("shops")
	private List<FilterSubItem> shops;

	@SerializedName("categories")
	private List<FilterSubItem> categories;

	@SerializedName("colors")
	private List<Object> colors;

	public void setBrands(List<FilterSubItem> brands){
		this.brands = brands;
	}

	public List<FilterSubItem> getBrands(){
		return brands;
	}

	public void setShops(List<FilterSubItem> shops){
		this.shops = shops;
	}

	public List<FilterSubItem> getShops(){
		return shops;
	}

	public void setCategories(List<FilterSubItem> categories){
		this.categories = categories;
	}

	public List<FilterSubItem> getCategories(){
		return categories;
	}

	public void setColors(List<Object> colors){
		this.colors = colors;
	}

	public List<Object> getColors(){
		return colors;
	}

	@Override
 	public String toString(){
		return 
			"Facets{" + 
			"brands = '" + brands + '\'' + 
			",shops = '" + shops + '\'' + 
			",categories = '" + categories + '\'' + 
			",colors = '" + colors + '\'' + 
			"}";
		}
}