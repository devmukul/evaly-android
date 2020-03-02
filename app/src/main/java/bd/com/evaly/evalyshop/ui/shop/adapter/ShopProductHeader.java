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
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.listener.ProductListener;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ItemsItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.Shop;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.models.xmpp.PresenceModel;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.product.productList.ProductGrid;
import bd.com.evaly.evalyshop.ui.product.productList.adapter.ProductGridAdapter;
import bd.com.evaly.evalyshop.ui.reviews.ReviewsActivity;
import bd.com.evaly.evalyshop.ui.shop.delivery.DeliveryBottomSheetFragment;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.util.xmpp.XmppCustomEventListener;

public class ShopProductHeader extends RecyclerView.ViewHolder implements ProductListener {

    View view;
    private Fragment fragmentInstance;
    private AppCompatActivity activityInstance;
    private NavController navController;
    private Context context;
    private String slug = "", title = "", categoryString = "", imgUrl = "", categorySlug = "";
    private ImageView logo;
    private TextView name, categoryName, address, number;
    private NestedScrollView nestedSV;
    private ShopCategoryAdapter adapter;
    private MainActivity mainActivity;
    private ProductGrid productGrid;
    private ImageView placeHolder;
    private ProgressBar progressBar;
    private View dummyView;
    private HashMap<String, String> data;
    private String groups = "", owner_number = "", shop_name = "", campaign_slug = "", logo_image;
    private TextView  tvOffer, followText;
    private ShimmerFrameLayout shimmer;
    private RecyclerView recyclerView;
    private ArrayList<TabsItem> itemList;
    private List<ProductItem> productItemList;
    private ProductGridAdapter adapterProducts;
    private RecyclerView productRecyclerView;
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
    private VCard vCard;
    private AppController mChatApp = AppController.getInstance();
    private XMPPHandler xmppHandler;
    private List<String> rosterList;
    private LinearLayout noItem;
    private View dummyViewTop;

    public ShopProductHeader(View itemView, Context context, AppCompatActivity activityInstance, Fragment fragmentInstance, NavController navController, HashMap<String, String> data) {
        super(itemView);
        this.context = context;
        this.fragmentInstance = fragmentInstance;
        this.activityInstance = activityInstance;
        this.navController = navController;
        this.data = data;

        this.slug = data.get("slug");
        this.title = data.get("title");
        this.categoryString = data.get("categoryString");
        this.categorySlug = data.get("categorySlug");

        view = itemView;
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.setFullSpan(true);
        initHeader(view);
    }

    private void initHeader(View view) {


        dummyViewTop = view.findViewById(R.id.dummyViewTop);
        dummyView = view.findViewById(R.id.dummyView);
        noItem = view.findViewById(R.id.noItem);
        dialog = new ViewDialog(activityInstance));
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

        userDetails = new UserDetails(context);

        rosterList = new ArrayList<>();

        if (xmppHandler != null) {
            rosterList = xmppHandler.rosterList;
        }

        try {
            shimmer.startShimmer();
        } catch (Exception e) {

        }



        itemList = new ArrayList<>();

        // type 4 means shop's category

        adapter = new ShopCategoryAdapter(context, itemList, fragmentInstance);
        recyclerView.setAdapter(adapter);

