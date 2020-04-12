package bd.com.evaly.evalyshop.ui.giftcard;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListPurchasedItem;
import bd.com.evaly.evalyshop.models.image.ImageDataModel;
import bd.com.evaly.evalyshop.rest.apiHelper.GiftCardApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ImageApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.PaymentApiHelper;
import bd.com.evaly.evalyshop.ui.giftcard.adapter.GiftCardListPurchasedAdapter;
import bd.com.evaly.evalyshop.ui.order.PayViaBkashActivity;
import bd.com.evaly.evalyshop.ui.order.PayViaCard;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;

import static android.app.Activity.RESULT_OK;


public class GiftCardPurchasedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static GiftCardPurchasedFragment instance;
    private View view;
    private RecyclerView recyclerView;
    private ArrayList<GiftCardListPurchasedItem> itemList;
    private GiftCardListPurchasedAdapter adapter;
    private RequestQueue rq;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout layoutBottomSheet;
    private ViewDialog dialog;
    private UserDetails userDetails;
    private String giftCardInvoice = "";
    private String amount;
    private LinearLayout noItem;
    private Context context;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheetInternal;
    private LinearLayout progressContainer;
    private ProgressBar progressBar;
    private int currentPage;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private TextView amountToPayView;
    private ImageView bkash, cards, bank;
    private SwipeRefreshLayout swipeLayout;
    private boolean loading = true;
    private boolean isImageSelected = false;


    public GiftCardPurchasedFragment() {
        // Required empty public constructor
    }

    public static GiftCardPurchasedFragment getInstance() {
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
    public void onResume() {
        super.onResume();

        itemList.clear();
        adapter.notifyDataSetChanged();
        currentPage = 1;
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
        adapter = new GiftCardListPurchasedAdapter(context, itemList, 0);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = manager.getChildCount();
                    totalItemCount = manager.getItemCount();
                    pastVisiblesItems = manager.findFirstVisibleItemPosition();
                    if (loading)
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount)
                            getGiftCardList();
                }
            }
        });

        return view;
    }

    public void buildBankDialog(Bitmap bitmap) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_bank_receipt, null);
        TextView amountET = dialogView.findViewById(R.id.amount);

        double amToPay = Double.parseDouble(amountToPayView.getText().toString());

        amountET.setText((int) amToPay + "");
        amountET.setVisibility(View.GONE);

        Button submit = dialogView.findViewById(R.id.buttonSubmit);
        Button cancel = dialogView.findViewById(R.id.buttonCancel);

        ImageView selectImage = dialogView.findViewById(R.id.upload);

        if (isImageSelected)
            selectImage.setImageBitmap(bitmap);

        selectImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Bank Deposit Photo"), 1000);
            dialogBuilder.dismiss();
        });
        cancel.setOnClickListener(view -> dialogBuilder.dismiss());
        submit.setOnClickListener(view -> {
            if (!isImageSelected) {
                Toast.makeText(context, "Please select your bank receipt image", Toast.LENGTH_LONG).show();
                return;
            }
            if (amount.equals("")) {
                Toast.makeText(context, "Please enter amount.", Toast.LENGTH_LONG).show();
                return;
            }
            uploadBankDepositImage(bitmap);
            dialogBuilder.dismiss();
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    public void paymentViaBank(String image) {

        ProgressDialog dialog = ProgressDialog.show(context, "",
                "Updating data...", true);


        HashMap<String, String> parameters = new HashMap<>();

        try {
            double a = Double.parseDouble(amountToPayView.getText().toString().trim());
            parameters.put("amount", a);
        } catch (Exception e) {
            Toast.makeText(context, "Invalid amount, enter only numbers!", Toast.LENGTH_SHORT).show();
            return;
        }

        parameters.put("context", "gift_card_order_payment");
        parameters.put("context_reference", giftCardInvoice);
        parameters.put("bank_receipt_copy", image);

        PaymentApiHelper.payViaBank(CredentialManager.getToken(), parameters, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                dialog.dismiss();
                Toast.makeText(context, response.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                getActivity().finish();
                startActivity(getActivity().getIntent());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    paymentViaBank(image);

            }
        });
    }

    private void uploadBankDepositImage(final Bitmap bitmap) {

        ProgressDialog dialog = ProgressDialog.show(context, "",
                "Uploading image...", true);

        ImageApiHelper.uploadImage(bitmap, new ResponseListenerAuth<CommonDataResponse<ImageDataModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<ImageDataModel> response, int statusCode) {
                dialog.dismiss();
                paymentViaBank(response.getData().getUrl());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    uploadBankDepositImage(bitmap);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                isImageSelected = true;
                buildBankDialog(bitmap);

            } catch (Exception e) {

            }
        }
    }

    public void initializeBottomSheet() {

        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_gift_card_payment);

        bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        bottomSheetInternal.setPadding(0, 0, 0, 0);

        new KeyboardUtil(getActivity(), bottomSheetInternal);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        layoutBottomSheet = bottomSheetDialog.findViewById(R.id.bottom_sheet);

        amountToPayView = bottomSheetDialog.findViewById(R.id.amountPay);
        bkash = bottomSheetDialog.findViewById(R.id.bkash);
        cards = bottomSheetDialog.findViewById(R.id.card);
        bank = bottomSheetDialog.findViewById(R.id.bank);

        TextView full_or_partial = bottomSheetDialog.findViewById(R.id.full_or_partial);
        full_or_partial.setVisibility(View.GONE);

        bkash.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            Intent intent = new Intent(context, PayViaBkashActivity.class);
            intent.putExtra("amount", amountToPayView.getText().toString());
            intent.putExtra("invoice_no", giftCardInvoice);
            intent.putExtra("context", "gift_card_order_payment");
            startActivityForResult(intent, 10002);
        });

        cards.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            double amToPay = Double.parseDouble(amountToPayView.getText().toString());
            addBalanceViaCard(giftCardInvoice, Utils.formatePrice(amToPay));
        });

        bank.setOnClickListener(v -> {
            isImageSelected = false;
            amount = amountToPayView.getText().toString();
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ic_upload_image_large);
            buildBankDialog(bitmap);
        });

    }


    public void toggleBottomSheet(GiftCardListPurchasedItem item) {

        initializeBottomSheet();

        giftCardInvoice = item.getInvoiceNo();
        amountToPayView.setText(Utils.formatePrice(item.getTotal()));
        amount = item.getTotal() + "";
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

        GiftCardApiHelper.getPurchasedGiftCardList("purchased", currentPage, new ResponseListenerAuth<CommonDataResponse<List<GiftCardListPurchasedItem>>, String>() {
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
                    noText.setText("You have not purchased any gift cards");
                } else {
                    noItem.setVisibility(View.GONE);
                }

                if (response.getData().size() < 10)
                    loading = false;

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    getGiftCardList();
            }
        });

    }

    public void addBalanceViaCard(String invoice, String amount) {

        if (amountToPayView.getText().toString().equals("")) {
            Toast.makeText(context, "Enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> payload = new HashMap<>();
        payload.put("amount", amount);
        payload.put("context", "gift_card_order_payment");
        payload.put("context_reference", invoice);

        dialog.showDialog();

        PaymentApiHelper.payViaCard(CredentialManager.getToken(), payload, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                dialog.hideDialog();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                String purl = response.get("payment_gateway_url").getAsString();
                Intent intent = new Intent(context, PayViaCard.class);
                intent.putExtra("url", purl);
                startActivityForResult(intent, 10002);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialog.hideDialog();
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });


    }


}
