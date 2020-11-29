package bd.com.evaly.evalyshop.ui.auth.password;

public interface SetPasswordPresenter {
    void setPassword(String otp, String password, String confirmPassword, String phoneNUmber, String requestId);
}
