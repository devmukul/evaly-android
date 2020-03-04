package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.newsfeed.createPost.CreatePostModel;
import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;
import bd.com.evaly.evalyshop.models.newsfeed.newsfeed.NewsfeedPost;
import bd.com.evaly.evalyshop.models.notification.NotificationCount;
import bd.com.evaly.evalyshop.models.notification.NotificationItem;

public class NewsfeedApiHelper extends BaseApiHelper {

    public static void getNewsfeedPosts(String token, String url, ResponseListenerAuth<JsonObject, String> listener){
        if (token.equals(""))
            token = null;

        getiApiClient().getNewsfeedPosts(token, url).enqueue(getResponseCallBackDefault(listener));
    }


    public static void getNewsfeedPostsList(String token, String url, ResponseListenerAuth<CommonDataResponse<List<NewsfeedPost>>, String> listener) {
        if (token.equals(""))
            token = null;

        getiApiClient().getNewsfeedPostsList(token, url).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getPostDetails(String token, String postId, ResponseListenerAuth<JsonObject, String> listener){
        getiApiClient().getNewsfeedPostDetails(token, postId).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getReplies(String token, String postId, int commentId, int page, ResponseListenerAuth<JsonObject, String> listener){
        getiApiClient().getNewsfeedReplies(token, postId, commentId, page).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getRepliesList(String token, String postId, int commentId, int page, ResponseListenerAuth<CommonDataResponse<List<CommentItem>>, String> listener) {
        getiApiClient().getNewsfeedRepliesList(token, postId, commentId, page).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getComments(String token, String postId,  int page, ResponseListenerAuth<JsonObject, String> listener){
        getiApiClient().getNewsfeedComments(token, postId, page).enqueue(getResponseCallBackDefault(listener));
    }


    public static void getCommentList(String token, String postId, int page, ResponseListenerAuth<CommonDataResponse<List<CommentItem>>, String> listener) {
        getiApiClient().getNewsfeedCommentsList(token, postId, page).enqueue(getResponseCallBackDefault(listener));
    }

    public static void postComment(String token, String postId, JsonObject body, ResponseListenerAuth<JsonObject, String> listener){
        getiApiClient().postNewsfeedComment(token, postId, body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void postReply(String token, String postId, int commentId, JsonObject body, ResponseListenerAuth<JsonObject, String> listener){
        getiApiClient().postNewsfeedReply(token, postId, commentId, body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void postLike(String token, String postSlug, boolean dislike, ResponseListenerAuth<JsonObject, String> listener){
        if (!dislike)
            getiApiClient().likeNewsfeedPost(token, postSlug).enqueue(getResponseCallBackDefault(listener));
        else
            getiApiClient().dislikeNewsfeedPost(token, postSlug).enqueue(getResponseCallBackDefault(listener));
    }

    public static void deleteItem(String token, String url, ResponseListenerAuth<JsonObject, String> listener){
        getiApiClient().deleteNewsfeedItem(token, url).enqueue(getResponseCallBackDefault(listener));
    }

    public static void actionPendingPost(String token, String postId, String type, JsonObject body, ResponseListenerAuth<JsonObject, String> listener){

        if (type.equals("delete"))
            getiApiClient().deletePendingNewsfeedPost(token, postId, body).enqueue(getResponseCallBackDefault(listener));
        else
            getiApiClient().approvePendingNewsfeedPost(token, postId, body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getNewsfeedNotification(String token, int page, ResponseListenerAuth<CommonResultResponse<List<NotificationItem>>, String> listener) {
        getiApiClient().getNewsfeedNotification(token, page).enqueue(getResponseCallBackDefault(listener));
    }

    public static void markNotificationAsRead(String token, ResponseListenerAuth<JsonObject, String> listener) {
        getiApiClient().markNewsfeedNotificationAsRead(token).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getNotificationCount(String token, ResponseListenerAuth<NotificationCount, String> listener) {
        getiApiClient().getNewsfeedNotificationCount(token).enqueue(getResponseCallBackDefault(listener));
    }

    public static void post(String token, CreatePostModel body, String postSlug, ResponseListenerAuth<JsonObject, String> listener) {

        if (postSlug == null)
            getiApiClient().createNewsfeedPost(token, body).enqueue(getResponseCallBackDefault(listener));
        else
            getiApiClient().editNewsfeedPost(token, body, postSlug).enqueue(getResponseCallBackDefault(listener));

    }


}
