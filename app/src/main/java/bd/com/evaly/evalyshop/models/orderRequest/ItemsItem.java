package bd.com.evaly.evalyshop.models.orderRequest;

import com.google.gson.annotations.SerializedName;

public class ItemsItem{

	@SerializedName("quantity")
	private int quantity;

	@SerializedName("price")
	private int price;

	@SerializedName("name")
	private String name;

	@SerializedName("shop_item_id")
	private int shopItemId;

	public void setQuantity(int quantity){
		this.quantity = quantity;
	}

	public int getQuantity(){
		return quantity;
	}

	public void setPrice(int price){
		this.price = price;
	}

	public int getPrice(){
		return price;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
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
			"ItemsItem{" + 
			"quantity = '" + quantity + '\'' + 
			",price = '" + price + '\'' + 
			",name = '" + name + '\'' + 
			",shop_item_id = '" + shopItemId + '\'' + 
			"}";
		}
}