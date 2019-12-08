package bd.com.evaly.evalyshop.activity.chat.invite;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.models.chat.EvalyUserModel;
import retrofit2.Response;

public class InviteViewModel extends ViewModel {
    MutableLiveData<List<EvalyUserModel>> userList = new MutableLiveData<>();
    MutableLiveData<Boolean> hasNext = new MutableLiveData<>();
    MutableLiveData<Boolean> isFailed = new MutableLiveData<>();

    public void findUsers(String query, int page, Activity context){
        AuthApiHelper.searchUser(query, page, new DataFetchingListener<Response<JsonObject>>() {
            @Override
            public void onDataFetched(Response<JsonObject> response) {
                if (response.code() == 201 || response.code() == 200){
                    List<EvalyUserModel> list = new Gson().fromJson(response.body().get("results"), new TypeToken<List<EvalyUserModel>>(){}.getType());
                    userList.setValue(list);
                    if (response.body().get("next") == null ){
                        hasNext.setValue(false);
                    }else {
                        hasNext.setValue(true);
                    }
                }else if (response.code() == 401){
                    AuthApiHelper.refreshToken(context, new DataFetchingListener<Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(Response<JsonObject> response) {
                            if (response.code() == 200 || response.code() == 201){
                                findUsers(query, page, context);
                            }else if (response.code() == 401){
                                AppController.logout(context);
                            }else {
                                isFailed.setValue(false);
                            }
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });
                }else {
                    isFailed.setValue(false);
                }
            }

            @Override
            public void onFailed(int status) {

            }
        });
    }
}
