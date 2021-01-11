package bd.com.evaly.evalyshop.models.auth.captcha;

import com.google.gson.annotations.SerializedName;

public class CaptchaResponse {

    @SerializedName("captcha_id")
    private String captchaId;

    @SerializedName("captcha")
    private String captcha;

    public void setCaptchaId(String captchaId) {
        this.captchaId = captchaId;
    }

    public String getCaptchaId() {
        return captchaId;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getCaptcha() {
//        if (captcha != null)
//            return captcha.replaceAll("data:image/png;base64,", "");
        return captcha;
    }

    @Override
    public String toString() {
        return
                "CaptchaResponse{" +
                        "captcha_id = '" + captchaId + '\'' +
                        ",captcha = '" + captcha + '\'' +
                        "}";
    }
}