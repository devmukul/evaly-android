package bd.com.evaly.evalyshop.models.auth;

import com.google.gson.annotations.SerializedName;

public class LoginResponse{

	@SerializedName("data")
	private String data;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setData(String data){
		this.data = data;
	}

	public String getData(){
		return data;
	}

	public void setSuccess(boolean success){
		this.success = success;
	}

	public boolean isSuccess(){
		return success;
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
			"LoginResponse{" + 
			"data = '" + data + '\'' + 
			",success = '" + success + '\'' + 
			",message = '" + message + '\'' + 
			"}";
		}
}