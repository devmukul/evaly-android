package bd.com.evaly.evalyshop.models.refundSettlement;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RefundSettlementResponse implements Serializable {

	@SerializedName("bank")
	private Bank bank;

	@SerializedName("bkash")
	private String bkash;

	@SerializedName("nagad")
	private String nagad;

	@SerializedName("username")
	private String username;

	public Bank getBank(){
		return bank;
	}

	public String getBkash(){
		return bkash;
	}

	public String getNagad(){
		return nagad;
	}

	public String getUsername(){
		return username;
	}
}