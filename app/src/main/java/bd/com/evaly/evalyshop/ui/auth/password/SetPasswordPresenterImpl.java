package bd.com.evaly.evalyshop.ui.auth.password;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.HashMap;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.Utils;


public class SetPasswordPresenterImpl implements SetPasswordPresenter {

    private Context context;
    private SetPasswordView view;

    public SetPasswordPresenterImpl(Context context, SetPasswordView view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void setPassword(String otp, String password, String confirmPassword, String phoneNumber, String requestId) {
        if (otp.trim().isEmpty() || otp.trim().length() < 5) {
            if (view != null) {
                view.onOTPEmpty();
            }
        } else if (password.trim().isEmpty()) {
            if (view != null) {
                view.onPasswordEmpty();
            }
        } else if (confirmPassword.trim().isEmpty()) {
            if (view != null) {
                view.onConfirmPasswordEmpty();
            }
        } else if (!confirmPassword.equals(password)) {
            if (view != null) {
                view.onPasswordMismatch();
            }
        } else if (password.trim().length() < 8) {
            if (view != null) {
                view.onShortPassword();
            }
        } else if (!Utils.isStrongPassword(password.trim()).equals("yes")) {

            if (view != null) {
                view.onPasswordSetFailed(Utils.isStrongPassword(password.trim()));
            }

        } else {

            HashMap<String, String> data = new HashMap<>();
            data.put("otp", otp);
            data.put("password", password);
            data.put("phone_number", phoneNumber);
            data.put("request_id", requestId);

            AuthApiHelper.setPassword(data, new ResponseListenerAuth<JsonObject, String>() {
                @Override
                public void onDataFetched(JsonObject response, int statusCode) {
                    if (view != null)
                        view.onPasswordSetSuccess();
                }

                @Override
                public void onFailed(String errorBody, int errorCode) {
                    if (errorBody != null && !errorBody.equals(""))
                        if (view != null)
                            view.onPasswordSetFailed(errorBody);
                        else
                            view.onPasswordSetFailed(context.getResources().getString(R.string.something_wrong));

                }

                @Override
                public void onAuthError(boolean logout) {

                }
            });
        }
    }
}
