package bd.com.evaly.evalyshop.ui.newsfeed.createPost;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.remote.ApiRepository;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.ResponseViewModel;
import bd.com.evaly.evalyshop.models.image.ImageDataModel;
import bd.com.evaly.evalyshop.models.newsfeed.createPost.CreatePostModel;
import bd.com.evaly.evalyshop.models.newsfeed.newsfeed.NewsfeedPost;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CreatePostViewModel extends ViewModel {

    private ApiRepository apiRepository;
    private MutableLiveData<NewsfeedPost> postMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ResponseViewModel> responseListenerAuthMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ImageDataModel> imageUploadSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> imageUploadFailed = new MutableLiveData<>();


    @Inject
    public CreatePostViewModel(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    public MutableLiveData<ImageDataModel> getImageUploadSuccess() {
        return imageUploadSuccess;
    }

    public MutableLiveData<Boolean> getImageUploadFailed() {
        return imageUploadFailed;
    }

    public LiveData<NewsfeedPost> getPostMutableLiveData() {
        return postMutableLiveData;
    }

    public void setPostData(NewsfeedPost postData) {
        this.postMutableLiveData.setValue(postData);
    }

    public LiveData<ResponseViewModel> getResponseListenerAuthMutableLiveData() {
        return responseListenerAuthMutableLiveData;
    }

    public void createPost(CreatePostModel body, String slug) {

        apiRepository.post(CredentialManager.getToken(), body, slug, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                responseListenerAuthMutableLiveData.setValue(new ResponseViewModel().build().setOnSuccess("true"));

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }


    public void uploadImage(Bitmap bm) {


        apiRepository.uploadImage(bm, new ResponseListenerAuth<CommonDataResponse<ImageDataModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<ImageDataModel> response, int statusCode) {

                imageUploadSuccess.setValue(response.getData());

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                imageUploadFailed.setValue(true);

            }

            @Override
            public void onAuthError(boolean logout) {

                if (!logout)
                    uploadImage(bm);

            }
        });

    }


}