        recyclerView = view.findViewById(R.id.categoriesRecycler);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dx > 0) //check for scroll down
                {
                    GridLayoutManager mLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = itemList.size();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            getSubCategories(++currentPage);
                        }
                    }
                }
            }
        });


        Shop shopDetails = itemList.get();

        List<ItemsItem> shopItems = shopData.getItems();

        if (currentPage == 1 && categorySlug == null) {

            shop_name = shopDetails.getName();
            owner_number = shopDetails.getOwnerName();
            subCount = shopData.getSubscriberCount();
            logo_image = shopDetails.getLogoImage();

            if (shopData.isSubscribed())
                followText.setText(String.format(Locale.ENGLISH, "Unfollow (%d)", subCount));
            else
                followText.setText(String.format(Locale.ENGLISH, "Follow (%d)", subCount));

            // click listeners

            followBtn.setOnClickListener(v -> subscribe());

            name.setText(shop_name);

            if (logo.getDrawable() == null)
                if (getContext() != null)
                    Glide.with(context)
                            .load(shopDetails.getLogoImage())
                            .skipMemoryCache(true)
                            .into(logo);

            callButton.setOnClickListener(v -> {
                String phone = shopDetails.getContactNumber();
                final Snackbar snackBar = Snackbar.make(view, phone + "", Snackbar.LENGTH_LONG);
                snackBar.setAction("Call", v12 -> {
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + shopDetails.getContactNumber()));
                        startActivity(intent);
                    } catch (Exception ignored) {
                    }
                    snackBar.dismiss();
                });
                snackBar.show();
            });


            location.setOnClickListener(v -> {
                String phone = shopDetails.getAddress();
                final Snackbar snackBar = Snackbar.make(view, phone + "", Snackbar.LENGTH_LONG);
                snackBar.setAction("Copy", v1 -> {
                    try {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("address", shopDetails.getAddress());
                        clipboard.setPrimaryClip(clip);
                    } catch (Exception ignored) {
                    }

                    snackBar.dismiss();
                });
                snackBar.show();
            });


            link.setOnClickListener(v -> {

                DeliveryBottomSheetFragment deliveryBottomSheetFragment = DeliveryBottomSheetFragment.newInstance(shopDetails.getShopDeliveryOptions());

                assert getFragmentManager() != null;
                deliveryBottomSheetFragment.show(getFragmentManager(), "delivery option");


//                        String phone = "https://evaly.com.bd/shops/" + shopDetails.getSlug();
//                        final Snackbar snackBar = Snackbar.make(view, phone + "", Snackbar.LENGTH_LONG);
//                        snackBar.setAction("Copy", v13 -> {
//                            try {
//                                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//                                ClipData clip = ClipData.newPlainText("Link", "https://evaly.com.bd/shops/" + shopDetails.getSlug());
//                                clipboard.setPrimaryClip(clip);
//                            } catch (Exception ignored) {
//                            }
//                            snackBar.dismiss();
//                        });
//                        snackBar.show();

            });


            reviews.setOnClickListener(v -> {
                String shop_id = slug;
                Intent intent = new Intent(context, ReviewsActivity.class);
                intent.putExtra("ratingJson", ratingJson);
                intent.putExtra("type", "shop");
                intent.putExtra("item_value", shop_id);
                startActivity(intent);
            });


            llInbox.setOnClickListener(v -> {

                setUpXmpp();

            });

            if (shopData.getMeta() != null) {
                int cashbackRate = shopData.getMeta().get("cashback_rate").getAsInt();
                adapterProducts.setCashback_rate(cashbackRate);
            }

            if (shopItems.size() == 0) {
                noItem.setVisibility(View.VISIBLE);
                categoryTitle.setVisibility(View.GONE);
            }


        }

    }


    public XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {

        //On User Presence Changed
        public void onPresenceChanged(PresenceModel presenceModel) {

            // Logger.d(presenceModel.getUserStatus());
        }

        public void onConnected() {
            Logger.d("===========");
            xmppHandler = AppController.getmService().xmpp;
            rosterList = xmppHandler.rosterList;
            if (!owner_number.equals("")) {
                try {
                    Logger.d(owner_number);
                    EntityBareJid jid = JidCreate.entityBareFrom(owner_number + "@"
                            + Constants.XMPP_HOST);
                    vCard = xmppHandler.getUserDetails(jid);
                    Logger.d(new Gson().toJson(vCard));
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onSuccess(int count) {

        if (count == 0) {
            ((TextView) view.findViewById(R.id.categoryTitle)).setText(" ");
            noItem.setVisibility(View.VISIBLE);
            try {
                if (context != null)
                    Glide.with(context)
                            .load(R.drawable.ic_emptycart)
                            .apply(new RequestOptions().override(600, 600))
                            .into(placeholder);
            } catch (Exception ignored) {
            }
            progressBar.setVisibility(View.GONE);
        } else {
            noItem.setVisibility(View.GONE);
        }
    }


}