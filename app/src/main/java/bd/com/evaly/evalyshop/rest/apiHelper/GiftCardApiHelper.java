package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;

public class GiftCardApiHelper extends BaseApiHelper{


    public static void payWithGiftCard(String token, HashMap<String, String> body, ResponseListenerAuth<JsonObject, String> listener) {

        getiApiClient().payWithGiftCard(token, body).enqueue(getResponseCallBackDefault(listener));
    }


    public static void getGiftCard(int page, ResponseListenerAuth<CommonDataResponse<List<GiftCardListItem>>, String> listener){

        getiApiClient().getGiftCardList(page).enqueue(getResponseCallBackDefault(listener));
    }

    public static void placeGiftCardOrder(String token, JsonObject body, ResponseListenerAuth<JsonObject, String> listener){

        getiApiClient().placeGiftCardOrder(token, body).enqueue(getResponseCallBackDefault(listener));
    }

}
