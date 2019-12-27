package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;
import java.util.HashMap;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;

public class GiftCardApiHelper extends BaseApiHelper{


    public static void payWithGiftCard(String token, HashMap<String, String> body, ResponseListenerAuth<JsonObject, String> listener) {

        getiApiClient().payWithGiftCard(token, body).enqueue(getResponseCallBackDefault(listener));
    }



}
