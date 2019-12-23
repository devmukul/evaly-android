package bd.com.evaly.evalyshop.models.shop;

import com.google.gson.annotations.SerializedName;

public class ShopDeliveryOptionsItem{

	@SerializedName("shop")
	private String shop;

	@SerializedName("id")
	private int id;

	@SerializedName("delivery_option")
	private DeliveryOption deliveryOption;

	public void setShop(String shop){
		this.shop = shop;
	}

	public String getShop(){
		return shop;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setDeliveryOption(DeliveryOption deliveryOption){
		this.deliveryOption = deliveryOption;
	}

	public DeliveryOption getDeliveryOption(){
		return deliveryOption;
	}

	@Override
 	public String toString(){
		return 
			"ShopDeliveryOptionsItem{" + 
			"shop = '" + shop + '\'' + 
			",id = '" + id + '\'' + 
			",delivery_option = '" + deliveryOption + '\'' + 
			"}";
		}
}