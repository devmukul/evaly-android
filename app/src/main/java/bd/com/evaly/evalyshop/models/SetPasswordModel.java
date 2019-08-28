package bd.com.evaly.evalyshop.models;

public class SetPasswordModel {
    private String otp_token;
    private String password;
    private String phone_number;

    public SetPasswordModel(String otp_token, String password, String phone_number) {
        this.otp_token = otp_token;
        this.password = password;
        this.phone_number = phone_number;
    }
}
