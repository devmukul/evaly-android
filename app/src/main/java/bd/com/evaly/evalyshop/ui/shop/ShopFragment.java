package bd.com.evaly.evalyshop.ui.shop;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.JsonPrimitive;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.FragmentShopNewBinding;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.NetworkErrorDialogListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.HomeHeaderItem;
import bd.com.evaly.evalyshop.models.ProgressHeaderItem;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.Data;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ItemsItem;
import bd.com.evaly.evalyshop.models.shop.shopDetails.Shop;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDetailsModel;
import bd.com.evaly.evalyshop.models.xmpp.PresenceModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.buynow.BuyNowFragment;
import bd.com.evaly.evalyshop.ui.chat.ChatDetailsActivity;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.networkError.NetworkErrorDialog;
import bd.com.evaly.evalyshop.ui.notification.NotificationActivity;
import bd.com.evaly.evalyshop.ui.shop.adapter.ShopProductAdapter;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.util.xmpp.XMPPService;
import bd.com.evaly.evalyshop.util.xmpp.XmppCustomEventListener;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;


public class ShopFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private String slug = "", title = "", owner_number = "", shop_name = "", campaign_slug = "", logo_image;
    private String categorySlug = null;
    private List<ProductItem> productItemList;
    private ShopProductAdapter adapterProducts;
    private Context context;
    private MainActivity mainActivity;
    private int currentPage = 1;
    private int totalCount = 0;
    private boolean isLoading = false;
    private VCard vCard;
    private AppController mChatApp = AppController.getInstance();
    private XMPPHandler xmppHandler;
    private List<String> rosterList;
    public XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {
        public void onPresenceChanged(PresenceModel presenceModel) {
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
    private boolean clickFromCategory = false;
    private ShopViewModel viewModel;
    private ProgressBar progressBar;
    private FragmentShopNewBinding binding;
    private boolean progressBarShowing = false;

    public ShopFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!CredentialManager.getToken().equals(""))
            Executors.newSingleThreadExecutor().execute(() -> startXmppService());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentShopNewBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(ShopViewModel.class);

        context = getContext();
        mainActivity = (MainActivity) getActivity();

        return binding.getRoot();
    }

    private void refreshFragment() {
        NavHostFragment.findNavController(ShopFragment.this).navigate(R.id.shopFragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.swipeRefresh.setOnRefreshListener(this);

        if (!Utils.isNetworkAvailable(context))
            new NetworkErrorDialog(context, new NetworkErrorDialogListener() {
                @Override
                public void onRetry() {
                    refreshFragment();
                }

                @Override
                public void onBackPress() {
                    NavHostFragment.findNavController(ShopFragment.this).navigate(R.id.homeFragment);
                }
            });


        MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        ImageView menuBtn = view.findViewById(R.id.menuBtn);


        menuBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_back));

        RelativeLayout notification = view.findViewById(R.id.notification_holder);
        menuBtn.setOnClickListener(v -> {
                mainViewModel.setBackOnClick(true);
        });

        notification.setOnClickListener(v -> {
            if (CredentialManager.getToken().equals("")) {
                context.startActivity(new Intent(context, SignInActivity.class));
            } else {
                context.startActivity(new Intent(context, NotificationActivity.class));
            }
        });

        LinearLayout homeSearch = view.findViewById(R.id.home_search);
        homeSearch.setOnClickListener(view12 -> {
            NavHostFragment.findNavController(this).navigate(R.id.shopSearchFragment);
        });


        if (getArguments() == null) {
            Toast.makeText(context, "Shop not available", Toast.LENGTH_SHORT).show();
            return;
        }

        if (getArguments().containsKey("shop_name"))
            title = getArguments().getString("shop_name");

        if (getArguments().getString("campaign_slug") != null)
            campaign_slug = getArguments().getString("campaign_slug");

        slug = getArguments().getString("shop_slug");


        viewModel.setCampaignSlug(campaign_slug);
        viewModel.setCategorySlug(categorySlug);
        viewModel.setShopSlug(slug);

        binding.shimmer.startShimmer();
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        productItemList = new ArrayList<>();

        HashMap<String, String> data = new HashMap<>();

        adapterProducts = new ShopProductAdapter(context, productItemList, (MainActivity) getActivity(), this, NavHostFragment.findNavController(this), data, viewModel);
        binding.products.setAdapter(adapterProducts);


        binding.products.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0) {
                    if (isLoading)
                        progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.products.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!isLoading && totalCount > productItemList.size()) {
                        if (currentPage > 1)
                            progressBar.setVisibility(View.VISIBLE);
                        viewModel.loadShopProducts();
                    }
                }
            }
        });

        int spanCount = 2; // 3 columns
        int spacing = (int) Utils.convertDpToPixel(10, Objects.requireNonNull(getContext())); // 50px
        boolean includeEdge = true;
        binding.products.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        viewModel.getOnChatClickLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                setUpXmpp();
        });

        viewModel.getShopDetailsLiveData().observe(getViewLifecycleOwner(), shopDetailsModel -> {

            if (progressBarShowing && productItemList.size() > 1) {
                productItemList.remove(productItemList.size() - 1);
                adapterProducts.notifyItemRemoved(productItemList.size());
                progressBarShowing = false;
            }

            loadShopDetails(shopDetailsModel);
        });

        viewModel.loadShopProducts();

        viewModel.getBuyNowLiveData().observe(getViewLifecycleOwner(), s -> {
            if (getActivity() != null) {
                BuyNowFragment addPhotoBottomDialogFragment =
                        BuyNowFragment.newInstance(slug, s);
                addPhotoBottomDialogFragment.show(getActivity().getSupportFragmentManager(),
                        "BuyNow");
            }
        });

        viewModel.getSelectedCategoryLiveData().observe(getViewLifecycleOwner(), tabsItem -> {

            clickFromCategory = true;

            categorySlug = tabsItem.getSlug();
            viewModel.setCategorySlug(categorySlug);
            viewModel.setCurrentPage(1);

            productItemList.clear();
            productItemList.add(new HomeHeaderItem());
            productItemList.add(new ProgressHeaderItem());
            adapterProducts.notifyDataSetChanged();
            progressBarShowing = true;

            currentPage = 1;

            viewModel.loadShopProducts();
            AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout);
            appBarLayout.setExpanded(false, true);
        });

        viewModel.getOnResetLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {

                viewModel.setCategorySlug(null);
                viewModel.setCurrentPage(1);

                productItemList.clear();
                productItemList.add(new HomeHeaderItem());
                adapterProducts.notifyDataSetChanged();
                currentPage = 1;
                viewModel.loadShopProducts();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mChatApp.getEventReceiver().setListener(xmppCustomEventListener);
    }

    private void startXmppService() {
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
        binding.products.setAdapter(null);
        disconnectXmpp();
    }

    private void disconnectXmpp() {
        if (xmppHandler != null)
            xmppHandler.disconnect();
        Objects.requireNonNull(getActivity()).stopService(new Intent(getActivity(), XMPPService.class));
    }


    public void loadShopDetails(ShopDetailsModel response) {

        adapterProducts.setShopDetails(response);

        Data shopData = response.getData();
        Shop shopDetails = shopData.getShop();

        totalCount = response.getCount();

        if (currentPage == 1 && productItemList.size() != 1) {
            productItemList.add(new HomeHeaderItem());
            adapterProducts.notifyItemInserted(0);
            shop_name = shopDetails.getName();
            owner_number = shopDetails.getOwnerName();
        }

        progressBar.setVisibility(View.INVISIBLE);

        if (shopData.getMeta() != null && currentPage == 1)
            adapterProducts.setCashbackRate(shopData.getMeta().get("cashback_rate").getAsInt());

        List<ItemsItem> shopItems = shopData.getItems();

        binding.products.setVisibility(View.VISIBLE);

        for (int i = 0; i < shopItems.size(); i++) {
            if (i == 0)
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

        if (clickFromCategory) {
            binding.products.scrollToPosition(1);
            clickFromCategory = false;
        }

        if (currentPage == 1 & shopItems.size() == 0) {
            //   binding.noItem.setVisibility(View.VISIBLE);
        } else {
            binding.noItem.setVisibility(View.GONE);
        }

        if (currentPage == 2) {

            binding.shimmerHolder.animate().alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            binding.shimmer.stopShimmer();
                            binding.shimmer.setVisibility(View.GONE);
                            binding.shimmerHolder.setVisibility(View.GONE);
                        }
                    });
        }


    }

    private void setUpXmpp() {

        ViewDialog dialog = new ViewDialog(getActivity());
        dialog.showDialog();

        if (CredentialManager.getToken().equals("")) {
            startActivity(new Intent(getActivity(), SignInActivity.class));
            Objects.requireNonNull(getActivity()).finish();
        } else {
            if (xmppHandler != null && xmppHandler.isConnected() && xmppHandler.isLoggedin()) {
                String jid = getContactFromRoster(owner_number);
                if (!CredentialManager.getUserName().equalsIgnoreCase(owner_number)) {
                    if (jid != null) {
                        dialog.hideDialog();
                        VCard vCard;
                        try {
                            vCard = xmppHandler.getUserDetails(JidCreate.entityBareFrom(jid));
                            if (vCard != null) {
                                if (vCard.getFrom() != null) {
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
                                } else {
                                    Toast.makeText(context, "Can't send message", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (XmppStringprepException e) {
                            e.printStackTrace();
                        }
                    } else {

                        dialog.hideDialog();

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

                                dialog.hideDialog();

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
        if (rosterList != null)
            for (String model : rosterList) {
                if (model.contains(number)) {
                    roasterModel = model;
                }
            }
        return roasterModel;
    }

    private void addRosterByOther() {

        if (CredentialManager.getUserData() != null) {
            ViewDialog dialog = new ViewDialog(getActivity());
            dialog.showDialog();

            if (CredentialManager.getUserData().getFirst_name() == null)
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
                    dialog.hideDialog();
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

                    dialog.hideDialog();

                }
            });
        }
    }

    @Override
    public void onRefresh() {

        binding.swipeRefresh.setRefreshing(false);
        currentPage = 1;
        viewModel.setCategorySlug(null);
        productItemList.clear();
        adapterProducts.notifyDataSetChanged();

        binding.shimmer.setVisibility(View.VISIBLE);
        binding.shimmerHolder.setVisibility(View.VISIBLE);
        binding.shimmerHolder.setAlpha(1);
        binding.shimmer.startShimmer();

        viewModel.setCurrentPage(1);
        viewModel.loadShopProducts();

    }

}

