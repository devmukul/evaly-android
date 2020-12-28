package bd.com.evaly.evalyshop.ui.checkout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.order.orderDetails.OrderDetailsModel;
import bd.com.evaly.evalyshop.models.order.placeOrder.OrderItemsItem;
import bd.com.evaly.evalyshop.models.order.placeOrder.PlaceOrderItem;
import bd.com.evaly.evalyshop.models.user.AddressItem;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.checkout.controller.CheckoutProductController;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.ui.order.orderList.OrderListActivity;
import bd.com.evaly.evalyshop.util.LocationUtils;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import dagger.hilt.android.AndroidEntryPoint;

import static androidx.core.content.ContextCompat.checkSelfPermission;

@AndroidEntryPoint
public class CheckoutFragment extends DialogFragment {

    @Inject
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FragmentCheckoutBinding binding;
    private CheckoutViewModel viewModel;
    private String deliveryChargeApplicable = null, deliveryDuration;
    private double deliveryChargeAmount = 0;
    private NavController navController;
    private CheckoutProductController controller;
    private AddressItem addressModel = null;
    private int minPrice = 0;

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
        checkRemoteConfig();
        setupRecycler();
        clickListeners();
        liveEvents();
        updateInfo();
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
        }
    }

    private void setupRecycler() {
        if (controller == null)
            controller = new CheckoutProductController();
        binding.recyclerView.setAdapter(controller.getAdapter());
    }

    private void clickListeners() {

        binding.toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());

        binding.btnEditContactNumber.setOnClickListener(v -> {
            editContact();
        });

        binding.btnEditAddress.setOnClickListener(v -> {
            editAddress(addressModel);
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
            if (addressModel == null || binding.address.getText().toString().equals("")) {
                ToastUtils.show("Please enter your address");
                return;
            }
            if (minPrice > 0) {
                Toast.makeText(getContext(), "You have to order more than TK. " + minPrice + " from an individual shop", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.placeOrder(generateOrderJson());
        });

    }

    private void checkRemoteConfig() {
        deliveryChargeApplicable = mFirebaseRemoteConfig.getString("delivery_charge_applicable");
        deliveryChargeAmount = mFirebaseRemoteConfig.getDouble("delivery_charge_amount");
    }


    private void updateViews() {

        binding.privacyText.setText(Html.fromHtml("Upon clicking on 'Place Order', I agree to the <a href=\"https://evaly.com.bd/about/terms-conditions\">Terms & Conditions</a> and <a href=\"https://evaly.com.bd/about/purchasing-policy\">Purchasing Policy</a> of Evaly."));
        binding.privacyText.setMovementMethod(LinkMovementMethod.getInstance());

        boolean selected = false;
        boolean isExpress = false;
        boolean showDeliveryCharge = false;

        HashMap<String, Integer> shopAmountMap = new HashMap<>();
        HashMap<String, Boolean> shopExpressMap = new HashMap<>();

        List<CartEntity> itemList = viewModel.liveList.getValue();
        if (itemList == null)
            itemList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            CartEntity cartItem = itemList.get(i);
            if (cartItem.isSelected()) {
                String ss = cartItem.getShopSlug();
                Integer am = shopAmountMap.get(ss);

                if (shopAmountMap.containsKey(ss) && am != null)
                    shopAmountMap.put(ss, am + cartItem.getPriceInt() * cartItem.getQuantity());
                else
                    shopAmountMap.put(ss, cartItem.getPriceInt() * cartItem.getQuantity());

                selected = true;

                JsonObject shopObject = JsonParser.parseString(cartItem.getShopJson()).getAsJsonObject();
                if (shopObject.has("is_express_shop")) {
                    if (shopObject.get("is_express_shop").getAsBoolean() || shopObject.get("is_express_shop").getAsString().equals("1"))
                        isExpress = true;
                    if (deliveryChargeApplicable != null) {
                        String[] array = deliveryChargeApplicable.split(",");
                        for (String s : array) {
                            String shopTitle = shopObject.get("shop_name").getAsString();
                            if (shopTitle.toLowerCase().contains(s.toLowerCase())) {
                                showDeliveryCharge = true;
                                break;
                            }
                        }
                    }

                } else {
                    if (cartItem.getShopSlug().contains("evaly-express"))
                        isExpress = true;
                }
                shopExpressMap.put(ss, isExpress);
            }
        }

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
            if (deliveryChargeAmount > 0)
                binding.deliveryCharge.setText(Utils.formatPriceSymbol(deliveryChargeAmount));
            binding.vatHolder.setVisibility(View.VISIBLE);
        } else {
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


    private void liveEvents() {

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
            binding.totalAmount.setText(Utils.formatPriceSymbol(totalAmount + deliveryChargeAmount));
            binding.totalText.setText(String.format("%s %s", getString(R.string.total_colon), Utils.formatPriceSymbol(totalAmount)));
            binding.totalItems.setText(String.format(Locale.ENGLISH, "%s %d", getString(R.string.items_colon), totalItems));
        });

        viewModel.orderPlacedLiveData.observe(getViewLifecycleOwner(), response -> {

            ToastUtils.show(response.getMessage());
            List<OrderDetailsModel> list = response.getData();
            if (list.size() > 0) {
                viewModel.deleteSelected();
                ToastUtils.show("Your order has been placed!");
            }

            for (OrderDetailsModel item : list) {
                String invoice = null;
                if (item.getInvoiceNo() != null)
                    invoice = item.getInvoiceNo();
                if (invoice == null || invoice.equals("")) {
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        if (getActivity() instanceof MainActivity && navController != null)
                            navController.navigate(R.id.orderListBaseFragment);
                        else
                            getActivity().startActivity(new Intent(getContext(), OrderListActivity.class));
                    }
                    break;
                } else {
                    Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
                    intent.putExtra("orderID", invoice);
                    startActivity(intent);
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
        orderObject.setCustomerAddress(addressModel.getFullAddressLine());
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
            case 0:
                break;
        }
    }

}
