package bd.com.evaly.evalyshop.ui.buynow;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.product.productDetails.AvailableShopModel;
import bd.com.evaly.evalyshop.models.shop.shopItem.ShopItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BuyNowViewModel extends ViewModel {

    protected MutableLiveData<List<ShopItem>> liveList = new MutableLiveData<>();
    protected SingleLiveEvent<Boolean> loadFromModel = new SingleLiveEvent<>();
    private String shopSlug, productSlug;
    private AvailableShopModel shopItem;
    private CartEntity cartItem;
    private CartDao cartDao;
    private ApiRepository apiRepository;

    @Inject
    public BuyNowViewModel(CartDao cartDao, SavedStateHandle savedStateHandle, ApiRepository apiRepository) {
        if (savedStateHandle.contains("shopSlug") && savedStateHandle.contains("productSlug")) {
            shopSlug = savedStateHandle.get("shopSlug");
            productSlug = savedStateHandle.get("productSlug");
            getProductDetails();
        } else if (savedStateHandle.contains("cartItem")) {
            shopItem = savedStateHandle.get("shopItem");
            cartItem = savedStateHandle.get("cartItem");
            loadFromModel.setValue(true);
        }
        this.cartDao = cartDao;
        this.apiRepository = apiRepository;
    }

    public void insertCartEntity(CartEntity cartEntity){
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CartEntity> dbItem = cartDao.checkExistsEntity(cartEntity.getProductID());
            if (dbItem.size() == 0)
                cartDao.insert(cartEntity);
            else
                cartDao.updateQuantity(cartEntity.getProductID(), dbItem.get(0).getQuantity() + 1);
        });
    }

    public AvailableShopModel getShopItem() {
        return shopItem;
    }

    public CartEntity getCartItem() {
        return cartItem;
    }

    public void getProductDetails() {

        apiRepository.getProductVariants(shopSlug, productSlug, new ResponseListenerAuth<CommonDataResponse<List<ShopItem>>, String>() {
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
