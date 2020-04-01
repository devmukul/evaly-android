package bd.com.evaly.evalyshop.models.auth;

import com.google.gson.annotations.SerializedName;

public class SetPasswordResponse{

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	@SerializedName("error")
	private String error;

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

	public void setError(String error){
		this.error = error;
	}

	public String getError(){
		return error;
	}

	@Override
 	public String toString(){
		return 
			"SetPasswordResponse{" + 
			"success = '" + success + '\'' + 
			",message = '" + message + '\'' + 
			",error = '" + error + '\'' + 
			"}";
		}
}