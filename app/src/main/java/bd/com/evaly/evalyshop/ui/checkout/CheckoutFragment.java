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
import android.view.View;
import android.view.ViewGroup;
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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.BottomSheetAddAddressBinding;
import bd.com.evaly.evalyshop.databinding.BottomSheetCheckoutContactBinding;
import bd.com.evaly.evalyshop.databinding.FragmentCheckoutBinding;
import bd.com.evaly.evalyshop.di.observers.SharedObservers;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.order.placeOrder.OrderItemsItem;
import bd.com.evaly.evalyshop.models.order.placeOrder.PlaceOrderItem;
import bd.com.evaly.evalyshop.models.user.AddressItem;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.ui.address.AddressFragment;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
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
public class CheckoutFragment extends DialogFragment {

    @Inject
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    @Inject
    SharedObservers sharedObservers;
    private FragmentCheckoutBinding binding;
    private CheckoutViewModel viewModel;
    private String deliveryChargeApplicable = null, deliveryDuration;
    private double deliveryChargeAmount = 0;
    private NavController navController;
    private CheckoutProductController controller;
    private AddressItem addressModel = null;
    private int minPrice = 0;
    private ViewDialog dialog;
    private double totalDeliveryCharge;
    private String selectedShopSlug;
    private ProgressDialog progressDialog;

