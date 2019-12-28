package bd.com.evaly.evalyshop.rest.apiHelper;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;

public class ShopApiHelper extends BaseApiHelper{

    public static void getShopDetailsItem(String token, String url, ResponseListenerAuth<ShopDetailsModel, String> listener){

        getiApiClient().getShopDetailsItems(token, url).enqueue(getResponseCallBackDefault(listener));
    }

}
