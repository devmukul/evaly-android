package bd.com.evaly.evalyshop.ui.product.productDetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListDao;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.newsfeed.createPost.CreatePostModel;
import bd.com.evaly.evalyshop.models.product.productDetails.AvailableShopModel;
import bd.com.evaly.evalyshop.models.product.productDetails.ProductDetailsModel;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.models.shop.shopItem.ShopItem;
import bd.com.evaly.evalyshop.rest.apiHelper.NewsfeedApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ReviewsApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ShopApiHelper;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ViewProductViewModel extends ViewModel {

    public LiveData<Integer> wishListLiveCount;
    public LiveData<Integer> cartLiveCount;
    protected MutableLiveData<ProductDetailsModel> productDetailsModel = new MutableLiveData<>();
    protected MutableLiveData<CommonDataResponse<List<ShopItem>>> productsVariantsOfShop = new MutableLiveData<>();
    protected MutableLiveData<ShopDetailsModel> shopDetails = new MutableLiveData<>();
    protected MutableLiveData<JsonObject> ratingSummary = new MutableLiveData<>();
    protected MutableLiveData<CommonDataResponse<List<AvailableShopModel>>> availableShops = new MutableLiveData<>();
    protected MutableLiveData<CommonDataResponse<List<AvailableShopModel>>> availableNearestShops = new MutableLiveData<>();
    protected MutableLiveData<JsonObject> createPostResponse = new MutableLiveData<>();
    private boolean isShop = false;
    private CartDao cartDao;
    private WishListDao wishListDao;

    @Inject
    public ViewProductViewModel(CartDao cartDao, WishListDao wishListDao) {
        this.cartDao = cartDao;
        this.wishListDao = wishListDao;
        cartLiveCount = cartDao.getLiveCount();
        wishListLiveCount = wishListDao.getLiveCount();
    }

    public CartDao getCartDao() {
        return cartDao;
    }


    public void deleteBySlugWishList(String slug) {
        Executors.newSingleThreadExecutor().execute(() -> wishListDao.deleteBySlug(slug));
    }

    public void insertWishList(WishListEntity entity) {
        Executors.newSingleThreadExecutor().execute(() -> wishListDao.insert(entity));
    }

    public int checkExistsWishList(String slug){
        return wishListDao.checkExists(slug);
    }

    public void getProductDetails(String slug) {

        ProductApiHelper.getProductDetails(slug, new ResponseListenerAuth<ProductDetailsModel, String>() {
            @Override
            public void onDataFetched(ProductDetailsModel response, int statusCode) {
                productDetailsModel.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }


    public void getVariantsByShop(String shop_slug, String shop_item_slug) {

        ProductApiHelper.getProductVariants(shop_slug, shop_item_slug, new ResponseListenerAuth<CommonDataResponse<List<ShopItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<ShopItem>> response, int statusCode) {
                productsVariantsOfShop.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }


    public void loadRatings(String slug) {

        ReviewsApiHelper.getReviewSummary(CredentialManager.getToken(), slug, isShop, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                ratingSummary.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    loadRatings(slug);

            }
        });
    }

    public void loadShopDetails(String shopSlug, String campaignSlug) {

        ShopApiHelper.getShopDetailsItem(CredentialManager.getToken(), shopSlug, 1, 0, null, campaignSlug, null, null, new ResponseListenerAuth<ShopDetailsModel, String>() {
            @Override
            public void onDataFetched(ShopDetailsModel response, int statusCode) {
                shopDetails.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    loadShopDetails(shopSlug, campaignSlug);
            }
        });
    }


    public void getAvailableShops(int variationID) {

        ProductApiHelper.getAvailableShops(variationID, new ResponseListenerAuth<CommonDataResponse<List<AvailableShopModel>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<AvailableShopModel>> response, int statusCode) {
                availableShops.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }

    public void getNearestAvailableShops(int variationID, double longitude, double latitude) {

        ProductApiHelper.getNearestAvailableShops(variationID, longitude, latitude, new ResponseListenerAuth<CommonDataResponse<List<AvailableShopModel>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<AvailableShopModel>> response, int statusCode) {
                availableNearestShops.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }


    public void createPost(CreatePostModel createPostModel) {
        NewsfeedApiHelper.post(CredentialManager.getToken(), createPostModel, null, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                createPostResponse.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                createPostResponse.setValue(null);
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    createPost(createPostModel);
            }
        });
    }


}
