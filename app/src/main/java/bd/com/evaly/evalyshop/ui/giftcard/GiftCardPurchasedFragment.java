package bd.com.evaly.evalyshop.ui.giftcard;


import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.BottomSheetGiftCardPaymentBinding;
import bd.com.evaly.evalyshop.databinding.FragmentGiftcardListBinding;
import bd.com.evaly.evalyshop.databinding.PaymentAmountInputLayoutBinding;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListPurchasedItem;
import bd.com.evaly.evalyshop.models.image.ImageDataModel;
import bd.com.evaly.evalyshop.models.remoteConfig.RemoteConfigBaseUrls;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.giftcard.adapter.GiftCardListPurchasedAdapter;
import bd.com.evaly.evalyshop.ui.payment.builder.PaymentWebBuilder;
import bd.com.evaly.evalyshop.ui.payment.listener.PaymentListener;
import bd.com.evaly.evalyshop.ui.payment.model.PurchaseRequestInfo;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

import static android.app.Activity.RESULT_OK;

@AndroidEntryPoint
public class GiftCardPurchasedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, PaymentListener {

    public static GiftCardPurchasedFragment instance;

    @Inject
    PreferenceRepository preferenceRepository;
    @Inject
    ApiRepository apiRepository;
    @Inject
    FirebaseRemoteConfig remoteConfig;

    private FragmentGiftcardListBinding binding;
    private BottomSheetGiftCardPaymentBinding paymentBinding;
    private ArrayList<GiftCardListPurchasedItem> itemList;
    private GiftCardListPurchasedAdapter adapter;
    private ViewDialog dialog;
    private String giftCardInvoice = "";
    private String amount;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetBehavior bottomSheetBehavior;
    private int currentPage;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private boolean isImageSelected = false;

    private PaymentWebBuilder paymentWebBuilder;
    private String baseUrl = BuildConfig.BASE_URL + "cpn/";


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
        binding.swipeContainer.setRefreshing(false);
        getGiftCardList();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGiftcardListBinding.inflate(inflater);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        paymentWebBuilder = new PaymentWebBuilder(getActivity());
        paymentWebBuilder.setPaymentListener(this);

        binding.swipeContainer.setOnRefreshListener(this);

        itemList = new ArrayList<>();
        dialog = new ViewDialog(getActivity());

        currentPage = 1;

        initializeBottomSheet();

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(manager);

        instance = this;
        adapter = new GiftCardListPurchasedAdapter(getContext(), itemList, 0);
        binding.recyclerView.setAdapter(adapter);

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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


        getGiftCardList();

        RemoteConfigBaseUrls baseUrls = new Gson().fromJson(remoteConfig.getValue("temp_urls").asString(), RemoteConfigBaseUrls.class);

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

