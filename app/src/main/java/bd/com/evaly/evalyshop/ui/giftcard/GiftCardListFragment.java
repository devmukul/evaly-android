package bd.com.evaly.evalyshop.ui.giftcard;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;
import bd.com.evaly.evalyshop.rest.apiHelper.GiftCardApiHelper;
import bd.com.evaly.evalyshop.ui.giftcard.adapter.GiftCardListAdapter;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;


public class GiftCardListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    static GiftCardListFragment instance;
    private View view;
    private RecyclerView recyclerView;
    private ArrayList<GiftCardListItem> itemList;
    private GiftCardListAdapter adapter;
    private RequestQueue rq;
    private ViewDialog dialog;
    private ImageView image, plus, minus;
    private UserDetails userDetails;
    private TextView details, name, amount, total, cardValue;
    private EditText quantity, phoneNumber;
    private int voucherAmount = 0;
    private Button placeOrder;
    private String giftCardSlug = "";
    private LinearLayout noItem;
    private Context context;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout layoutBottomSheet;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheetInternal;
    private LinearLayout progressContainer;
    private ProgressBar progressBar;
    private int currentPage;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private SwipeRefreshLayout swipeLayout;

    public GiftCardListFragment() {
        // Required empty public constructor
    }

    public static GiftCardListFragment getInstance() {
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
        userDetails = new UserDetails(context);

        progressContainer = view.findViewById(R.id.progressContainer);
        progressBar = view.findViewById(R.id.progressBar);
        currentPage = 1;

        initializeBottomSheet();

        noItem = view.findViewById(R.id.noItem);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);

        instance = this;
        adapter = new GiftCardListAdapter(context, itemList);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        getGiftCardList();

        return view;
    }

    public void initializeBottomSheet() {


        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_gift_cards);

        bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        bottomSheetInternal.setPadding(0, 0, 0, 0);

        // new KeyboardUtil(getActivity(), bottomSheetInternal);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        layoutBottomSheet = bottomSheetDialog.findViewById(R.id.bottom_sheet);

        image = bottomSheetDialog.findViewById(R.id.image);
        plus = bottomSheetDialog.findViewById(R.id.plus);
        minus = bottomSheetDialog.findViewById(R.id.minus);
        quantity = bottomSheetDialog.findViewById(R.id.quantity);
        details = bottomSheetDialog.findViewById(R.id.details);
        name = bottomSheetDialog.findViewById(R.id.name);
        amount = bottomSheetDialog.findViewById(R.id.amount);
        cardValue = bottomSheetDialog.findViewById(R.id.cardValue);

        total = bottomSheetDialog.findViewById(R.id.total);
        placeOrder = bottomSheetDialog.findViewById(R.id.place_order);
        phoneNumber = bottomSheetDialog.findViewById(R.id.phone);

        TextView privacyText = bottomSheetDialog.findViewById(R.id.privacyText);

        assert privacyText != null;
        privacyText.setText(Html.fromHtml("I agree to the <a href=\"https://evaly.com.bd/about/terms-conditions\">Terms & Conditions</a> and <a href=\"https://evaly.com.bd/about/purchasing-policy\">Purchasing Policy</a> of Evaly."));
        privacyText.setMovementMethod(LinkMovementMethod.getInstance());

        CheckBox checkBox = bottomSheetDialog.findViewById(R.id.checkBox);


        plus.setOnClickListener(v -> {
            int quan = Integer.parseInt(quantity.getText().toString());
            total.setText(String.format(Locale.ENGLISH, "৳ %d", quan * voucherAmount));
            quan += 1;
            quantity.setText(String.format(Locale.ENGLISH, "%d", quan));
        });

        minus.setOnClickListener(v -> {
            int quan = Integer.parseInt(quantity.getText().toString());
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

            if (phoneNumber.getText().toString().equals(userDetails.getUserName())) {
                Toast.makeText(context, "You can't buy gift cards for yourself", Toast.LENGTH_LONG).show();
                return;
            }

            if (phoneNumber.getText().toString().equals("")) {
                Toast.makeText(context, "Please enter a number", Toast.LENGTH_LONG).show();
                return;
            }

            if (!Utils.isValidNumber(phoneNumber.getText().toString())) {
                Toast.makeText(context, "Please enter a correct phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Utils.isNumeric(quantity.getText().toString())) {
                if (Integer.parseInt(quantity.getText().toString()) > 10) {
                    Toast.makeText(context, "Quantity must be less than 10", Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                Toast.makeText(context, "Enter valid quantity", Toast.LENGTH_LONG).show();
                return;
            }

            assert checkBox != null;
            if (!checkBox.isChecked()) {
                Toast.makeText(context, "You must accept terms & conditions and purchasing policy to place an order.", Toast.LENGTH_LONG).show();
                return;
            }

            createOrder(giftCardSlug);
        });

    }

    public void catchError() {
        try {
            dialog.hideDialog();
        } catch (Exception e) {
        }

        Toast.makeText(context, "Sorry something went wrong. Please try again.", Toast.LENGTH_SHORT).show();

    }

    public void toggleBottomSheet(GiftCardListItem item) {
        quantity.setText("1");
        giftCardSlug = item.getSlug();
        getGiftCardDetails(giftCardSlug);
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

        GiftCardApiHelper.getGiftCard(currentPage, new ResponseListenerAuth<CommonDataResponse<List<GiftCardListItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<GiftCardListItem>> response, int statusCode) {

                loading = true;
                progressBar.setVisibility(View.GONE);

                List<GiftCardListItem> list = response.getData();

                itemList.addAll(list);
                adapter.notifyItemRangeChanged(itemList.size() - list.size(), list.size());

                if (currentPage == 1)
                    progressContainer.setVisibility(View.GONE);

                if (list.size() == 0 && currentPage == 1)
                    noItem.setVisibility(View.VISIBLE);

                currentPage++;
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }

    public void getGiftCardDetails(String slug) {

        if (userDetails.getToken().equals("")) {
            Toast.makeText(context, "You need to login first", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.showDialog();
        initializeBottomSheet();


        GiftCardApiHelper.getGiftCardDetails(slug, new ResponseListenerAuth<JsonObject, String>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                dialog.hideDialog();
                if (response.get("success").getAsBoolean()) {

                    Gson gson = new Gson();
                    GiftCardListItem item = gson.fromJson(response.get("data").toString(), GiftCardListItem.class);

                    name.setText(item.getName());
                    details.setText(item.getDescription());
                    voucherAmount = item.getPrice();
                    amount.setText(String.format("৳ %d", item.getPrice()));
                    total.setText(String.format("৳ %d", item.getPrice()));
                    cardValue.setText(String.format("৳ %d", item.getValue()));

                    if (item.getImageUrl() == null)
                        Glide.with(context).load("https://beta.evaly.com.bd/static/images/gift-card.jpg").placeholder(R.drawable.ic_placeholder_small).into(image);
                    else
                        Glide.with(context).load(item.getImageUrl()).placeholder(R.drawable.ic_placeholder_small).into(image);

                    bottomSheetDialog.show();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


                } else {
                    Toast.makeText(context, "Sorry the gift card is not available", Toast.LENGTH_SHORT).show();
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


    public void createOrder(String slug) {

        dialog.showDialog();

        JsonObject parameters = new JsonObject();

        parameters.addProperty("to", phoneNumber.getText().toString().trim());
        parameters.addProperty("gift_card", slug);
        int q = Integer.parseInt(quantity.getText().toString());
        parameters.addProperty("quantity", q);

        GiftCardApiHelper.placeGiftCardOrder(CredentialManager.getToken(), parameters, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                dialog.hideDialog();

                Toast.makeText(context, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                bottomSheetDialog.hide();
                startActivity(Objects.requireNonNull(getActivity()).getIntent());
                getActivity().finish();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }


}
