package bd.com.evaly.evalyshop.models.hero;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data{

	@SerializedName("driving_license")
	private List<String> drivingLicense;

	@SerializedName("confirmation_status")
	private String confirmationStatus;

	@SerializedName("nid")
	private List<String> nid;

	@SerializedName("alias")
	private String alias;

	@SerializedName("work_status")
	private String workStatus;

	@SerializedName("user")
	private User user;

	@SerializedName("emergency_contact")
	private String emergencyContact;

	public void setDrivingLicense(List<String> drivingLicense){
		this.drivingLicense = drivingLicense;
	}

	public List<String> getDrivingLicense(){
		return drivingLicense;
	}

	public void setConfirmationStatus(String confirmationStatus){
		this.confirmationStatus = confirmationStatus;
	}

	public String getConfirmationStatus(){
		return confirmationStatus;
	}

	public void setNid(List<String> nid){
		this.nid = nid;
	}

	public List<String> getNid(){
		return nid;
	}

	public void setAlias(String alias){
		this.alias = alias;
	}

	public String getAlias(){
		return alias;
	}

	public void setWorkStatus(String workStatus){
		this.workStatus = workStatus;
	}

	public String getWorkStatus(){
		return workStatus;
	}

	public void setUser(User user){
		this.user = user;
	}

	public User getUser(){
		return user;
	}

	public void setEmergencyContact(String emergencyContact){
		this.emergencyContact = emergencyContact;
	}

	public String getEmergencyContact(){
		return emergencyContact;
	}

	@Override
 	public String toString(){
		return 
			"Data{" + 
			"driving_license = '" + drivingLicense + '\'' + 
			",confirmation_status = '" + confirmationStatus + '\'' + 
			",nid = '" + nid + '\'' + 
			",alias = '" + alias + '\'' + 
			",work_status = '" + workStatus + '\'' + 
			",user = '" + user + '\'' + 
			",emergency_contact = '" + emergencyContact + '\'' + 
			"}";
		}
}