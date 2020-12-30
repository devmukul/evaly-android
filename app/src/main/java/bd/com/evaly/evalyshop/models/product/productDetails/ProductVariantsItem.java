package bd.com.evaly.evalyshop.models.product.productDetails;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductVariantsItem{

	@SerializedName("category_name")
	private String categoryName;

	@SerializedName("category_image")
	private String categoryImage;

	@SerializedName("brand_image")
	private String brandImage;

	@SerializedName("brand_slug")
	private String brandSlug;

	@SerializedName("brand_name")
	private String brandName;

	@SerializedName("product_name")
	private String productName;

	@SerializedName("product_images")
	private List<String> productImages;

	@SerializedName("approved")
	private boolean approved;

	@SerializedName("variant_id")
	private int variantId;

	@SerializedName("max_price")
	private int maxPrice;

	@SerializedName("min_price")
	private int minPrice;

	@SerializedName("category_id")
	private int categoryId;

	@SerializedName("attribute_values")
	private List<Integer> attributeValues;

	@SerializedName("color_image")
	private String colorImage;

	@SerializedName("sku")
	private String sku;

	@SerializedName("product_description")
	private String productDescription;

	@SerializedName("category_slug")
	private String categorySlug;

	public void setCategoryName(String categoryName){
		this.categoryName = categoryName;
	}

	public String getCategoryName(){
		return categoryName;
	}

	public String getCategoryImage() {
		return categoryImage;
	}

	public void setCategoryImage(String categoryImage) {
		this.categoryImage = categoryImage;
	}

	public String getBrandImage() {
		return brandImage;
	}

	public void setBrandImage(String brandImage) {
		this.brandImage = brandImage;
	}

	public void setBrandSlug(String brandSlug){
		this.brandSlug = brandSlug;
	}

	public String getBrandSlug(){
		return brandSlug;
	}

	public void setBrandName(String brandName){
		this.brandName = brandName;
	}

	public String getBrandName(){
		return brandName;
	}

	public void setProductName(String productName){
		this.productName = productName;
	}

	public String getProductName(){
		return productName;
	}

	public void setProductImages(List<String> productImages){
		this.productImages = productImages;
	}

	public List<String> getProductImages(){
		return productImages;
	}

	public void setApproved(boolean approved){
		this.approved = approved;
	}

	public boolean getApproved(){
		return approved;
	}

	public void setVariantId(int variantId){
		this.variantId = variantId;
	}

	public int getVariantId(){
		return variantId;
	}

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

	public void setCategoryId(int categoryId){
		this.categoryId = categoryId;
	}

	public int getCategoryId(){
		return categoryId;
	}

	public void setAttributeValues(List<Integer> attributeValues){
		this.attributeValues = attributeValues;
	}

	public List<Integer> getAttributeValues(){
		return attributeValues;
	}

	public void setColorImage(String colorImage){
		this.colorImage = colorImage;
	}

	public String getColorImage(){
		return colorImage;
	}

	public void setSku(String sku){
		this.sku = sku;
	}

	public String getSku(){
		return sku;
	}

	public void setProductDescription(String productDescription){
		this.productDescription = productDescription;
	}

	public String getProductDescription(){
		return productDescription;
	}

	public void setCategorySlug(String categorySlug){
		this.categorySlug = categorySlug;
	}

	public String getCategorySlug(){
		return categorySlug;
	}

	@Override
 	public String toString(){
		return 
			"{" +
			"category_name = '" + categoryName + '\'' + 
			",brand_slug = '" + brandSlug + '\'' + 
			",brand_name = '" + brandName + '\'' + 
			",product_name = '" + productName + '\'' + 
			",product_images = '" + productImages + '\'' + 
			",approved = '" + approved + '\'' + 
			",variant_id = '" + variantId + '\'' + 
			",max_price = '" + maxPrice + '\'' + 
			",min_price = '" + minPrice + '\'' + 
			",category_id = '" + categoryId + '\'' + 
			",attribute_values = '" + attributeValues + '\'' + 
			",color_image = '" + colorImage + '\'' + 
			",sku = '" + sku + '\'' + 
			",product_description = '" + productDescription + '\'' + 
			",category_slug = '" + categorySlug + '\'' + 
			"}";
		}
}