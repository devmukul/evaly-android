package bd.com.evaly.evalyshop.models.profile;

import com.google.gson.annotations.SerializedName;

public class PersonalInfoRequest {

	@SerializedName("gender")
	private String gender;

	@SerializedName("birth_date")
	private String birthDate;

	@SerializedName("contact")
	private String contact;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("first_name")
	private String firstName;

	public void setGender(String gender){
		this.gender = gender;
	}

	public String getGender(){
		return gender;
	}

	public void setBirthDate(String birthDate){
		this.birthDate = birthDate;
	}

	public String getBirthDate(){
		return birthDate;
	}

	public void setContact(String contact){
		this.contact = contact;
	}

	public String getContact(){
		return contact;
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

	@Override
 	public String toString(){
		return 
			"PersonalInfoRequest{" +
			"gender = '" + gender + '\'' + 
			",birth_date = '" + birthDate + '\'' + 
			",contact = '" + contact + '\'' + 
			",last_name = '" + lastName + '\'' + 
			",first_name = '" + firstName + '\'' + 
			"}";
		}
}