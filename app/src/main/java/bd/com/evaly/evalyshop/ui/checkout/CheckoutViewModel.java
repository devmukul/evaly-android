package bd.com.evaly.evalyshop.ui.checkout;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.image.ImageDataModel;
import bd.com.evaly.evalyshop.models.order.AttachmentCheckResponse;
import bd.com.evaly.evalyshop.models.order.placeOrder.PlaceOrderItem;
import bd.com.evaly.evalyshop.rest.apiHelper.ImageApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.disposables.CompositeDisposable;

@HiltViewModel
public class CheckoutViewModel extends ViewModel {

    public SingleLiveEvent<Integer> imagePicker = new SingleLiveEvent<>();
    protected LiveData<List<CartEntity>> liveList = new MutableLiveData<>();
    protected SingleLiveEvent<Boolean> errorOrder = new SingleLiveEvent<>();
    protected SingleLiveEvent<Boolean> hideDialog = new SingleLiveEvent<>();
    protected MutableLiveData<Double> deliveryChargeLiveData = new MutableLiveData<>();
    protected MutableLiveData<List<AttachmentCheckResponse>> attachmentCheckLiveData = new MutableLiveData<>();
    protected MutableLiveData<CommonDataResponse<List<JsonObject>>> orderPlacedLiveData = new MutableLiveData<>();
    protected MutableLiveData<HashMap<String, List<String>>> attachmentMapLiveData = new MutableLiveData<>();
    private CartDao cartDao;
    private CompositeDisposable compositeDisposable;
    private HashMap<String, List<String>> attachmentMap = new HashMap<>();
    private String selectedShopSlug;

    @Inject
    public CheckoutViewModel(CartDao cartDao, SavedStateHandle savedStateHandle) {
        this.cartDao = cartDao;

        if (savedStateHandle != null && savedStateHandle.contains("model")) {
            List<CartEntity> list = new ArrayList<>();
            list.add(savedStateHandle.get("model"));
            MutableLiveData<List<CartEntity>> tempList = new MutableLiveData<>();
            tempList.setValue(list);
            liveList = tempList;
        } else
            liveList = cartDao.getAllSelectedLive();

        compositeDisposable = new CompositeDisposable();
    }

    public String getSelectedShopSlug() {
        return selectedShopSlug;
    }

    public void setSelectedShopSlug(String selectedShopSlug) {
        this.selectedShopSlug = selectedShopSlug;
    }

    public List<String> getAttachmentList() {
        return getAttachmentList(selectedShopSlug);
    }

    public List<String> getAttachmentList(String shopSlug) {
        List<String> attachmentList = new ArrayList<>();
        if (attachmentMap.containsKey(shopSlug) && attachmentMap.get(shopSlug) != null)
            attachmentList = attachmentMap.get(shopSlug);
        return attachmentList;
    }

    public void checkAttachmentRequirements(List<Integer> list) {
        OrderApiHelper.isAttachmentRequired(list, new ResponseListenerAuth<CommonDataResponse<List<AttachmentCheckResponse>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<AttachmentCheckResponse>> response, int statusCode) {
                attachmentCheckLiveData.setValue(response.getData());
                List<String> shopSlugs = new ArrayList<>();
                double deliveryCharge = 0;
                for (AttachmentCheckResponse item : response.getData()) {
                    if (item.isApplyDeliveryCharge() && !shopSlugs.contains(item.getShopSlug())) {
                        deliveryCharge += item.getDeliveryCharge();
                        shopSlugs.add(item.getShopSlug());
                    }
                }
                deliveryChargeLiveData.setValue(deliveryCharge);
                shopSlugs.clear();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                errorOrder.setValue(true);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }


    public void placeOrder(PlaceOrderItem payload) {
        Gson gson = new Gson();
        JsonElement element = gson.fromJson(gson.toJson(attachmentMap), JsonElement.class);
        JsonObject jsonObj = element.getAsJsonObject();
        payload.setAttachments(jsonObj);
        OrderApiHelper.placeOrder(payload, new ResponseListenerAuth<CommonDataResponse<List<JsonObject>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<JsonObject>> response, int statusCode) {
                orderPlacedLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                errorOrder.setValue(true);
                ToastUtils.show((errorBody != null && !errorBody.equals("")) ? errorBody : "Couldn't place order, try again later.");
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    placeOrder(payload);
            }
        });
    }

    public void deleteAttachment(String shopSlug, int position) {
        List<String> attachmentList = new ArrayList<>();
        if (attachmentMap.containsKey(shopSlug) && attachmentMap.get(shopSlug) != null)
            attachmentList = attachmentMap.get(shopSlug);
        attachmentList.remove(position);
        attachmentMap.put(shopSlug, attachmentList);
        attachmentMapLiveData.setValue(attachmentMap);
    }

    public void uploadImage(Bitmap bitmap) {
        ImageApiHelper.uploadImage(bitmap, new ResponseListenerAuth<CommonDataResponse<ImageDataModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<ImageDataModel> response, int statusCode) {
                List<String> attachmentList = new ArrayList<>();
                if (attachmentMap.containsKey(selectedShopSlug) && attachmentMap.get(selectedShopSlug) != null)
                    attachmentList = attachmentMap.get(selectedShopSlug);
                attachmentList.add(response.getData().getUrl());

                attachmentMap.put(selectedShopSlug, attachmentList);
                attachmentMapLiveData.setValue(attachmentMap);
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
