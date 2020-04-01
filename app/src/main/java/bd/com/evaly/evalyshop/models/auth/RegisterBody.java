package bd.com.evaly.evalyshop.models.auth;

import com.google.gson.annotations.SerializedName;

public class RegisterBody{

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("phone_number")
	private String phoneNumber;

	@SerializedName("first_name")
	private String firstName;

	public void setLastName(String lastName){
		this.lastName = lastName;
	}

	public String getLastName(){
		return lastName;
	}

	public void setPhoneNumber(String phoneNumber){
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public String getFirstName(){
		return firstName;
	}

	@Override
 	public String toString(){
		return 
			"RegisterBody{" + 
			"last_name = '" + lastName + '\'' + 
			",phone_number = '" + phoneNumber + '\'' + 
			",first_name = '" + firstName + '\'' + 
			"}";
		}
}