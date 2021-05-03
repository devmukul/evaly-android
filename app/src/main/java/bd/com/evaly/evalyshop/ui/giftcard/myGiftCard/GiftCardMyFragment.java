package bd.com.evaly.evalyshop.ui.giftcard.myGiftCard;


import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.FragmentGiftcardListBinding;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
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
public class GiftCardMyFragment extends BaseFragment<FragmentGiftcardListBinding, GiftCardMyViewModel> implements SwipeRefreshLayout.OnRefreshListener {
    static GiftCardMyFragment instance;
    @Inject
    FirebaseRemoteConfig firebaseRemoteConfig;
    @Inject
    PreferenceRepository preferenceRepository;
    @Inject
    ApiRepository apiRepository;
    private ArrayList<GiftCardListPurchasedItem> itemList;
    private GiftCardListPurchasedAdapter adapter;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout layoutBottomSheet;
    private ViewDialog dialog;
    private String giftCardInvoice = "";
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheetInternal;
    private int currentPage;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private String baseUrl = BuildConfig.BASE_URL + "cpn/";

    public GiftCardMyFragment() {
        super(GiftCardMyViewModel.class, R.layout.fragment_giftcard_list);
    }

    public static GiftCardMyFragment getInstance() {
        return instance;
    }

    @Override
    public void onRefresh() {
        itemList.clear();
        adapter.notifyDataSetChanged();
        currentPage = 1;
        binding.swipeContainer.setRefreshing(false);
        getGiftCardList();
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

        currentPage = 1;
        getGiftCardList();
    }

    @Override
    protected void liveEventsObservers() {

    }

    @Override
    protected void clickListeners() {

    }

    @Override
    protected void setupRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(manager);

        instance = this;
        adapter = new GiftCardListPurchasedAdapter(getContext(), itemList, 1);
        binding.recyclerView.setAdapter(adapter);

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = manager.getChildCount();
                    totalItemCount = manager.getItemCount();
                    pastVisiblesItems = manager.findFirstVisibleItemPosition();
                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount)
                            getGiftCardList();
                    }
                }
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

        layoutBottomSheet = bottomSheetDialog.findViewById(R.id.bottom_sheet);

        Button button = bottomSheetDialog.findViewById(R.id.button);
        EditText email = bottomSheetDialog.findViewById(R.id.email);
        TextView details = bottomSheetDialog.findViewById(R.id.details);

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
            } else
                redeemCard(giftCardInvoice);
        });

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.show();
    }

    public void getGiftCardList() {

        loading = false;

        if (currentPage == 1) {
            binding.progressContainer.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        } else {
            binding.progressContainer.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }

        apiRepository.getPurchasedGiftCardList("gifts", currentPage, baseUrl, new ResponseListener<CommonDataResponse<List<GiftCardListPurchasedItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<GiftCardListPurchasedItem>> response, int statusCode) {

                loading = true;
                binding.progressBar.setVisibility(View.GONE);
                if (currentPage == 1)
                    binding.progressContainer.setVisibility(View.GONE);

                itemList.addAll(response.getData());
                adapter.notifyItemRangeInserted(itemList.size() - response.getData().size(), response.getData().size());
                currentPage++;

                if (itemList.size() == 0 && currentPage == 1) {
                    loading = false;
                    binding.noItem.setVisibility(View.VISIBLE);
                    binding.noText.setText("You have no gift cards");
                } else {
                    binding.noItem.setVisibility(View.GONE);
                }

                if (response.getData().size() < 10)
                    loading = false;

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {


            }

        });
    }

    public void redeemCard(String invoice_no) {

        dialog.showDialog();

        apiRepository.redeem(invoice_no, baseUrl, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                dialog.hideDialog();
                Toast.makeText(getContext(), response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                bottomSheetDialog.hide();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                Toast.makeText(getContext(), "Couldn't redeem gift card", Toast.LENGTH_SHORT).show();
                dialog.hideDialog();
            }

        });
    }


}
