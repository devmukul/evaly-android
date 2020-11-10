package bd.com.evaly.evalyshop.models.orderRequest;

import com.google.gson.annotations.SerializedName;

public class OrderItemsItem{

	@SerializedName("quantity")
	private int quantity;

	@SerializedName("item_id")
	private int itemId;

	@SerializedName("price")
	private int price;

	@SerializedName("name")
	private String name;

	public void setQuantity(int quantity){
		this.quantity = quantity;
	}

	public int getQuantity(){
		return quantity;
	}

	public void setItemId(int itemId){
		this.itemId = itemId;
	}

	public int getItemId(){
		return itemId;
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

	@Override
 	public String toString(){
		return 
			"OrderItemsItem{" + 
			"quantity = '" + quantity + '\'' + 
			",item_id = '" + itemId + '\'' + 
			",price = '" + price + '\'' + 
			",name = '" + name + '\'' + 
			"}";
		}
}