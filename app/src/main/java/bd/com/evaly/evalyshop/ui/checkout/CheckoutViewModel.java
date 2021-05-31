package bd.com.evaly.evalyshop.ui.checkout;

import android.annotation.SuppressLint;
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

import bd.com.evaly.evalyshop.data.roomdb.address.AddressListDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.image.ImageDataModel;
import bd.com.evaly.evalyshop.models.order.AttachmentCheckResponse;
import bd.com.evaly.evalyshop.models.order.placeOrder.PlaceOrderItem;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.util.SingleLiveEvent;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

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
    protected MutableLiveData<AddressResponse> selectedAddress = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable;
    private HashMap<String, List<String>> attachmentMap = new HashMap<>();
    private String selectedShopSlug;
    private ApiRepository apiRepository;

    @SuppressLint("CheckResult")
    @Inject
    public CheckoutViewModel(CartDao cartDao, AddressListDao addressListDao, SavedStateHandle savedStateHandle, ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                addressListDao.getAllPrimary()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(addressResponses -> {
                            if (addressResponses != null && addressResponses.size() > 0)
                                selectedAddress.setValue(addressResponses.get(0));
                        }, throwable -> {
                        }));

        if (savedStateHandle != null && savedStateHandle.contains("model")) {
            List<CartEntity> list = new ArrayList<>();
            list.add(savedStateHandle.get("model"));
            MutableLiveData<List<CartEntity>> tempList = new MutableLiveData<>();
            tempList.setValue(list);
            liveList = tempList;
        } else
            liveList = cartDao.getAllSelectedLive();

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
        apiRepository.isAttachmentRequired(list, new ResponseListener<CommonDataResponse<List<AttachmentCheckResponse>>, String>() {
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

        });
    }

    public String getMinAmountErrorMessage(HashMap<String, Integer> shopAmount) {

        String message = "";
        int count = 0;

        if (attachmentCheckLiveData == null ||
                attachmentCheckLiveData.getValue() == null ||
                attachmentCheckLiveData.getValue().size() == 0)
            return message;

        for (AttachmentCheckResponse item : attachmentCheckLiveData.getValue()) {
            if (shopAmount.get(item.getShopSlug()) != null && item.getMinOrderAmount() > shopAmount.get(item.getShopSlug())) {
                String name = item.getShopName();
                int minAmount = item.getMinOrderAmount();
                if (count > 0)
                    message += ",";
                message += " " + name + ": " + Utils.formatPriceSymbol(minAmount);
                count++;
            }
        }

        return message;

    }

    public void placeOrder(PlaceOrderItem payload) {
        Gson gson = new Gson();
        JsonElement element = gson.fromJson(gson.toJson(attachmentMap), JsonElement.class);
        JsonObject jsonObj = element.getAsJsonObject();
        payload.setAttachments(jsonObj);
        apiRepository.placeOrder(payload, new ResponseListener<CommonDataResponse<List<JsonObject>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<JsonObject>> response, int statusCode) {
                orderPlacedLiveData.setValue(response);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                errorOrder.setValue(true);
                ToastUtils.show((errorBody != null && !errorBody.equals("")) ? errorBody : "Couldn't place order, try again later.");
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
        apiRepository.uploadImage(bitmap, new ResponseListener<CommonDataResponse<ImageDataModel>, String>() {
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
        });
    }

}
