package bd.com.evaly.evalyshop.ui.buynow;

import androidx.hilt.Assisted;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.product.productDetails.AvailableShopModel;
import bd.com.evaly.evalyshop.models.shop.shopItem.ShopItem;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;

public class BuyNowViewModel extends ViewModel {

    protected MutableLiveData<List<ShopItem>> liveList = new MutableLiveData<>();
    protected SingleLiveEvent<Boolean> loadFromModel = new SingleLiveEvent<>();
    private String shopSlug, productSlug;
    private AvailableShopModel shopItem;
    private CartEntity cartItem;

    @Inject
    public BuyNowViewModel(@Assisted SavedStateHandle savedStateHandle) {
        if (savedStateHandle.contains("shopSlug") && savedStateHandle.contains("productSlug")) {
            shopSlug = savedStateHandle.get("shop_slug");
            productSlug = savedStateHandle.get("product_slug");
            getProductDetails();
        } else if (savedStateHandle.contains("cartItem")) {
            shopItem = savedStateHandle.get("shopItem");
            cartItem = savedStateHandle.get("cartItem");
            loadFromModel.setValue(true);
        }
    }

    public AvailableShopModel getShopItem() {
        return shopItem;
    }

    public CartEntity getCartItem() {
        return cartItem;
    }

    public void getProductDetails() {

        ProductApiHelper.getProductVariants(shopSlug, productSlug, new ResponseListenerAuth<CommonDataResponse<List<ShopItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<ShopItem>> response, int statusCode) {

                liveList.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

}
