package bd.com.evaly.evalyshop.models.shop.shopDetails;

import com.google.gson.annotations.SerializedName;

public class ShopDetailsModel{

	@SerializedName("data")
	private Data data;

	@SerializedName("success")
	private boolean success;

	@SerializedName("count")
	private int count;

	@SerializedName("message")
	private String message;

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