package bd.com.evaly.evalyshop.ui.shop;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.FragmentShopBinding;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.NetworkErrorDialogListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
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
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.networkError.NetworkErrorDialog;
import bd.com.evaly.evalyshop.ui.shop.controller.ShopController;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.InitializeActionBar;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.util.xmpp.XMPPService;
import bd.com.evaly.evalyshop.util.xmpp.XmppCustomEventListener;
import bd.com.evaly.evalyshop.views.GridSpacingItemDecoration;


public class ShopFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private String slug = "", campaign_slug = "", title = "";
    private String categorySlug = null;
    private int currentPage;
    private int totalCount = 0;
    private boolean isLoading = false;
    private VCard vCard;
    private AppController mChatApp = AppController.getInstance();
    private XMPPHandler xmppHandler;
    private List<String> rosterList;
    private ShopController controller;
    private boolean isInitiated = false;
    private boolean clickFromCategory = false;
    private ShopViewModel viewModel;
    private FragmentShopBinding binding;
    private Shop shopDetailsModel;
    public XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {
        public void onPresenceChanged(PresenceModel presenceModel) {
        }

        public void onConnected() {
            if (shopDetailsModel == null)
                return;

            xmppHandler = AppController.getmService().xmpp;
            rosterList = xmppHandler.rosterList;

//            if (!shopDetailsModel.getOwnerName().equals("")) {
//                try {
//                    EntityBareJid jid = JidCreate.entityBareFrom(shopDetailsModel.getOwnerName() + "@"
//                            + Constants.XMPP_HOST);
//                    vCard = xmppHandler.getUserDetails(jid);
//                    Logger.d(new Gson().toJson(vCard));
//                } catch (XmppStringprepException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    };

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

        binding = FragmentShopBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void refreshFragment() {
        NavHostFragment.findNavController(ShopFragment.this).navigate(R.id.shopFragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentPage = 1;
        totalCount = 0;

        viewModel = new ViewModelProvider(this).get(ShopViewModel.class);

        binding.swipeRefresh.setOnRefreshListener(this);

        if (!Utils.isNetworkAvailable(getContext()))
            new NetworkErrorDialog(getContext(), new NetworkErrorDialogListener() {
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

        new InitializeActionBar(view.findViewById(R.id.header_logo), getActivity(), "shop", mainViewModel);


        binding.appBarLayout.homeSearch.setOnClickListener(view12 -> {
            Bundle bundle = new Bundle();
            bundle.putString("shop_slug", slug);
            bundle.putString("shop_name", shopDetailsModel.getName());
            bundle.putString("campaign_slug", campaign_slug);
            NavHostFragment.findNavController(this).navigate(R.id.shopSearchActivity, bundle);
        });

        binding.appBarLayout.homeSearch.setEnabled(false);

        binding.appBarLayout.searchTitle.setText("Search in this shop...");

        if (getArguments() == null) {
            Toast.makeText(getContext(), "Shop not available", Toast.LENGTH_SHORT).show();
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
        controller = new ShopController();
        controller.setActivity((AppCompatActivity) getActivity());
        controller.setFragment(this);
        controller.setViewModel(viewModel);

        int spanCount = 2;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        controller.setSpanCount(spanCount);

        int spacing = (int) Utils.convertDpToPixel(10, getActivity());
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));
        binding.recyclerView.setLayoutManager(layoutManager);

        binding.recyclerView.setAdapter(controller.getAdapter());

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    int[] firstVisibleItems = null;
                    firstVisibleItems = layoutManager.findFirstVisibleItemPositions(null);
                    if (firstVisibleItems != null && firstVisibleItems.length > 0)
                        pastVisiblesItems = firstVisibleItems[0];

                    if (!isLoading && totalItemCount < totalCount)
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            controller.showEmptyPage(false, false);
                            controller.setLoadingMore(true);
                            viewModel.loadShopProducts();
                            isLoading = true;
                        }
                }
            }
        });

        viewModel.getOnChatClickLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                setUpXmpp();
        });

        viewModel.getShopDetailsLiveData().observe(getViewLifecycleOwner(), shopDetailsModel -> loadShopDetails(shopDetailsModel));

        viewModel.setCategoryCurrentPage(1);

        if (isInitiated)
            viewModel.setCurrentPage(2);
        else
            viewModel.setCurrentPage(1);

        if (!isInitiated) {
            isInitiated = true;
            viewModel.loadShopProducts();
        }

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

            currentPage = 1;
            controller.clear();
            controller.setLoadingMore(true);
            viewModel.loadShopProducts();
            AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout);
            appBarLayout.setExpanded(false, true);
        });

        viewModel.getOnResetLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                viewModel.setCategorySlug(null);
                viewModel.setCurrentPage(1);
                currentPage = 1;
                controller.clear();
                controller.setLoadingMore(true);
                clickFromCategory = true;
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
        if (getActivity() == null || getActivity().isFinishing())
            return;

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


    private void disconnectXmpp() {
        if (xmppHandler != null)
            xmppHandler.disconnect();
        Objects.requireNonNull(getActivity()).stopService(new Intent(getActivity(), XMPPService.class));
    }


    public void loadShopDetails(ShopDetailsModel response) {

        isLoading = false;

        Data shopData = response.getData();
        Shop shopDetails = shopData.getShop();

        totalCount = response.getCount();

        if (controller.getShopInfo() == null) {
            controller.setAttr(shopDetails);
            controller.setSubCount(shopData.getSubscriberCount());
            controller.setSubscribed(shopData.isSubscribed());
            if (shopData.getMeta() != null)
                controller.setCashbackRate(shopData.getMeta().get("cashback_rate").getAsInt());

            shopDetailsModel = shopDetails;
        }


        binding.appBarLayout.homeSearch.setEnabled(true);

        controller.setLoadingMore(false);

        totalCount = response.getCount();

        List<ItemsItem> shopItems = shopData.getItems();

        binding.recyclerView.setVisibility(View.VISIBLE);

        List<ProductItem> tempList = new ArrayList<>();

        long timeInMill = Calendar.getInstance().getTimeInMillis();
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
            item.setUniqueId(item.getSlug() + timeInMill);

            tempList.add(item);
        }

        controller.addData(tempList);

        if (totalCount == 0) controller.showEmptyPage(true, true);

        if (clickFromCategory) {
            binding.recyclerView.postDelayed(() -> binding.recyclerView.smoothScrollToPosition(2), 200);
            clickFromCategory = false;
        }

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

    private void setUpXmpp() {

        if (shopDetailsModel == null)
            return;

        ViewDialog dialog = new ViewDialog(getActivity());
        dialog.showDialog();

        if (CredentialManager.getToken().equals("")) {
            startActivity(new Intent(getActivity(), SignInActivity.class));
            Objects.requireNonNull(getActivity()).finish();
        } else {

            if (xmppHandler != null && xmppHandler.isConnected()) {
                String jid = getContactFromRoster(shopDetailsModel.getOwnerName());
                if (!CredentialManager.getUserName().equalsIgnoreCase(shopDetailsModel.getOwnerName())) {
                    if (jid != null) {
                        dialog.hideDialog();
                        VCard vCard;
                        try {
                            vCard = xmppHandler.getUserDetails(JidCreate.entityBareFrom(jid));
                            if (vCard != null) {
                                if (vCard.getFrom() != null) {
                                    RosterTable rosterTable = new RosterTable();
                                    rosterTable.name = shopDetailsModel.getName();
                                    rosterTable.id = vCard.getFrom().asUnescapedString();
                                    rosterTable.imageUrl = shopDetailsModel.getLogoImage();
                                    rosterTable.status = 0;
                                    rosterTable.lastMessage = "";
                                    rosterTable.nick_name = vCard.getNickName();
                                    rosterTable.time = 0;
                                    Logger.d(new Gson().toJson(rosterTable));
                                    startActivity(new Intent(getActivity(), ChatDetailsActivity.class).putExtra("roster", rosterTable));
                                } else {
                                    Toast.makeText(getContext(), "Can't send message", Toast.LENGTH_SHORT).show();
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
                        data1.put("user", shopDetailsModel.getOwnerName());
                        data1.put("server", Constants.XMPP_HOST);
                        data1.put("nick", shopDetailsModel.getName());
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
                Executors.newSingleThreadExecutor().execute(() -> startXmppService());
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
            data.put("localuser", shopDetailsModel.getName());
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
                        EntityBareJid jid = JidCreate.entityBareFrom(shopDetailsModel.getOwnerName() + "@"
                                + Constants.XMPP_HOST);
                        VCard mVCard = xmppHandler.getUserDetails(jid);
                        HashMap<String, String> data1 = new HashMap<>();
                        data1.put("phone_number", shopDetailsModel.getOwnerName());
                        data1.put("text", "You are invited to \n https://play.google.com/store/apps/details?id=bd.com.evaly.evalyshop");

                        Logger.d(response.body());

                        if (mVCard.getFirstName() == null) {
                            dialog.hideDialog();
                            RosterTable table = new RosterTable();
                            table.id = jid.asUnescapedString();
                            table.rosterName = shopDetailsModel.getName();
                            table.name = "";
                            table.status = 0;
                            table.unreadCount = 0;
                            table.nick_name = "";
                            table.imageUrl = shopDetailsModel.getLogoImage();
                            table.lastMessage = "";

                            startActivity(new Intent(getActivity(), ChatDetailsActivity.class).putExtra("roster", table));
                        } else {
                            dialog.hideDialog();
                            RosterTable rosterTable = new RosterTable();
                            rosterTable.name = shopDetailsModel.getName();
                            rosterTable.id = mVCard.getFrom().asUnescapedString();
                            rosterTable.imageUrl = shopDetailsModel.getLogoImage();
                            rosterTable.status = 0;
                            rosterTable.lastMessage = "";
                            rosterTable.nick_name = mVCard.getNickName();
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

        binding.shimmer.setVisibility(View.VISIBLE);
        binding.shimmerHolder.setVisibility(View.VISIBLE);
        binding.shimmerHolder.setAlpha(1);
        binding.shimmer.startShimmer();

        controller.clear();
        viewModel.setCurrentPage(1);
        viewModel.loadShopProducts();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerView.setAdapter(null);
        disconnectXmpp();
    }

}

