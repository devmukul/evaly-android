package bd.com.evaly.evalyshop.models.refundSettlement.request;

import com.google.gson.annotations.SerializedName;

public class MFSAccountRequest{

	@SerializedName("otp")
	private String otp;

	@SerializedName("request_id")
	private String requestId;

	@SerializedName("account")
	private String account;

	public void setOtp(String otp){
		this.otp = otp;
	}

	public String getOtp(){
		return otp;
	}

	public void setRequestId(String requestId){
		this.requestId = requestId;
	}

	public String getRequestId(){
		return requestId;
	}

	public void setAccount(String account){
		this.account = account;
	}

	public String getAccount(){
		return account;
	}

	@Override
 	public String toString(){
		return 
			"MFSAccountRequest{" + 
			"otp = '" + otp + '\'' + 
			",request_id = '" + requestId + '\'' + 
			",account = '" + account + '\'' + 
			"}";
		}
}