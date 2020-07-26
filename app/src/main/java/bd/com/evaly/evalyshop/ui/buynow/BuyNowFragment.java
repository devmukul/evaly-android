package bd.com.evaly.evalyshop.ui.buynow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.databinding.FragmentBuyNowBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.order.placeOrder.OrderItemsItem;
import bd.com.evaly.evalyshop.models.order.placeOrder.PlaceOrderItem;
import bd.com.evaly.evalyshop.models.product.productDetails.AvailableShopModel;
import bd.com.evaly.evalyshop.models.shop.shopItem.AttributesItem;
import bd.com.evaly.evalyshop.models.shop.shopItem.ShopItem;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.buynow.adapter.VariationAdapter;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.util.LocationUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class BuyNowFragment extends BottomSheetDialogFragment implements VariationAdapter.ClickListenerVariation {

    private FragmentBuyNowBinding binding;
    private Button btnBottomSheet;
    private EditText customAddress, contact_number;
    private BottomSheetDialog bottomSheetDialog;
    private CheckBox checkBox;
    private View bottomSheetView;
    private SkeletonScreen skeleton;
    private Context context;
    private ArrayList<ShopItem> itemsList;
    private String shop_slug = "tvs-bangladesh";
    private String shop_item_slug = "tvs-apache-rtr-160cc-single-disc";
    private int shop_item_id;
    private int quantityCount = 1;
    private double productPriceInt = 0;
    private AppDatabase appDatabase;
    private CartDao cartDao;
    private VariationAdapter adapterVariation;
    private ViewDialog dialog;
    private List<OrderItemsItem> list;
    private boolean isExpress = false;
    private CartEntity cartItem;
    private AvailableShopModel shopItem;
    private NavController navController;

    public static BuyNowFragment newInstance(String shopSlug, String productSlug) {
        BuyNowFragment f = new BuyNowFragment();
        Bundle args = new Bundle();
        args.putString("shopSlug", shopSlug);
        args.putString("productSlug", productSlug);
        f.setArguments(args);
        return f;
    }

    public static BuyNowFragment createInstance(CartEntity cartItem, AvailableShopModel shopItemModel) {
        BuyNowFragment f = new BuyNowFragment();
        Bundle args = new Bundle();
        args.putSerializable("cartItem", cartItem);
        args.putSerializable("shopItem", shopItemModel);
        f.setArguments(args);
        return f;
    }

    @Override
    public void selectVariation(int position) {

        for (int i = 0; i < itemsList.size(); i++) {
            if (i == position) {
                itemsList.get(i).setSelected(true);
                if (itemsList.get(i).getAttributes().size() > 0) {
                    AttributesItem attributesItem = itemsList.get(i).getAttributes().get(0);
                    String varName = attributesItem.getName();
                    String varValue = attributesItem.getValue();
                    //variationTitle.setText(varName + ": " + varValue);
                    loadProductById(position);
                }
            } else
                itemsList.get(i).setSelected(false);
        }
        adapterVariation.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentInputBottomSheetDialog);

        Bundle args = getArguments();
        appDatabase = AppDatabase.getInstance(getContext());
        cartDao = appDatabase.cartDao();
        dialog = new ViewDialog(getActivity());

        if (args.containsKey("shopSlug"))
            shop_slug = args.getString("shopSlug");
        if (args.containsKey("productSlug"))
            shop_item_slug = args.getString("productSlug");

        if (args.containsKey("shopItem"))
            shopItem = (AvailableShopModel) args.getSerializable("shopItem");
        if (args.containsKey("cartItem"))
            cartItem = (CartEntity) args.getSerializable("cartItem");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBuyNowBinding.inflate(inflater, container, false);

        return binding.getRoot();
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

    private void inflateFromApi() {
        skeleton.show();
        getProductDetails();
    }


    public void inflateFromModel() {
        skeleton.hide();

        shop_item_id = shopItem.getShopItemId();
        isExpress = shopItem.isExpressShop();
        shop_item_slug = cartItem.getSlug();
        shop_slug = shopItem.getShopSlug();
        isExpress = shopItem.isExpressShop();
        productPriceInt = shopItem.getPrice();

        if (shopItem.getDiscountedPrice() != null)
            if (shopItem.getDiscountedPrice() > 0) {
                double disPrice = shopItem.getDiscountedPrice();
                binding.price.setText(String.format("%s x 1", Utils.formatPriceSymbol(disPrice)));
                binding.priceTotal.setText(Utils.formatPriceSymbol(disPrice));
                productPriceInt = disPrice;
            }

        binding.productName.setText(cartItem.getName());
        binding.shopName.setText(String.format("Seller: %s", shopItem.getShopName()));
        binding.price.setText(String.format("%s x 1", Utils.formatPriceSymbol(productPriceInt)));
        binding.quantity.setText("1");
        binding.priceTotal.setText(Utils.formatPriceSymbol(productPriceInt));

        Glide.with(this)
                .load(cartItem.getImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(250, 250))
                .into(binding.productImage);

        binding.variationHolder.setVisibility(View.GONE);

        binding.addToCart.setOnClickListener(v -> {

            int quantity = 1;
            try {
                quantity = Integer.parseInt(binding.quantity.getText().toString());
            } catch (Exception ignore) {
            }

            Calendar calendar = Calendar.getInstance();
            String price = Utils.formatPrice(shopItem.getPrice());

            if (shopItem.getDiscountedPrice() != null)
                if (shopItem.getDiscountedPrice() > 0)
                    price = Utils.formatPrice(shopItem.getDiscountedPrice());

            String sellerJson = new Gson().toJson(shopItem);

            CartEntity cartEntity = new CartEntity();
            cartEntity.setName(cartItem.getName());
            cartEntity.setImage(cartItem.getImage());
            cartEntity.setPriceRound(price);
            cartEntity.setTime(calendar.getTimeInMillis());
            cartEntity.setShopJson(sellerJson);
            cartEntity.setQuantity(quantity);
            cartEntity.setShopSlug(shopItem.getShopSlug());
            cartEntity.setSlug(cartItem.getImage());
            cartEntity.setProductID(String.valueOf(shopItem.getShopItemId()));

            Executors.newSingleThreadExecutor().execute(() -> {
                List<CartEntity> dbItem = cartDao.checkExistsEntity(cartEntity.getProductID());
                if (dbItem.size() == 0)
                    cartDao.insert(cartEntity);
                else
                    cartDao.updateQuantity(cartEntity.getProductID(), dbItem.get(0).getQuantity() + quantityCount);
            });

            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
            dismiss();

        });
    }

    @SuppressLint("DefaultLocale")
    private void inflateQuantity() {
        binding.quantity.setText(String.format("%d", quantityCount));
        binding.price.setText(String.format("%s x %d", Utils.formatPriceSymbol(productPriceInt), quantityCount));
        binding.priceTotal.setText(Utils.formatPriceSymbol(productPriceInt * quantityCount));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        context = view.getContext();

        if (getActivity() instanceof MainActivity)
            navController = NavHostFragment.findNavController(this);

        skeleton = Skeleton.bind((LinearLayout) view.findViewById(R.id.linearLayout))
                .load(R.layout.skeleton_buy_now_modal)
                .color(R.color.ddd)
                .shimmer(true).show();

        itemsList = new ArrayList<>();
        adapterVariation = new VariationAdapter(itemsList, context, this);

        LinearLayoutManager managerVariation = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerViewVariation.setLayoutManager(managerVariation);
        binding.recyclerViewVariation.setAdapter(adapterVariation);

        binding.minus.setOnClickListener(view1 -> {
            if (quantityCount > 1) {
                quantityCount--;
                inflateQuantity();
            }
        });

        binding.plus.setOnClickListener(view1 -> {
            quantityCount++;
            inflateQuantity();
        });

        // bottom sheet

        bottomSheetDialog = new BottomSheetDialog(context, R.style.TransparentBottomSheetDialog);
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_checkout, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        btnBottomSheet = bottomSheetView.findViewById(R.id.bs_button);
        contact_number = bottomSheetView.findViewById(R.id.contact_number);
        customAddress = bottomSheetView.findViewById(R.id.customAddress);
        if (CredentialManager.getUserData() != null) {
            contact_number.setText(CredentialManager.getUserData().getContacts());
            customAddress.setText(CredentialManager.getUserData().getAddresses());
        }
        checkBox = bottomSheetView.findViewById(R.id.checkBox);


        list = new ArrayList<>();

        btnBottomSheet.setOnClickListener(view12 -> {

            if (CredentialManager.getToken().equals("")) {
                startActivity(new Intent(context, SignInActivity.class));
                return;
            }
            if (!checkBox.isChecked()) {
                Toast.makeText(context, "You must accept terms & conditions and purchasing policy to place an order.", Toast.LENGTH_LONG).show();
                return;
            }
            if (customAddress.getText().toString().equals("")) {
                Toast.makeText(context, "Please enter address.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Utils.isValidNumber(contact_number.getText().toString())) {
                Toast.makeText(context, "Please enter a correct phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            PlaceOrderItem orderJson = new PlaceOrderItem();

            orderJson.setContactNumber(contact_number.getText().toString());
            orderJson.setCustomerAddress(customAddress.getText().toString());
            orderJson.setPaymentMethod("evaly_pay");
            orderJson.setOrderOrigin("app");

            if (CredentialManager.getLatitude() != null && CredentialManager.getLongitude() != null) {
                orderJson.setDeliveryLatitude(CredentialManager.getLatitude());
                orderJson.setDeliveryLongitude(CredentialManager.getLongitude());
            }

            OrderItemsItem item = new OrderItemsItem();
            item.setQuantity(Integer.parseInt(binding.quantity.getText().toString()));
            item.setShopItemId(shop_item_id);

            int minOrderValue = 500;

            if (shop_slug.contains("food") && isExpress)
                minOrderValue = 300;

            if (!shop_slug.equals("evaly-amol-1") && productPriceInt * item.getQuantity() < minOrderValue) {
                Toast.makeText(getContext(), "You have to order more than TK. " + minOrderValue + " from an individual shop", Toast.LENGTH_SHORT).show();
                return;
            }

            list = new ArrayList<>();
            list.add(item);

            orderJson.setOrderItems(list);

            JsonObject payload = new Gson().toJsonTree(orderJson).getAsJsonObject();

            placeOrder(payload);
        });

        TextView deliveryDuration = bottomSheetView.findViewById(R.id.deliveryDuration);

        binding.buyNow.setOnClickListener(view1 -> {
            if (CredentialManager.getToken().equals("")) {
                startActivity(new Intent(context, SignInActivity.class));
                return;
            }
            if (shop_slug.contains("evaly-express"))
                deliveryDuration.setText("Delivery of the products will be completed within approximately 1 to 72 hours after payment depending on service.");
            else
                deliveryDuration.setText("Delivery will be made within 7 to 45 working days, depending on product and campaign.");

            checkLocationPermission();
            bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetDialog.show();
        });


        if (shopItem == null)
            inflateFromApi();
        else
            inflateFromModel();

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog dialogz = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = dialogz.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheet != null;
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
            BottomSheetBehavior.from(bottomSheet).setHideable(true);
        });
        return bottomSheetDialog;
    }

    public void getProductDetails() {

        ProductApiHelper.getProductVariants(shop_slug, shop_item_slug, new ResponseListenerAuth<CommonDataResponse<List<ShopItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<ShopItem>> response, int statusCode) {

                skeleton.hide();

                itemsList.clear();
                itemsList.addAll(response.getData());
                adapterVariation.notifyDataSetChanged();

                if (itemsList.size() > 0) {
                    itemsList.get(0).setSelected(true);
                    loadProductById(0);
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    private void loadProductById(int position) {

        ShopItem firstItem = itemsList.get(position);

        try {
            productPriceInt = (int) Math.round(Double.parseDouble(firstItem.getShopItemPrice()));
        } catch (Exception ignored) {
        }

        isExpress = itemsList.get(position).isExpress();

        if (firstItem.getShopItemDiscountedPrice() != null)
            if (!(firstItem.getShopItemDiscountedPrice().equals("0.0") || firstItem.getShopItemDiscountedPrice().equals("0"))) {
                int disPrice = (int) Math.round(Double.parseDouble(firstItem.getShopItemDiscountedPrice()));
                binding.price.setText(String.format("%s x 1", Utils.formatPriceSymbol(disPrice)));
                binding.priceTotal.setText(Utils.formatPriceSymbol(disPrice));
                productPriceInt = disPrice;
            }

        binding.productName.setText(firstItem.getShopItemName());
        binding.shopName.setText(String.format("Seller: %s", firstItem.getShopName()));
        binding.price.setText(String.format("%s x 1", Utils.formatPriceSymbol(productPriceInt)));
        binding.priceTotal.setText(Utils.formatPriceSymbol(productPriceInt));
        binding.quantity.setText("1");
        binding.priceTotal.setText(Utils.formatPriceSymbol(productPriceInt));

        shop_item_id = firstItem.getShopItemId();

        if (getContext() != null && this.isVisible() && !Objects.requireNonNull(getActivity()).isDestroyed())
            Glide.with(getContext())
                    .load(firstItem.getShopItemImage())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(new RequestOptions().override(250, 250))
                    .into(binding.productImage);

        if (firstItem.getAttributes().size() > 0) {
            binding.variationHolder.setVisibility(View.VISIBLE);
            AttributesItem attributesItem = firstItem.getAttributes().get(0);
            String varName = attributesItem.getName();
            String varValue = attributesItem.getValue();
            // variationTitle.setText(varName + ": " + varValue);
        } else
            binding.variationHolder.setVisibility(View.GONE);

        binding.addToCart.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();
            String price = firstItem.getShopItemPrice();

            if (firstItem.getShopItemDiscountedPrice() != null)
                if (!(firstItem.getShopItemDiscountedPrice().trim().equals("0") || firstItem.getShopItemDiscountedPrice().trim().equals("0.0")))
                    price = firstItem.getShopItemDiscountedPrice();

            String sellerJson = new Gson().toJson(firstItem);

            CartEntity cartEntity = new CartEntity();
            cartEntity.setName(firstItem.getShopItemName());
            cartEntity.setImage(firstItem.getShopItemImage());
            cartEntity.setPriceRound(price);
            cartEntity.setTime(calendar.getTimeInMillis());
            cartEntity.setShopJson(sellerJson);
            cartEntity.setQuantity(quantityCount);
            cartEntity.setShopSlug(firstItem.getShopSlug());
            cartEntity.setSlug(shop_item_slug);
            cartEntity.setProductID(String.valueOf(firstItem.getShopItemId()));

            Executors.newSingleThreadExecutor().execute(() -> {
                List<CartEntity> dbItem = cartDao.checkExistsEntity(cartEntity.getProductID());
                if (dbItem.size() == 0)
                    cartDao.insert(cartEntity);
                else
                    cartDao.updateQuantity(cartEntity.getProductID(), dbItem.get(0).getQuantity() + quantityCount);
            });

            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
            dismiss();

        });

    }


    public void placeOrder(JsonObject payload) {

        dialog.showDialog();

        OrderApiHelper.placeOrder(CredentialManager.getToken(), payload, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                if (bottomSheetDialog.isShowing())
                    bottomSheetDialog.dismiss();

                if (dialog.isShowing())
                    dialog.hideDialog();

                dismissDialog();

                if (response != null && getContext() != null) {
                    String errorMsg = response.get("message").getAsString();
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                    if (response.has("data") && response.getAsJsonArray("data").size() > 0) {
                        JsonArray data = response.getAsJsonArray("data");
                        JsonObject item = data.get(0).getAsJsonObject();
                        String invoice = item.get("invoice_no").getAsString();
                        if (getActivity() instanceof MainActivity) {
                            Bundle bundle = new Bundle();
                            bundle.putString("invoice_no", invoice);
                            try {
                                bundle.putString("shop_slug", item.get("shop").getAsJsonObject().get("slug").getAsString());
                            } catch (Exception ignored) {

                            }
                            if (navController != null)
                                navController.navigate(R.id.paymentFragment, bundle);
                        } else {
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
                    placeOrder(payload);
                else if (getContext() != null)
                    AppController.logout(getActivity());
            }
        });

    }

    private void dismissDialog() {
        if (getActivity() != null) {
            if (isVisible() && isCancelable() && !getActivity().isDestroyed() && !getActivity().isFinishing())
                dismissAllowingStateLoss();
        }
    }

}
