package bd.com.evaly.evalyshop.models.auth;

import com.google.gson.annotations.SerializedName;

public class LoginBody{

	@SerializedName("password")
	private String password;

	@SerializedName("phone_number")
	private String phoneNumber;

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return password;
	}

	public void setPhoneNumber(String phoneNumber){
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	@Override
 	public String toString(){
		return 
			"LoginBody{" + 
			"password = '" + password + '\'' + 
			",phone_number = '" + phoneNumber + '\'' + 
			"}";
		}
}