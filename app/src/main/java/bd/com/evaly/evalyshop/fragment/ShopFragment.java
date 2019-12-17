package bd.com.evaly.evalyshop.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.ProductGrid;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.GlobalSearchActivity;
import bd.com.evaly.evalyshop.activity.InitializeActionBar;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.activity.ReviewsActivity;
import bd.com.evaly.evalyshop.activity.SignInActivity;
import bd.com.evaly.evalyshop.activity.buynow.BuyNowFragment;
import bd.com.evaly.evalyshop.activity.chat.ChatDetailsActivity;
import bd.com.evaly.evalyshop.adapter.ShopCategoryAdapter;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.ProductListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.TabsItem;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.PresenceModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.xmpp.XMPPService;
import bd.com.evaly.evalyshop.xmpp.XmppCustomEventListener;


public class ShopFragment extends Fragment implements ProductListener {

    String slug = "", title = "", groups = "", owner_number = "", shop_name = "", campaign_slug="";
    ImageView logo;
    TextView name, address, number, tvOffer, followText;
    NestedScrollView nestedSV;

    ShimmerFrameLayout shimmer;
    RecyclerView recyclerView;
    ShopCategoryAdapter adapter;
    ArrayList<TabsItem> itemList;
    LinearLayout callButton, location, link, reviews, llInbox, followBtn;
    View view;
    Context context;
    MainActivity mainActivity;
    ProductGrid productGrid;
    private TextView categoryTitle;
    private TextView reset;
    int currentPage = 1;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    ImageView placeholder;
    ProgressBar progressBar;
    String ratingJson = "{\"total_ratings\":0,\"avg_ratings\":\"0.0\",\"star_5\":0,\"star_4\":0,\"star_3\":0,\"star_2\":0,\"star_1\":0}";
    RequestQueue rq;
    UserDetails userDetails;
    int subCount = 0;
    private ViewDialog dialog;

    private VCard vCard;
    AppController mChatApp = AppController.getInstance();

    XMPPHandler xmppHandler;
    private List<String> rosterList;


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
            LinearLayout noItem = view.findViewById(R.id.noItem);
            noItem.setVisibility(View.VISIBLE);

            try {
                Glide.with(context)
                        .load(R.drawable.ic_emptycart)
                        .apply(new RequestOptions().override(600, 600))
                        .into(placeholder);
            } catch (Exception e) {

            }

