package bd.com.evaly.evalyshop.ui.shop;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.ui.chat.ChatListActivity;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.NetworkErrorDialogListener;
import bd.com.evaly.evalyshop.listener.ProductListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.Data;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ItemsItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.Shop;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.models.xmpp.PresenceModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ShopApiHelper;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.buynow.BuyNowFragment;
import bd.com.evaly.evalyshop.ui.chat.ChatDetailsActivity;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.networkError.NetworkErrorDialog;
import bd.com.evaly.evalyshop.ui.product.productList.adapter.ProductGridAdapter;
import bd.com.evaly.evalyshop.ui.reviews.ReviewsActivity;
import bd.com.evaly.evalyshop.ui.search.GlobalSearchActivity;
import bd.com.evaly.evalyshop.ui.shop.adapter.ShopCategoryAdapter;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.util.xmpp.XMPPService;
import bd.com.evaly.evalyshop.util.xmpp.XmppCustomEventListener;


public class ShopFragment extends Fragment implements ProductListener {

    private String slug = "", title = "", groups = "", owner_number = "", shop_name = "", campaign_slug="", logo_image;
    private String categorySlug = null;
    private ImageView logo;
    private TextView name, address, number, tvOffer, followText;
    private NestedScrollView nestedSV;
    private ShimmerFrameLayout shimmer;
    private RecyclerView recyclerView;
    private ShopCategoryAdapter adapter;
    private ArrayList<TabsItem> itemList;
    private List<ProductItem> productItemList;
    private ProductGridAdapter adapterProducts;
    private RecyclerView productRecyclerView;
    private ProgressBar progressBar;
    private LinearLayout callButton, location, link, reviews, llInbox, followBtn;
    private View view;
    private Context context;
    private MainActivity mainActivity;
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
    private View dummyView;
    private View dummyViewTop;



    @Override
    public void buyNow(String productSlug) {

        BuyNowFragment addPhotoBottomDialogFragment =
                BuyNowFragment.newInstance(slug, productSlug);
        addPhotoBottomDialogFragment.show(getActivity().getSupportFragmentManager(),
                "BuyNow");
    }

    @Override
    public void onSuccess(int count) {

        if (count == 0){
            ((TextView) view.findViewById(R.id.categoryTitle)).setText(" ");
            noItem.setVisibility(View.VISIBLE);
            try {
                if (getContext() != null)
                    Glide.with(context)
                            .load(R.drawable.ic_emptycart)
                            .apply(new RequestOptions().override(600, 600))
                            .into(placeholder);
            } catch (Exception ignored) { }
            progressBar.setVisibility(View.GONE);
        } else {
            noItem.setVisibility(View.GONE);
        }
    }


