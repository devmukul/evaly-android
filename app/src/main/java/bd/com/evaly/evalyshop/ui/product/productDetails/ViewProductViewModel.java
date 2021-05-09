package bd.com.evaly.evalyshop.ui.product.productDetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListDao;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListEntity;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.newsfeed.createPost.CreatePostModel;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.product.productDetails.AvailableShopResponse;
import bd.com.evaly.evalyshop.models.product.productDetails.ProductDetailsModel;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.models.shop.shopItem.ShopItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ViewProductViewModel extends ViewModel {

    private PreferenceRepository preferenceRepository;
    public LiveData<Integer> wishListLiveCount;
    public LiveData<Integer> cartLiveCount;
    protected MutableLiveData<List<ProductItem>> relatedProductLiveList = new MutableLiveData<>();
    protected MutableLiveData<ProductDetailsModel> productDetailsModel = new MutableLiveData<>();
    protected MutableLiveData<CommonDataResponse<List<ShopItem>>> productsVariantsOfShop = new MutableLiveData<>();
    protected MutableLiveData<ShopDetailsModel> shopDetails = new MutableLiveData<>();
    protected MutableLiveData<JsonObject> ratingSummary = new MutableLiveData<>();
    protected MutableLiveData<CommonDataResponse<List<AvailableShopResponse>>> availableShops = new MutableLiveData<>();
    protected MutableLiveData<CommonDataResponse<List<AvailableShopResponse>>> availableNearestShops = new MutableLiveData<>();
    protected MutableLiveData<JsonObject> createPostResponse = new MutableLiveData<>();
    private boolean isShop = false;
    private CartDao cartDao;
    private WishListDao wishListDao;
    private ApiRepository apiRepository;

    @Inject
    public ViewProductViewModel(CartDao cartDao, WishListDao wishListDao, ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        this.cartDao = cartDao;
        this.wishListDao = wishListDao;
        cartLiveCount = cartDao.getLiveCount();
        wishListLiveCount = wishListDao.getLiveCount();
        this.apiRepository = apiRepository;
        this.preferenceRepository = preferenceRepository;
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

    public int checkExistsWishList(String slug) {
        return wishListDao.checkExists(slug);
    }

    public void getProductDetails(String slug) {

        apiRepository.getProductDetails(slug, new ResponseListener<ProductDetailsModel, String>() {
            @Override
            public void onDataFetched(ProductDetailsModel response, int statusCode) {
                productDetailsModel.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }


    public void getVariantsByShop(String shop_slug, String shop_item_slug) {

        apiRepository.getProductVariants(shop_slug, shop_item_slug, new ResponseListener<CommonDataResponse<List<ShopItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<ShopItem>> response, int statusCode) {
                productsVariantsOfShop.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }


    public void loadRatings(String slug) {

        apiRepository.getReviewSummary(preferenceRepository.getToken(), slug, isShop, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                ratingSummary.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void loadShopDetails(String shopSlug, String campaignSlug) {

        apiRepository.getShopDetailsItem(preferenceRepository.getToken(), shopSlug, 1, 0, null, campaignSlug, null, null, new ResponseListener<ShopDetailsModel, String>() {
            @Override
            public void onDataFetched(ShopDetailsModel response, int statusCode) {
                shopDetails.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }


    public void getAvailableShops(int variationID) {

        apiRepository.getAvailableShops(variationID, new ResponseListener<CommonDataResponse<List<AvailableShopResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<AvailableShopResponse>> response, int statusCode) {
                availableShops.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void getNearestAvailableShops(int variationID, double longitude, double latitude) {

        apiRepository.getNearestAvailableShops(variationID, longitude, latitude, new ResponseListener<CommonDataResponse<List<AvailableShopResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<AvailableShopResponse>> response, int statusCode) {
                availableNearestShops.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

    public void createPost(CreatePostModel createPostModel) {
        apiRepository.post(preferenceRepository.getToken(), createPostModel, null, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                createPostResponse.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                createPostResponse.setValue(null);
            }

        });
    }

    public void getRelatedProducts(String category){
        apiRepository.getCategoryBrandProducts(1, category, null, new ResponseListener<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {
                relatedProductLiveList.setValue(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }
        });
    }

}
