package bd.com.evaly.evalyshop.ui.giftcard;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
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
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListPurchasedItem;
import bd.com.evaly.evalyshop.models.remoteConfig.RemoteConfigBaseUrls;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.giftcard.adapter.GiftCardListPurchasedAdapter;
import bd.com.evaly.evalyshop.ui.user.editProfile.EditProfileActivity;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GiftCardMyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @Inject
    FirebaseRemoteConfig firebaseRemoteConfig;

    @Inject
    PreferenceRepository preferenceRepository;
    @Inject
    ApiRepository apiRepository;
    static GiftCardMyFragment instance;
    private View view;
    private RecyclerView recyclerView;
    private ArrayList<GiftCardListPurchasedItem> itemList;
    private GiftCardListPurchasedAdapter adapter;
    private RequestQueue rq;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout layoutBottomSheet;
    private ViewDialog dialog;
    private String giftCardInvoice = "";
    private LinearLayout noItem;
    private Context context;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheetInternal;
    private LinearLayout progressContainer;
    private ProgressBar progressBar;
    private int currentPage;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private SwipeRefreshLayout swipeLayout;
    private boolean loading = true;
    private String baseUrl = BuildConfig.BASE_URL + "cpn/";

    public GiftCardMyFragment() {
        // Required empty public constructor
    }

    public static GiftCardMyFragment getInstance() {
        return instance;
    }

    @Override
    public void onRefresh() {

        itemList.clear();
        adapter.notifyDataSetChanged();
        currentPage = 1;
        swipeLayout.setRefreshing(false);
        getGiftCardList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_giftcard_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        swipeLayout = view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        itemList = new ArrayList<>();
        dialog = new ViewDialog(getActivity());

        context = getContext();
        rq = Volley.newRequestQueue(context);

        progressContainer = view.findViewById(R.id.progressContainer);
        progressBar = view.findViewById(R.id.progressBar);
        currentPage = 1;
        noItem = view.findViewById(R.id.noItem);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);

        instance = this;
        adapter = new GiftCardListPurchasedAdapter(context, itemList, 1);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        getGiftCardList();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
    }


    public void toggleBottomSheet(GiftCardListPurchasedItem item) {

        giftCardInvoice = item.getInvoiceNo();

        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
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
                context.startActivity(new Intent(context, EditProfileActivity.class));
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
            progressContainer.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            progressContainer.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        apiRepository.getPurchasedGiftCardList("gifts", currentPage, baseUrl, new ResponseListener<CommonDataResponse<List<GiftCardListPurchasedItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<GiftCardListPurchasedItem>> response, int statusCode) {

                loading = true;
                progressBar.setVisibility(View.GONE);
                if (currentPage == 1)
                    progressContainer.setVisibility(View.GONE);

                itemList.addAll(response.getData());
                adapter.notifyItemRangeInserted(itemList.size() - response.getData().size(), response.getData().size());
                currentPage++;

                if (itemList.size() == 0 && currentPage == 1) {
                    loading = false;
                    noItem.setVisibility(View.VISIBLE);

                    TextView noText = view.findViewById(R.id.noText);
                    noText.setText("You have no gift cards");
                } else {
                    noItem.setVisibility(View.GONE);
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
                Toast.makeText(context, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                bottomSheetDialog.hide();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                Toast.makeText(context, "Couldn't redeem gift card", Toast.LENGTH_SHORT).show();
                dialog.hideDialog();
            }

        });
    }


}
