package bd.com.evaly.evalyshop.models.catalog.products;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ProductResponse{

	@SerializedName("max_price")
	private int maxPrice;

	@SerializedName("min_price")
	private int minPrice;

	@SerializedName("name")
	private String name;

	@SerializedName("image_urls")
	private List<String> imageUrls;

	@SerializedName("price_type")
	private String priceType;

	@SerializedName("slug")
	private String slug;

	@SerializedName("min_discounted_price")
	private int minDiscountedPrice;

	public void setMaxPrice(int maxPrice){
		this.maxPrice = maxPrice;
	}

	public int getMaxPrice(){
		return maxPrice;
	}

	public void setMinPrice(int minPrice){
		this.minPrice = minPrice;
	}

	public int getMinPrice(){
		return minPrice;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setImageUrls(List<String> imageUrls){
		this.imageUrls = imageUrls;
	}

	public List<String> getImageUrls(){
		return imageUrls;
	}

	public void setPriceType(String priceType){
		this.priceType = priceType;
	}

	public String getPriceType(){
		return priceType;
	}

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}

	public void setMinDiscountedPrice(int minDiscountedPrice){
		this.minDiscountedPrice = minDiscountedPrice;
	}

	public int getMinDiscountedPrice(){
		return minDiscountedPrice;
	}

	@Override
 	public String toString(){
		return 
			"ProductResponse{" + 
			"max_price = '" + maxPrice + '\'' + 
			",min_price = '" + minPrice + '\'' + 
			",name = '" + name + '\'' + 
			",image_urls = '" + imageUrls + '\'' + 
			",price_type = '" + priceType + '\'' + 
			",slug = '" + slug + '\'' + 
			",min_discounted_price = '" + minDiscountedPrice + '\'' + 
			"}";
		}
}