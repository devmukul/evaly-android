package bd.com.evaly.evalyshop.ui.buynow;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.order.placeOrder.OrderItemsItem;
import bd.com.evaly.evalyshop.models.order.placeOrder.PlaceOrderItem;
import bd.com.evaly.evalyshop.models.shop.shopItem.AttributesItem;
import bd.com.evaly.evalyshop.models.shop.shopItem.ShopItem;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.buynow.adapter.VariationAdapter;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.database.DbHelperCart;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BuyNowFragment extends BottomSheetDialogFragment implements VariationAdapter.ClickListenerVariation {


    private Context context;
    private UserDetails userDetails;
    private ArrayList<ShopItem> itemsList;
    private String shop_slug = "tvs-bangladesh";
    private String shop_item_slug = "tvs-apache-rtr-160cc-single-disc";
    private int shop_item_id;
    private int quantityCount = 1;
    private int productPriceInt = 0;
    private DbHelperCart db;

    @BindView(R.id.shop) TextView shopName;
    @BindView(R.id.product_name) TextView productName;
    @BindView(R.id.product_image) ImageView productImage;
    @BindView(R.id.minus) ImageView minus;
    @BindView(R.id.plus) ImageView plus;
    @BindView(R.id.price) TextView productPrice;
    @BindView(R.id.priceTotal) TextView productTotalPrice;
    @BindView(R.id.wholeSalePrice) TextView productWholesalePrice;
    @BindView(R.id.quantity) EditText productQuantity;
    @BindView(R.id.variation_title) TextView variationTitle;
    @BindView(R.id.add_to_cart) TextView addToCartBtn;
    @BindView(R.id.buy_now) TextView checkOutBtn;
    @BindView(R.id.variationHolder) LinearLayout variationHolder;

    // checkout bottomsheet
    CheckBox selectAll;
    Button checkout;
    Button btnBottomSheet;
    EditText customAddress, contact_number;
    BottomSheetDialog bottomSheetDialog;
    CheckBox checkBox;
    View bottomSheetView;

    SkeletonScreen skeleton;


    @BindView(R.id.recyclerViewVariation)
    RecyclerView recyclerVariation;
    private VariationAdapter adapterVariation;
    private ViewDialog dialog;
    private List<OrderItemsItem> list;

    @Override
    public void selectVariation(int position) {

        for (int i = 0; i < itemsList.size(); i++){
            if (i==position) {
                itemsList.get(i).setSelected(true);

                if (itemsList.get(i).getAttributes().size()>0) {
                    AttributesItem attributesItem = itemsList.get(i).getAttributes().get(0);
                    String varName = attributesItem.getName();
                    String varValue = attributesItem.getValue();
                    variationTitle.setText(varName + ": " + varValue);
                    loadProductById(position);
                }
            }
            else
                itemsList.get(i).setSelected(false);
        }
        adapterVariation.notifyDataSetChanged();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //bottom sheet round corners can be obtained but the while background appears to remove that we need to add this.
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);

        Bundle args = getArguments();

        db = new DbHelperCart(getActivity());
        dialog = new ViewDialog(getActivity());

        shop_slug = args.getString("shopSlug");
        shop_item_slug = args.getString("productSlug");


    }


    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy_now, container, false);
        ButterKnife.bind(this, view);
        return view;
    }



    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = view.getContext();
        userDetails = new UserDetails(context);


        skeleton = Skeleton.bind((LinearLayout) view.findViewById(R.id.linearLayout))
                .load(R.layout.skeleton_buy_now_modal)
                .color(R.color.ddd)
                .shimmer(true)
                .show();

        itemsList = new ArrayList<>();
        adapterVariation = new VariationAdapter(itemsList, context, this);

        LinearLayoutManager managerVariation = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerVariation.setLayoutManager(managerVariation);
        recyclerVariation.setAdapter(adapterVariation);

        getProductDetails();

        minus.setOnClickListener(view1 -> {
            if (quantityCount>1){
                quantityCount--;
                productQuantity.setText(quantityCount+"");
                productPrice.setText("৳ " + productPriceInt + " x "+quantityCount);
                productTotalPrice.setText("৳ " + productPriceInt*quantityCount);
            }
        });

        plus.setOnClickListener(view1 -> {
            quantityCount++;
            productQuantity.setText(quantityCount+"");
            productPrice.setText("৳ " + productPriceInt + " x "+quantityCount);
            productTotalPrice.setText("৳ " + productPriceInt*quantityCount);
        });

        // bottom sheet

        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_checkout, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        btnBottomSheet = bottomSheetView.findViewById(R.id.bs_button);
        contact_number = bottomSheetView.findViewById(R.id.contact_number);
        customAddress = bottomSheetView.findViewById(R.id.customAddress);
        contact_number.setText(userDetails.getPhone());
        customAddress.setText(userDetails.getJsonAddress());
        checkBox = bottomSheetView.findViewById(R.id.checkBox);



        list = new ArrayList<>();

        btnBottomSheet.setOnClickListener(view12 -> {


            if(userDetails.getToken().equals("")) {
                startActivity(new Intent(context, SignInActivity.class));
                return;
            }
            if (!checkBox.isChecked()) {
                Toast.makeText(context, "You must accept terms & conditions and purchasing policy to place an order.", Toast.LENGTH_LONG).show();
                return;
            }
            if (customAddress.getText().toString().equals("")){
                Toast.makeText(context, "Please enter address.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Utils.isValidNumber(contact_number.getText().toString())){
                Toast.makeText(context, "Please enter a correct phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            PlaceOrderItem orderJson = new PlaceOrderItem();

            orderJson.setContactNumber(contact_number.getText().toString());
            orderJson.setCustomerAddress(customAddress.getText().toString());
            orderJson.setPaymentMethod("evaly_pay");
            orderJson.setOrderOrigin("app");

            OrderItemsItem item = new OrderItemsItem();
            item.setQuantity(Integer.parseInt(productQuantity.getText().toString()));
            item.setShopItemId(shop_item_id);


            list = new ArrayList<>();

            list.add(item);

            orderJson.setOrderItems(list);

            JsonObject payload = new Gson().toJsonTree(orderJson).getAsJsonObject();

            placeOrder(payload);


        });


        checkOutBtn.setOnClickListener(view1 -> {

            if(userDetails.getToken().equals("")) {
                startActivity(new Intent(context, SignInActivity.class));
                return;
            }

            bottomSheetDialog.show();

        });



    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog=(BottomSheetDialog)super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog dialogz = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet =  dialogz.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
            BottomSheetBehavior.from(bottomSheet).setHideable(true);
        });
        return bottomSheetDialog;
    }

    public static BuyNowFragment newInstance(String shopSlug, String productSlug) {
        BuyNowFragment f = new BuyNowFragment();
        Bundle args = new Bundle();
        args.putString("shopSlug", shopSlug);
        args.putString("productSlug", productSlug);
        f.setArguments(args);

        return f;
    }


    public void getProductDetails(){

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



    private void loadProductById(int position){

        ShopItem firstItem = itemsList.get(position);

        try {
            productPriceInt = (int) Math.round(Double.parseDouble(firstItem.getShopItemPrice()));

        } catch (Exception e){

        }


        if (firstItem.getShopItemDiscountedPrice() != null)
            if (!(firstItem.getShopItemDiscountedPrice().equals("0.0") || firstItem.getShopItemDiscountedPrice().equals("0"))) {
                int disPrice = (int) Math.round(Double.parseDouble(firstItem.getShopItemDiscountedPrice()));
                productPrice.setText("৳ " + disPrice + " x 1");
                productTotalPrice.setText("৳ " + disPrice);
                productPriceInt = disPrice;
            }

        productName.setText(firstItem.getShopItemName());
        shopName.setText("Seller: " + firstItem.getShopName());
        productPrice.setText("৳ "+productPriceInt + " x 1");
        productQuantity.setText("1");
        productTotalPrice.setText("৳ "+productPriceInt);

        shop_item_id = firstItem.getShopItemId();



        Glide.with(context)
                .load(firstItem.getShopItemImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(250, 250))
                .into(productImage);

        if (firstItem.getAttributes().size()>0) {

            variationHolder.setVisibility(View.VISIBLE);

            AttributesItem attributesItem = firstItem.getAttributes().get(0);
            String varName = attributesItem.getName();
            String varValue = attributesItem.getValue();
            variationTitle.setText(varName + ": " + varValue);
        } else
            variationHolder.setVisibility(View.GONE);





        addToCartBtn.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            String price  = firstItem.getShopItemPrice();

            if (firstItem.getShopItemDiscountedPrice() != null)
                if (!firstItem.getShopItemDiscountedPrice().equals("0"))
                    price = firstItem.getShopItemDiscountedPrice();

            String sellerJson = new Gson().toJson(firstItem);

            if(db.insertData(shop_item_slug,firstItem.getShopItemName(),firstItem.getShopItemImage(), (int) Math.round(Double.parseDouble(price)), calendar.getTimeInMillis(), sellerJson, 1, firstItem.getShopSlug(), String.valueOf(firstItem.getShopItemId()))){

                Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
                BuyNowFragment.this.dismiss();
            }

        });

    }


    public void placeOrder(JsonObject payload){

        dialog.showDialog();

        OrderApiHelper.placeOrder(CredentialManager.getToken(), payload, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                bottomSheetDialog.hide();
                dialog.hideDialog();

                if (response != null) {

                    String errorMsg = response.get("message").getAsString();

                    if (response.getAsJsonArray("data").size() < 1) {
                        Toast.makeText(context, "Order couldn't be placed", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                        BuyNowFragment.this.dismiss();
                    }

                    JsonArray data = response.getAsJsonArray("data");
                    for (int i = 0; i < data.size(); i++) {
                        JsonObject item = data.get(i).getAsJsonObject();
                        String invoice = item.get("invoice_no").getAsString();
                        Intent intent = new Intent(context, OrderDetailsActivity.class);
                        intent.putExtra("orderID", invoice);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                dialog.hideDialog();
                Toast.makeText(context, "Couldn't place order, might be a server error.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    placeOrder(payload);
                else
                    if (getContext() != null)
                    AppController.logout(getActivity());
            }
        });

    }


}