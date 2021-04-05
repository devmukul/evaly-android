package bd.com.evaly.evalyshop.rest.apiHelper;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListPurchasedItem;

public class GiftCardApiHelper extends BaseApiHelper {

    public static void payWithGiftCard(String token, HashMap<String, String> body, ResponseListenerAuth<JsonObject, String> listener) {

        getiApiClient().payWithGiftCard(token, body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getGiftCard(int page, String url, ResponseListenerAuth<CommonDataResponse<List<GiftCardListItem>>, String> listener) {
        url += "gift-cards/custom/list";
        getiApiClient().getGiftCardList(url, page).enqueue(getResponseCallBackDefault(listener));
    }

    public static void placeGiftCardOrder(String token, JsonObject body, String url, ResponseListenerAuth<JsonObject, String> listener) {
        url += "gift-card-orders/place/";
        getiApiClient().placeGiftCardOrder(token, url, body).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getGiftCardDetails(String slug, String url, ResponseListenerAuth<JsonObject, String> listener) {
        url += "gift-cards/retrieve/" + slug;
        getiApiClient().getGiftCardDetails(url).enqueue(getResponseCallBackDefault(listener));
    }

    public static void getPurchasedGiftCardList(String show, int page, String url, ResponseListenerAuth<CommonDataResponse<List<GiftCardListPurchasedItem>>, String> listener) {
        url += "gift-card-orders";
        getiApiClient().getPurchasedGiftCardList(CredentialManager.getToken(), url, show, page).enqueue(getResponseCallBackDefault(listener));
    }

    public static void redeem(String invoiceNo, String url, ResponseListenerAuth<JsonObject, String> listener) {

        HashMap<String, String> body = new HashMap<>();
        body.put("invoice_no", invoiceNo);

        url += "gift-card-orders/gift-code/retrieve";

        getiApiClient().redeemGiftCard(CredentialManager.getToken(), url, body).enqueue(getResponseCallBackDefault(listener));
    }
}
