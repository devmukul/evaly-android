package bd.com.evaly.evalyshop.models.campaign.carousel;

import com.google.gson.annotations.SerializedName;

public class BannersItem{

	@SerializedName("banner_id")
	private String bannerId;

	@SerializedName("banner_image")
	private String bannerImage;

	@SerializedName("banner_sub_text_1")
	private String bannerSubText1;

	@SerializedName("banner_sub_text_3")
	private String bannerSubText3;

	@SerializedName("redirect_url")
	private String redirectUrl;

	@SerializedName("banner_sub_text_2")
	private String bannerSubText2;

	public void setBannerId(String bannerId){
		this.bannerId = bannerId;
	}

	public String getBannerId(){
		return bannerId;
	}

	public void setBannerImage(String bannerImage){
		this.bannerImage = bannerImage;
	}

	public String getBannerImage(){
		return bannerImage;
	}

	public void setBannerSubText1(String bannerSubText1){
		this.bannerSubText1 = bannerSubText1;
	}

	public String getBannerSubText1(){
		return bannerSubText1;
	}

	public void setBannerSubText3(String bannerSubText3){
		this.bannerSubText3 = bannerSubText3;
	}

	public String getBannerSubText3(){
		return bannerSubText3;
	}

	public void setRedirectUrl(String redirectUrl){
		this.redirectUrl = redirectUrl;
	}

	public String getRedirectUrl(){
		return redirectUrl;
	}

	public void setBannerSubText2(String bannerSubText2){
		this.bannerSubText2 = bannerSubText2;
	}

	public String getBannerSubText2(){
		return bannerSubText2;
	}

	@Override
 	public String toString(){
		return 
			"BannersItem{" + 
			"banner_id = '" + bannerId + '\'' + 
			",banner_image = '" + bannerImage + '\'' + 
			",banner_sub_text_1 = '" + bannerSubText1 + '\'' + 
			",banner_sub_text_3 = '" + bannerSubText3 + '\'' + 
			",redirect_url = '" + redirectUrl + '\'' + 
			",banner_sub_text_2 = '" + bannerSubText2 + '\'' + 
			"}";
		}
}