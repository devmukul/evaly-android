package bd.com.evaly.evalyshop.models.profile;

import com.google.gson.annotations.SerializedName;

public class UserInfoResponse{

	@SerializedName("full_name")
	private String fullName;

	@SerializedName("occupation")
	private Object occupation;

	@SerializedName("other_email")
	private String otherEmail;

	@SerializedName("father_name")
	private String fatherName;

	@SerializedName("father_phone_number")
	private String fatherPhoneNumber;

	@SerializedName("organization")
	private Object organization;

	@SerializedName("mother_name")
	private String motherName;

	@SerializedName("phone_number")
	private String phoneNumber;

	@SerializedName("primary_email")
	private String primaryEmail;

	@SerializedName("mother_phone_number")
	private String motherPhoneNumber;

	public void setFullName(String fullName){
		this.fullName = fullName;
	}

	public String getFullName(){
		return fullName;
	}

	public void setOccupation(Object occupation){
		this.occupation = occupation;
	}

	public Object getOccupation(){
		return occupation;
	}

	public void setOtherEmail(String otherEmail){
		this.otherEmail = otherEmail;
	}

	public String getOtherEmail(){
		return otherEmail;
	}

	public void setFatherName(String fatherName){
		this.fatherName = fatherName;
	}

	public String getFatherName(){
		return fatherName;
	}

	public void setFatherPhoneNumber(String fatherPhoneNumber){
		this.fatherPhoneNumber = fatherPhoneNumber;
	}

	public String getFatherPhoneNumber(){
		return fatherPhoneNumber;
	}

	public void setOrganization(Object organization){
		this.organization = organization;
	}

	public Object getOrganization(){
		return organization;
	}

	public void setMotherName(String motherName){
		this.motherName = motherName;
	}

	public String getMotherName(){
		return motherName;
	}

	public void setPhoneNumber(String phoneNumber){
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public void setPrimaryEmail(String primaryEmail){
		this.primaryEmail = primaryEmail;
	}

	public String getPrimaryEmail(){
		return primaryEmail;
	}

	public void setMotherPhoneNumber(String motherPhoneNumber){
		this.motherPhoneNumber = motherPhoneNumber;
	}

	public String getMotherPhoneNumber(){
		return motherPhoneNumber;
	}

	@Override
 	public String toString(){
		return 
			"UserInfoResponse{" + 
			"full_name = '" + fullName + '\'' + 
			",occupation = '" + occupation + '\'' + 
			",other_email = '" + otherEmail + '\'' + 
			",father_name = '" + fatherName + '\'' + 
			",father_phone_number = '" + fatherPhoneNumber + '\'' + 
			",organization = '" + organization + '\'' + 
			",mother_name = '" + motherName + '\'' + 
			",phone_number = '" + phoneNumber + '\'' + 
			",primary_email = '" + primaryEmail + '\'' + 
			",mother_phone_number = '" + motherPhoneNumber + '\'' + 
			"}";
		}
}