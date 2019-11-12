package bd.com.evaly.evalyshop.activity.buynow;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.CartActivity;
import bd.com.evaly.evalyshop.activity.SignInActivity;
import bd.com.evaly.evalyshop.activity.buynow.adapter.VariationAdapter;
import bd.com.evaly.evalyshop.activity.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.models.order.OrderItems;
import bd.com.evaly.evalyshop.models.placeOrder.OrderItemsItem;
import bd.com.evaly.evalyshop.models.placeOrder.PlaceOrderItem;
import bd.com.evaly.evalyshop.models.shopItem.AttributesItem;
import bd.com.evaly.evalyshop.models.shopItem.ShopItem;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.database.DbHelperCart;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BuyNowFragment extends BottomSheetDialogFragment implements VariationAdapter.ClickListenerVariation {


    private Context context;
    private UserDetails userDetails;
    private RequestQueue rq;
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
        rq = Volley.newRequestQueue(context);


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

        btnBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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

                OrderItemsItem item = new OrderItemsItem();
                item.setQuantity(Integer.parseInt(productQuantity.getText().toString()));
                item.setShopItemId(shop_item_id);


                list = new ArrayList<>();

                list.add(item);

                orderJson.setOrderItems(list);


                String payload = new Gson().toJson(orderJson);

                try {

                    JSONObject jsonPayload = new JSONObject(payload);
                    placeOrder(jsonPayload);

                }catch (Exception e){}




            }
        });





        checkOutBtn.setOnClickListener(view1 -> {

            if(userDetails.getToken().equals("")) {
                startActivity(new Intent(context, SignInActivity.class));
                return;
            }


            int totalPrice = 0;

            try {
                totalPrice = Integer.parseInt(productTotalPrice.getText().toString());
            } catch (Exception e){

            }

            if (shop_slug.equals("evaly-amol-1")){

                if (productPriceInt*quantityCount != 16){
                    Toast.makeText(context, "You can't order below or more than 16 Tk from Evaly Amol.", Toast.LENGTH_SHORT).show();

                    return;
                }

            } else {
                if (productPriceInt*quantityCount < 500){
                    Toast.makeText(context, "You can't order below 500 Tk.", Toast.LENGTH_SHORT).show();

                    return;
                }
            }


            bottomSheetDialog.show();

        });



    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog=(BottomSheetDialog)super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog dialogz = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet =  dialogz.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
                BottomSheetBehavior.from(bottomSheet).setHideable(true);
            }
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




    public void getProductDetails() {


        String url = UrlUtils.BASE_URL+"/public/shops/"+ shop_slug +"/items/" +shop_item_slug+ "/variants";
        Log.d("json rating", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null,
                response -> {

                    skeleton.hide();

                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        for(int i = 0; i < jsonArray.length(); i++){
                            Gson gson = new Gson();
                            ShopItem item = gson.fromJson(jsonArray.getJSONObject(i).toString(), ShopItem.class);

                            if (i==0)
                                item.setSelected(true);

                            itemsList.add(item);
                            adapterVariation.notifyDataSetChanged();
                        }

                        if (itemsList.size() > 0){

                            loadProductById(0);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        rq.getCache().clear();
        rq.add(request);


    }


    private void loadProductById(int position){


        ShopItem firstItem = itemsList.get(position);
        productName.setText(firstItem.getShopItemName());
        shopName.setText("Seller: " + firstItem.getShopName());
        productPrice.setText("৳ "+firstItem.getShopItemPrice() + " x 1");
        productQuantity.setText("1");
        productTotalPrice.setText("৳ "+firstItem.getShopItemPrice());

        shop_item_id = firstItem.getShopItemId();

        try {

            productPriceInt = Integer.parseInt(firstItem.getShopItemPrice());

        } catch (Exception e){ }


        if (!firstItem.getShopItemDiscountedPrice().equals("0")) {

            productPrice.setText("৳ " + firstItem.getShopItemDiscountedPrice() + " x 1");
            productTotalPrice.setText("৳ " + firstItem.getShopItemDiscountedPrice());

            try {
                productPriceInt = Integer.parseInt(firstItem.getShopItemDiscountedPrice());
            } catch (Exception e){ }
        }

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

            if (!firstItem.getShopItemDiscountedPrice().equals("0"))
                price = firstItem.getShopItemDiscountedPrice();

            String sellerJson = new Gson().toJson(firstItem);

            if(db.insertData(shop_item_slug,firstItem.getShopItemName(),firstItem.getShopItemImage(), Integer.parseInt(price), calendar.getTimeInMillis(), sellerJson, 1, firstItem.getShopSlug(), String.valueOf(firstItem.getShopItemId()))){

                Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
                BuyNowFragment.this.dismiss();
            }

        });

    }



    public void placeOrder(JSONObject payload){

        String url = UrlUtils.BASE_URL+"custom/order/create/";
        dialog.showDialog();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // Logger.d(response);

                bottomSheetDialog.hide();
                dialog.hideDialog();


                String errorMsg = "Couldn't place order, might be a server error";

                Log.d("json order", response.toString());

                try {

                    errorMsg = response.getString("message");

                }catch (Exception e){

                }

                try {
                    if (response.getJSONArray("data").length() < 1) {
                        Toast.makeText(context, "Order couldn't be placed", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                        BuyNowFragment.this.dismiss();

                } catch (Exception e){}

                try {

                    JSONArray data = response.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item = data.getJSONObject(i);
                        String invoice = item.getString("invoice_no");
                        Intent intent = new Intent(context, OrderDetailsActivity.class);
                        intent.putExtra("orderID", invoice);
                        startActivity(intent);
                    }

                } catch (Exception e) {

                    Log.e("json exception", e.toString());
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());


                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        if (response.statusCode == 401){

                            AuthApiHelper.refreshToken(getActivity(), new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                                @Override
                                public void onDataFetched(retrofit2.Response<JsonObject> response) {
                                    placeOrder(payload);
                                }

                                @Override
                                public void onFailed(int status) {

                                }
                            });

                            return;

                    }}


                dialog.hideDialog();
                Toast.makeText(context, "Couldn't place holder, might be a server error.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());
                return headers;
            }

        };
        RequestQueue queue= Volley.newRequestQueue(context);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }




}
