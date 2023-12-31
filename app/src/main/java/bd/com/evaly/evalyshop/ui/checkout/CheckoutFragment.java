package bd.com.evaly.evalyshop.ui.checkout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.JsonObject;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.BottomSheetCheckoutContactBinding;
import bd.com.evaly.evalyshop.databinding.FragmentCheckoutBinding;
import bd.com.evaly.evalyshop.di.observers.SharedObservers;
import bd.com.evaly.evalyshop.models.order.AttachmentCheckResponse;
import bd.com.evaly.evalyshop.models.order.placeOrder.OrderItemsItem;
import bd.com.evaly.evalyshop.models.order.placeOrder.PlaceOrderItem;
import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.ui.address.AddressFragment;
import bd.com.evaly.evalyshop.ui.auth.login.SignInActivity;
import bd.com.evaly.evalyshop.ui.base.BaseDialogFragment;
import bd.com.evaly.evalyshop.ui.cart.CartViewModel;
import bd.com.evaly.evalyshop.ui.checkout.controller.CheckoutProductController;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.ui.order.orderList.OrderListActivity;
import bd.com.evaly.evalyshop.util.LocationUtils;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.checkSelfPermission;

@AndroidEntryPoint
public class CheckoutFragment extends BaseDialogFragment<FragmentCheckoutBinding, CheckoutViewModel> {

    @Inject
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    @Inject
    SharedObservers sharedObservers;
    @Inject
    PreferenceRepository preferenceRepository;

    private String deliveryText = "";
    private String deliveryTextExpress = "";
    private CartViewModel cartViewModel;
    private CheckoutProductController controller;
    private AddressResponse addressModel = null;
    private ViewDialog dialog;
    private double totalDeliveryCharge;
    private ProgressDialog progressDialog;
    private List<Uri> selectedImagesList;
    private HashMap<String, Integer> shopAmountMap;
    private double totalAmount = 0;

    public CheckoutFragment() {
        super(CheckoutViewModel.class, R.layout.fragment_checkout);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }

    @Override
    protected void initViews() {
        deliveryTextExpress =  getString(R.string.express_delivery_text);
        deliveryText = getString(R.string.delivery_text);
        shopAmountMap = new HashMap<>();
        dialog = new ViewDialog(getActivity());
        dialog.showDialog();
        progressDialog = new ProgressDialog(getActivity());
        selectedImagesList = new ArrayList<>();
        startAnimation();
        updateInfo();
    }

    private void startAnimation() {
        Animation mLoadAnimation = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        mLoadAnimation.setDuration(1000);
        binding.getRoot().startAnimation(mLoadAnimation);
    }

    private void updateInfo() {
        UserModel userModel = preferenceRepository.getUserData();
        if (userModel != null) {
            binding.userName.setText(userModel.getFullName());
            binding.contact.setText(userModel.getUsername());
        }
    }

    @Override
    protected void setupRecycler() {
        if (controller == null)
            controller = new CheckoutProductController();
        binding.recyclerView.setAdapter(controller.getAdapter());
        controller.setViewModel(viewModel);
    }

