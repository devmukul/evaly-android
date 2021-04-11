package bd.com.evaly.evalyshop.models.remoteConfig;

import com.google.gson.annotations.SerializedName;

public class RemoteConfigBaseUrls{

	@SerializedName("dev_balance_url")
	private String devBalanceUrl;

	@SerializedName("prod_balance_url")
	private String prodBalanceUrl;

	@SerializedName("prod_transection_history_url")
	private String prodTransectionHistoryUrl;

	@SerializedName("prod_gift_card_base_url")
	private String prodGiftCardBaseUrl;

	@SerializedName("prod_cashback_claim_url")
	private String prodCashbackClaimUrl;

	@SerializedName("dev_gift_card_base_url")
	private String devGiftCardBaseUrl;

	@SerializedName("dev_transection_history_url")
	private String devTransectionHistoryUrl;

	@SerializedName("dev_cashback_claim_url")
	private String devCashbackClaimUrl;

	public void setDevBalanceUrl(String devBalanceUrl){
		this.devBalanceUrl = devBalanceUrl;
	}

	public String getDevBalanceUrl(){
		return devBalanceUrl;
	}

	public void setProdBalanceUrl(String prodBalanceUrl){
		this.prodBalanceUrl = prodBalanceUrl;
	}

	public String getProdBalanceUrl(){
		return prodBalanceUrl;
	}

	public void setProdTransectionHistoryUrl(String prodTransectionHistoryUrl){
		this.prodTransectionHistoryUrl = prodTransectionHistoryUrl;
	}

	public String getProdTransectionHistoryUrl(){
		return prodTransectionHistoryUrl;
	}

	public void setProdGiftCardBaseUrl(String prodGiftCardBaseUrl){
		this.prodGiftCardBaseUrl = prodGiftCardBaseUrl;
	}

	public String getProdGiftCardBaseUrl(){
		return prodGiftCardBaseUrl;
	}

	public void setProdCashbackClaimUrl(String prodCashbackClaimUrl){
		this.prodCashbackClaimUrl = prodCashbackClaimUrl;
	}

	public String getProdCashbackClaimUrl(){
		return prodCashbackClaimUrl;
	}

	public void setDevGiftCardBaseUrl(String devGiftCardBaseUrl){
		this.devGiftCardBaseUrl = devGiftCardBaseUrl;
	}

	public String getDevGiftCardBaseUrl(){
		return devGiftCardBaseUrl;
	}

	public void setDevTransectionHistoryUrl(String devTransectionHistoryUrl){
		this.devTransectionHistoryUrl = devTransectionHistoryUrl;
	}

	public String getDevTransectionHistoryUrl(){
		return devTransectionHistoryUrl;
	}

	public void setDevCashbackClaimUrl(String devCashbackClaimUrl){
		this.devCashbackClaimUrl = devCashbackClaimUrl;
	}

	public String getDevCashbackClaimUrl(){
		return devCashbackClaimUrl;
	}

	@Override
 	public String toString(){
		return 
			"RemoteConfigBaseUrls{" + 
			"dev_balance_url = '" + devBalanceUrl + '\'' + 
			",prod_balance_url = '" + prodBalanceUrl + '\'' + 
			",prod_transection_history_url = '" + prodTransectionHistoryUrl + '\'' + 
			",prod_gift_card_base_url = '" + prodGiftCardBaseUrl + '\'' + 
			",prod_cashback_claim_url = '" + prodCashbackClaimUrl + '\'' + 
			",dev_gift_card_base_url = '" + devGiftCardBaseUrl + '\'' + 
			",dev_transection_history_url = '" + devTransectionHistoryUrl + '\'' + 
			",dev_cashback_claim_url = '" + devCashbackClaimUrl + '\'' + 
			"}";
		}
}