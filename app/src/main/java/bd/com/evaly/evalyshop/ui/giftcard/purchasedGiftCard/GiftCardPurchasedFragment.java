package bd.com.evaly.evalyshop.ui.giftcard.purchasedGiftCard;


import android.os.Bundle;
import android.view.View;

import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.FragmentGiftcardListBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListPurchasedItem;
import bd.com.evaly.evalyshop.models.remoteConfig.RemoteConfigPaymentBaseUrl;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.giftcard.adapter.GiftCardListPurchasedAdapter;
import bd.com.evaly.evalyshop.ui.payment.builder.PaymentWebBuilder;
import bd.com.evaly.evalyshop.ui.payment.listener.PaymentListener;
import bd.com.evaly.evalyshop.ui.payment.model.PurchaseRequestInfo;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GiftCardPurchasedFragment extends BaseFragment<FragmentGiftcardListBinding, GiftCardPurchasedViewModel> implements GiftCardListPurchasedAdapter.ClickListener, SwipeRefreshLayout.OnRefreshListener, PaymentListener {

    @Inject
    PreferenceRepository preferenceRepository;
    @Inject
    FirebaseRemoteConfig remoteConfig;

    private ArrayList<GiftCardListPurchasedItem> itemList;
    private GiftCardListPurchasedAdapter adapter;
    private boolean isLoading = true;
    private PaymentWebBuilder paymentWebBuilder;


    public GiftCardPurchasedFragment() {
        super(GiftCardPurchasedViewModel.class, R.layout.fragment_giftcard_list);
    }

    @Override
    public void onRefresh() {
        viewModel.clear();
        itemList.clear();
        adapter.notifyDataSetChanged();
        binding.swipeContainer.setRefreshing(false);
        getGiftCardList(true);
    }

    @Override
    protected void initViews() {
        paymentWebBuilder = new PaymentWebBuilder(getActivity());
        paymentWebBuilder.setPaymentListener(this);
        binding.swipeContainer.setOnRefreshListener(this);
        itemList = new ArrayList<>();
        getGiftCardList(false);
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.liveList.observe(getViewLifecycleOwner(), giftCardListPurchasedItems -> {
            isLoading = false;
            binding.progressBar.setVisibility(View.GONE);
            if (viewModel.getCurrentPage() <= 2)
                binding.progressContainer.setVisibility(View.GONE);

            itemList.clear();
            itemList.addAll(giftCardListPurchasedItems);
            adapter.notifyDataSetChanged();

            if (itemList.size() == 0 && viewModel.getCurrentPage() <= 2) {
                binding.noItem.setVisibility(View.VISIBLE);
                binding.noText.setText("You have no gift cards");
            } else {
                binding.noItem.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void setupRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(manager);

        adapter = new GiftCardListPurchasedAdapter(getContext(), itemList, 0);
        adapter.setClickListener(this);
        binding.recyclerView.setAdapter(adapter);

        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(manager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading)
                    getGiftCardList(true);
            }
        });
    }

    @Override
    protected void clickListeners() {

    }

    public void getGiftCardList(boolean loadFromApi) {

        isLoading = true;

        if (viewModel.getCurrentPage() <= 2) {
            binding.progressContainer.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        } else {
            binding.progressContainer.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }

        if (loadFromApi)
            viewModel.getGiftCardList();
    }


    @Override
    public void onPaymentSuccess(HashMap<String, String> values) {
        NavHostFragment.findNavController(this).popBackStack();
        Bundle bundle = new Bundle();
        bundle.putString("type", "purchased");
        NavHostFragment.findNavController(this).navigate(R.id.giftCardFragment, bundle);
    }

    @Override
    public void onPaymentFailure(HashMap<String, String> values) {

    }

    @Override
    public void onPaymentSuccess(String message) {
        onRefresh();
    }

    @Override
    public void onClick(GiftCardListPurchasedItem item) {
        String giftCardInvoice = item.getInvoiceNo();
        String amount = item.getTotal() + "";

        String url;
        RemoteConfigPaymentBaseUrl baseUrls = new Gson().fromJson(remoteConfig.getValue("gift_card_payment_url").asString(), RemoteConfigPaymentBaseUrl.class);

        if (baseUrls == null) {
            ToastUtils.show("Please reload the page");
            return;
        }

        if (BuildConfig.DEBUG)
            url = baseUrls.getDevPaymentBaseUrl();
        else
            url = baseUrls.getProdPaymentBaseUrl();

        if (url == null)
            return;

        String paymentUrl = url + giftCardInvoice + "?t=" + preferenceRepository.getTokenNoBearer() + "&context=gift_card_order_payment&amount=" + amount;
        PurchaseRequestInfo purchaseRequestInfo = new PurchaseRequestInfo(preferenceRepository.getTokenNoBearer(), String.valueOf(amount), giftCardInvoice, "bKash");
        paymentWebBuilder.setToolbarTitle("Gift Card Payment");
        paymentWebBuilder.loadPaymentURL(paymentUrl, "evaly.com.bd/gift-card", purchaseRequestInfo);
    }
}