    @Override
    protected void clickListeners() {

        binding.toolbar.setNavigationOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        binding.btnEditContactNumber.setOnClickListener(v -> {
            editContact();
        });

        binding.btnEditAddress.setOnClickListener(v -> {
            openLocationSelector();
        });

        binding.btnPlaceOrder.setOnClickListener(view -> {
            if (preferenceRepository.getToken().equals("")) {
                startActivity(new Intent(getContext(), SignInActivity.class));
                return;
            }
            if (!Utils.isValidNumber(binding.contact.getText().toString())) {
                ToastUtils.show("Please enter a correct phone number");
                return;
            }
            if (addressModel == null || binding.address.getText().toString().equals("") || binding.address.getText().toString().equals("No address provided")) {
                ToastUtils.show("Please enter delivery address");
                return;
            }

            String minAmountErrorMessage = viewModel.getMinAmountErrorMessage(shopAmountMap);

            if (minAmountErrorMessage != null && minAmountErrorMessage.length() > 0) {
                Toast.makeText(getContext(), "Minimum order amount required for " + minAmountErrorMessage, Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.showDialog();
            viewModel.placeOrder(generateOrderJson());
        });
    }

    private void openLocationSelector() {
        AddressFragment addressFragment = new AddressFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_picker", true);
        addressFragment.setArguments(bundle);
        addressFragment.show(getParentFragmentManager(), "Address Picker");
    }

    private void updateViews() {
        binding.privacyText.setText(Html.fromHtml("Upon clicking on 'Place Order', I agree to the <a href=\"https://evaly.com.bd/about/terms-conditions\">Terms & Conditions</a> and <a href=\"https://evaly.com.bd/about/purchasing-policy\">Purchasing Policy</a> of Evaly."));
        binding.privacyText.setMovementMethod(LinkMovementMethod.getInstance());
        boolean isExpress = false;
        boolean isNormalOrder = false;
        totalDeliveryCharge = 0;

        List<CartEntity> itemList = viewModel.liveList.getValue();
        List<Integer> productIdList = new ArrayList<>();

        if (itemList == null)
            itemList = new ArrayList<>();

        for (int i = 0; i < itemList.size(); i++) {
            CartEntity cartItem = itemList.get(i);
            if (Utils.isNumeric(cartItem.getProductID()))
                productIdList.add(Integer.parseInt(cartItem.getProductID()));
            if (cartItem.isSelected()) {
                String ss = cartItem.getShopSlug();
                Integer am = shopAmountMap.get(ss);
                if (shopAmountMap.containsKey(ss) && am != null)
                    shopAmountMap.put(ss, (int) (am + cartItem.getDiscountedPriceD() * cartItem.getQuantity()));
                else
                    shopAmountMap.put(ss, (int) cartItem.getDiscountedPriceD() * cartItem.getQuantity());
            }
            if (!isExpress && cartItem.isExpressShop()) {
                isExpress = true;
            }
            if (!isNormalOrder && !cartItem.isExpressShop()) {
                isNormalOrder = true;
            }
            setDeliveryTimeText(isExpress, isNormalOrder);
        }

        viewModel.checkAttachmentRequirements(productIdList);

        checkLocationPermission();
    }

    private void setDeliveryTimeText(boolean isExpress, boolean isNormalOrder) {
        if (Utils.isNetworkAvailable(requireActivity())) {
            mFirebaseRemoteConfig
                    .fetchAndActivate()
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            deliveryTextExpress = mFirebaseRemoteConfig.getString("delivery_time_text_express");
                            deliveryText = mFirebaseRemoteConfig.getString("delivery_time_text");
                            updateDeliveryView(deliveryTextExpress, deliveryText, isExpress, isNormalOrder);
                        }
                    });
            updateDeliveryView(deliveryTextExpress, deliveryText, isExpress, isNormalOrder);
        }

    }

    private void updateDeliveryView(String deliveryTextExpress, String deliveryText, boolean isExpress, boolean isNormalOrder) {
        if (isExpress && isNormalOrder) {
            String text = deliveryText + "\n" + deliveryTextExpress;
            binding.deliveryDuration.setText(text);
            return;
        }
        if (isExpress && !isNormalOrder) {
            binding.deliveryDuration.setText(deliveryTextExpress);
            return;
        }
        if (isNormalOrder && !isExpress) {
            binding.deliveryDuration.setText(deliveryText);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            updateLocation();
        else
            requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1212);
    }

    private void updateLocation() {

        LocationUtils locationUtils = new LocationUtils();
        locationUtils.getLocation(getContext(), new LocationUtils.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                if (getActivity() == null || getActivity().isFinishing() || getActivity().isDestroyed())
                    return;
                if (getContext() == null || location == null)
                    return;
                if (location.getLatitude() == 0 || location.getLongitude() == 0)
                    return;

                preferenceRepository.saveLongitude(String.valueOf(location.getLongitude()));
                preferenceRepository.saveLatitude(String.valueOf(location.getLatitude()));
            }
        });
    }

    private void openImagePicker() {
        Matisse.from(this)
                .choose(MimeType.ofImage(), true)
                .countable(true)
                .maxSelectable(3 - viewModel.getAttachmentList().size())
                .theme(R.style.Matisse_Dracula)
                .thumbnailScale(0.85f)
                .capture(true)
                .captureStrategy(new CaptureStrategy(false, "bd.com.evaly.evalyshop.fileprovider"))
                .showPreview(false)
                .imageEngine(new GlideEngine())
                .forResult(2020);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (getActivity() != null && data != null) {
                if (requestCode == 2020) {
                    selectedImagesList = Matisse.obtainResult(data);
                    uploadImage(selectedImagesList.get(0));
                    selectedImagesList.remove(0);
                } else if (requestCode == 2040) {
                    Uri imageUri = data.getData();
                    uploadImage(imageUri);
                }
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        if (getActivity() == null)
            return;

        Glide.with(getActivity())
                .asBitmap()
                .load(imageUri)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        viewModel.uploadImage(resource);
                        progressDialog.setMessage("Uploading...");
                        progressDialog.show();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }


    @Override
    protected void liveEventsObservers() {

        viewModel.selectedAddress.observe(getViewLifecycleOwner(), addressResponse -> {
            if (addressResponse == null)
                binding.address.setText("No address provided");
            else {
                addressModel = addressResponse;
                binding.address.setText(addressResponse.getFullAddressLine());
                binding.userName.setText(addressResponse.getFullName());
                binding.contact.setText(addressResponse.getPhoneNumber());
            }
        });

        viewModel.deliveryChargeLiveData.observe(getViewLifecycleOwner(), deliveryCharge -> {
            dialog.hideDialog();
            if (deliveryCharge == null)
                binding.deliveryCharge.setText(Utils.formatPriceSymbol(0));
            else {
                binding.deliveryCharge.setText(Utils.formatPriceSymbol(deliveryCharge));
                totalDeliveryCharge = deliveryCharge;
                binding.totalAmount.setText(Utils.formatPriceSymbol(totalAmount + totalDeliveryCharge));
                binding.totalText.setText(String.format("%s %s", getString(R.string.total_colon), Utils.formatPriceSymbol(totalAmount + totalDeliveryCharge)));
            }
        });

        viewModel.attachmentCheckLiveData.observe(getViewLifecycleOwner(), list -> {
            HashMap<String, AttachmentCheckResponse> map = new HashMap<>();
            for (AttachmentCheckResponse item : list) {
                map.put(item.getShopSlug(), item);
            }
            controller.setShowAttachmentMap(map);
            controller.requestModelBuild();
        });

        viewModel.imagePicker.observe(getViewLifecycleOwner(), integer -> {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4040);
            else
                openImagePicker();
        });

        viewModel.attachmentMapLiveData.observe(getViewLifecycleOwner(), map -> {
            progressDialog.dismiss();
            controller.setAttachmentMap(map);
            controller.requestModelBuild();
            if (selectedImagesList.size() > 0) {
                uploadImage(selectedImagesList.get(0));
                selectedImagesList.remove(0);
            }
        });

        sharedObservers.onAddressChanged.observe(getViewLifecycleOwner(), addressItem -> {
            viewModel.selectedAddress.setValue(addressItem);
            addressModel = addressItem;
        });

        viewModel.errorOrder.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                dialog.hideDialog();
        });

        viewModel.liveList.observe(getViewLifecycleOwner(), cartEntities -> {
            controller.setList(cartEntities);
            controller.requestModelBuild();
            updateViews();
            totalAmount = 0;
            int totalItems = 0;
            for (CartEntity cartEntity : cartEntities) {
                totalAmount += cartEntity.getDiscountedPriceD() * cartEntity.getQuantity();
                totalItems += cartEntity.getQuantity();
            }
            binding.subtotal.setText(Utils.formatPriceSymbol(totalAmount));
            binding.totalAmount.setText(Utils.formatPriceSymbol(totalAmount + totalDeliveryCharge));
            binding.totalText.setText(String.format("%s %s", getString(R.string.total_colon), Utils.formatPriceSymbol(totalAmount + totalDeliveryCharge)));
            binding.totalItems.setText(String.format(Locale.ENGLISH, "%s %d", getString(R.string.items_colon), totalItems));
        });

        viewModel.orderPlacedLiveData.observe(getViewLifecycleOwner(), response -> {
            if (getActivity() == null)
                return;
            dialog.hideDialog();
            ToastUtils.show(response.getMessage());

            List<JsonObject> list = response.getData();
            if (list == null || list.size() == 0)
                return;

            if ((getArguments() == null || !getArguments().containsKey("model"))) {
                cartViewModel.deleteSelected();
            }

            if (isVisible())
                dismissAllowingStateLoss();

            if (getActivity() != null && !getActivity().isFinishing()) {
                if (list.size() == 1 && list.get(0).has("invoice_no") && list.get(0).get("invoice_no") != null && !list.get(0).get("invoice_no").getAsString().equals("")) {
                    Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
                    intent.putExtra("orderID", list.get(0).get("invoice_no").getAsString());
                    startActivity(intent);
                } else {
                    if (getActivity() instanceof MainActivity && navController != null)
                        navController.navigate(R.id.orderListBaseFragment);
                    else
                        getActivity().startActivity(new Intent(getContext(), OrderListActivity.class));
                }
            }
        });
    }


    public void editContact() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        final BottomSheetCheckoutContactBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.bottom_sheet_checkout_contact, null, false);

        dialogBinding.contact.setText(binding.contact.getText().toString());
        dialogBinding.save.setOnClickListener(view -> {

            String phoneNumber = dialogBinding.contact.getText().toString().trim();
            String error = null;

            if (phoneNumber.equals(""))
                error = "Please enter phone number";
            else if (!Utils.isValidNumber(phoneNumber))
                error = "Please enter a valid phone number";

            if (error != null) {
                ToastUtils.show(error);
                return;
            }

            binding.contact.setText(phoneNumber);
            dialog.cancel();
        });

        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
    }


    private PlaceOrderItem generateOrderJson() {

        PlaceOrderItem orderObject = new PlaceOrderItem();

        orderObject.setContactNumber(binding.contact.getText().toString());
        if (!binding.userName.getText().toString().equals(preferenceRepository.getUserData().getFullName()))
            orderObject.setCustomerAddress(addressModel.getFullAddressWithName());
        else
            orderObject.setCustomerAddress(addressModel.getFullAddress());

        orderObject.setOrderOrigin("app");

        if (preferenceRepository.getLatitude() != null && preferenceRepository.getLongitude() != null) {
            orderObject.setDeliveryLatitude(preferenceRepository.getLatitude());
            orderObject.setDeliveryLongitude(preferenceRepository.getLongitude());
        }

        orderObject.setPaymentMethod("evaly_pay");

        List<OrderItemsItem> productList = new ArrayList<>();
        List<CartEntity> adapterItems = viewModel.liveList.getValue();

        for (int i = 0; i < adapterItems.size(); i++) {
            if (adapterItems.get(i).isSelected()) {
                OrderItemsItem item = new OrderItemsItem();
                item.setQuantity(adapterItems.get(i).getQuantity());
                item.setShopItemId(Integer.parseInt(adapterItems.get(i).getProductID()));
                productList.add(item);
            }
        }

        orderObject.setOrderItems(productList);

        return orderObject;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1212:
                if (checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    updateLocation();
                }
                break;
            case 4040:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImagePicker();
                } else {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, 2040);
                }
                break;
            case 0:
                break;
        }
    }

}
