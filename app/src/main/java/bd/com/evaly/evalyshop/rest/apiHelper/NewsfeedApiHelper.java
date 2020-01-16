package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;

public class NewsfeedApiHelper extends BaseApiHelper {

    public static void getNewsfeedPosts(String token, String url, ResponseListenerAuth<JsonObject, String> listener){
        if (token.equals(""))
            token = null;

        getiApiClient().getNewsfeedPosts(token, url).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getReplies(String token, String postId, String commentId, int page, ResponseListenerAuth<JsonObject, String> listener){
        getiApiClient().getNewsfeedReplies(token, postId, commentId, page).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getComments(String token, String postId,  int page, ResponseListenerAuth<JsonObject, String> listener){
        getiApiClient().getNewsfeedComments(token, postId, page).enqueue(getResponseCallBackDefault(listener));
    }

    public static void postComment(String token, String postId, JsonObject body, ResponseListenerAuth<JsonObject, String> listener){
        getiApiClient().postNewsfeedComment(token, postId, body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void postReply(String token, String postId, String commentId, JsonObject body, ResponseListenerAuth<JsonObject, String> listener){
        getiApiClient().postNewsfeedReply(token, postId, commentId, body).enqueue(getResponseCallBackDefault(listener));
    }

}
