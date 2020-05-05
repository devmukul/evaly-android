package bd.com.evaly.evalyshop.models.express;

import com.google.gson.annotations.SerializedName;

public class ExpressServiceDetailsModel{

	@SerializedName("end_date")
	private String endDate;

	@SerializedName("image")
	private String image;

	@SerializedName("service_type")
	private String serviceType;

	@SerializedName("name")
	private String name;

	@SerializedName("description")
	private String description;

	@SerializedName("banner_image")
	private String bannerImage;

	@SerializedName("allowed_payment_methods")
	private String allowedPaymentMethods;

	@SerializedName("slug")
	private String slug;

	@SerializedName("start_date")
	private String startDate;

	@SerializedName("app_name")
	private String appName;

	@SerializedName("app_logo")
	private String appLogo;

	@SerializedName("app_bg_color")
	private String appBgColor;

	public void setEndDate(String endDate){
		this.endDate = endDate;
	}

	public String getEndDate(){
		return endDate;
	}

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setServiceType(String serviceType){
		this.serviceType = serviceType;
	}

	public String getServiceType(){
		return serviceType;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setBannerImage(String bannerImage){
		this.bannerImage = bannerImage;
	}

	public String getBannerImage(){
		return bannerImage;
	}

	public void setAllowedPaymentMethods(String allowedPaymentMethods){
		this.allowedPaymentMethods = allowedPaymentMethods;
	}

	public String getAllowedPaymentMethods(){
		return allowedPaymentMethods;
	}

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}

	public void setStartDate(String startDate){
		this.startDate = startDate;
	}

	public String getStartDate(){
		return startDate;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppLogo() {
		return appLogo;
	}

	public void setAppLogo(String appLogo) {
		this.appLogo = appLogo;
	}

	public String getAppBgColor() {
		return appBgColor;
	}

	public void setAppBgColor(String appBgColor) {
		this.appBgColor = appBgColor;
	}

	@Override
 	public String toString(){
		return 
			"ExpressServiceDetailsModel{" + 
			"end_date = '" + endDate + '\'' + 
			",image = '" + image + '\'' + 
			",service_type = '" + serviceType + '\'' + 
			",name = '" + name + '\'' + 
			",description = '" + description + '\'' + 
			",banner_image = '" + bannerImage + '\'' + 
			",allowed_payment_methods = '" + allowedPaymentMethods + '\'' + 
			",slug = '" + slug + '\'' + 
			",start_date = '" + startDate + '\'' + 
			"}";
		}
}