    public ShopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shop, container, false);
        context = getContext();
        mainActivity = (MainActivity) getActivity();
        if (!CredentialManager.getToken().equals(""))
            Executors.newSingleThreadExecutor().execute(() -> startXmppService());

        return view;
    }


    private void refreshFragment(){
        NavHostFragment.findNavController(ShopFragment.this).navigate(R.id.shopFragment);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!Utils.isNetworkAvailable(context))
            new NetworkErrorDialog(context, new NetworkErrorDialogListener() {
                @Override
                public void onRetry() {
                    refreshFragment();
                }
                @Override
                public void onBackPress() {
                    if (getFragmentManager() != null)
                        NavHostFragment.findNavController(ShopFragment.this).navigate(R.id.homeFragment);
                }
            });


        new InitializeActionBar( view.findViewById(R.id.header_logo), mainActivity, "shop");
        LinearLayout homeSearch = view.findViewById(R.id.home_search);
        homeSearch.setOnClickListener(view12 -> {
            Intent intent = new Intent(context, GlobalSearchActivity.class);
            intent.putExtra("type", 1);
            startActivity(intent);
        });


        dummyViewTop = view.findViewById(R.id.dummyViewTop);
        dummyView = view.findViewById(R.id.dummyView);
        noItem = view.findViewById(R.id.noItem);
        dialog = new ViewDialog(getActivity());
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


        if (getArguments() == null){

            Toast.makeText(context,"Shop not available", Toast.LENGTH_SHORT).show();
            return;
        }

        title = getArguments().getString("shop_name");

        if (getArguments().getString("campaign_slug") != null)
            campaign_slug = getArguments().getString("campaign_slug");

        name.setText(title);


        if (getArguments().getString("logo_image") != null) {
            if (getContext() != null)
                Glide.with(context)
                        .load(getArguments().getString("logo_image"))
                        .into(logo);
        }

        itemList = new ArrayList<>();

        // type 4 means shop's category

        adapter = new ShopCategoryAdapter(context, itemList, this);
        recyclerView.setAdapter(adapter);

        slug = getArguments().getString("shop_slug");

        productRecyclerView = view.findViewById(R.id.productsRecyclerView);

        productItemList = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        productRecyclerView.setLayoutManager(mLayoutManager);
        productRecyclerView.setNestedScrollingEnabled(false);
        productRecyclerView.setHasFixedSize(false);
        adapterProducts = new ProductGridAdapter(context, productItemList);
        adapterProducts.setHasStableIds(true);
        adapterProducts.setproductListener(this);
        adapterProducts.setShopSlug(slug);
        productRecyclerView.setAdapter(adapterProducts);


        getProductRating(slug);
        getSubCategories(currentPage);

        getShopProductCount();

        nestedSV = view.findViewById(R.id.stickyScrollView);

        nestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                getShopProductCount();
            }
        });

        reset = view.findViewById(R.id.resetBtn);

        reset.setVisibility(View.GONE);

        reset.setOnClickListener(view1 -> {
            showProductsByCategory("All Products", null, 0);
            reset.setVisibility(View.GONE);
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        mChatApp.getEventReceiver().setListener(xmppCustomEventListener);
    }

    private void startXmppService() {

        //Start XMPP Service (if not running already)
        if (getContext() != null && getActivity() != null) {
            if (!XMPPService.isServiceRunning) {
                Intent intent = new Intent(getActivity(), XMPPService.class);
                mChatApp.UnbindService();
                mChatApp.BindService(intent);
            } else {
                xmppHandler = AppController.getmService().xmpp;
                if (!xmppHandler.isConnected()) {
                    xmppHandler.connect();
                } else {
                    xmppHandler.setUserPassword(CredentialManager.getUserName(), CredentialManager.getPassword());
                    xmppHandler.login();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disconnectXmpp();
    }

    private void disconnectXmpp(){
        if (xmppHandler != null){
            xmppHandler.disconnect();
        }
        getActivity().stopService(new Intent(getActivity(), XMPPService.class));
    }

    public void showProductsByCategory(String categoryName, String categorySlug, int position) {

        this.categorySlug = categorySlug;
        reset.setVisibility(View.VISIBLE);
        categoryTitle.setText(categoryName);
        productItemList.clear();
        adapterProducts.notifyDataSetChanged();
        currentPage = 1;
        getShopProductCount();

        dummyView.setVisibility(View.VISIBLE);

        nestedSV.postDelayed(() -> {

            AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout);

            appBarLayout.setExpanded(false, true);

            TextView tv = view.findViewById(R.id.catTitle);
            int scrollTo = ((View) tv.getParent()).getTop() + tv.getTop() + 30;
            nestedSV.smoothScrollTo(0, scrollTo);
        }, 100);



    }


    public void getShopProductCount() {

        progressBar.setVisibility(View.VISIBLE);

        if (currentPage==1)
            dummyViewTop.setVisibility(View.VISIBLE);

        ShopApiHelper.getShopDetailsItem(CredentialManager.getToken(), slug , currentPage, 21, categorySlug, campaign_slug, new ResponseListenerAuth<ShopDetailsModel, String>() {
            @Override
            public void onDataFetched(ShopDetailsModel response, int statusCode) {


                dummyView.setVisibility(View.GONE);
                dummyViewTop.setVisibility(View.GONE);

                Data shopData = response.getData();
                Shop shopDetails = shopData.getShop();

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
                        String phone = "https://evaly.com.bd/shops/" + shopDetails.getSlug();
                        final Snackbar snackBar = Snackbar.make(view, phone + "", Snackbar.LENGTH_LONG);
                        snackBar.setAction("Copy", v13 -> {
                            try {
                                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Link", "https://evaly.com.bd/shops/" + shopDetails.getSlug());
                                clipboard.setPrimaryClip(clip);
                            } catch (Exception ignored) {
                            }
                            snackBar.dismiss();
                        });
                        snackBar.show();

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

                    if (shopData.getMeta() != null){
                        int cashbackRate =shopData.getMeta().get("cashback_rate").getAsInt();
                        adapterProducts.setCashback_rate(cashbackRate);
                    }

                    if (shopItems.size() == 0) {
                        noItem.setVisibility(View.VISIBLE);
                        categoryTitle.setVisibility(View.GONE);
                    }


                }

                progressBar.setVisibility(View.INVISIBLE);
                productRecyclerView.setVisibility(View.VISIBLE);


                for (int i=0; i<shopItems.size(); i++){
                    if (i==0)
                        currentPage++;

                    ItemsItem shopItem = shopItems.get(i);

                    ProductItem item = new ProductItem();
                    item.setImageUrls(shopItem.getItemImages());
                    item.setSlug(shopItem.getShopItemSlug());
                    item.setName(shopItem.getItemName());
                    item.setMaxPrice(String.valueOf(shopItem.getItemPrice()));
                    item.setMinPrice(String.valueOf(shopItem.getItemPrice()));
                    item.setMinDiscountedPrice(String.valueOf(shopItem.getDiscountedPrice()));

                    productItemList.add(item);

                    adapterProducts.notifyItemInserted(productItemList.size());

                }

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }


    private void setUpXmpp(){


        if (CredentialManager.getToken() == null || CredentialManager.getToken().equals("")) {
            startActivity(new Intent(getActivity(), SignInActivity.class));
            getActivity().finish();
        } else {
            if (xmppHandler != null && xmppHandler.isConnected() && xmppHandler.isLoggedin()) {
                String jid = getContactFromRoster(owner_number);
                if (!CredentialManager.getUserName().equalsIgnoreCase(owner_number)) {
                    if (jid != null) {
                        dialog.hideDialog();
                        VCard vCard = null;
                        try {
                            vCard = xmppHandler.getUserDetails(JidCreate.entityBareFrom(jid));
                            RosterTable rosterTable = new RosterTable();
                            rosterTable.name = shop_name;
                            rosterTable.id = vCard.getFrom().asUnescapedString();
                            rosterTable.imageUrl = logo_image;
                            rosterTable.status = 0;
                            rosterTable.lastMessage = "";
                            rosterTable.nick_name = vCard.getNickName();
                            rosterTable.time = 0;
                            Logger.d(new Gson().toJson(rosterTable));
                            startActivity(new Intent(getActivity(), ChatDetailsActivity.class).putExtra("roster", rosterTable));

                        } catch (XmppStringprepException e) {
                            e.printStackTrace();
                        }
                    } else {
                        dialog.showDialog();
                        HashMap<String, String> data1 = new HashMap<>();
                        data1.put("localuser", CredentialManager.getUserName());
                        data1.put("localserver", Constants.XMPP_HOST);
                        data1.put("user", owner_number);
                        data1.put("server", Constants.XMPP_HOST);
                        data1.put("nick", shop_name);
                        data1.put("subs", "both");
                        data1.put("group", "evaly");

                        AuthApiHelper.addRoster(data1, new DataFetchingListener<retrofit2.Response<JsonPrimitive>>() {
                            @Override
                            public void onDataFetched(retrofit2.Response<JsonPrimitive> response1) {
                                if (response1.code() == 200 || response1.code() == 201)
                                    addRosterByOther();
                                else
                                    Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void onFailed(int status) {
                                dialog.hideDialog();
                                Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else
                    Toast.makeText(getContext(), "You can't invite yourself!", Toast.LENGTH_LONG).show();
            } else
                startXmppService();
        }
    }


    private String getContactFromRoster(String number) {
        String roasterModel = null;
        for (String model : rosterList) {
            if (model.contains(number)) {
                roasterModel = model;
            }
        }
        return roasterModel;
    }

    public void getProductRating(final String sku) {

        GeneralApiHelper.getShopReviews(CredentialManager.getToken(), sku, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                try {
                    response = response.getAsJsonObject("data");
                    ratingJson = response.toString();
                    double avg = response.get("avg_rating").getAsDouble();
                    RatingBar ratingBar = view.findViewById(R.id.ratingBar);
                    TextView ratingsCount = view.findViewById(R.id.ratings_count);
                    int tratings = response.get("total_ratings").getAsInt();
                    ratingsCount.setText("(" + tratings + ")");
                    ratingBar.setRating((float) avg);

                } catch (Exception e) {
                    e.printStackTrace();
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



    public void getSubCategories(int currentPage) {

        ProductApiHelper.getCategoriesOfShop(slug, campaign_slug, currentPage, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                try {
                    JsonArray jsonArray = response.getAsJsonArray("data");

                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject ob = jsonArray.get(i).getAsJsonObject();
                        TabsItem tabsItem = new TabsItem();
                        tabsItem.setTitle(ob.get("category_name").getAsString());
                        tabsItem.setImage(ob.get("category_image").getAsString());
                        tabsItem.setSlug(ob.get("category_slug").getAsString());
                        tabsItem.setCategory(slug);
                        itemList.add(tabsItem);
                        adapter.notifyItemInserted(itemList.size());
                    }

                    if (itemList.size() < 4) {

                        GridLayoutManager mLayoutManager = new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false);
                        recyclerView.setLayoutManager(mLayoutManager);
                    }

                    if (itemList.size() < 1)
                        ((TextView) view.findViewById(R.id.catTitle)).setText(" ");

                    try {

                        shimmer.stopShimmer();
                    } catch (Exception e) {
                    }

                    shimmer.setVisibility(View.GONE);
                    loading = true;

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Category load error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                loading = false;

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
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


    private void addRosterByOther() {

        if (CredentialManager.getUserData() !=  null) {

            if (CredentialManager.getUserData().getFirst_name()== null)
                CredentialManager.getUserData().setFirst_name("");

            HashMap<String, String> data = new HashMap<>();
            data.put("localuser", shop_name);
            data.put("localserver", Constants.XMPP_HOST);
            data.put("user", CredentialManager.getUserName());
            data.put("server", Constants.XMPP_HOST);
            data.put("nick", CredentialManager.getUserData().getFirst_name());
            data.put("subs", "both");
            data.put("group", "evaly");
            AuthApiHelper.addRoster(data, new DataFetchingListener<retrofit2.Response<JsonPrimitive>>() {
                @Override
                public void onDataFetched(retrofit2.Response<JsonPrimitive> response) {
                    try {
                        EntityBareJid jid = JidCreate.entityBareFrom(owner_number + "@"
                                + Constants.XMPP_HOST);
                        VCard mVCard = xmppHandler.getUserDetails(jid);
                        HashMap<String, String> data1 = new HashMap<>();
                        data1.put("phone_number", owner_number);
                        data1.put("text", "You are invited to \n https://play.google.com/store/apps/details?id=bd.com.evaly.evalyshop");

                        Logger.d(new Gson().toJson(mVCard.getFirstName()) + "       ====");
                        if (mVCard.getFirstName() == null) {
                            dialog.hideDialog();
                            RosterTable table = new RosterTable();
                            table.id = jid.asUnescapedString();
                            table.rosterName = shop_name;
                            table.name = "";
                            table.status = 0;
                            table.unreadCount = 0;
                            table.nick_name = "";
                            table.imageUrl = logo_image;
                            table.lastMessage = "";

                            startActivity(new Intent(getActivity(), ChatDetailsActivity.class).putExtra("roster", table));
                        } else {
                            dialog.hideDialog();
                            RosterTable rosterTable = new RosterTable();
                            rosterTable.name = vCard.getFirstName() + " " + vCard.getLastName();
                            rosterTable.id = vCard.getFrom().asUnescapedString();
                            rosterTable.imageUrl = vCard.getField("URL");
                            rosterTable.status = 0;
                            rosterTable.lastMessage = "";
                            rosterTable.nick_name = vCard.getNickName();
                            rosterTable.time = 0;
                            startActivity(new Intent(getActivity(), ChatDetailsActivity.class).putExtra("roster", rosterTable));

                        }
                    } catch (XmppStringprepException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(int status) {

                }
            });
        }
    }


    public void subscribe() {

        if (userDetails.getToken().equals("")) {
            Toast.makeText(context, "You need to login first to follow a shop", Toast.LENGTH_LONG).show();
            return;
        }

        boolean subscribe = true;

        if (followText.getText().toString().contains("Unfollow")) {
            subscribe = false;
            followText.setText("Follow (" + (--subCount) + ")");
        } else
            followText.setText("Unfollow (" + (++subCount) + ")");


        GeneralApiHelper.subscribeToShop(CredentialManager.getToken(), slug, subscribe, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

                if (!logout)
                    subscribe();

            }
        });


    }

}
