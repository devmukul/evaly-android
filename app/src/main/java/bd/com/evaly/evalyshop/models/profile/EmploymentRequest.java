package bd.com.evaly.evalyshop.models.profile;

import com.google.gson.annotations.SerializedName;

public class EmploymentRequest{

	@SerializedName("occupation")
	private String occupation;

	@SerializedName("organization")
	private String organization;

	public void setOccupation(String occupation){
		this.occupation = occupation;
	}

	public String getOccupation(){
		return occupation;
	}

	public void setOrganization(String organization){
		this.organization = organization;
	}

	public String getOrganization(){
		return organization;
	}

	@Override
 	public String toString(){
		return 
			"EmploymentRequest{" + 
			"occupation = '" + occupation + '\'' + 
			",organization = '" + organization + '\'' + 
			"}";
		}
}