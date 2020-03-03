package bd.com.evaly.evalyshop.ui.newsfeed.replies;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;
import bd.com.evaly.evalyshop.models.newsfeed.comment.RepliesItem;
import bd.com.evaly.evalyshop.rest.apiHelper.NewsfeedApiHelper;


public class ReplyViewModel extends ViewModel {


    private MutableLiveData<List<RepliesItem>> replyListLiveData = new MutableLiveData<>();
    private MutableLiveData<JsonObject> replyCreatedLiveData = new MutableLiveData<>();


    public LiveData<List<RepliesItem>> getReplyListLiveData() {
        return replyListLiveData;
    }

    public LiveData<JsonObject> getReplyCreatedLiveData() {
        return replyCreatedLiveData;
    }


    public void loadReplies(int page, String postSlug, int commentId) {


        NewsfeedApiHelper.getRepliesList(CredentialManager.getToken(), postSlug, commentId, page, new ResponseListenerAuth<CommonDataResponse<List<CommentItem>>, String>() {
                    @Override
                    public void onDataFetched(CommonDataResponse<List<CommentItem>> response, int statusCode) {

                        if (response.getData().size() > 0)
                            replyListLiveData.setValue(response.getData().get(0).getReplies());
                    }

                    @Override
                    public void onFailed(String errorBody, int errorCode) {


                    }

                    @Override
                    public void onAuthError(boolean logout) {

                    }
                }
        );


    }

    public void createReply(JsonObject body, String postSlug, int commentId) {


        NewsfeedApiHelper.postReply(CredentialManager.getToken(), postSlug, commentId, body, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                replyCreatedLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    createReply(body, postSlug, commentId);

            }
        });

    }


}
