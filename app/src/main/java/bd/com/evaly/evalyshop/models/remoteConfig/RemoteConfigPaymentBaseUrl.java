package bd.com.evaly.evalyshop.models.remoteConfig;

import com.google.gson.annotations.SerializedName;

public class RemoteConfigPaymentBaseUrl{

	@SerializedName("prod_payment_base_url")
	private String prodPaymentBaseUrl;

	@SerializedName("dev_payment_base_url")
	private String devPaymentBaseUrl;

	public void setProdPaymentBaseUrl(String prodPaymentBaseUrl){
		this.prodPaymentBaseUrl = prodPaymentBaseUrl;
	}

	public String getProdPaymentBaseUrl(){
		return prodPaymentBaseUrl;
	}

	public void setDevPaymentBaseUrl(String devPaymentBaseUrl){
		this.devPaymentBaseUrl = devPaymentBaseUrl;
	}

	public String getDevPaymentBaseUrl(){
		return devPaymentBaseUrl;
	}

	@Override
 	public String toString(){
		return 
			"RemoteConfigPaymentBaseUrl{" + 
			"prod_payment_base_url = '" + prodPaymentBaseUrl + '\'' + 
			",dev_payment_base_url = '" + devPaymentBaseUrl + '\'' + 
			"}";
		}
}