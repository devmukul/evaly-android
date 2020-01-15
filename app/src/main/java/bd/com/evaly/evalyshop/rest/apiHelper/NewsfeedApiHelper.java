package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;

public class NewsfeedApiHelper extends BaseApiHelper {

    public static void getNewsfeedPosts(String token, String url, ResponseListenerAuth<JsonObject, String> listener){
        if (token.equals(""))
            token = null;

        getiApiClient().getNewsfeedPosts(token, url).enqueue(getResponseCallBackDefault(listener));
    }


}
