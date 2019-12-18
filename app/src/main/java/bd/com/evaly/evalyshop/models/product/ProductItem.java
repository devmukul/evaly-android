package bd.com.evaly.evalyshop.models.product;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ProductItem {

	@SerializedName("max_price")
	private String maxPrice;

	@SerializedName("min_price")
	private String minPrice;

	@SerializedName("name")
	private String name;

	@SerializedName("image_urls")
	private List<String> imageUrls;

	@SerializedName("price_type")
	private String priceType;

	@SerializedName("slug")
	private String slug;

	@SerializedName("min_discounted_price")
	private String minDiscountedPrice;

	public void setMaxPrice(String maxPrice){
		this.maxPrice = maxPrice;
	}

	public String getMaxPrice(){
		return maxPrice;
	}

	public void setMinPrice(String minPrice){
		this.minPrice = minPrice;
	}

	public String getMinPrice(){
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

	public void setMinDiscountedPrice(String minDiscountedPrice){
		this.minDiscountedPrice = minDiscountedPrice;
	}

	public String getMinDiscountedPrice(){
		return minDiscountedPrice;
	}



	public double getMaxPriceD(){
		try {
			return Math.ceil(Double.parseDouble(maxPrice));
		}catch (Exception e){
			return 0.0;
		}
	}

	public double getMinPriceD(){
		try {
			return Math.ceil(Double.parseDouble(minPrice));
		}catch (Exception e){
			return 0.0;
		}
	}

	public double getMinDiscountedPriceD(){
		try {
			return Math.ceil(Double.parseDouble(minDiscountedPrice));
		}catch (Exception e){
			return 0.0;
		}
	}

	@Override
 	public String toString(){
		return 
			"ProductItem{" +
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