package bd.com.evaly.evalyshop.activity.buynow;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.ReviewsActivity;
import bd.com.evaly.evalyshop.activity.buynow.adapter.VariationAdapter;
import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;
import bd.com.evaly.evalyshop.models.shopItem.ShopItem;
import bd.com.evaly.evalyshop.models.user.User;
import bd.com.evaly.evalyshop.reviewratings.BarLabels;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BuyNowFragment extends BottomSheetDialogFragment {


    private Context context;
    private UserDetails userDetails;
    private RequestQueue rq;
    private ArrayList<ShopItem> itemsList;


    String shop_slug = "tvs-bangladesh";
    String shop_item_slug = "tvs-apache-rtr-160cc-single-disc";

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


    // checkout
    @BindView(R.id.customAddress)
    EditText customAddress;

    @BindView(R.id.contact_number)
    EditText contactNumber;


    @BindView(R.id.recyclerViewVariation)
    RecyclerView recyclerVariation;
    private VariationAdapter adapterVariation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //bottom sheet round corners can be obtained but the while background appears to remove that we need to add this.
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
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

        customAddress.setText(userDetails.getJsonAddress());
        contactNumber.setText(userDetails.getPhone());



        itemsList = new ArrayList<>();
        adapterVariation = new VariationAdapter(itemsList, context);

        LinearLayoutManager managerVariation = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerVariation.setLayoutManager(managerVariation);

        recyclerVariation.setAdapter(adapterVariation);


        getProductDetails();


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

    public static BuyNowFragment newInstance() {
        return new BuyNowFragment();
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

                            Glide.with(context)
                                    .load(firstItem.getShopItemImage())
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .apply(new RequestOptions().override(250, 250))
                                    .into(productImage);


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
