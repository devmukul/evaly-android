package bd.com.evaly.evalyshop.ui.giftcard.giftCardList;


import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.FragmentGiftcardListBinding;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;
import bd.com.evaly.evalyshop.models.remoteConfig.RemoteConfigBaseUrls;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.giftcard.adapter.GiftCardListAdapter;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

();


@AndroidEntryPoint
public class GiftCardListFragment extends BaseFragment<FragmentGiftcardListBinding, GiftCardListViewModel> implements SwipeRefreshLayout.OnRefreshListener {

    static GiftCardListFragment instance;
    @Inject
    PreferenceRepository preferenceRepository;
    @Inject
    ApiRepository apiRepository;
    @Inject
    FirebaseRemoteConfig firebaseRemoteConfig;
    private ArrayList<GiftCardListItem> itemList;
    private GiftCardListAdapter adapter;
    private ViewDialog dialog;
    private int voucherAmount = 0;
    private String giftCardSlug = "";
    private BottomSheetBehavior sheetBehavior;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheetInternal;
    private int currentPage;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private String baseUrl = BuildConfig.BASE_URL + "cpn/";

    public GiftCardListFragment() {
        super(GiftCardListViewModel.class, R.layout.fragment_giftcard_list);
    }

    public static GiftCardListFragment getInstance() {
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

        initializeBottomSheet();

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
        adapter = new GiftCardListAdapter(getContext(), itemList);
        binding.recyclerView.setAdapter(adapter);

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = manager.getChildCount();
                    totalItemCount = manager.getItemCount();
                    pastVisiblesItems = manager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            getGiftCardList();
                        }
                    }
                }
            }
        });
    }

    public void initializeBottomSheet() {


        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_gift_cards);

        bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        bottomSheetInternal.setPadding(0, 0, 0, 0);

        // new KeyboardUtil(getActivity(), bottomSheetInternal);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        LinearLayout layoutBottomSheet = bottomSheetDialog.findViewById(R.id.bottom_sheet);

        ImageView image = bottomSheetDialog.findViewById(R.id.image);
        TextView plus = bottomSheetDialog.findViewById(R.id.plus);
        TextView minus = bottomSheetDialog.findViewById(R.id.minus);
        TextView quantity = bottomSheetDialog.findViewById(R.id.quantity);
        TextView details = bottomSheetDialog.findViewById(R.id.details);
        TextView name = bottomSheetDialog.findViewById(R.id.name);
        TextView amount = bottomSheetDialog.findViewById(R.id.amount);
        TextView cardValue = bottomSheetDialog.findViewById(R.id.cardValue);

        TextView total = bottomSheetDialog.findViewById(R.id.total);
        Button placeOrder = bottomSheetDialog.findViewById(R.id.place_order);
        EditText phoneNumber = bottomSheetDialog.findViewById(R.id.phone);

        TextView privacyText = bottomSheetDialog.findViewById(R.id.privacyText);

        assert privacyText != null;
        privacyText.setText(Html.fromHtml("I agree to the <a href=\"https://evaly.com.bd/about/terms-conditions\">Terms & Conditions</a> and <a href=\"https://evaly.com.bd/about/purchasing-policy\">Purchasing Policy</a> of Evaly."));
        privacyText.setMovementMethod(LinkMovementMethod.getInstance());

        CheckBox checkBox = bottomSheetDialog.findViewById(R.id.checkBox);

        plus.setOnClickListener(v -> {
            int quan;
            try {
                quan = Integer.parseInt(quantity.getText().toString());
            } catch (Exception e) {
                Toast.makeText(getContext(), "Invalid quantity", Toast.LENGTH_SHORT).show();
                return;
            }
            total.setText(String.format(Locale.ENGLISH, "৳ %d", quan * voucherAmount));
            quan += 1;
            quantity.setText(String.format(Locale.ENGLISH, "%d", quan));
        });

        minus.setOnClickListener(v -> {
            int quan;
            try {
                quan = Integer.parseInt(quantity.getText().toString());
            } catch (Exception e) {
                Toast.makeText(getContext(), "Invalid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            quan -= 1;
            if (quan < 1) {
                quan = 1;
            }
            total.setText(String.format(Locale.ENGLISH, "৳ %d", quan * voucherAmount));
            quantity.setText(String.format(Locale.ENGLISH, "%d", quan));
        });

        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int quan = Integer.parseInt(s.toString());
                    total.setText(String.format(Locale.ENGLISH, "৳ %d", quan * voucherAmount));
                } catch (Exception e) {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        placeOrder.setOnClickListener(v -> {

            if (phoneNumber.getText().toString().equals(preferenceRepository.getUserName())) {
                Toast.makeText(getContext(), "You can't buy gift cards for yourself", Toast.LENGTH_LONG).show();
                return;
            }

            if (phoneNumber.getText().toString().equals("")) {
                Toast.makeText(getContext(), "Please enter a number", Toast.LENGTH_LONG).show();
                return;
            }

            if (!Utils.isValidNumber(phoneNumber.getText().toString())) {
                Toast.makeText(getContext(), "Please enter a correct phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Utils.isNumeric(quantity.getText().toString())) {
                if (Integer.parseInt(quantity.getText().toString()) > 10) {
                    Toast.makeText(getContext(), "Quantity must be less than 10", Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                Toast.makeText(getContext(), "Enter valid quantity", Toast.LENGTH_LONG).show();
                return;
            }

            assert checkBox != null;
            if (!checkBox.isChecked()) {
                Toast.makeText(getContext(), "You must accept terms & conditions and purchasing policy to place an order.", Toast.LENGTH_LONG).show();
                return;
            }

            createOrder(giftCardSlug);
        });

    }

    public void toggleBottomSheet(GiftCardListItem item) {
        quantity.setText("1");
        giftCardSlug = item.getSlug();
        getGiftCardDetails(giftCardSlug);
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

        apiRepository.getGiftCard(currentPage, baseUrl, new ResponseListener<CommonDataResponse<List<GiftCardListItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<GiftCardListItem>> response, int statusCode) {

                loading = true;
                binding.progressBar.setVisibility(View.GONE);

                List<GiftCardListItem> list = response.getData();

                itemList.addAll(list);
                adapter.notifyItemRangeChanged(itemList.size() - list.size(), list.size());

                if (currentPage == 1)
                    binding.progressContainer.setVisibility(View.GONE);

                if (list.size() == 0 && currentPage == 1)
                    binding.noItem.setVisibility(View.VISIBLE);

                currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });

    }

    public void getGiftCardDetails(String slug) {

        if (preferenceRepository.getToken().equals("")) {
            Toast.makeText(getContext(), "You need to login first", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.showDialog();
        initializeBottomSheet();

        apiRepository.getGiftCardDetails(slug, baseUrl, new ResponseListener<CommonDataResponse<GiftCardListItem>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<GiftCardListItem> response, int statusCode) {
                dialog.hideDialog();
                if (response.getSuccess()) {
                    GiftCardListItem item = response.getData();

                    name.setText(item.getName());
                    details.setText(item.getDescription());
                    voucherAmount = item.getPrice();
                    amount.setText(String.format("৳ %s", Utils.formatPrice(voucherAmount)));
                    total.setText(String.format("৳ %s", Utils.formatPrice(voucherAmount)));
                    cardValue.setText(String.format("৳ %d", item.getValue()));

                    if (getContext() != null) {
                        if (item.getImageUrl() == null)
                            Glide.with(getContext()).load("https://beta.evaly.com.bd/static/images/gift-card.jpg").placeholder(R.drawable.ic_placeholder_small).into(image);
                        else
                            Glide.with(getContext()).load(item.getImageUrl()).placeholder(R.drawable.ic_placeholder_small).into(image);
                    }
                    bottomSheetDialog.show();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


                } else {
                    Toast.makeText(getContext(), "Sorry the gift card is not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }
        });
    }


    public void createOrder(String slug) {

        dialog.showDialog();

        JsonObject parameters = new JsonObject();

        parameters.addProperty("to", phoneNumber.getText().toString().trim());
        parameters.addProperty("gift_card", slug);
        int q = Integer.parseInt(quantity.getText().toString());
        parameters.addProperty("quantity", q);

        apiRepository.placeGiftCardOrder(preferenceRepository.getToken(), parameters, baseUrl, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                dialog.hideDialog();
                Toast.makeText(getContext(), response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                bottomSheetDialog.hide();
                NavHostFragment.findNavController(GiftCardListFragment.this).popBackStack();
                Bundle bundle = new Bundle();
                bundle.putString("type", "purchased");
                NavHostFragment.findNavController(GiftCardListFragment.this).navigate(R.id.giftCardFragment, bundle);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialog.hideDialog();
                ToastUtils.show(errorBody);
            }

        });
    }

}
