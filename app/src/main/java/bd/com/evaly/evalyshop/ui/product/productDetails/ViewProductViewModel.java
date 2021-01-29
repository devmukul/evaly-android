package bd.com.evaly.evalyshop.ui.product.productDetails;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;

import java.util.List;

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

public class ViewProductViewModel extends ViewModel {

    protected MutableLiveData<ProductDetailsModel> productDetailsModel = new MutableLiveData<>();
    protected MutableLiveData<CommonDataResponse<List<ShopItem>>> productsVariantsOfShop = new MutableLiveData<>();
    protected MutableLiveData<ShopDetailsModel> shopDetails = new MutableLiveData<>();
    protected MutableLiveData<JsonObject> ratingSummary = new MutableLiveData<>();
    protected MutableLiveData<CommonDataResponse<List<AvailableShopModel>>> availableShops = new MutableLiveData<>();
    protected MutableLiveData<CommonDataResponse<List<AvailableShopModel>>> availableNearestShops = new MutableLiveData<>();
    protected MutableLiveData<JsonObject> createPostResponse = new MutableLiveData<>();
    private boolean isShop = false;

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
