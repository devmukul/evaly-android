package bd.com.evaly.evalyshop.models.refundSettlement.request;

import com.google.gson.annotations.SerializedName;

public class BankAccountRequest{

	@SerializedName("otp")
	private String otp;

	@SerializedName("request_id")
	private String requestId;

	@SerializedName("account")
	private Account account;

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

	public void setAccount(Account account){
		this.account = account;
	}

	public Account getAccount(){
		return account;
	}

	@Override
 	public String toString(){
		return 
			"BankAccountRequest{" + 
			"otp = '" + otp + '\'' + 
			",request_id = '" + requestId + '\'' + 
			",account = '" + account + '\'' + 
			"}";
		}
}