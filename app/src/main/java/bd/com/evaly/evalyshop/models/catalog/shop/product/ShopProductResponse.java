package bd.com.evaly.evalyshop.models.catalog.shop.product;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ShopProductResponse{

	@SerializedName("shop_item_name")
	private String shopItemName;

	@SerializedName("item_price")
	private int itemPrice;

	@SerializedName("shop_name")
	private String shopName;

	@SerializedName("in_stock")
	private int inStock;

	@SerializedName("discount_type")
	private String discountType;

	@SerializedName("shop_image")
	private String shopImage;

	@SerializedName("shop_item_id")
	private int shopItemId;

	@SerializedName("shop_slug")
	private String shopSlug;

	@SerializedName("discounted_price")
	private int discountedPrice;

	@SerializedName("is_express_shop")
	private boolean isExpressShop;

	@SerializedName("item_images")
	private List<String> itemImages;

	@SerializedName("attributes")
	private List<AttributesItem> attributes;

	@SerializedName("shop_item_category")
	private String shopItemCategory;

	@SerializedName("shop_item_slug")
	private String shopItemSlug;

	public void setShopItemName(String shopItemName){
		this.shopItemName = shopItemName;
	}

	public String getShopItemName(){
		return shopItemName;
	}

	public void setItemPrice(int itemPrice){
		this.itemPrice = itemPrice;
	}

	public int getItemPrice(){
		return itemPrice;
	}

	public void setShopName(String shopName){
		this.shopName = shopName;
	}

	public String getShopName(){
		return shopName;
	}

	public void setInStock(int inStock){
		this.inStock = inStock;
	}

	public int getInStock(){
		return inStock;
	}

	public void setDiscountType(String discountType){
		this.discountType = discountType;
	}

	public String getDiscountType(){
		return discountType;
	}

	public void setShopImage(String shopImage){
		this.shopImage = shopImage;
	}

	public String getShopImage(){
		return shopImage;
	}

	public void setShopItemId(int shopItemId){
		this.shopItemId = shopItemId;
	}

	public int getShopItemId(){
		return shopItemId;
	}

	public void setShopSlug(String shopSlug){
		this.shopSlug = shopSlug;
	}

	public String getShopSlug(){
		return shopSlug;
	}

	public void setDiscountedPrice(int discountedPrice){
		this.discountedPrice = discountedPrice;
	}

	public int getDiscountedPrice(){
		return discountedPrice;
	}

	public void setIsExpressShop(boolean isExpressShop){
		this.isExpressShop = isExpressShop;
	}

	public boolean isIsExpressShop(){
		return isExpressShop;
	}

	public void setItemImages(List<String> itemImages){
		this.itemImages = itemImages;
	}

	public List<String> getItemImages(){
		return itemImages;
	}

	public void setAttributes(List<AttributesItem> attributes){
		this.attributes = attributes;
	}

	public List<AttributesItem> getAttributes(){
		return attributes;
	}

	public void setShopItemCategory(String shopItemCategory){
		this.shopItemCategory = shopItemCategory;
	}

	public String getShopItemCategory(){
		return shopItemCategory;
	}

	public void setShopItemSlug(String shopItemSlug){
		this.shopItemSlug = shopItemSlug;
	}

	public String getShopItemSlug(){
		return shopItemSlug;
	}

	@Override
 	public String toString(){
		return 
			"ShopProductResponse{" + 
			"shop_item_name = '" + shopItemName + '\'' + 
			",item_price = '" + itemPrice + '\'' + 
			",shop_name = '" + shopName + '\'' + 
			",in_stock = '" + inStock + '\'' + 
			",discount_type = '" + discountType + '\'' + 
			",shop_image = '" + shopImage + '\'' + 
			",shop_item_id = '" + shopItemId + '\'' + 
			",shop_slug = '" + shopSlug + '\'' + 
			",discounted_price = '" + discountedPrice + '\'' + 
			",is_express_shop = '" + isExpressShop + '\'' + 
			",item_images = '" + itemImages + '\'' + 
			",attributes = '" + attributes + '\'' + 
			",shop_item_category = '" + shopItemCategory + '\'' + 
			",shop_item_slug = '" + shopItemSlug + '\'' + 
			"}";
		}
}