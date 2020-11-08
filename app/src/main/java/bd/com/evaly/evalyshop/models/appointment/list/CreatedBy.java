package bd.com.evaly.evalyshop.models.appointment.list;

import com.google.gson.annotations.SerializedName;

public class CreatedBy{

	@SerializedName("is_active")
	private boolean isActive;

	@SerializedName("is_superuser")
	private boolean isSuperuser;

	@SerializedName("is_staff")
	private boolean isStaff;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("first_name")
	private String firstName;

	@SerializedName("email")
	private String email;

	@SerializedName("username")
	private String username;

	public void setIsActive(boolean isActive){
		this.isActive = isActive;
	}

	public boolean isIsActive(){
		return isActive;
	}

	public void setIsSuperuser(boolean isSuperuser){
		this.isSuperuser = isSuperuser;
	}

	public boolean isIsSuperuser(){
		return isSuperuser;
	}

	public void setIsStaff(boolean isStaff){
		this.isStaff = isStaff;
	}

	public boolean isIsStaff(){
		return isStaff;
	}

	public void setLastName(String lastName){
		this.lastName = lastName;
	}

	public String getLastName(){
		return lastName;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public String getFirstName(){
		return firstName;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public String getUsername(){
		return username;
	}

	@Override
 	public String toString(){
		return 
			"CreatedBy{" + 
			"is_active = '" + isActive + '\'' + 
			",is_superuser = '" + isSuperuser + '\'' + 
			",is_staff = '" + isStaff + '\'' + 
			",last_name = '" + lastName + '\'' + 
			",first_name = '" + firstName + '\'' + 
			",email = '" + email + '\'' + 
			",username = '" + username + '\'' + 
			"}";
		}
}