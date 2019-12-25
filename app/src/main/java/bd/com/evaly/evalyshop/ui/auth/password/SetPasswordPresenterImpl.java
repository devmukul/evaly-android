package bd.com.evaly.evalyshop.ui.auth.password;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.HashMap;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.models.auth.SetPasswordModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.Utils;
import retrofit2.Response;


public class SetPasswordPresenterImpl implements SetPasswordPresenter {

    private Context context;
    private SetPasswordView view;

    public SetPasswordPresenterImpl(Context context, SetPasswordView view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void setPassword(String otp, String password, String confirmPassword, String phoneNumber) {
        if (otp.trim().isEmpty() || otp.trim().length()<5){
            if (view != null){
                view.onOTPEmpty();
            }
        }else if (password.trim().isEmpty()){
            if (view != null){
                view.onPasswordEmpty();
            }
        }else if (confirmPassword.trim().isEmpty()){
            if (view != null){
                view.onConfirmPasswordEmpty();
            }
        }else if (!confirmPassword.equals(password)){
            if (view != null){
                view.onPasswordMismatch();
            }
        }else if (password.trim().length()<8){
            if (view != null){
                view.onShortPassword();
            }
        } else if(!Utils.isStrongPassword(password.trim()).equals("yes")){

            if (view != null){
                view.onPasswordSetFailed(Utils.isStrongPassword(password.trim()));
            }

        }else {
            SetPasswordModel model = new SetPasswordModel(otp, password, phoneNumber);

            HashMap<String, String> data = new HashMap<>();
            data.put("otp_token", otp);
            data.put("password", password);
            data.put("phone_number", phoneNumber);

            AuthApiHelper.setPassword(data, new DataFetchingListener<Response<JsonObject>>() {
                @Override
                public void onDataFetched(Response<JsonObject> response) {
                    if (response.code() == 201){
                        if (view!= null){
                            view.onPasswordSetSuccess();
                        }
                    }else if (response.code() == 200){
                        if (view!= null){
                            view.onPasswordSetFailed("Incorrect OTP");
                        }
                    }else if (response.code() == 429){
                        if (view!= null){
                            view.onPasswordSetFailed("Too many attempts, try again later!");
                        }
                    }else {
                        if (view!= null){
                            view.onPasswordSetFailed(context.getResources().getString(R.string.something_wrong));
                        }
                    }
                }

                @Override
                public void onFailed(int status) {
                    if (view!= null){
                        view.onPasswordSetFailed(context.getResources().getString(R.string.something_wrong));
                    }
                }
            });
        }
    }
}
