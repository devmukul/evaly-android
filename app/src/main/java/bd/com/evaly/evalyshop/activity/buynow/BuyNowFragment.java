package bd.com.evaly.evalyshop.activity.buynow;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.os.Bundle;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.buynow.adapter.VariationAdapter;
import bd.com.evaly.evalyshop.models.shopItem.AttributesItem;
import bd.com.evaly.evalyshop.models.shopItem.ShopItem;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BuyNowFragment extends BottomSheetDialogFragment implements VariationAdapter.ClickListenerVariation {


    private Context context;
    private UserDetails userDetails;
    private RequestQueue rq;
    private ArrayList<ShopItem> itemsList;
    private String shop_slug = "tvs-bangladesh";
    private String shop_item_slug = "tvs-apache-rtr-160cc-single-disc";
    private int quantityCount = 1;
    private int productPriceInt = 0;

    @BindView(R.id.shop)
    TextView shopName;

    @BindView(R.id.product_name)
    TextView productName;

    @BindView(R.id.product_image)
    ImageView productImage;

    @BindView(R.id.minus)
    ImageView minus;

    @BindView(R.id.plus)
    ImageView plus;

    @BindView(R.id.price)
    TextView productPrice;

    @BindView(R.id.priceTotal)
    TextView productTotalPrice;

    @BindView(R.id.wholeSalePrice)
    TextView productWholesalePrice;

    @BindView(R.id.quantity)
    EditText productQuantity;

    @BindView(R.id.variation_title)
    TextView variationTitle;

    @BindView(R.id.add_to_cart)
    TextView addToCartBtn;

    @BindView(R.id.buy_now)
    TextView checkOutBtn;

    @BindView(R.id.variationHolder)
    LinearLayout variationHolder;


    // checkout
    CheckBox selectAll;
    Button checkout;
    Button btnBottomSheet;
    EditText customAddress, contact_number;
    BottomSheetDialog bottomSheetDialog;
    View bottomSheetView;


    @BindView(R.id.recyclerViewVariation)
    RecyclerView recyclerVariation;
    private VariationAdapter adapterVariation;

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


        itemsList = new ArrayList<>();
        adapterVariation = new VariationAdapter(itemsList, context, this);

        LinearLayoutManager managerVariation = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerVariation.setLayoutManager(managerVariation);
        recyclerVariation.setAdapter(adapterVariation);


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



        getProductDetails();



        // bottom sheet

        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_checkout, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        btnBottomSheet = bottomSheetView.findViewById(R.id.bs_button);



        btnBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        // bottom sheet

        contact_number = bottomSheetView.findViewById(R.id.contact_number);
        customAddress = bottomSheetView.findViewById(R.id.customAddress);
        contact_number.setText(userDetails.getPhone());
        customAddress.setText(userDetails.getJsonAddress());



        checkOutBtn.setOnClickListener(view1 -> {

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

                            ShopItem firstItem = itemsList.get(0);
                            productName.setText(firstItem.getShopItemName());
                            shopName.setText("Seller: " + firstItem.getShopName());
                            productPrice.setText("৳ "+firstItem.getShopItemPrice() + " x 1");
                            productQuantity.setText("1");
                            productTotalPrice.setText("৳ "+firstItem.getShopItemPrice());

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

                                AttributesItem attributesItem = firstItem.getAttributes().get(0);
                                String varName = attributesItem.getName();
                                String varValue = attributesItem.getValue();
                                variationTitle.setText(varName + ": " + varValue);

                            } else
                                variationHolder.setVisibility(View.GONE);

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


}
