package bd.com.evaly.evalyshop.ui.newsfeed.replies;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;
import bd.com.evaly.evalyshop.models.newsfeed.comment.RepliesItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.UrlUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ReplyViewModel extends ViewModel {

    private ApiRepository apiRepository;
    private PreferenceRepository preferenceRepository;
    private MutableLiveData<List<RepliesItem>> replyListLiveData = new MutableLiveData<>();
    private MutableLiveData<JsonObject> replyCreatedLiveData = new MutableLiveData<>();
    private String postSlug;
    private int commentId;

    public LiveData<List<RepliesItem>> getReplyListLiveData() {
        return replyListLiveData;
    }

    public LiveData<JsonObject> getReplyCreatedLiveData() {
        return replyCreatedLiveData;
    }

    @Inject
    public ReplyViewModel(ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
    }

    public void loadReplies(int page, String postSlug, int commentId) {

        apiRepository.getRepliesList(preferenceRepository.getToken(), postSlug, commentId, page, new ResponseListener<CommonDataResponse<List<CommentItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<CommentItem>> response, int statusCode) {

                if (response.getData().size() > 0)
                    replyListLiveData.setValue(response.getData().get(0).getReplies());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {


            }

                }
        );


    }

    public void createReply(JsonObject body, String postSlug, int commentId) {


        apiRepository.postReply(preferenceRepository.getToken(), postSlug, commentId, body, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                replyCreatedLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });

    }

    public void deleteReply(int id) {

        String url = UrlUtils.BASE_URL_NEWSFEED + "comments/" + id;

        apiRepository.deleteItem(preferenceRepository.getToken(), url, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public String getPostSlug() {
        return postSlug;
    }

    public void setPostSlug(String postSlug) {
        this.postSlug = postSlug;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }
}
