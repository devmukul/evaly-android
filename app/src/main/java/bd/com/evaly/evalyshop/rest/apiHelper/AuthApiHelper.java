package bd.com.evaly.evalyshop.rest.apiHelper;

import android.app.Activity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.auth.LoginBody;
import bd.com.evaly.evalyshop.models.auth.LoginResponse;
import bd.com.evaly.evalyshop.models.auth.RegisterBody;
import bd.com.evaly.evalyshop.models.auth.RegisterResponse;
import bd.com.evaly.evalyshop.models.auth.SetPasswordBody;
import bd.com.evaly.evalyshop.models.auth.SetPasswordResponse;
import bd.com.evaly.evalyshop.models.newsfeed.createPost.CreatePostModel;
import bd.com.evaly.evalyshop.models.order.OrderIssueModel;
import bd.com.evaly.evalyshop.models.profile.AddressRequest;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.models.profile.UserInfoResponse;
import bd.com.evaly.evalyshop.models.transaction.TransactionItem;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.rest.ApiClient;
import bd.com.evaly.evalyshop.rest.IApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthApiHelper extends BaseApiHelper {


    public static void checkUpdate(DataFetchingListener<Response<JsonObject>> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call = iApiClient.checkUpdate();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.onDataFetched(response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                listener.onFailed(0);
            }
        });

    }

    public static void setPassword(HashMap<String, String> model, DataFetchingListener<Response<JsonObject>> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call = iApiClient.setPassword(model);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.onDataFetched(response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                listener.onFailed(0);
            }
        });

    }

    public static void register(HashMap<String, String> data, ResponseListener<JsonObject, String> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call = iApiClient.register(data);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.onDataFetched(response.body(), response.code());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (t instanceof IOException)
                    listener.onFailed(AppController.getmContext().getString(R.string.networkError), 0);
                else
                    listener.onFailed("Error occurred, please try again later", 0);
            }
        });

    }


    public static void login(HashMap<String, String> data, ResponseListener<JsonObject, String> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call = iApiClient.login(data);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.onDataFetched(response.body(), response.code());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (t instanceof IOException)
                    listener.onFailed(AppController.getmContext().getString(R.string.networkError), 0);
                else
                    listener.onFailed("Error occurred, please try again later", 0);
            }
        });

    }


    public static void refreshToken(Activity context, DataFetchingListener<Response<JsonObject>> listener) {


        IApiClient iApiClient = getiApiClient();
        HashMap<String, String> data = new HashMap<>();
        data.put("access", CredentialManager.getTokenNoBearer());
        data.put("refresh", CredentialManager.getRefreshToken());
        Call<JsonObject> call = iApiClient.refreshToken(data);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 401) {
                    AppController.logout(context);
                } else if (response.code() == 200 || response.code() == 201) {
                    String token = response.body().get("access").getAsString();
                    String refresh = response.body().get("refresh").getAsString();
                    CredentialManager.saveToken(token);
                    CredentialManager.saveRefreshToken(refresh);
                    listener.onDataFetched(response);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                listener.onFailed(0);
            }
        });

    }


    public static void changeXmppPassword(HashMap<String, String> data, DataFetchingListener<Response<JsonPrimitive>> listener) {

        IApiClient iApiClient = ApiClient.getXmppClient().create(IApiClient.class);
        Call<JsonPrimitive> call = iApiClient.changeXmppPassword(data);
        call.enqueue(new Callback<JsonPrimitive>() {
            @Override
            public void onResponse(Call<JsonPrimitive> call, Response<JsonPrimitive> response) {
                listener.onDataFetched(response);
            }

            @Override
            public void onFailure(Call<JsonPrimitive> call, Throwable t) {
                Logger.d(t.getMessage());
                listener.onFailed(0);
            }
        });

    }

    public static void registerXMPP(HashMap<String, String> data, DataFetchingListener<Response<JsonObject>> listener) {

        IApiClient iApiClient = ApiClient.getXmppClient().create(IApiClient.class);
        Call<JsonObject> call = iApiClient.registerXmpp(CredentialManager.getToken(), data);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 201) {
                    CredentialManager.saveUserRegistered(true);
                    listener.onDataFetched(response);
                } else if (response.code() == 200) {
                    CredentialManager.saveUserRegistered(true);
                    listener.onDataFetched(response);
                } else
                    listener.onFailed(0);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Logger.d(t.getMessage());
                listener.onFailed(0);
            }
        });

    }

    public static void addRoster(HashMap<String, String> data, DataFetchingListener<Response<JsonPrimitive>> listener) {

        IApiClient iApiClient = ApiClient.getXmppClient().create(IApiClient.class);
        Call<JsonPrimitive> call = iApiClient.addRoster(data);
        call.enqueue(new Callback<JsonPrimitive>() {
            @Override
            public void onResponse(Call<JsonPrimitive> call, Response<JsonPrimitive> response) {
                listener.onDataFetched(response);
            }

            @Override
            public void onFailure(Call<JsonPrimitive> call, Throwable t) {
                Logger.d(t.getMessage());
                listener.onFailed(0);
            }
        });

    }

    public static void getInvitationList(String phone, DataFetchingListener<Response<JsonArray>> listener) {
        IApiClient iApiClient = ApiClient.getXmppClient().create(IApiClient.class);
        Call<JsonArray> call = iApiClient.getInvitationList(CredentialManager.getToken(), phone);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                listener.onDataFetched(response);
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Logger.d(t.getMessage());
                listener.onFailed(0);
            }
        });
    }


    public static void sendCustomMessage(HashMap<String, String> data, DataFetchingListener<Response<JsonObject>> listener) {

        IApiClient iApiClient = ApiClient.getClient().create(IApiClient.class);
        Call<JsonObject> call = iApiClient.sendCustomMessage(CredentialManager.getToken(), data);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.onDataFetched(response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Logger.d(t.getMessage());
                listener.onFailed(0);
            }
        });

    }

    public static void createPost(HashMap<String, CreatePostModel> data, DataFetchingListener<Response<JsonObject>> listener) {
        Logger.d("{}{}{}{}{}{}{}");
        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call = iApiClient.createPost(CredentialManager.getToken(), data);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Logger.d(response.body());
                listener.onDataFetched(response);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Logger.d(t.getMessage());
                listener.onFailed(0);
            }
        });

    }

    public static void submitIssue(OrderIssueModel model, String invoice, DataFetchingListener<Response<JsonObject>> listener) {
        Logger.d("{}{}{}{}{}{}{}       " + invoice);
        IApiClient iApiClient = getiApiClient();
        HashMap<String, OrderIssueModel> data = new HashMap<>();
        data.put("order_issue", model);
        Call<JsonObject> call = iApiClient.submitIssue(CredentialManager.getToken(), invoice, data);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Logger.d(response.body());
                listener.onDataFetched(response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Logger.d(t.getMessage());
                listener.onFailed(0);
            }
        });
    }

    public static void replyIssue(String reply, String id, DataFetchingListener<Response<JsonObject>> listener) {
        IApiClient iApiClient = getiApiClient();
        HashMap<String, String> data = new HashMap<>();
        data.put("body", reply);
        HashMap<String, HashMap> body = new HashMap<>();
        body.put("issue_reply", data);
        Call<JsonObject> call = iApiClient.replyIssue(CredentialManager.getToken(), id, body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Logger.d(response.body());
                listener.onDataFetched(response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Logger.d(t.getMessage());
                listener.onFailed(0);
            }
        });
    }

    public static void getIssueList(String invoice, DataFetchingListener<Response<JsonObject>> listener) {
        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call = iApiClient.getIssueList(CredentialManager.getToken(), invoice);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Logger.d(response.body());
                listener.onDataFetched(response);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Logger.d(t.getMessage());
                listener.onFailed(0);
            }
        });
    }


    public static void searchUser(String search, int page, DataFetchingListener<Response<JsonObject>> listener) {

        IApiClient iApiClient = getiApiClient();
        if (call != null) {
            call.cancel();
        }
        call = iApiClient.searchEvalyUsers(CredentialManager.getToken(), search, page);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                listener.onDataFetched(response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                listener.onFailed(0);
            }
        });

    }

    // user info pay

    public static void getUserInfoPay(String token, String username, ResponseListenerAuth<JsonObject, String> listener) {
        getiApiClient().getUserInfoPay(token, username).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getUserProfile(String token, ResponseListenerAuth<CommonDataResponse<UserModel>, String> listener) {
        getiApiClient().getUserProfile(token).enqueue(getResponseCallBackDefault(listener));
    }

    public static void withdrawRefundRequest(String invoice, ResponseListenerAuth<CommonDataResponse, String> listener) {
        getiApiClient().withdrawRefundRequest(CredentialManager.getToken(), invoice).enqueue(getResponseCallBackDefault(listener));
    }

    // change password

    public static void changePassword(String token, HashMap<String, String> body, ResponseListenerAuth<JsonObject, String> listener) {
        getiApiClient().changePassword(token, body).enqueue(getResponseCallBackDefault(listener));
    }


    // update profile data

    public static void setUserData(String token, HashMap<String, String> body, ResponseListenerAuth<CommonDataResponse<UserModel>, String> listener) {
        getiApiClient().setUserData(token, body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void setUserData(String token, JsonObject body, ResponseListenerAuth<CommonDataResponse<UserModel>, String> listener) {
        getiApiClient().setUserData(token, body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void addUserData(String token, HashMap<String, String> body, ResponseListenerAuth<CommonDataResponse<UserInfoResponse>, String> listener) {
        getiApiClient().addUserData(token, body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getUserInfo(ResponseListenerAuth<CommonDataResponse<UserInfoResponse>, String> listener) {
        getiApiClient().getUserInfo(CredentialManager.getToken()).enqueue(getResponseCallBackDefault(listener));
    }

    public static void setUserDataToXmpp(HashMap<String, String> body, ResponseListenerAuth<JsonObject, String> listener) {
        IApiClient iApiClient = ApiClient.getXmppClient().create(IApiClient.class);
        iApiClient.setUserDataToXmpp(body).enqueue(getResponseCallBackDefault(listener));
    }

    // balance, transaction
    public static void getTransactionHistory(String token, String username, int page, ResponseListenerAuth<CommonDataResponse<List<TransactionItem>>, String> listener) {
        getiApiClient().getTransactionHistory(token, username, page).enqueue(getResponseCallBackDefault(listener));
    }

    // forget password

    public static void forgetPassword(String phone, ResponseListenerAuth<JsonObject, String> listener) {

        HashMap<String, String> body = new HashMap<>();
        body.put("phone_number", phone);
        getiApiClient().forgetPassword(body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getUserAddress(ResponseListenerAuth<CommonDataResponse<List<AddressResponse>>, String> listener) {
        getiApiClient().getAddressList(CredentialManager.getToken()).enqueue(getResponseCallBackDefault(listener));
    }

    public static void addAddress(AddressRequest body, ResponseListenerAuth<CommonDataResponse<AddressResponse>, String> listener) {
        getiApiClient().addAddress(CredentialManager.getToken(), body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void removeAddress(int id, ResponseListenerAuth<CommonDataResponse, String> listener) {
        getiApiClient().removeAddress(CredentialManager.getToken(), id).enqueue(getResponseCallBackDefault(listener));
    }

    // auth 2.0

    public static void authLogin(LoginBody body, ResponseListenerAuth<LoginResponse, String> listener) {
        getiApiClient().authLogin(body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void authRegister(RegisterBody body, ResponseListenerAuth<RegisterResponse, String> listener) {
        getiApiClient().authRegister(body).enqueue(getResponseCallBackDefault(listener));
    }


    public static void authSetPassword(SetPasswordBody body, ResponseListenerAuth<SetPasswordResponse, String> listener) {
        getiApiClient().authSetPassword(body).enqueue(getResponseCallBackDefault(listener));
    }


    public static void updateProductStatus(HashMap<String, String> data, ResponseListenerAuth<JsonObject, String> listener) {
        getiApiClient().updateProductStatus(CredentialManager.getToken(), data).enqueue(getResponseCallBackDefault(listener));
    }


}
