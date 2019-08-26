package bd.com.evaly.evalyshop.activity.password;

import android.content.Context;

import com.google.gson.JsonObject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.models.SetPasswordModel;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
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
        }else {
            SetPasswordModel model = new SetPasswordModel(otp, password, phoneNumber);
            AuthApiHelper.setPassword(model, new DataFetchingListener<Response<JsonObject>>() {
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