    public CheckoutFragment() {
        //setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        viewModel = new ViewModelProvider(this).get(CheckoutViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof MainActivity)
            navController = NavHostFragment.findNavController(CheckoutFragment.this);
        dialog = new ViewDialog(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        startAnimation();
        checkRemoteConfig();
        setupRecycler();
        clickListeners();
        liveEvents();
        updateInfo();
    }

    private void startAnimation() {
        Animation mLoadAnimation = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        mLoadAnimation.setDuration(1000);
        binding.getRoot().startAnimation(mLoadAnimation);
    }

    private void updateInfo() {
        UserModel userModel = CredentialManager.getUserData();

        binding.userName.setText(userModel.getFullName());
        binding.contact.setText(userModel.getUsername());

        if (userModel.getAddresses() != null &&
                userModel.getAddresses().getData() != null &&
                userModel.getAddresses().getData().size() > 0) {
            addressModel = userModel.getAddresses().getData().get(0);
            binding.address.setText(addressModel.getFullAddressLine());
        } else
            binding.address.setText("No address provided");
    }

    private void setupRecycler() {
        if (controller == null)
            controller = new CheckoutProductController();
        binding.recyclerView.setAdapter(controller.getAdapter());
    }

    private void clickListeners() {

        binding.toolbar.setNavigationOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        binding.btnEditContactNumber.setOnClickListener(v -> {
            editContact();
        });

        binding.btnEditAddress.setOnClickListener(v -> {
            editAddress(addressModel);
        });

        binding.changeAddress.setOnClickListener(v -> {
            openLocationSelector();
        });

        binding.btnPlaceOrder.setOnClickListener(view -> {
            if (CredentialManager.getToken().equals("")) {
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
            if (minPrice > 0) {
                Toast.makeText(getContext(), "You have to order more than TK. " + minPrice + " from an individual shop", Toast.LENGTH_SHORT).show();
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

    private void checkRemoteConfig() {
        deliveryChargeApplicable = mFirebaseRemoteConfig.getString("delivery_charge_applicable");
        deliveryChargeAmount = mFirebaseRemoteConfig.getDouble("delivery_charge_amount");
    }

    private void updateViews() {

        binding.privacyText.setText(Html.fromHtml("Upon clicking on 'Place Order', I agree to the <a href=\"https://evaly.com.bd/about/terms-conditions\">Terms & Conditions</a> and <a href=\"https://evaly.com.bd/about/purchasing-policy\">Purchasing Policy</a> of Evaly."));
        binding.privacyText.setMovementMethod(LinkMovementMethod.getInstance());


        boolean isExpress = false;
        boolean showDeliveryCharge = false;
        totalDeliveryCharge = 0;
        HashMap<String, Integer> shopAmountMap = new HashMap<>();
        HashMap<String, Boolean> shopExpressMap = new HashMap<>();
        HashMap<String, String> expressShopSlugs = new HashMap();

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
                    shopAmountMap.put(ss, am + cartItem.getPriceInt() * cartItem.getQuantity());
                else
                    shopAmountMap.put(ss, cartItem.getPriceInt() * cartItem.getQuantity());

                JsonObject shopObject = JsonParser.parseString(cartItem.getShopJson()).getAsJsonObject();
                if (shopObject.has("is_express_shop")) {
                    if (shopObject.get("is_express_shop").getAsBoolean() || shopObject.get("is_express_shop").getAsString().equals("1")) {
                        isExpress = true;
                        if (deliveryChargeApplicable != null) {
                            String[] array = deliveryChargeApplicable.split(",");
                            for (String s : array) {
                                String shopTitle = shopObject.get("shop_name").getAsString();
                                if (shopTitle.toLowerCase().contains(s.toLowerCase())) {
                                    if (!expressShopSlugs.containsKey(cartItem.getShopSlug()))
                                        totalDeliveryCharge += deliveryChargeAmount;
                                    expressShopSlugs.put(cartItem.getShopSlug(), "added");
                                    showDeliveryCharge = true;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    if (cartItem.getShopSlug().contains("evaly-express")) {
                        isExpress = true;
                    }
                }
                shopExpressMap.put(ss, isExpress);
            }
        }

        viewModel.checkAttachmentRequirements(productIdList);

        for (String key : shopAmountMap.keySet()) {
            Integer am = shopAmountMap.get(key);
            Boolean express = shopExpressMap.get(key);

            int minAmount = 500;
            if (express && key.contains("food"))
                minAmount = 300;

            if (!key.equals("evaly-amol-1") && am != null && am < minAmount) {
                minPrice = minAmount;
            }
        }

        checkLocationPermission();

        if (isExpress) {
            binding.deliveryDuration.setText("Delivery of the products will be completed within approximately 1 to 72 hours after payment depending on service.");
        } else {
            binding.deliveryDuration.setText("Delivery will be made within 7 to 45 working days, depending on product and campaign");
        }

        if (showDeliveryCharge && isExpress) {
            if (totalDeliveryCharge > 0)
                binding.deliveryCharge.setText(Utils.formatPriceSymbol(totalDeliveryCharge));
            binding.vatHolder.setVisibility(View.VISIBLE);
        } else {
            totalDeliveryCharge = 0;
            binding.vatHolder.setVisibility(View.GONE);
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

                CredentialManager.saveLongitude(String.valueOf(location.getLongitude()));
                CredentialManager.saveLatitude(String.valueOf(location.getLatitude()));
            }
        });
    }

    private void openImagePicker(String shopSlug) {
        selectedShopSlug = shopSlug;
        Matisse.from(this)
                .choose(MimeType.ofImage(), true)
                .countable(true)
                .maxSelectable(5 - viewModel.getAttachmentList(shopSlug).size())
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
                    List<Uri>  selectedImagesList = Matisse.obtainResult(data);
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
                        viewModel.uploadImage(selectedShopSlug, resource);
                        progressDialog.setMessage("Uploading...");
                        progressDialog.show();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private void liveEvents() {

        viewModel.imagePicker.observe(getViewLifecycleOwner(), integer -> {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4040);
            else
                openImagePicker(selectedShopSlug);
        });

        viewModel.attachmentMapLiveData.observe(getViewLifecycleOwner(), map -> {
            controller.setAttachmentMap(map);
            controller.requestModelBuild();
        });

        sharedObservers.onAddressChanged.observe(getViewLifecycleOwner(), addressItem -> {
            addressModel = addressItem;
            binding.address.setText(addressItem.getFullAddressLine());
        });

        viewModel.errorOrder.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                dialog.hideDialog();
        });

        viewModel.liveList.observe(getViewLifecycleOwner(), cartEntities -> {
            controller.setList(cartEntities);
            controller.requestModelBuild();
            updateViews();
            double totalAmount = 0;
            int totalItems = 0;
            for (CartEntity cartEntity : cartEntities) {
                totalAmount += cartEntity.getPriceDouble() * cartEntity.getQuantity();
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
                viewModel.deleteSelected();
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

    public void editAddress(AddressItem model) {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        final BottomSheetAddAddressBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.bottom_sheet_add_address, null, false);

        dialogBinding.fullName.setVisibility(View.GONE);
        dialogBinding.fullNameTitle.setVisibility(View.GONE);
        dialogBinding.contactNumber.setVisibility(View.GONE);
        dialogBinding.contactTitle.setVisibility(View.GONE);

        if (model != null) {
            dialogBinding.address.setText(model.getAddress());
            dialogBinding.area.setText(model.getArea());
            dialogBinding.city.setText(model.getCity());
            dialogBinding.region.setText(model.getRegion());
            if (model.getFullName() == null || model.getFullName().equals(""))
                dialogBinding.fullName.setText(CredentialManager.getUserData().getFullName());
            else
                dialogBinding.fullName.setText(model.getFullName());
            if (model.getPhoneNumber() == null || model.getPhoneNumber().equals(""))
                dialogBinding.contactNumber.setText(CredentialManager.getUserData().getContact());
            else
                dialogBinding.contactNumber.setText(model.getPhoneNumber());
        } else {
            dialogBinding.fullName.setText(CredentialManager.getUserData().getFullName());
            dialogBinding.contactNumber.setText(CredentialManager.getUserData().getContact());
        }

        dialogBinding.save.setOnClickListener(view -> {
            String address = dialogBinding.address.getText().toString().trim();
            String area = dialogBinding.area.getText().toString().trim();
            String city = dialogBinding.city.getText().toString().trim();
            String region = dialogBinding.region.getText().toString().trim();

            String error = null;
            if (address.isEmpty())
                error = "Please enter address line 1";
            else if (area.isEmpty())
                error = "Please enter area";
            else if (city.isEmpty())
                error = "Please enter city";

            if (error != null) {
                ToastUtils.show(error);
                return;
            }

            AddressItem body = new AddressItem();
            body.setAddress(address);
            body.setArea(area);
            body.setCity(city);
            body.setRegion(region);
            addressModel = body;

            binding.address.setText(body.getFullAddressLine());
            dialog.cancel();
        });

        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
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
        orderObject.setCustomerAddress(addressModel.getFullAddress());
        orderObject.setOrderOrigin("app");

        if (CredentialManager.getLatitude() != null && CredentialManager.getLongitude() != null) {
            orderObject.setDeliveryLatitude(CredentialManager.getLatitude());
            orderObject.setDeliveryLongitude(CredentialManager.getLongitude());
        }

        orderObject.setPaymentMethod("evaly_pay");

        List<OrderItemsItem> productList = new ArrayList<>();
        List<CartEntity> adapterItems = viewModel.liveList.getValue();

        for (int i = 0; i < adapterItems.size(); i++) {
            if (adapterItems.get(i).isSelected()) {
                String fromShopJson = adapterItems.get(i).getShopJson();
                OrderItemsItem item = new OrderItemsItem();
                item.setQuantity(adapterItems.get(i).getQuantity());
                try {
                    JSONObject sellerJson = new JSONObject(fromShopJson);
                    String item_id = sellerJson.getString("shop_item_id");
                    item.setShopItemId(Integer.parseInt(item_id));
                    productList.add(item);
                } catch (Exception e) {
                }
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
