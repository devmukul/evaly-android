package bd.com.evaly.evalyshop.ui.product.productDetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.product.productDetails.ProductDetailsModel;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;

public class ViewProductViewModel extends ViewModel {

    private MutableLiveData<ProductDetailsModel> productDetailsModel = new MutableLiveData<>();

    public void getProductDetails(String slug){

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

    public LiveData<ProductDetailsModel> observeProductDetails(){
        return productDetailsModel;
    }


}
