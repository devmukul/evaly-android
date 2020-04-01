package bd.com.evaly.evalyshop.models.auth;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse{

	@SerializedName("data")
	private String data;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	@SerializedName("request_id")
	private String requestId;

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

	public void setRequestId(String requestId){
		this.requestId = requestId;
	}

	public String getRequestId(){
		return requestId;
	}

	@Override
 	public String toString(){
		return 
			"RegisterResponse{" + 
			"data = '" + data + '\'' + 
			",success = '" + success + '\'' + 
			",message = '" + message + '\'' + 
			",request_id = '" + requestId + '\'' + 
			"}";
		}
}