package bd.com.evaly.evalyshop.rest.apiHelper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.orhanobut.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.newsfeed.CreatePostModel;
import bd.com.evaly.evalyshop.models.transaction.TransactionItem;
import bd.com.evaly.evalyshop.models.order.OrderIssueModel;
import bd.com.evaly.evalyshop.models.xmpp.RosterItemModel;
import bd.com.evaly.evalyshop.rest.ApiClient;
import bd.com.evaly.evalyshop.rest.IApiClient;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.ui.chat.viewmodel.ImageUploadView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    public static void register(HashMap<String, String> data, DataFetchingListener<Response<JsonObject>> listener) {

        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call = iApiClient.register(data);
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
                listener.onFailed("Error", 0);
            }
        });

    }


    public static void refreshToken(Activity context, DataFetchingListener<Response<JsonObject>> listener) {

        UserDetails userDetails = new UserDetails(context);

        IApiClient iApiClient = getiApiClient();
        HashMap<String, String> data = new HashMap<>();
        data.put("access", userDetails.getToken());
        data.put("refresh", userDetails.getRefreshToken());
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
                    try {
                        userDetails.setToken(token);
                        userDetails.setRefreshToken(refresh);
                    } catch (Exception e) {

                    }
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

    public static void getRosterList(String phone, int page, int limit, DataFetchingListener<Response<List<RosterItemModel>>> listener) {
        IApiClient iApiClient = ApiClient.getXmppClient().create(IApiClient.class);
        Call<List<RosterItemModel>> call = iApiClient.getRosterList(CredentialManager.getToken(), phone, page, limit);
        call.enqueue(new Callback<List<RosterItemModel>>() {
            @Override
            public void onResponse(Call<List<RosterItemModel>> call, Response<List<RosterItemModel>> response) {
                listener.onDataFetched(response);
            }

            @Override
            public void onFailure(Call<List<RosterItemModel>> call, Throwable t) {
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

    public static void getBanners(DataFetchingListener<Response<JsonObject>> listener) {
        IApiClient iApiClient = getiApiClient();
        Call<JsonObject> call = iApiClient.getBanners(CredentialManager.getToken());
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

    public static void updateProductStatus(HashMap<String, String> data, DataFetchingListener<Response<JsonObject>> listener) {

        IApiClient iApiClient = ApiClient.getClient().create(IApiClient.class);
        Call<JsonObject> call = iApiClient.updateProductStatus(CredentialManager.getToken(), data);

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

    public static void uploadImage(final Context context, Bitmap profileImg, ImageUploadView view) {
        IApiClient iApiClient = getiApiClient();

        File f = new File(context.getCacheDir(), "image.jpg");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.d(e.getMessage());
        }

        Bitmap bm = profileImg;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Logger.d(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Logger.d(e.getMessage());
            e.printStackTrace();
        }
        Logger.d("============");

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), f);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", f.getName(), requestFile);

        Call<JsonObject> call = iApiClient.imageUpload(CredentialManager.getToken(), "multipart/form-data", body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Logger.d(response.body());
                if (response.code() == 201) {
                    try {
                        if (view != null) {
                            String img = response.body().getAsJsonObject("data").get("url").getAsString();
                            String smImg = response.body().getAsJsonObject("data").get("url_sm").getAsString();
                            view.onImageUploadSuccess(img, smImg);
                        }

                    } catch (Exception e) {
                        Logger.d(e.getMessage());
                    }
                } else {
                    if (view != null) {
                        view.onImageUploadFailed("Something went wrong, please try again");
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Logger.d(t.getMessage());
                if (view != null) {
                    view.onImageUploadFailed("Something went wrong, please try again");
                }
            }
        });
    }



    // user info pay

    public static void getUserInfoPay(String token, String username, ResponseListenerAuth<JsonObject, String> listener){
        getiApiClient().getUserInfoPay(token, username).enqueue(getResponseCallBackDefault(listener));
    }

    // change password

    public static void changePassword(String token, HashMap<String, String> body, ResponseListenerAuth<JsonObject, String> listener){
        getiApiClient().changePassword(token, body).enqueue(getResponseCallBackDefault(listener));
    }


    // update profile data

    public static void setUserData(String token, HashMap<String, String> body, ResponseListenerAuth<JsonObject, String> listener){
        getiApiClient().setUserData(token, body).enqueue(getResponseCallBackDefault(listener));
    }

    // balance, transaction
    public static void getTransactionHistory(String token, String username, int page, ResponseListenerAuth<CommonDataResponse<List<TransactionItem>>, String> listener){
        getiApiClient().getTransactionHistory(token, username, page).enqueue(getResponseCallBackDefault(listener));
    }

    // forget password

    public static void forgetPassword(String phone, ResponseListenerAuth<JsonObject, String> listener){

        HashMap<String,String> body = new HashMap<>();
        body.put("phone_number", phone);
        getiApiClient().forgetPassword(body).enqueue(getResponseCallBackDefault(listener));
    }


}
