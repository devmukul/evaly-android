package bd.com.evaly.evalyshop.models.shop.shopDetails;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ItemsItem implements Serializable {

	@SerializedName("item_price")
	private double itemPrice;

	@SerializedName("item_name")
	private String itemName;

	@SerializedName("item_images")
	private List<String> itemImages;

	@SerializedName("shop_item_slug")
	private String shopItemSlug;

	@SerializedName("shop_slug")
	private String shopSlug;

	@SerializedName("discounted_price")
	private double discountedPrice;

	@SerializedName("in_stock")
	private int inStock;

	public int getInStock() {
		return inStock;
	}

	public void setInStock(int inStock) {
		this.inStock = inStock;
	}

	public void setItemPrice(double itemPrice){
		this.itemPrice = itemPrice;
	}

	public double getItemPrice(){
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

	public void setShopItemSlug(String shopItemSlug){
		this.shopItemSlug = shopItemSlug;
	}

	public String getShopItemSlug(){
		return shopItemSlug;
	}

	public void setShopSlug(String shopSlug){
		this.shopSlug = shopSlug;
	}

	public String getShopSlug(){
		return shopSlug;
	}

	public void setDiscountedPrice(double discountedPrice){
		this.discountedPrice = discountedPrice;
	}

	public double getDiscountedPrice(){
		return discountedPrice;
	}

	@Override
 	public String toString(){
		return 
			"ItemsItem{" + 
			"item_price = '" + itemPrice + '\'' + 
			",item_name = '" + itemName + '\'' + 
			",item_images = '" + itemImages + '\'' + 
			",shop_item_slug = '" + shopItemSlug + '\'' + 
			",shop_slug = '" + shopSlug + '\'' + 
			",discounted_price = '" + discountedPrice + '\'' + 
			"}";
		}
}