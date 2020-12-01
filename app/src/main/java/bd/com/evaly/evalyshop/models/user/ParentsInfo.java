package bd.com.evaly.evalyshop.models.user;

import com.google.gson.annotations.SerializedName;

public class ParentsInfo{

	@SerializedName("father_name")
	private String fatherName;

	@SerializedName("father_phone_number")
	private String fatherPhoneNumber;

	@SerializedName("mother_name")
	private String motherName;

	@SerializedName("mother_phone_number")
	private String motherPhoneNumber;

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

	public void setMotherName(String motherName){
		this.motherName = motherName;
	}

	public String getMotherName(){
		return motherName;
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
			"ParentsInfo{" + 
			"father_name = '" + fatherName + '\'' + 
			",father_phone_number = '" + fatherPhoneNumber + '\'' + 
			",mother_name = '" + motherName + '\'' + 
			",mother_phone_number = '" + motherPhoneNumber + '\'' + 
			"}";
		}
}