package bd.com.evaly.evalyshop.models.order.orderDetails;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderItemsItem{

	@SerializedName("quantity")
	private int quantity;

	@SerializedName("product_slug")
	private String productSlug;

	@SerializedName("order_time_price")
	private String orderTimePrice;

	@SerializedName("variations")
	private List<JsonObject> variations;

	@SerializedName("shop_item")
	private int shopItem;

	@SerializedName("item_name")
	private String itemName;

	@SerializedName("item_images")
	private List<String> itemImages;

	@SerializedName("id")
	private int id;

	public void setQuantity(int quantity){
		this.quantity = quantity;
	}

	public int getQuantity(){
		return quantity;
	}

	public void setProductSlug(String productSlug){
		this.productSlug = productSlug;
	}

	public String getProductSlug(){
		return productSlug;
	}

	public void setOrderTimePrice(String orderTimePrice){
		this.orderTimePrice = orderTimePrice;
	}

	public String getOrderTimePrice(){
		return orderTimePrice;
	}

	public void setVariations(List<JsonObject> variations){
		this.variations = variations;
	}

	public List<JsonObject> getVariations(){
		return variations;
	}

	public void setShopItem(int shopItem){
		this.shopItem = shopItem;
	}

	public int getShopItem(){
		return shopItem;
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

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	@Override
 	public String toString(){
		return 
			"OrderItemsItem{" + 
			"quantity = '" + quantity + '\'' + 
			",product_slug = '" + productSlug + '\'' + 
			",order_time_price = '" + orderTimePrice + '\'' + 
			",variations = '" + variations + '\'' + 
			",shop_item = '" + shopItem + '\'' + 
			",item_name = '" + itemName + '\'' + 
			",item_images = '" + itemImages + '\'' + 
			",id = '" + id + '\'' + 
			"}";
		}
}