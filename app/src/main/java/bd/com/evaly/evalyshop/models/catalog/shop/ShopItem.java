package bd.com.evaly.evalyshop.models.catalog.shop;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShopItem {

	@SerializedName("item_price")
	private int itemPrice;

	@SerializedName("item_name")
	private String itemName;

	@SerializedName("item_images")
	private List<String> itemImages;

	@SerializedName("shop_item_category")
	private String shopItemCategory;

	@SerializedName("shop_item_slug")
	private String shopItemSlug;

	@SerializedName("shop_name")
	private String shopName;

	@SerializedName("shop_image")
	private String shopImage;

	@SerializedName("in_stock")
	private int inStock;

	@SerializedName("discount_type")
	private String discountType;

	@SerializedName("shop_slug")
	private String shopSlug;

	@SerializedName("is_express_shop")
	private boolean isExpressShop;

	@SerializedName("discounted_price")
	private int discountedPrice;

	public void setItemPrice(int itemPrice){
		this.itemPrice = itemPrice;
	}

	public int getItemPrice(){
		return itemPrice;
	}

	public void setItemName(String itemName){
		this.itemName = itemName;
	}

	public String getItemName(){
		return itemName;
	}

	public void setItemImages(List<String> itemImages){
		this.itemImages = itemImages;
	}

	public List<String> getItemImages(){
		return itemImages;
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

	public void setShopName(String shopName){
		this.shopName = shopName;
	}

	public String getShopName(){
		return shopName;
	}

	public void setShopImage(String shopImage){
		this.shopImage = shopImage;
	}

	public String getShopImage(){
		return shopImage;
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

	public void setShopSlug(String shopSlug){
		this.shopSlug = shopSlug;
	}

	public String getShopSlug(){
		return shopSlug;
	}

	public void setIsExpressShop(boolean isExpressShop){
		this.isExpressShop = isExpressShop;
	}

	public boolean isIsExpressShop(){
		return isExpressShop;
	}

	public void setDiscountedPrice(int discountedPrice){
		this.discountedPrice = discountedPrice;
	}

	public int getDiscountedPrice(){
		return discountedPrice;
	}

	@Override
 	public String toString(){
		return 
			"ItemsItem{" + 
			"item_price = '" + itemPrice + '\'' + 
			",item_name = '" + itemName + '\'' + 
			",item_images = '" + itemImages + '\'' + 
			",shop_item_category = '" + shopItemCategory + '\'' + 
			",shop_item_slug = '" + shopItemSlug + '\'' + 
			",shop_name = '" + shopName + '\'' + 
			",shop_image = '" + shopImage + '\'' + 
			",in_stock = '" + inStock + '\'' + 
			",discount_type = '" + discountType + '\'' + 
			",shop_slug = '" + shopSlug + '\'' + 
			",is_express_shop = '" + isExpressShop + '\'' + 
			",discounted_price = '" + discountedPrice + '\'' + 
			"}";
		}
}