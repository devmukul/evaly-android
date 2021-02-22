package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.cart.CartHolderModel;

public class CartApiHelper extends BaseApiHelper {

    public static void getCartList(ResponseListenerAuth<CommonDataResponse<CartHolderModel>, String> listener) {
        getiApiClient().getCartList(CredentialManager.getToken()).enqueue(getResponseCallBackDefault(listener));
    }

    public static void syncCartList(CartHolderModel body, ResponseListenerAuth<CommonDataResponse<CartHolderModel>, String> listener) {
        getiApiClient().syncCartList(CredentialManager.getToken(), body).enqueue(getResponseCallBackDefault(listener));
    }

}
