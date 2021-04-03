package bd.com.evaly.evalyshop.models.refundSettlement.request;

import com.google.gson.annotations.SerializedName;

public class Account{

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

	public void setAccountNumber(String accountNumber){
		this.accountNumber = accountNumber;
	}

	public String getAccountNumber(){
		return accountNumber;
	}

	public void setBranchName(String branchName){
		this.branchName = branchName;
	}

	public String getBranchName(){
		return branchName;
	}

	public void setAccountName(String accountName){
		this.accountName = accountName;
	}

	public String getAccountName(){
		return accountName;
	}

	public void setBankName(String bankName){
		this.bankName = bankName;
	}

	public String getBankName(){
		return bankName;
	}

	public void setRoutingNumber(String routingNumber){
		this.routingNumber = routingNumber;
	}

	public String getRoutingNumber(){
		return routingNumber;
	}

	@Override
 	public String toString(){
		return 
			"Account{" + 
			"account_number = '" + accountNumber + '\'' + 
			",branch_name = '" + branchName + '\'' + 
			",account_name = '" + accountName + '\'' + 
			",bank_name = '" + bankName + '\'' + 
			",routing_number = '" + routingNumber + '\'' + 
			"}";
		}
}