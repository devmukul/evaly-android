package bd.com.evaly.evalyshop.models.pay;

import com.google.gson.annotations.SerializedName;

public class BalanceResponse{

	@SerializedName("balance")
	private double balance;

	@SerializedName("holding_balance")
	private double holdingBalance;

	@SerializedName("gift_card_balance")
	private double giftCardBalance;

	@SerializedName("cashback_balance")
	private double cashbackBalance;

	public void setBalance(double balance){
		this.balance = balance;
	}

	public double getBalance(){
		return balance;
	}

	public void setHoldingBalance(double holdingBalance){
		this.holdingBalance = holdingBalance;
	}

	public double getHoldingBalance(){
		return holdingBalance;
	}

	public void setGiftCardBalance(double giftCardBalance){
		this.giftCardBalance = giftCardBalance;
	}

	public double getGiftCardBalance(){
		return giftCardBalance;
	}

	public void setCashbackBalance(double cashbackBalance){
		this.cashbackBalance = cashbackBalance;
	}

	public double getCashbackBalance(){
		return cashbackBalance;
	}

	@Override
 	public String toString(){
		return 
			"BalanceResponse{" + 
			"balance = '" + balance + '\'' + 
			",holding_balance = '" + holdingBalance + '\'' + 
			",gift_card_balance = '" + giftCardBalance + '\'' + 
			",cashback_balance = '" + cashbackBalance + '\'' + 
			"}";
		}
}