package bd.com.evaly.evalyshop.models.auth.captcha;

import com.google.gson.annotations.SerializedName;

public class CaptchaRequest {

	@SerializedName("captcha_id")
	private String captchaId;

	@SerializedName("captcha_value")
	private String captchaValue;

	public void setCaptchaId(String captchaId){
		this.captchaId = captchaId;
	}

	public String getCaptchaId(){
		return captchaId;
	}

	public void setCaptchaValue(String captcha){
		this.captchaValue = captcha;
	}

	public String getCaptchaValue(){
		return captchaValue;
	}

	@Override
 	public String toString(){
		return 
			"CaptchaResponse{" + 
			"captcha_id = '" + captchaId + '\'' + 
			",captcha_value = '" + captchaValue + '\'' +
			"}";
		}
}