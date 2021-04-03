package bd.com.evaly.evalyshop.models.refundSettlement;

import com.google.gson.annotations.SerializedName;

public class OtpResponse{

	@SerializedName("test_otp")
	private int testOtp;

	@SerializedName("request_id")
	private String requestId;

	@SerializedName("username")
	private String username;

	public void setTestOtp(int testOtp){
		this.testOtp = testOtp;
	}

	public int getTestOtp(){
		return testOtp;
	}

	public void setRequestId(String requestId){
		this.requestId = requestId;
	}

	public String getRequestId(){
		return requestId;
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
			"OtpResponse{" + 
			"test_otp = '" + testOtp + '\'' + 
			",request_id = '" + requestId + '\'' + 
			",username = '" + username + '\'' + 
			"}";
		}
}