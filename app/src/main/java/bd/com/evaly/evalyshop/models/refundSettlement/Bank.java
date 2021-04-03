package bd.com.evaly.evalyshop.models.refundSettlement;

import com.google.gson.annotations.SerializedName;

public class Bank{

	@SerializedName("account_number")
	private String accountNumber;

	@SerializedName("branch_name")
	private String branchName;

	@SerializedName("account_name")
	private String accountName;

	@SerializedName("bank_name")
	private String bankName;

	@SerializedName("routing_number")
	private String routingNumber;

	public String getAccountNumber(){
		return accountNumber;
	}

	public String getBranchName(){
		return branchName;
	}

	public String getAccountName(){
		return accountName;
	}

	public String getBankName(){
		return bankName;
	}

	public String getRoutingNumber(){
		return routingNumber;
	}
}