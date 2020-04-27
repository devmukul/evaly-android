package bd.com.evaly.evalyshop.ui.cart;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.order.placeOrder.OrderItemsItem;
import bd.com.evaly.evalyshop.models.order.placeOrder.PlaceOrderItem;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.cart.adapter.CartAdapter;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.util.LocationUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;

import static androidx.core.content.ContextCompat.checkSelfPermission;


public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private LinearLayoutManager manager;
    private Context context;
    private CheckBox selectAll;
    private Button checkout, btnBottomSheet;
    private EditText customAddress, contact_number;
    private Switch addressSwitch;
    private Spinner addressSpinner;
    private List<String> spinnerArray;
    private List<Integer> spinnerArrayID;
    private UserDetails userDetails;
    private ViewDialog dialog;
    private boolean cartItem = false;
    private ViewDialog alert;
    private CompoundButton.OnCheckedChangeListener selectAllListener;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private View view;
    private AppDatabase appDatabase;
    private CartDao cartDao;
    private Toolbar mToolbar;
    private int paymentMethod = 2;
    private double totalPriceDouble = 0;
    private TextView tvTotalPrice;

    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        dialog = new ViewDialog(getActivity());
        userDetails = new UserDetails(context);

        appDatabase = AppDatabase.getInstance(getContext());
        cartDao = appDatabase.cartDao();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;

        mToolbar = view.findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });
        mToolbar.inflateMenu(R.menu.delete_btn);
        mToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case android.R.id.home:
                    return true;
                case R.id.action_delete:

                    if (adapter.getItemCount() == 0) {
                        Toast.makeText(context, "No item is available in cart to delete", Toast.LENGTH_SHORT).show();
                    } else {
                        new AlertDialog.Builder(context)
                                .setMessage("Are you sure you want to delete the selected products from the cart?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {

                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        List<CartEntity> listAdapter = adapter.getItemList();
                                        for (int i = 0; i < listAdapter.size(); i++) {
                                            if (listAdapter.get(i).isSelected())
                                                cartDao.deleteBySlug(listAdapter.get(i).getProductID());
                                        }
                                    });

                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                    return true;
            }

            return false;
        });

        recyclerView = view.findViewById(R.id.recycle);
        checkout = view.findViewById(R.id.button);
        selectAll = view.findViewById(R.id.checkBox);
        alert = new ViewDialog(getActivity());
        tvTotalPrice = view.findViewById(R.id.totalPrice);
        manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        adapter = new CartAdapter(context, cartDao);
        adapter.setHasStableIds(true);

        adapter.setListener(new CartAdapter.CartListener() {
            @Override
            public void updateCartFromRecycler() {

            }

            @Override
            public void uncheckSelectAllBtn(boolean isChecked) {
                CartFragment.this.uncheckSelectAllBtn(isChecked);
            }
        });

        recyclerView.setAdapter(adapter);

        // bottom sheet

        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.TransparentBottomSheetDialog);
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_checkout, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        btnBottomSheet = bottomSheetView.findViewById(R.id.bs_button);

        TextView deliveryDuration = bottomSheetView.findViewById(R.id.deliveryDuration);
        checkout.setOnClickListener(v -> {

            if (userDetails.getToken().equals("")) {
                Toast.makeText(context, "You need to login first.", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean selected = false;
            boolean isExpress = true;

            HashMap<String, Integer> shopAmount = new HashMap<>();

            for (int i = 0; i < adapter.getItemList().size(); i++) {
                CartEntity cartItem = adapter.getItemList().get(i);
                if (cartItem.isSelected()) {

                    String ss = cartItem.getShopSlug();
                    Integer am = shopAmount.get(ss);

                    if (shopAmount.containsKey(ss) && am != null)
                        shopAmount.put(ss, am + cartItem.getPriceInt() * cartItem.getQuantity());
                    else
                        shopAmount.put(ss, cartItem.getPriceInt() * cartItem.getQuantity());

                    selected = true;
                    if (!cartItem.getShopSlug().contains("evaly-express"))
                        isExpress = false;
                }
            }

            for (String key : shopAmount.keySet()) {
                Integer am = shopAmount.get(key);
                if (!key.equals("evaly-amol-1") && am != null && am < 500) {
                    Toast.makeText(getContext(), "You have to order more than TK. 500 from an individual shop", Toast.LENGTH_SHORT).show();
                    return;
                }
            }


            checkLocationPermission();

            if (isExpress)
                deliveryDuration.setText("Delivery of the products will be completed within approximately 3 days after payment.");
            else
                deliveryDuration.setText("Delivery of the products will be completed within approximately 30 working days after payment.");

            if (!selected)
                Toast.makeText(context, "Please select item from cart", Toast.LENGTH_SHORT).show();
            else
                bottomSheetDialog.show();
        });


        selectAllListener = (buttonView, isChecked) -> {
            Executors.newSingleThreadExecutor().execute(() -> {

                List<CartEntity> listAdapter = cartDao.getAll();
                for (int i = 0; i < listAdapter.size(); i++) {
                    cartDao.markSelected(listAdapter.get(i).getProductID(), isChecked);
                }
            });
        };

        TextView privacyText = bottomSheetView.findViewById(R.id.privacyText);
        privacyText.setText(Html.fromHtml("I agree to the <a href=\"https://evaly.com.bd/about/terms-conditions\">Terms & Conditions</a> and <a href=\"https://evaly.com.bd/about/purchasing-policy\">Purchasing Policy</a> of Evaly."));
        privacyText.setMovementMethod(LinkMovementMethod.getInstance());
        CheckBox checkBox = bottomSheetView.findViewById(R.id.checkBox);

        selectAll.setOnCheckedChangeListener(selectAllListener);

        btnBottomSheet.setOnClickListener(v -> {

            if (userDetails.getToken().equals("")) {
                startActivity(new Intent(context, SignInActivity.class));
                return;
            }
            if (!checkBox.isChecked()) {
                Toast.makeText(getContext(), "You must accept terms & conditions and purchasing policy to place an order.", Toast.LENGTH_LONG).show();
                return;
            }
            if (addressSwitch.isChecked() && customAddress.getText().toString().equals("")) {
                Toast.makeText(context, "Please enter address.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Utils.isValidNumber(contact_number.getText().toString())) {
                Toast.makeText(context, "Please enter a correct phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            placeOrder(generateOrderJson(), dialog);
        });

        // bottom sheet

        contact_number = bottomSheetView.findViewById(R.id.contact_number);
        addressSwitch = bottomSheetView.findViewById(R.id.addressSwitch);
        customAddress = bottomSheetView.findViewById(R.id.customAddress);
        addressSpinner = bottomSheetView.findViewById(R.id.spinner);
        contact_number.setText(userDetails.getPhone());
        spinnerArray = new ArrayList<>();
        spinnerArrayID = new ArrayList<>();
        customAddress.setText(userDetails.getJsonAddress());


        if (spinnerArray.size() < 1) {
            addressSwitch.setChecked(true);
        }

        // updateAddress();
        ImageView cod = bottomSheetView.findViewById(R.id.cod);
        ImageView evalyPay = bottomSheetView.findViewById(R.id.evaly_pay);

        // select payment method
        cod.setOnClickListener(v -> paymentMethod = 1);

        cod.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.performClick();
            }
        });

        evalyPay.setOnClickListener(v -> paymentMethod = 2);

        evalyPay.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.performClick();
            }
        });

        getCartList();

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

    public void uncheckSelectAllBtn(boolean isChecked) {

        if (!isChecked) {
            selectAll.setOnCheckedChangeListener(null);
            selectAll.setChecked(false);
            selectAll.setOnCheckedChangeListener(selectAllListener);
        } else {

            boolean isAllSelected = true;

            for (int i = 0; i < adapter.getItemList().size(); i++) {
                if (!adapter.getItemList().get(i).isSelected()) {
                    isAllSelected = false;
                    break;
                }
            }
            selectAll.setOnCheckedChangeListener(null);
            selectAll.setChecked(isAllSelected);
            selectAll.setOnCheckedChangeListener(selectAllListener);
        }
    }


    public void getCartList() {

        cartDao.getAllLive().observe(this, cartEntities -> {

            if (cartEntities.size() == 0) {

                LinearLayout empty = view.findViewById(R.id.empty);
                LinearLayout cal = view.findViewById(R.id.cal);
                recyclerView.setVisibility(View.GONE);
                cal.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
                Button button = view.findViewById(R.id.button);
                button.setVisibility(View.GONE);
                NestedScrollView scrollView = view.findViewById(R.id.scroller);
                scrollView.setBackgroundColor(Color.WHITE);
            } else {
                totalPriceDouble = 0;
                cartItem = true;

                adapter.setItemList(cartEntities);

                List<CartEntity> itemList = adapter.getItemList();

                for (int i = 0; i < adapter.getItemList().size(); i++) {
                    if (itemList.get(i).isSelected())
                        totalPriceDouble += itemList.get(i).getPriceInt() * itemList.get(i).getQuantity();
                }

                tvTotalPrice.setText(String.format(Locale.ENGLISH, "৳%d", (int) totalPriceDouble));
            }
        });

    }


    public void placeOrder(JsonObject payload, ViewDialog dialog) {

        dialog.showDialog();

        OrderApiHelper.placeOrder(CredentialManager.getToken(), payload, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                bottomSheetDialog.hide();
                dialog.hideDialog();

                if (response != null && getActivity() != null) {
                    String message = response.get("message").getAsString();
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                    if (response.has("data")) {
                        JsonArray data = response.getAsJsonArray("data");

                        if (data.size() > 0)
                            orderPlaced();

                        for (int i = 0; i < data.size(); i++) {
                            JsonObject item = data.get(i).getAsJsonObject();
                            String invoice = item.get("invoice_no").getAsString();
                            Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
                            intent.putExtra("orderID", invoice);
                            startActivity(intent);
                        }
                    }
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialog.hideDialog();
                Toast.makeText(context, "Couldn't place order, try again later.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    placeOrder(payload, dialog);
                else if (getContext() != null)
                    AppController.logout(getActivity());
            }
        });
    }


    private JsonObject generateOrderJson() {

        PlaceOrderItem orderObejct = new PlaceOrderItem();

        orderObejct.setContactNumber(contact_number.getText().toString());
        orderObejct.setCustomerAddress(customAddress.getText().toString());
        orderObejct.setOrderOrigin("app");

        if (CredentialManager.getLatitude() != null && CredentialManager.getLongitude() != null) {
            orderObejct.setDeliveryLatitude(CredentialManager.getLatitude());
            orderObejct.setDeliveryLongitude(CredentialManager.getLongitude());
        }

        orderObejct.setPaymentMethod("evaly_pay");

        List<OrderItemsItem> productList = new ArrayList<>();

        List<CartEntity> adapterItems = adapter.getItemList();

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

        orderObejct.setOrderItems(productList);

        return new Gson().toJsonTree(orderObejct).getAsJsonObject();
    }

    public void orderPlaced() {

        dialog.hideDialog();

        Executors.newSingleThreadExecutor().execute(() -> {
            List<CartEntity> listAdapter = adapter.getItemList();
            for (int i = 0; i < listAdapter.size(); i++) {
                if (listAdapter.get(i).isSelected())
                    cartDao.deleteBySlug(listAdapter.get(i).getProductID());
            }
        });

        bottomSheetDialog.hide();
        Toast.makeText(context, "Your order has been placed!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }

}
