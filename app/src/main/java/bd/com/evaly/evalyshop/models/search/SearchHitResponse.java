package bd.com.evaly.evalyshop.models.search;

import com.google.gson.annotations.SerializedName;

import bd.com.evaly.evalyshop.models.BaseModel;

public class SearchHitResponse extends BaseModel{

	@SerializedName("category_name")
	private String categoryName;

	@SerializedName("color_variants")
	private String colorVariants;

	@SerializedName("color")
	private String color;

	@SerializedName("product_image")
	private String productImage;

	@SerializedName("brand_name")
	private String brandName;

	@SerializedName("shop_name")
	private String shopName;

	@SerializedName("shop_item_id")
	private int shopItemId;

	@SerializedName("discounted_price")
	private double discountedPrice;

	@SerializedName("_highlightResult")
	private HighlightResult highlightResult;

	@SerializedName("max_price")
	private double maxPrice;

	@SerializedName("min_price")
	private double minPrice;

	@SerializedName("price")
	private double price;

	@SerializedName("name")
	private String name;

	@SerializedName("slug")
	private String slug;

	@SerializedName("objectID")
	private String objectID;

	public void setCategoryName(String categoryName){
		this.categoryName = categoryName;
	}

	public String getCategoryName(){
		return categoryName;
	}

	public void setColorVariants(String colorVariants){
		this.colorVariants = colorVariants;
	}

	public String getColorVariants(){
		return colorVariants;
	}

	public void setColor(String color){
		this.color = color;
	}

	public String getColor(){
		return color;
	}

	public void setProductImage(String productImage){
		this.productImage = productImage;
	}

	public String getProductImage(){
		return productImage;
	}

	public void setBrandName(String brandName){
		this.brandName = brandName;
	}

	public String getBrandName(){
		return brandName;
	}

	public void setShopName(String shopName){
		this.shopName = shopName;
	}

	public String getShopName(){
		return shopName;
	}

	public void setShopItemId(int shopItemId){
		this.shopItemId = shopItemId;
	}

	public int getShopItemId(){
		return shopItemId;
	}

	public void setDiscountedPrice(double discountedPrice){
		this.discountedPrice = discountedPrice;
	}

	public double getDiscountedPrice(){
		return discountedPrice;
	}

	public void setHighlightResult(HighlightResult highlightResult){
		this.highlightResult = highlightResult;
	}

	public HighlightResult getHighlightResult(){
		return highlightResult;
	}

	public void setMaxPrice(double maxPrice){
		this.maxPrice = maxPrice;
	}

	public double getMaxPrice(){
		return maxPrice;
	}

	public void setMinPrice(double minPrice){
		this.minPrice = minPrice;
	}

	public double getMinPrice(){
		return minPrice;
	}

	public void setPrice(double price){
		this.price = price;
	}

	public double getPrice(){
		return price;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}

	public void setObjectID(String objectID){
		this.objectID = objectID;
	}

	public String getObjectID(){
		return objectID;
	}

	@Override
 	public String toString(){
		return 
			"SearchHitResponse{" + 
			"category_name = '" + categoryName + '\'' + 
			",color_variants = '" + colorVariants + '\'' + 
			",color = '" + color + '\'' + 
			",product_image = '" + productImage + '\'' + 
			",brand_name = '" + brandName + '\'' + 
			",shop_name = '" + shopName + '\'' + 
			",shop_item_id = '" + shopItemId + '\'' + 
			",discounted_price = '" + discountedPrice + '\'' + 
			",_highlightResult = '" + highlightResult + '\'' + 
			",max_price = '" + maxPrice + '\'' + 
			",min_price = '" + minPrice + '\'' + 
			",price = '" + price + '\'' + 
			",name = '" + name + '\'' + 
			",slug = '" + slug + '\'' + 
			",objectID = '" + objectID + '\'' + 
			"}";
		}
}