    public void buildBankDialog(Bitmap bitmap) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_bank_receipt, null);
        TextView amountET = dialogView.findViewById(R.id.amount);

        double amToPay = Double.parseDouble(paymentBinding.amountPay.getText().toString());

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
                ToastUtils.show("Please select your bank receipt image");
                return;
            }
            if (amount.equals("")) {
                ToastUtils.show("Please enter amount.");
                return;
            }
            uploadBankDepositImage(bitmap);
            dialogBuilder.dismiss();
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    public void paymentViaBank(String image) {

        ProgressDialog dialog = ProgressDialog.show(getContext(), "",
                "Updating data...", true);

        HashMap<String, String> parameters = new HashMap<>();
        try {
            double a = Double.parseDouble(paymentBinding.amountPay.getText().toString().trim());
            parameters.put("amount", Utils.formatPrice(a));
        } catch (Exception e) {
            ToastUtils.show("Invalid amount, enter only numbers!");
            return;
        }

        parameters.put("context", "gift_card_order_payment");
        parameters.put("context_reference", giftCardInvoice);
        parameters.put("bank_receipt_copy", image);

        apiRepository.payViaBank(preferenceRepository.getToken(), parameters, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                dialog.dismiss();
                ToastUtils.show(response.get("message").getAsString());

                if (bottomSheetBehavior != null)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                currentPage = 1;
                itemList.clear();
                adapter.notifyDataSetChanged();
                getGiftCardList();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialog.dismiss();
                if (getContext() != null)
                    Toast.makeText(getContext(), "Error occurred!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void uploadBankDepositImage(final Bitmap bitmap) {

        ProgressDialog dialog = ProgressDialog.show(getContext(), "",
                "Uploading image...", true);

        apiRepository.uploadImage(bitmap, new ResponseListener<CommonDataResponse<ImageDataModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<ImageDataModel> response, int statusCode) {
                dialog.dismiss();
                paymentViaBank(response.getData().getUrl());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), data.getData());
                isImageSelected = true;
                buildBankDialog(bitmap);
            } catch (Exception ignore) {
            }

        } else if (requestCode == 10003) {
            itemList.clear();
            adapter.notifyDataSetChanged();
            currentPage = 1;
            getGiftCardList();
        }
    }

    private void paymentAmountInput() {
        PaymentAmountInputLayoutBinding paymentAmountInputLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.payment_amount_input_layout, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter your payable amount");
        builder.setView(paymentAmountInputLayoutBinding.getRoot());
        AlertDialog dialog = builder.create();
        double amountDouble = Double.parseDouble(amount);
        paymentAmountInputLayoutBinding.amountPay.setText(String.format("%s", (int) (amountDouble)));
        paymentAmountInputLayoutBinding.makePayment.setOnClickListener(view -> {
            try {
                if (paymentAmountInputLayoutBinding.amountPay.getText().toString().trim().isEmpty()) {
                    paymentAmountInputLayoutBinding.amountPay.setError("Amount is required!");
                } else if (Integer.parseInt(paymentAmountInputLayoutBinding.amountPay.getText().toString()) == 0) {
                    paymentAmountInputLayoutBinding.amountPay.setError("Invalid amount!");
                } else if (Double.parseDouble(paymentAmountInputLayoutBinding.amountPay.getText().toString()) > Double.parseDouble(amount)) {
                    paymentAmountInputLayoutBinding.amountPay.setError("You have entered more than due amount");
                } else {
                    String paymentUrl = BuildConfig.WEB_URL + "giftcard-payment/init/" + giftCardInvoice + "?t=" + preferenceRepository.getTokenNoBearer() + "&context=gift_card_order_payment&amount=" + paymentAmountInputLayoutBinding.amountPay.getText().toString();
                    PurchaseRequestInfo purchaseRequestInfo = new PurchaseRequestInfo(preferenceRepository.getTokenNoBearer(), String.valueOf(amount), giftCardInvoice, "bKash");
                    paymentWebBuilder.setToolbarTitle("Gift Card Payment");
                    paymentWebBuilder.loadPaymentURL(paymentUrl, "success.html", purchaseRequestInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        paymentAmountInputLayoutBinding.close.setOnClickListener(view -> {
            dialog.dismiss();
        });
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white_round));
        dialog.show();
    }

    public void initializeBottomSheet() {

        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
            paymentBinding = BottomSheetGiftCardPaymentBinding.inflate(LayoutInflater.from(getContext()));
            bottomSheetDialog.setContentView(paymentBinding.getRoot());
        }

        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        bottomSheetInternal.setPadding(0, 0, 0, 0);

        new KeyboardUtil(getActivity(), bottomSheetInternal);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        if (remoteConfig.getString("nagad_description").isEmpty())
            paymentBinding.offerText.setVisibility(View.GONE);
        else {
            paymentBinding.offerText.setVisibility(View.VISIBLE);
            paymentBinding.offerText.setText("• " + remoteConfig.getString("nagad_description"));
        }

        TextView full_or_partial = bottomSheetDialog.findViewById(R.id.full_or_partial);
        full_or_partial.setVisibility(View.GONE);

        paymentBinding.bkash.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            onPaymentRedirect(BuildConfig.BKASH_URL, amount, giftCardInvoice);
        });

        paymentBinding.nagad.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            double amToPay = Double.parseDouble(paymentBinding.amountPay.getText().toString());
            payViaNagad(giftCardInvoice, Utils.formatPrice(amToPay));
        });

        paymentBinding.card.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            double amToPay = Double.parseDouble(paymentBinding.amountPay.getText().toString());
            addBalanceViaCard(giftCardInvoice, Utils.formatPrice(amToPay));
        });

        paymentBinding.others.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            double amToPay = Double.parseDouble(paymentBinding.amountPay.getText().toString());
            addBalanceViaSSL(giftCardInvoice, Utils.formatPrice(amToPay));
        });

        paymentBinding.bank.setOnClickListener(v -> {
            isImageSelected = false;
            amount = paymentBinding.amountPay.getText().toString();
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),
                    R.drawable.ic_upload_image_large);
            buildBankDialog(bitmap);
        });

    }


    public void toggleBottomSheet(GiftCardListPurchasedItem item) {

        initializeBottomSheet();

        giftCardInvoice = item.getInvoiceNo();
        paymentBinding.amountPay.setText(Utils.formatPrice(item.getTotal()));
        amount = item.getTotal() + "";
        paymentAmountInput();

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

        apiRepository.getPurchasedGiftCardList("purchased", currentPage, baseUrl,
                new ResponseListener<CommonDataResponse<List<GiftCardListPurchasedItem>>, String>() {
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
                            binding.noText.setText("You have not purchased any gift cards");
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


    public void payViaNagad(String invoice, String amount) {

        HashMap<String, String> payload = new HashMap<>();
        payload.put("amount", amount);
        payload.put("context", "gift_card_order_payment");
        payload.put("context_reference", invoice);
        payload.put("source", "MOBILE_APP");

        apiRepository.payViaNagad(preferenceRepository.getToken(), payload, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                if ((response != null && response.has("callBackUrl")) && !response.get("callBackUrl").isJsonNull()) {
                    String purl = response.get("callBackUrl").getAsString();
                    onPaymentRedirect(purl, amount, giftCardInvoice);
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });

    }

    public void addBalanceViaCard(String invoice, String amount) {

        if (paymentBinding.amountPay.getText().toString().equals("")) {
            ToastUtils.show("Enter amount");
            return;
        }

        onPaymentRedirect(BuildConfig.WEB_URL + "sebl/payment?amount=" + amount + "&invoice=" + invoice + "&token=" + preferenceRepository.getToken().replace("Bearer ", "") + "&context_reference=gift_card_order_payment", amount, giftCardInvoice);


//        HashMap<String, String> payload = new HashMap<>();
//        payload.put("amount", amount);
//        payload.put("context", "gift_card_order_payment");
//        payload.put("context_reference", invoice);
//
//        dialog.showDialog();
//
//        PaymentApiHelper.payViaCard(preferenceRepository.getToken(), payload, new ResponseListenerAuth<JsonObject, String>() {
//            @Override
//            public void onDataFetched(JsonObject response, int statusCode) {
//
//                dialog.hideDialog();
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//                String purl = response.get("payment_gateway_url").getAsString();
//                onPaymentRedirect(purl, amount, giftCardInvoice);
//
//            }
//
//            @Override
//            public void onFailed(String errorBody, int errorCode) {
//                dialog.hideDialog();
//                if (getContext() != null)
//                    Toast.makeText(getContext(), "Error occurred!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAuthError(boolean logout) {
//
//            }
//        });
    }

    public void addBalanceViaSSL(String invoice, String amount) {

        if (paymentBinding.amountPay.getText().toString().equals("")) {
            ToastUtils.show("Enter amount");
            return;
        }

        HashMap<String, String> payload = new HashMap<>();
        payload.put("amount", amount);
        payload.put("context", "gift_card_order_payment");
        payload.put("context_reference", invoice);

        dialog.showDialog();

        apiRepository.payViaCard(preferenceRepository.getToken(), payload, new ResponseListener<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                dialog.hideDialog();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                String purl = response.get("payment_gateway_url").getAsString();
                onPaymentRedirect(purl, amount, giftCardInvoice);

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialog.hideDialog();
                if (getContext() != null)
                    Toast.makeText(getContext(), "Error occurred!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void onPaymentRedirect(String url, String amount, String invoice_no) {
        String successURL;
        PurchaseRequestInfo purchaseRequestInfo = null;
        if (url.equals(BuildConfig.BKASH_URL)) {
            successURL = Constants.BKASH_SUCCESS_URL;
            paymentWebBuilder.setToolbarTitle(getResources().getString(R.string.bkash_payment));
            purchaseRequestInfo = new PurchaseRequestInfo(preferenceRepository.getTokenNoBearer(), amount, invoice_no, "gift_code");
        } else if (url.contains("nagad")) {
            successURL = Constants.SSL_SUCCESS_URL;
            paymentWebBuilder.setToolbarTitle("Pay via Nagad");
            ToastUtils.show("Opening Nagad gateway");
        } else if (url.contains("sebl")) {
            successURL = Constants.SSL_SUCCESS_URL + "===";
            paymentWebBuilder.setToolbarTitle("Pay via Visa / Master Card");
            purchaseRequestInfo = new PurchaseRequestInfo(preferenceRepository.getTokenNoBearer(), amount, invoice_no, "sebl");
            ToastUtils.show("Opening SEBL gateway");
        } else {
            successURL = Constants.SSL_SUCCESS_URL;
            paymentWebBuilder.setToolbarTitle(getResources().getString(R.string.pay_via_card));
            ToastUtils.show("Open payment gateway");
        }
        paymentWebBuilder.loadPaymentURL(url, successURL, purchaseRequestInfo);
    }

    @Override
    public void onPaymentSuccess(HashMap<String, String> values) {

    }

    @Override
    public void onPaymentFailure(HashMap<String, String> values) {

    }

    @Override
    public void onPaymentSuccess(String message) {
        itemList.clear();
        adapter.notifyDataSetChanged();
        currentPage = 1;
        getGiftCardList();
    }
}
