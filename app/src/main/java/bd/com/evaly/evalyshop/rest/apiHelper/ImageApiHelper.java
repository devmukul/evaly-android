package bd.com.evaly.evalyshop.rest.apiHelper;

import android.graphics.Bitmap;

import com.orhanobut.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.image.ImageDataModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageApiHelper extends BaseApiHelper {

    public static void uploadImage(Bitmap imageBitmap, ResponseListenerAuth<CommonDataResponse<ImageDataModel>, String> listener) {

        File f = new File(AppController.getmContext().getCacheDir(), "image.jpg");

//        try {
//            f.createNewFile();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Logger.d(e.getMessage());
//        }


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Logger.d(e.getMessage());
            e.printStackTrace();
        }

        RequestBody requestFile = RequestBody.create(f, MediaType.parse("image/jpg"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", f.getName(), requestFile);

        getiApiClient().imageUploadNew(CredentialManager.getToken(), "multipart/form-data", body).enqueue(getResponseCallBackDefault(listener));

    }

}
