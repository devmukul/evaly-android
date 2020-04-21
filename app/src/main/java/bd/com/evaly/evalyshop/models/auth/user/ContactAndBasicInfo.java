package bd.com.evaly.evalyshop.models.auth.user;

import com.google.gson.annotations.SerializedName;

public class ContactAndBasicInfo{

	@SerializedName("createdAt")
	private String createdAt;

	@SerializedName("gender")
	private String gender;

	@SerializedName("email")
	private String email;

	@SerializedName("updatedAt")
	private String updatedAt;

	@SerializedName("mobile")
	private String mobile;


	@SerializedName("address")
	private String address;

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setGender(String gender){
		this.gender = gender;
	}

	public String getGender(){
		return gender;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	@Override
 	public String toString(){
		return 
			"ContactAndBasicInfo{" + 
			"createdAt = '" + createdAt + '\'' + 
			",gender = '" + gender + '\'' + 
			",email = '" + email + '\'' + 
			",updatedAt = '" + updatedAt + '\'' + 
			"}";
		}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}