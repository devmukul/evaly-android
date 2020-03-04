package bd.com.evaly.evalyshop.ui.shop.adapter;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.shop.shopDetails.Data;
import bd.com.evaly.evalyshop.models.shop.shopDetails.Shop;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.ui.reviews.ReviewsActivity;
import bd.com.evaly.evalyshop.ui.shop.ShopViewModel;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class ShopProductHeader extends RecyclerView.ViewHolder {

    View view;
    private Fragment fragmentInstance;
    private AppCompatActivity activityInstance;
    private NavController navController;
    private Context context;
    private String slug = "", title = "", categoryString = "", imgUrl = "", categorySlug = "";
    private ImageView logo;
    private TextView name, categoryName, address, number;
    private ShopCategoryAdapter adapterShopCategory;
    private ProgressBar progressBar;
    private View dummyView;
    private HashMap<String, String> data;
    private String groups = "", owner_number = "", shop_name = "", campaign_slug = "", logo_image;
    private TextView tvOffer, followText;
    private ShimmerFrameLayout shimmer;
    private RecyclerView recyclerViewCategory;
    private ArrayList<TabsItem> itemListCategory;
    private LinearLayout callButton, location, link, reviews, llInbox, followBtn;
    private TextView categoryTitle;
    private TextView reset;
    private int currentPage = 1;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private ImageView placeholder;
    private String ratingJson = "{\"total_ratings\":0,\"avg_ratings\":\"0.0\",\"star_5\":0,\"star_4\":0,\"star_3\":0,\"star_2\":0,\"star_1\":0}";
    private UserDetails userDetails;
    private int subCount = 0;
    private ViewDialog dialog;
    private List<String> rosterList;
    private LinearLayout noItem;
    private View dummyViewTop;
    private ShopViewModel viewModel;
    private ShopDetailsModel shopDetails;

    public ShopProductHeader(View itemView, Context context, AppCompatActivity activityInstance, Fragment fragmentInstance, NavController navController, HashMap<String, String> data, ShopDetailsModel shopDetails, ShopViewModel viewModel) {
        super(itemView);
        this.context = context;
        this.fragmentInstance = fragmentInstance;
        this.activityInstance = activityInstance;
        this.navController = navController;
        this.data = data;
        this.shopDetails = shopDetails;
        this.viewModel = viewModel;


        Shop shop = shopDetails.getData().getShop();

        this.slug = shop.getSlug();
        this.title = shop.getName();

//        this.categoryString = data.get("categoryString");
//        this.categorySlug = data.get("categorySlug");

        view = itemView;
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.setFullSpan(true);
        initHeader(view);
    }

    private void initHeader(View view) {


        dummyViewTop = view.findViewById(R.id.dummyViewTop);
        dummyView = view.findViewById(R.id.dummyView);
        noItem = view.findViewById(R.id.noItem);
        dialog = new ViewDialog(activityInstance);
        name = view.findViewById(R.id.name);
        tvOffer = view.findViewById(R.id.tvOffer);
        address = view.findViewById(R.id.address);
        number = view.findViewById(R.id.number);
        logo = view.findViewById(R.id.logo);
        shimmer = view.findViewById(R.id.shimmer);
        callButton = view.findViewById(R.id.call_button);
        location = view.findViewById(R.id.location);
        link = view.findViewById(R.id.link);
        reviews = view.findViewById(R.id.reviews);
        llInbox = view.findViewById(R.id.llInbox);
        followText = view.findViewById(R.id.follow_text);

        placeholder = view.findViewById(R.id.placeholder_image);
        progressBar = view.findViewById(R.id.progressBar);
        categoryTitle = view.findViewById(R.id.categoryTitle);
        followBtn = view.findViewById(R.id.follow_btn);
        recyclerViewCategory = view.findViewById(R.id.categoriesRecycler);

        userDetails = new UserDetails(context);

        rosterList = new ArrayList<>();

        try {
            shimmer.startShimmer();
        } catch (Exception e) {

        }


        itemListCategory = new ArrayList<>();

        // type 4 means shop's category

        adapterShopCategory = new ShopCategoryAdapter(context, itemListCategory, viewModel);
        recyclerViewCategory.setAdapter(adapterShopCategory);

        recyclerViewCategory = view.findViewById(R.id.categoriesRecycler);
        recyclerViewCategory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dx > 0) //check for scroll down
                {
                    GridLayoutManager mLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = itemListCategory.size();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            viewModel.loadShopCategories(slug, ++currentPage, campaign_slug);
                        }
                    }
                }
            }
        });


        Data shopData = shopDetails.getData();
        Shop shopInfo = shopData.getShop();

        if (currentPage == 1 && categorySlug == null) {

            shop_name = shopInfo.getName();
            owner_number = shopInfo.getOwnerName();
            subCount = shopData.getSubscriberCount();
            logo_image = shopInfo.getLogoImage();

            if (shopData.isSubscribed())
                followText.setText(String.format(Locale.ENGLISH, "Unfollow (%d)", subCount));
            else
                followText.setText(String.format(Locale.ENGLISH, "Follow (%d)", subCount));

            // click listeners

            //  followBtn.setOnClickListener(v -> subscribe());

            name.setText(shop_name);

            if (logo.getDrawable() == null)
                if (context != null)
                    Glide.with(context)
                            .load(shopInfo.getLogoImage())
                            .skipMemoryCache(true)
                            .into(logo);

            callButton.setOnClickListener(v -> {
                String phone = shopInfo.getContactNumber();
                final Snackbar snackBar = Snackbar.make(view, phone + "", Snackbar.LENGTH_LONG);
                snackBar.setAction("Call", v12 -> {
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + shopInfo.getContactNumber()));
                        activityInstance.startActivity(intent);
                    } catch (Exception ignored) {
                    }
                    snackBar.dismiss();
                });
                snackBar.show();
            });


            location.setOnClickListener(v -> {
                String phone = shopInfo.getAddress();
                final Snackbar snackBar = Snackbar.make(view, phone + "", Snackbar.LENGTH_LONG);
                snackBar.setAction("Copy", v1 -> {
                    try {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("address", shopInfo.getAddress());
                        clipboard.setPrimaryClip(clip);
                    } catch (Exception ignored) {
                    }

                    snackBar.dismiss();
                });
                snackBar.show();
            });


            link.setOnClickListener(v -> {

//                DeliveryBottomSheetFragment deliveryBottomSheetFragment = DeliveryBottomSheetFragment.newInstance(shopDetails.getShopDeliveryOptions());
//
//                assert getFragmentManager() != null;
//                deliveryBottomSheetFragment.show(getFragmentManager(), "delivery option");
//
//
////                        String phone = "https://evaly.com.bd/shops/" + shopDetails.getSlug();
////                        final Snackbar snackBar = Snackbar.make(view, phone + "", Snackbar.LENGTH_LONG);
////                        snackBar.setAction("Copy", v13 -> {
////                            try {
////                                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
////                                ClipData clip = ClipData.newPlainText("Link", "https://evaly.com.bd/shops/" + shopDetails.getSlug());
////                                clipboard.setPrimaryClip(clip);
////                            } catch (Exception ignored) {
////                            }
////                            snackBar.dismiss();
////                        });
////                        snackBar.show();

            });


            reviews.setOnClickListener(v -> {
                String shop_id = slug;
                Intent intent = new Intent(context, ReviewsActivity.class);
                intent.putExtra("ratingJson", ratingJson);
                intent.putExtra("type", "shop");
                intent.putExtra("item_value", shop_id);
                activityInstance.startActivity(intent);
            });


            llInbox.setOnClickListener(v -> {

                viewModel.setOnChatClickLiveData(true);

            });


        }


        viewModel.getShopCategoryListLiveData().observe(fragmentInstance.getViewLifecycleOwner(), itemListCategory -> loadSubCategories(itemListCategory));

    }


    public void loadSubCategories(List<TabsItem> categoryList) {

        if (categoryList.size() < 4) {

            GridLayoutManager mLayoutManager = new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false);
            recyclerViewCategory.setLayoutManager(mLayoutManager);
        }

        if (itemListCategory.size() < 1)
            ((TextView) view.findViewById(R.id.catTitle)).setText(" ");

        try {

            shimmer.stopShimmer();
        } catch (Exception e) {
        }

        shimmer.setVisibility(View.GONE);
        loading = true;

        itemListCategory.addAll(categoryList);
        adapterShopCategory.notifyItemRangeInserted(itemListCategory.size() - categoryList.size(), itemListCategory.size());


    }


}