            progressBar.setVisibility(View.GONE);

        }

    }


    public ShopFragment() {
        // Required empty public constructor
    }

    public XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {

        //On User Presence Changed
        public void onPresenceChanged(PresenceModel presenceModel) {

            // Logger.d(presenceModel.getUserStatus());
        }

        public void onConnected() {
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shop, container, false);
        context = getContext();
        mainActivity = (MainActivity) getActivity();
        rq = Volley.newRequestQueue(context);

        if (!CredentialManager.getToken().equals("")) {
            startXmppService();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InitializeActionBar InitializeActionbar = new InitializeActionBar( view.findViewById(R.id.header_logo), mainActivity, "shop");
        LinearLayout homeSearch = view.findViewById(R.id.home_search);
        homeSearch.setOnClickListener(view12 -> {
            Intent intent = new Intent(context, GlobalSearchActivity.class);
            intent.putExtra("type", 1);
            startActivity(intent);
        });
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


        title = getArguments().getString("shop_name");

        if (getArguments().getString("campaign_slug") != null)
            campaign_slug = getArguments().getString("campaign_slug");

        name.setText(title);


        if (getArguments().getString("logo_image") != null) {
            Glide.with(context)
                    .load(getArguments().getString("logo_image"))
                    .listener(new RequestListener<Drawable>() {
                                  @Override
                                  public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                      return false;
                                  }

                                  @Override
                                  public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                      Bitmap bitmap = Utils.changeColor(((BitmapDrawable) resource).getBitmap(), Color.parseColor("#ecf3f9"), Color.WHITE);
                                      logo.setImageBitmap(bitmap);
                                      return true;
                                  }
                              }
                    )
                    .into(logo);
        }


        itemList = new ArrayList<>();

        // type 4 means shop's category

        adapter = new ShopCategoryAdapter(context, itemList, this);
        recyclerView.setAdapter(adapter);
        Logger.d(getArguments());

        slug = getArguments().getString("shop_slug");

        if (groups != null && groups.equalsIgnoreCase("evaly1919")) {
            tvOffer.setVisibility(View.VISIBLE);
        } else {
            tvOffer.setVisibility(View.GONE);
        }


        getShopProductCount();
        getProductRating(slug);
        getSubCategories(currentPage);

        AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout);
        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar_layout);
        CoordinatorLayout rootLayout = view.findViewById(R.id.root_coordinator);

        nestedSV = view.findViewById(R.id.stickyScrollView);

        if (nestedSV != null) {

            nestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                String TAG = "nested_sync";

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    Log.i(TAG, "BOTTOM SCROLL");

                    try {
                        productGrid.loadNextShopProducts();
                    } catch (Exception e) {
                        Log.e("scroll error", e.toString());
                    }
                }
            });
        }

        reset = view.findViewById(R.id.resetBtn);

        reset.setVisibility(View.GONE);

        reset.setOnClickListener(view1 -> {
            showProductsByCategory("All Products", "", 0);
            reset.setVisibility(View.GONE);
        });



        productGrid = new ProductGrid(mainActivity, view.findViewById(R.id.products), slug, "", campaign_slug, 1, progressBar);
        productGrid.setScrollView(nestedSV);
        productGrid.setListener(this);

    }

    private void startXmppService() {

        //Start XMPP Service (if not running already)
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


    public void showProductsByCategory(String categoryName, String categorySlug, int position) {
        reset.setVisibility(View.VISIBLE);
        categoryTitle.setText(categoryName);
        productGrid = new ProductGrid(mainActivity, view.findViewById(R.id.products), slug, categorySlug, campaign_slug, 1, view.findViewById(R.id.progressBar));
        productGrid.setScrollView(nestedSV);
        productGrid.setListener(this);
    }

    public void getShopProductCount() {

        String url;

        if (campaign_slug.equals(""))
            url = UrlUtils.BASE_URL + "public/shops/items/" + slug + "/?page=" + currentPage;
        else
            url = UrlUtils.CAMPAIGNS+"/" + campaign_slug + "/shops/"+slug+"/items?page=" + currentPage;


        Log.d("json url", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null,
                response -> {
                    try {
                        Log.d("json response", response.toString());

                        JSONObject data = response.getJSONObject("data");
                        JSONObject jsonObject = data.getJSONObject("shop");
                        boolean subscribed = data.getBoolean("subscribed");

                        shop_name = jsonObject.getString("name");
                        owner_number = jsonObject.getString("owner_name");
                        subCount = data.getInt("subscriber_count");

                        llInbox.setOnClickListener(v -> {
                            if (userDetails.getToken() == null || userDetails.getToken().equals("")) {
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
                                                rosterTable.name = vCard.getFirstName() + " " + vCard.getLastName();
                                                rosterTable.id = vCard.getFrom().asUnescapedString();
                                                rosterTable.imageUrl = vCard.getField("URL");
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


                                            Logger.d(data1);
                                            AuthApiHelper.addRoster(data1, new DataFetchingListener<retrofit2.Response<JsonPrimitive>>() {
                                                @Override
                                                public void onDataFetched(retrofit2.Response<JsonPrimitive> response1) {

                                                    if (response1.code() == 200 || response1.code() == 201) {
                                                        addRosterByOther();


                                                    } else {
                                                        Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailed(int status) {
                                                    dialog.hideDialog();
                                                    Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "You can't invite yourself!", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    startXmppService();
                                }
                            }
                        });

                        if (subscribed)
                            followText.setText("Unfollow (" + subCount + ")");
                        else
                            followText.setText("Follow (" + subCount + ")");

                        if (response.getInt("count") > 0) {

                            try {
                                followBtn.setOnClickListener(v -> subscribe());

                            } catch (Exception e) {
                            }

                            try {
                                name.setText(jsonObject.getString("name"));
                                address.setText(jsonObject.getString("address"));
                                try {
                                    Glide.with(context)
                                            .load(jsonObject.getString("logo_image"))
                                            .listener(new RequestListener<Drawable>() {
                                                          @Override
                                                          public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                              return false;
                                                          }

                                                          @Override
                                                          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                              Bitmap bitmap = Utils.changeColor(((BitmapDrawable) resource).getBitmap(), Color.parseColor("#ecf3f9"), Color.WHITE);
                                                              logo.setImageBitmap(bitmap);
                                                              return true;
                                                          }
                                                      }
                                            )
                                            .into(logo);
                                } catch (Exception e) {

                                }

                                try {
                                    number.setText(jsonObject.getString("contact_number"));
                                    callButton.setOnClickListener(v -> {
                                        String phone = "Not provided";
                                        try {
                                            phone = jsonObject.getString("contact_number");
                                            final Snackbar snackBar = Snackbar.make(view, phone + "", Snackbar.LENGTH_LONG);
                                            snackBar.setAction("Call", v12 -> {

                                                try {
                                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                                    intent.setData(Uri.parse("tel:" + jsonObject.getString("contact_number")));
                                                    startActivity(intent);
                                                } catch (Exception e) {
                                                }
                                                snackBar.dismiss();
                                            });
                                            snackBar.show();

                                        } catch (JSONException e) {
                                            Toast.makeText(context, "Sorry shop number is not available", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    });


                                } catch (Exception e) {
                                    number.setText("");
                                }

                                location.setOnClickListener(v -> {
                                    String phone = "Not provided";
                                    try {
                                        phone = jsonObject.getString("address");
                                        final Snackbar snackBar = Snackbar.make(view, phone + "", Snackbar.LENGTH_LONG);
                                        snackBar.setAction("Copy", v1 -> {

                                            try {
                                                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clip = ClipData.newPlainText("address", jsonObject.getString("address"));
                                                clipboard.setPrimaryClip(clip);
                                            } catch (Exception e) {
                                            }

                                            snackBar.dismiss();
                                        });
                                        snackBar.show();

                                    } catch (JSONException e) {
                                        Toast.makeText(context, "Sorry shop number is not available", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }

                                });


                                link.setOnClickListener(v -> {

                                    String phone = "https://evaly.com.bd/";
                                    try {
                                        phone = "https://evaly.com.bd/shops/" + jsonObject.getString("slug");
                                        final Snackbar snackBar = Snackbar.make(view, phone + "", Snackbar.LENGTH_LONG);
                                        snackBar.setAction("Copy", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                try {
                                                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                                    ClipData clip = ClipData.newPlainText("Link", "https://evaly.com.bd/shops/" + jsonObject.getString("slug"));
                                                    clipboard.setPrimaryClip(clip);
                                                } catch (Exception e) {
                                                }

                                                snackBar.dismiss();
                                            }
                                        });
                                        snackBar.show();

                                    } catch (JSONException e) {
                                        Toast.makeText(context, "Sorry shop link is not available", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }

                                });

                                reviews.setOnClickListener(v -> {

                                    String shop_id = "98989";
                                    shop_id = slug;
                                    Intent intent = new Intent(context, ReviewsActivity.class);
                                    intent.putExtra("ratingJson", ratingJson);
                                    intent.putExtra("type", "shop");
                                    intent.putExtra("item_value", shop_id);
                                    startActivity(intent);

                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        JSONArray jsonArray = data.getJSONArray("groups");
                        if (jsonArray.length() > 0) {
                            JSONObject ob = jsonArray.getJSONObject(0);
                            String offers = ob.getString("slug");
                            if (offers != null && offers.equalsIgnoreCase("evaly1919")) {
                                tvOffer.setVisibility(View.VISIBLE);
                            } else {
                                tvOffer.setVisibility(View.GONE);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    error.printStackTrace();

                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        if (error.networkResponse.statusCode == 401) {

                            AuthApiHelper.refreshToken(getActivity(), new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                                @Override
                                public void onDataFetched(retrofit2.Response<JsonObject> response) {
                                    getShopProductCount();
                                }

                                @Override
                                public void onFailed(int status) {

                                }
                            });

                            return;

                        }
                    }

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (!userDetails.getToken().equals(""))
                    headers.put("Authorization", CredentialManager.getToken());
                return headers;
            }
        };

        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        request.setShouldCache(false);

        rq.add(request);
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

        GeneralApiHelper.getShopReviews(sku, new ResponseListenerAuth<JsonObject, String>() {
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

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });


    }

    private void addRosterByOther() {
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
                    data1.put("text", "You are invited to \n https://play.google.com/store/apps/details?id=bd.com.evaly.merchant");

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
                        table.imageUrl = "";
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
