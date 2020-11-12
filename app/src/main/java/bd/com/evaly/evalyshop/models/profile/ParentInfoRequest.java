package bd.com.evaly.evalyshop.models.profile;

import com.google.gson.annotations.SerializedName;

public class ParentInfoRequest{

	@SerializedName("other_email")
	private String otherEmail;

	@SerializedName("primary_email")
	private String primaryEmail;

	public void setOtherEmail(String otherEmail){
		this.otherEmail = otherEmail;
	}

	public String getOtherEmail(){
		return otherEmail;
	}

	public void setPrimaryEmail(String primaryEmail){
		this.primaryEmail = primaryEmail;
	}

	public String getPrimaryEmail(){
		return primaryEmail;
	}

	@Override
 	public String toString(){
		return 
			"ParentInfoRequest{" + 
			"other_email = '" + otherEmail + '\'' + 
			",primary_email = '" + primaryEmail + '\'' + 
			"}";
		}
}