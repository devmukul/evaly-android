package bd.com.evaly.evalyshop.ui.giftcard.myGiftCard;


import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import java.util.ArrayList;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.FragmentGiftcardListBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListPurchasedItem;
import bd.com.evaly.evalyshop.models.remoteConfig.RemoteConfigBaseUrls;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.giftcard.adapter.GiftCardListPurchasedAdapter;
import bd.com.evaly.evalyshop.ui.user.editProfile.EditProfileActivity;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GiftCardMyFragment extends BaseFragment<FragmentGiftcardListBinding, GiftCardMyViewModel> implements SwipeRefreshLayout.OnRefreshListener, GiftCardListPurchasedAdapter.ClickListener {

    @Inject
    FirebaseRemoteConfig firebaseRemoteConfig;
    @Inject
    PreferenceRepository preferenceRepository;
    @Inject
    ApiRepository apiRepository;

    private ArrayList<GiftCardListPurchasedItem> itemList;
    private GiftCardListPurchasedAdapter adapter;
    private ViewDialog dialog;
    private String giftCardInvoice = "";
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheetInternal;
    private boolean isLoading = true;
    private String baseUrl = BuildConfig.BASE_URL + "cpn/";

    public GiftCardMyFragment() {
        super(GiftCardMyViewModel.class, R.layout.fragment_giftcard_list);
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
        RemoteConfigBaseUrls baseUrls = new Gson().fromJson(firebaseRemoteConfig.getValue("temp_urls").asString(), RemoteConfigBaseUrls.class);

        String url = null;
        if (baseUrls != null) {
            if (BuildConfig.DEBUG)
                url = baseUrls.getDevGiftCardBaseUrl();
            else
                url = baseUrls.getProdGiftCardBaseUrl();
        }

        if (url != null)
            baseUrl = url;

        binding.swipeContainer.setOnRefreshListener(this);

        itemList = new ArrayList<>();
        dialog = new ViewDialog(getActivity());

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
    protected void clickListeners() {

    }

    @Override
    protected void setupRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(manager);

        adapter = new GiftCardListPurchasedAdapter(getContext(), itemList, 1);
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

    public void toggleBottomSheet(GiftCardListPurchasedItem item) {

        giftCardInvoice = item.getInvoiceNo();

        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_gift_card_redeem);
        bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        bottomSheetInternal.setPadding(0, 0, 0, 0);
        new KeyboardUtil(getActivity(), bottomSheetInternal);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        Button button = bottomSheetDialog.findViewById(R.id.button);
        EditText email = bottomSheetDialog.findViewById(R.id.email);

        String primaryEmail = preferenceRepository.getUserData().getPrimaryEmailNonNull();
        email.setText(primaryEmail);

        if (primaryEmail.equals(""))
            button.setText("ADD EMAIL FIRST");
        else
            button.setText("REDEEM");

        button.setOnClickListener(view1 -> {
            if (primaryEmail.equals("")) {
                getContext().startActivity(new Intent(getContext(), EditProfileActivity.class));
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            } else {
                dialog.showDialog();
                viewModel.redeemCard(giftCardInvoice);
            }
        });

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.show();
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
    public void onClick(GiftCardListPurchasedItem item) {
        toggleBottomSheet(item);
    }
}
