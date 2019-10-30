package bd.com.evaly.evalyshop.models.apiHelper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.SetPasswordModel;
import bd.com.evaly.evalyshop.models.User;
import bd.com.evaly.evalyshop.rest.ApiClient;
import bd.com.evaly.evalyshop.rest.IApiClient;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.viewmodel.ImageUploadView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthApiHelper {

    public static void checkUpdate(DataFetchingListener<Response<JsonObject>> listener){

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

    public static void setPassword(HashMap<String, String> model, DataFetchingListener<Response<JsonObject>> listener){

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

    public static void register(HashMap<String, String> data, DataFetchingListener<Response<JsonObject>> listener){

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
                if (response.code() == 401){
                    AppController.logout(context);
                }else {
                    listener.onDataFetched(response);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                listener.onFailed(0);
            }
        });

    }


    public static void changeXmppPassword(HashMap<String, String> data, DataFetchingListener<Response<JsonPrimitive>> listener){

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

    public static void addRoster(HashMap<String, String> data, DataFetchingListener<Response<JsonPrimitive>> listener){

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
    public static void sendCustomMessage(HashMap<String, String> data, DataFetchingListener<Response<JsonObject>> listener){

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

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), f);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", f.getName(), requestFile);

        Call<JsonObject> call = iApiClient.imageUpload(CredentialManager.getToken(),"multipart/form-data", body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Logger.d(response.body());
                if (response.code() == 201) {
                    try {
                        if (view != null){
                            String img = response.body().getAsJsonObject("data").get("url").getAsString();
                            String smImg = response.body().getAsJsonObject("data").get("url_sm").getAsString();
                            view.onImageUploadSuccess(img, smImg);
                        }

                    } catch (Exception e) {
                        Logger.d(e.getMessage());
                    }
                }else {
                    if (view != null){
                        view.onImageUploadFailed("Something went wrong, please try again");
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Logger.d(t.getMessage());
                if (view != null){
                    view.onImageUploadFailed("Something went wrong, please try again");
                }
            }
        });
    }

    private static IApiClient getiApiClient() {
        return ApiClient.getClient().create(IApiClient.class);
    }
}
