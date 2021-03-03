package bd.com.evaly.evalyshop.models.search.product;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class SearchRequest{

	@SerializedName("color_filters")
	private List<String> colorFilters;

	@SerializedName("size")
	private int size;

	@SerializedName("brand_filters")
	private List<String> brandFilters;

	@SerializedName("category_filters")
	private List<String> categoryFilters;

	@SerializedName("term")
	private String term;

	@SerializedName("from")
	private int from;

	@SerializedName("sort")
	private List<SortItem> sort;

	@SerializedName("shop_filters")
	private List<String> shopFilters;

	@SerializedName("bucket_size")
	private int bucketSize;

	public void setColorFilters(List<String> colorFilters){
		this.colorFilters = colorFilters;
	}

	public List<String> getColorFilters(){
		return colorFilters;
	}

	public void setSize(int size){
		this.size = size;
	}

	public int getSize(){
		return size;
	}

	public void setBrandFilters(List<String> brandFilters){
		this.brandFilters = brandFilters;
	}

	public List<String> getBrandFilters(){
		return brandFilters;
	}

	public void setCategoryFilters(List<String> categoryFilters){
		this.categoryFilters = categoryFilters;
	}

	public List<String> getCategoryFilters(){
		return categoryFilters;
	}

	public void setTerm(String term){
		this.term = term;
	}

	public String getTerm(){
		return term;
	}

	public void setFrom(int from){
		this.from = from;
	}

	public int getFrom(){
		return from;
	}

	public void setSort(List<SortItem> sort){
		this.sort = sort;
	}

	public List<SortItem> getSort(){
		return sort;
	}

	public void setShopFilters(List<String> shopFilters){
		this.shopFilters = shopFilters;
	}

	public List<String> getShopFilters(){
		return shopFilters;
	}

	public void setBucketSize(int bucketSize){
		this.bucketSize = bucketSize;
	}

	public int getBucketSize(){
		return bucketSize;
	}

	@Override
 	public String toString(){
		return 
			"SearchRequest{" + 
			"color_filters = '" + colorFilters + '\'' + 
			",size = '" + size + '\'' + 
			",brand_filters = '" + brandFilters + '\'' + 
			",category_filters = '" + categoryFilters + '\'' + 
			",term = '" + term + '\'' + 
			",from = '" + from + '\'' + 
			",sort = '" + sort + '\'' + 
			",shop_filters = '" + shopFilters + '\'' + 
			",bucket_size = '" + bucketSize + '\'' + 
			"}";
		}
}