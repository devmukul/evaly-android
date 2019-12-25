package bd.com.evaly.evalyshop.models.order.placeOrder;


import com.google.gson.annotations.SerializedName;

public class OrderItemsItem{

	@SerializedName("quantity")
	private int quantity;

	@SerializedName("shop_item_id")
	private int shopItemId;

	public void setQuantity(int quantity){
		this.quantity = quantity;
	}

	public int getQuantity(){
		return quantity;
	}

	public void setShopItemId(int shopItemId){
		this.shopItemId = shopItemId;
	}

	public int getShopItemId(){
		return shopItemId;
	}

	@Override
 	public String toString(){
		return 
			"OrderItemsItem{" + 
			"quantity = '" + quantity + '\'' + 
			",shop_item_id = '" + shopItemId + '\'' + 
			"}";
		}
}