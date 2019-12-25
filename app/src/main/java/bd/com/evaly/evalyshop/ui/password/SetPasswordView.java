package bd.com.evaly.evalyshop.ui.password;

public interface SetPasswordView {
    void onOTPEmpty();
    void onPasswordEmpty();
    void onConfirmPasswordEmpty();
    void onPasswordMismatch();
    void onPasswordSetSuccess();
    void onShortPassword();
    void onSpecialCharMiss();
    void onPasswordSetFailed(String msg);
}
