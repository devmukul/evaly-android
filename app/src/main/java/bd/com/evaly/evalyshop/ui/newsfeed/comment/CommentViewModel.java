package bd.com.evaly.evalyshop.ui.newsfeed.comment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.network.NetworkState;
import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;
import bd.com.evaly.evalyshop.models.newsfeed.newsfeed.NewsfeedPost;
import bd.com.evaly.evalyshop.rest.apiHelper.NewsfeedApiHelper;
import bd.com.evaly.evalyshop.util.UrlUtils;

public class CommentViewModel extends ViewModel {


    private LiveData networkState;
    private MutableLiveData<NewsfeedPost> postMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<CommentItem>> commentItemMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<JsonObject> commentCreatedLiveData = new MutableLiveData<>();

    public MutableLiveData<JsonObject> getCommentCreatedLiveData() {
        return commentCreatedLiveData;
    }


    public LiveData<Integer> getTotalCount() {
        return totalCount;
    }

    private MutableLiveData<Integer> totalCount = new MutableLiveData<>();

    public LiveData<List<CommentItem>> getCommentLiveData() {
        return commentItemMutableLiveData;
    }


    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<NewsfeedPost> getPostData() {

        return postMutableLiveData;
    }

    public void setPostData(NewsfeedPost model) {
        postMutableLiveData.setValue(model);
    }

    public void loadComments(int page, String postSlug) {
        NewsfeedApiHelper.getCommentList(CredentialManager.getToken(), postSlug, page, new ResponseListenerAuth<CommonDataResponse<List<CommentItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CommentItem>> response, int statusCode) {
                commentItemMutableLiveData.setValue(response.getData());
                totalCount.setValue(response.getCount());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    public void createComment(JsonObject body, String slug) {

        NewsfeedApiHelper.postComment(CredentialManager.getToken(), slug, body, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                commentCreatedLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    createComment(body, slug);
            }
        });

    }

    public void deleteComment(int commentId){

        String url = UrlUtils.BASE_URL_NEWSFEED + "comments/" + commentId;

        NewsfeedApiHelper.deleteItem(CredentialManager.getToken(), url, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

}
