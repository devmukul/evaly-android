package bd.com.evaly.evalyshop.models.auth;

import com.google.gson.annotations.SerializedName;

public class SetPasswordBody{

	@SerializedName("password")
	private String password;

	@SerializedName("otp_token")
	private String otpToken;

	@SerializedName("phone_number")
	private String phoneNumber;

	@SerializedName("request_id")
	private String requestId;

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return password;
	}

	public void setOtpToken(String otpToken){
		this.otpToken = otpToken;
	}

	public String getOtpToken(){
		return otpToken;
	}

	public void setPhoneNumber(String phoneNumber){
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber(){
		return phoneNumber;
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
			"SetPasswordBody{" + 
			"password = '" + password + '\'' + 
			",otp_token = '" + otpToken + '\'' + 
			",phone_number = '" + phoneNumber + '\'' + 
			",request_id = '" + requestId + '\'' + 
			"}";
		}
}