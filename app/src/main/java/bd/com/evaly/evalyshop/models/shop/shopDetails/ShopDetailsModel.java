package bd.com.evaly.evalyshop.models.shop.shopDetails;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ShopDetailsModel implements Serializable {

	@SerializedName("data")
	private Data data;

	@SerializedName("success")
	private boolean success;

	@SerializedName("count")
	private int count;

	@SerializedName("message")
	private String message;

	@SerializedName("shop_delivery_options")
	private List<ShopDeliveryOption> shopDeliveryOptions;

	public void setData(Data data){
		this.data = data;
	}

	public Data getData(){
		return data;
	}

	public void setSuccess(boolean success){
		this.success = success;
	}

	public boolean isSuccess(){
		return success;
	}

	public void setCount(int count){
		this.count = count;
	}

	public int getCount(){
		return count;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public List<ShopDeliveryOption> getShopDeliveryOptions() {
		return shopDeliveryOptions;
	}

	public void setShopDeliveryOptions(List<ShopDeliveryOption> shopDeliveryOptions) {
		this.shopDeliveryOptions = shopDeliveryOptions;
	}

	@Override
 	public String toString(){
		return 
			"ShopDetailsModel{" + 
			"data = '" + data + '\'' + 
			",success = '" + success + '\'' + 
			",count = '" + count + '\'' + 
			",message = '" + message + '\'' + 
			"}";
		}
}