package bd.com.evaly.evalyshop.models.search.product.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Facets{

	@SerializedName("brands")
	private List<FacetsItem> brands;

	@SerializedName("shops")
	private List<FacetsItem> shops;

	@SerializedName("categories")
	private List<FacetsItem> categories;

	@SerializedName("colors")
	private List<Object> colors;

	public void setBrands(List<FacetsItem> brands){
		this.brands = brands;
	}

	public List<FacetsItem> getBrands(){
		return brands;
	}

	public void setShops(List<FacetsItem> shops){
		this.shops = shops;
	}

	public List<FacetsItem> getShops(){
		return shops;
	}

	public void setCategories(List<FacetsItem> categories){
		this.categories = categories;
	}

	public List<FacetsItem> getCategories(){
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