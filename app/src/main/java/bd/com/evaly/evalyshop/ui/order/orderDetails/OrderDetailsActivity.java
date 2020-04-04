package bd.com.evaly.evalyshop.ui.order.orderDetails;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.badoualy.stepperindicator.StepperIndicator;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.order.OrderDetailsProducts;
import bd.com.evaly.evalyshop.models.order.OrderIssueModel;
import bd.com.evaly.evalyshop.models.order.OrderStatus;
import bd.com.evaly.evalyshop.models.order.orderDetails.OrderDetailsModel;
import bd.com.evaly.evalyshop.models.order.orderDetails.OrderItemsItem;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.GiftCardApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.chat.viewmodel.ImageUploadView;
import bd.com.evaly.evalyshop.ui.issue.IssuesActivity;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.order.orderDetails.adapter.OrderDetailsProductAdapter;
import bd.com.evaly.evalyshop.ui.order.orderDetails.adapter.OrderStatusAdapter;
import bd.com.evaly.evalyshop.ui.payment.PaymentBottomSheet;
import bd.com.evaly.evalyshop.ui.order.orderDetails.refund.RefundBottomSheet;
import bd.com.evaly.evalyshop.util.Balance;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.RealPathUtil;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderDetailsActivity extends BaseActivity {

    private double total_amount = 0.0, paid_amount = 0.0, due_amount = 0.0;
    private String shopSlug = "";
    private String invoice_no = "";
    private String orderStatus = "pending", paymentStatus = "unpaid", paymentMethod = "";
    private UserDetails userDetails;
    private StepperIndicator indicator;
    private TextView tvPaymentStatus, shopName, shopAddress, shopnumber, username, userAddress, userNumber, totalPriceTextView, paidAmountTextView, duePriceTextView, tvCampaignRule;
    private TextView orderNumber, orderDate, paymentMethods, balance;
    private RecyclerView recyclerView, orderList;
    private ArrayList<OrderStatus> orderStatuses;
    private OrderStatusAdapter adapter;
    private ArrayList<OrderDetailsProducts> orderDetailsProducts;
    private OrderDetailsProductAdapter orderDetailsProductAdapter;
    private ViewDialog dialog;
    private TextView makePayment, payParially, full_or_partial;
    private RelativeLayout shopInfo;
    private TextView payViaGiftCard;
    private LinearLayout layoutBottomSheet, campaignRuleHolder;
    private BottomSheetDialog bottomSheetDialog;
    private Context context;
    private TextView cancelBtn;
    private NestedScrollView scrollView;
    private String imageUrl;
    private MenuItem cancelMenuItem;
    private MenuItem refundMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.order_details_menu, menu);

        cancelMenuItem = menu.findItem(R.id.action_cancel);
        refundMenuItem = menu.findItem(R.id.action_refund);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_cancel:
                cancelOrder();
                break;
            case R.id.action_refund:
                requestRefund();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void requestRefund() {

        if (invoice_no.equals("") || orderStatus.equals("") || paymentMethod.equals("") || paymentStatus.equals("")) {
            Toast.makeText(getApplicationContext(), "Can't request refund, reload the page", Toast.LENGTH_SHORT).show();
        } else {
            RefundBottomSheet refundBottomSheet = RefundBottomSheet.newInstance(invoice_no, orderStatus, paymentMethod, paymentStatus);
            refundBottomSheet.show(getSupportFragmentManager(), "Refund");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(R.string.order_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        tvPaymentStatus = findViewById(R.id.paymentStatus);
        cancelBtn = findViewById(R.id.cancelBtn);
        campaignRuleHolder = findViewById(R.id.campaignRuleHolder);
        tvCampaignRule = findViewById(R.id.tvCampaignRule);
        full_or_partial = findViewById(R.id.full_or_partial);
        scrollView = findViewById(R.id.scroll);
        shopName = findViewById(R.id.billfromName);
        shopAddress = findViewById(R.id.billfromAddress);
        shopnumber = findViewById(R.id.billfromPhone);
        username = findViewById(R.id.billtoName1);
        userAddress = findViewById(R.id.billtoAddress1);
        userNumber = findViewById(R.id.billtoPhone);
        totalPriceTextView = findViewById(R.id.total_price);
        paidAmountTextView = findViewById(R.id.paid_amount);
        duePriceTextView = findViewById(R.id.due_price);
        orderNumber = findViewById(R.id.order_id);
        orderNumber.setText(invoice_no);
        orderDate = findViewById(R.id.order_date);
        paymentMethods = findViewById(R.id.payment_method);
        recyclerView = findViewById(R.id.recycle);
        balance = findViewById(R.id.balance);
        orderList = findViewById(R.id.product_list);
        shopInfo = findViewById(R.id.shop_info);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        orderList.setLayoutManager(new LinearLayoutManager(this));
        userDetails = new UserDetails(this);
        orderStatuses = new ArrayList<>();
        adapter = new OrderStatusAdapter(orderStatuses, this);
        recyclerView.setAdapter(adapter);
        orderDetailsProducts = new ArrayList<>();
        orderDetailsProductAdapter = new OrderDetailsProductAdapter(this, orderDetailsProducts);
        orderList.setAdapter(orderDetailsProductAdapter);

        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == 0)
                getSupportActionBar().setElevation(0);
            else
                getSupportActionBar().setElevation(4f);
        });

        shopInfo.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailsActivity.this, MainActivity.class);
            intent.putExtra("type", 3);
            intent.putExtra("shop_slug", shopSlug);
            intent.putExtra("category", "root");
            startActivity(intent);
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            invoice_no = extras.getString("orderID");
            Log.d("order_id", invoice_no);

            orderNumber.setText("#" + invoice_no);
            getOrderDetails();
        }
        balance.setText(Html.fromHtml(getString(R.string.balance) + ": <b>৳ " + userDetails.getBalance() + "</b>"));

        getOrderHistory();

        indicator = findViewById(R.id.indicator);
        indicator.setStepCount(6);

        dialog = new ViewDialog(this);
        dialog.showDialog();

        makePayment = findViewById(R.id.makePayment);
        payParially = findViewById(R.id.payPartially);

        makePayment.setOnClickListener(v -> {
            PaymentBottomSheet paymentBottomSheet = PaymentBottomSheet.newInstance(invoice_no, total_amount, paid_amount);
            paymentBottomSheet.show(getSupportFragmentManager(), "payment");
        });

        payViaGiftCard = findViewById(R.id.payViaGiftCard);
        payViaGiftCard.setOnClickListener(v -> dialogGiftCardPayment());


    }

    @OnClick(R.id.tvViewIssue)
    void viewIssues() {
        startActivity(new Intent(OrderDetailsActivity.this, IssuesActivity.class).putExtra("invoice", invoice_no));
    }

    @OnClick(R.id.tvReport)
    void report() {
        OrderIssueModel model = new OrderIssueModel();

        bottomSheetDialog = new BottomSheetDialog(OrderDetailsActivity.this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.report_view);

        Spinner spinner = bottomSheetDialog.findViewById(R.id.spnDelivery);
        EditText etDescription = bottomSheetDialog.findViewById(R.id.etDescription);
        Button btnSubmit = bottomSheetDialog.findViewById(R.id.btnSubmit);
        ImageView ivClose = bottomSheetDialog.findViewById(R.id.ivClose);
        LinearLayout addPhoto = bottomSheetDialog.findViewById(R.id.addPhoto);

        List<String> options = new ArrayList<>();

        List<OrderIssueModel> list;
        if (orderStatus.equals("pending"))
            list = Constants.getIssueListPending();
        else
            list = Constants.getDelivaryIssueList();

        for (int i = 0; i < list.size(); i++) {
            options.add(list.get(i).getDescription());
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, options);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                model.setIssue_type(list.get(i).getIssue_type());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addPhoto.setOnClickListener(view -> openImageSelector());

        btnSubmit.setOnClickListener(view -> {
            if (etDescription.getText().toString().trim().isEmpty()) {
                etDescription.setError("Required");
                return;
            }
            model.setDescription(etDescription.getText().toString());

            model.setAttachment(imageUrl);
            dialog.showDialog();
            submitIssue(model, bottomSheetDialog);
            imageUrl = "";
        });

        View bottomSheetInternal = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
        new KeyboardUtil(OrderDetailsActivity.this, bottomSheetInternal);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    view.post(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetDialog.dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        bottomSheetDialog.setCancelable(false);

        bottomSheetDialog.show();

        ivClose.setOnClickListener(view -> bottomSheetDialog.dismiss());
    }

    private void submitIssue(OrderIssueModel model, BottomSheetDialog bottomSheetDialog) {
        AuthApiHelper.submitIssue(model, invoice_no, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
            @Override
            public void onDataFetched(retrofit2.Response<JsonObject> response) {
                dialog.hideDialog();
                if (response.code() == 200 || response.code() == 201) {
                    Toast.makeText(getApplicationContext(), "Your issue has been submitted, you will be notified shortly", Toast.LENGTH_LONG).show();
                    bottomSheetDialog.dismiss();
                } else if (response.code() == 401) {
                    submitIssue(model, bottomSheetDialog);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(int status) {
                dialog.hideDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void setPostPic() {

        if (imageUrl != null && bottomSheetDialog.isShowing()) {

            bottomSheetDialog.findViewById(R.id.postImage).setVisibility(View.VISIBLE);
            Glide.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .skipMemoryCache(true)
                    .fitCenter()
                    .optionalCenterCrop()
                    .placeholder(R.drawable.half_dp_bg_light)
                    .into((ImageView) bottomSheetDialog.findViewById(R.id.postImage));
        }

    }

    private void openSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, 1001);
    }

    private void openImageSelector() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    8000);
        } else {
            openSelector();
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 8000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openSelector();
            else {
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void dialogGiftCardPayment() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.WideDialog));

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_pay_with_gift_card, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();

        Button d_submit = dialogView.findViewById(R.id.submit);
        final EditText amount = dialogView.findViewById(R.id.amount);
        final EditText code = dialogView.findViewById(R.id.code);
        ImageView closeBtn = dialogView.findViewById(R.id.closeBtn);

        amount.setText((int) due_amount + "");

        closeBtn.setOnClickListener(view -> alertDialog.dismiss());

        alertDialog.getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        alertDialog.show();

        d_submit.setOnClickListener(v -> {
            if (amount.getText().toString().equals("")) {
                Toast.makeText(context, "Please enter an amount.", Toast.LENGTH_SHORT).show();
                return;
            } else if (code.getText().toString().equals("")) {
                Toast.makeText(context, "Please enter gift card coupon code.", Toast.LENGTH_SHORT).show();
                return;
            }
            double partial_amount = Double.parseDouble(amount.getText().toString());

            if (partial_amount > total_amount) {
                Toast.makeText(context, "You have entered an amount that is larger than your due amount.", Toast.LENGTH_LONG).show();
                return;
            }

            makePaymentViaGiftCard(code.getText().toString(), invoice_no, String.valueOf((int) partial_amount));

            alertDialog.dismiss();

        });
    }


    public void makePaymentViaGiftCard(String giftCode, String invoice, String amount) {

        HashMap<String, String> payload = new HashMap<>();

        payload.put("invoice_no", invoice);
        payload.put("gift_code", giftCode);
        payload.put("amount", amount);

        ViewDialog dialog2 = new ViewDialog(this);
        dialog2.showDialog();

        GiftCardApiHelper.payWithGiftCard(CredentialManager.getToken(), payload, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                dialog2.hideDialog();
                Toast.makeText(OrderDetailsActivity.this, response.get("message").getAsString(), Toast.LENGTH_LONG).show();
                if (response.has("success")) {
                    if (response.get("success").getAsBoolean())
                        giftCardSuccessDialog();
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                Toast.makeText(OrderDetailsActivity.this, "Payment unsuccessful!", Toast.LENGTH_LONG).show();
                dialog2.hideDialog();
            }

            @Override
            public void onAuthError(boolean logout) {
                if (logout)
                    AppController.logout(OrderDetailsActivity.this);
                else
                    makePaymentViaGiftCard(giftCode, invoice, amount);
            }
        });
    }


    private void giftCardSuccessDialog() {

        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setMessage("Thank you for your payment. We are updating your order and you will be notified soon. If your order is not updated, please contact us.")

                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    finish();
                    startActivity(getIntent());
                })
                .show();


    }


    @Override
    public void onResume() {
        super.onResume();
        Balance.update(this, false);
        checkCardBalance();

    }

    public void updatePage() {

        scrollView.postDelayed(() -> scrollView.fullScroll(View.FOCUS_UP), 50);
        getOrderHistory();
        Balance.update(this, balance);
        getOrderDetails();

    }


    public void checkCardBalance() {

        AuthApiHelper.getUserInfoPay(CredentialManager.getToken(), CredentialManager.getUserName(), new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                response = response.getAsJsonObject("data");

                if (response.get("gift_card_balance").getAsDouble() < 1)
                    payViaGiftCard.setVisibility(View.GONE);
                balance.setText(Html.fromHtml(getString(R.string.balance) + ": <b>৳ " + response.get("balance").getAsString() + "</b>"));
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10002) {
            if (resultCode == Activity.RESULT_OK) {
                getOrderDetails();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        }

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImage = data.getData();
            String imagePath = RealPathUtil.getRealPath(context, selectedImage);
            Log.d("json image uri", imagePath);

            try {
                try {

                    Uri resultUri = data.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(resultUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(imageStream);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (bitmap == null) {
                        Toast.makeText(getApplicationContext(), R.string.something_wrong, Toast.LENGTH_LONG).show();
                        return;
                    }

                    dialog.showDialog();

                    Logger.d("+_+_+_+_+_+");

                    AuthApiHelper.uploadImage(this, bitmap, new ImageUploadView() {
                        @Override
                        public void onImageUploadSuccess(String img, String smImg) {
                            dialog.hideDialog();
                            imageUrl = img;
                            setPostPic();
                        }

                        @Override
                        public void onImageUploadFailed(String msg) {
                            dialog.hideDialog();
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (Exception e) {

                }

            } catch (Exception e) {

                Log.d("json image error", e.toString());
                Toast.makeText(context, "Error occurred while uploading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void inflateMenu() {

        if (refundMenuItem == null || cancelMenuItem == null)
            return;

        if (Utils.canRefundRequest(paymentStatus, orderStatus, paymentMethod))
            refundMenuItem.setVisible(true);
        else
            refundMenuItem.setVisible(false);

        if (orderStatus.equals("pending") && paid_amount < 1)
            cancelMenuItem.setVisible(true);
        else
            cancelMenuItem.setVisible(false);
    }

    public void getOrderDetails() {

        OrderApiHelper.getOrderDetails(CredentialManager.getToken(), invoice_no, new ResponseListenerAuth<OrderDetailsModel, String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataFetched(OrderDetailsModel response, int statusCode) {

                dialog.hideDialog();

                orderStatus = response.getOrderStatus().toLowerCase();
                paymentMethod = response.getPaymentMethod();
                paymentStatus = response.getPaymentStatus();


                if (paymentStatus.toLowerCase().equals("refund_requested"))
                    tvPaymentStatus.setText("Refund Requested");
                else
                    tvPaymentStatus.setText(Utils.toFirstCharUpperAll(paymentStatus));


                if (paymentStatus.toLowerCase().equals("paid")) {
                    tvPaymentStatus.setBackgroundColor(Color.parseColor("#33d274"));
                    tvPaymentStatus.setTextColor(Color.parseColor("#ffffff"));
                } else if (paymentStatus.toLowerCase().equals("unpaid")) {
                    tvPaymentStatus.setBackgroundColor(Color.parseColor("#f0ac4e"));
                    tvPaymentStatus.setTextColor(Color.parseColor("#ffffff"));
                } else if (paymentStatus.toLowerCase().equals("partial")) {
                    tvPaymentStatus.setBackgroundColor(Color.parseColor("#009688"));
                    tvPaymentStatus.setTextColor(Color.parseColor("#ffffff"));
                } else if (paymentStatus.toLowerCase().equals("refunded")) {
                    tvPaymentStatus.setTextColor(Color.parseColor("#333333"));
                    tvPaymentStatus.setBackgroundColor(Color.parseColor("#eeeeee"));
                } else if (paymentStatus.toLowerCase().equals("refund_requested")) {
                    tvPaymentStatus.setBackgroundColor(Color.parseColor("#c45da8"));
                    tvPaymentStatus.setTextColor(Color.parseColor("#ffffff"));
                }


                if (orderStatus.equals("pending")) {
                    indicator.setCurrentStep(1);
                } else if (orderStatus.equals("confirmed")) {
                    indicator.setCurrentStep(2);
                } else if (orderStatus.equals("processing")) {
                    indicator.setCurrentStep(3);
                } else if (orderStatus.equals("picked")) {
                    indicator.setCurrentStep(4);
                } else if (orderStatus.equals("shipped")) {
                    indicator.setCurrentStep(5);
                } else if (orderStatus.equals("delivered")) {
                    indicator.setCurrentStep(6);
                }

                if (!response.isDeliveryConfirmed() && response.isDeliveryConfirmationRequired())
                    deliveryConfirmationDialog();

                orderDate.setText(Utils.formattedDateFromString("", "yyyy-MM-d", response.getDate()));

                if (response.getOrderStatus().toLowerCase().equals("cancel")) {

                    StepperIndicator indicatorCancelled = findViewById(R.id.indicatorCancelled);
                    indicatorCancelled.setVisibility(View.VISIBLE);
                    indicatorCancelled.setCurrentStep(6);
                    indicator.setDoneIcon(getDrawable(R.drawable.ic_close_smallest));
                    indicator.setVisibility(View.GONE);
                    makePayment.setVisibility(View.GONE);
                    payParially.setVisibility(View.GONE);
                    payViaGiftCard.setVisibility(View.GONE);

                } else if (response.getOrderStatus().toLowerCase().equals("delivered")) {

                    makePayment.setVisibility(View.GONE);
                    payParially.setVisibility(View.GONE);
                    payViaGiftCard.setVisibility(View.GONE);
                }

                username.setText(String.format("%s %s", response.getCustomer().getFirstName(), response.getCustomer().getLastName()));
                userAddress.setText(response.getCustomerAddress());
                userNumber.setText(response.getContactNumber());
                totalPriceTextView.setText(String.format(Locale.ENGLISH, "৳ %d", Math.round(Double.parseDouble(response.getTotal()))));
                paidAmountTextView.setText(String.format(Locale.ENGLISH, "৳ %d", Math.round(Double.parseDouble(response.getPaidAmount()))));
                duePriceTextView.setText(String.format(Locale.ENGLISH, "৳ %d", Math.round(Double.parseDouble(response.getTotal())) - Math.round(Double.parseDouble(response.getPaidAmount()))));

                if (response.getCampaignRules().size() > 0) {

                    JsonObject campaignRuleObject = response.getCampaignRules().get(0);

                    double cashback_percentage = campaignRuleObject.get("cashback_percentage").getAsDouble();

                    if (cashback_percentage > 0) {

                        String payMethod = campaignRuleObject.get("cashback_on_payment_by").getAsString();

                        campaignRuleHolder.setVisibility(View.VISIBLE);
                        payMethod = payMethod.replaceAll("card", "Credit/Debit Card");
                        payMethod = payMethod.replaceAll("balance", "Balance");
                        payMethod = payMethod.replaceAll("bank", "Bank Account");
                        payMethod = payMethod.replaceAll("gift_code", "Gift Code");
                        payMethod = payMethod.replaceAll(",", ", ");
                        payMethod = payMethod.replaceAll("  ", " ");

                        if (!campaignRuleObject.get("cashback_date").isJsonNull()) {

                            String cashback_date = campaignRuleObject.get("cashback_date").getAsString();
                            String inputFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";

                            SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
                            df_input.setTimeZone(TimeZone.getTimeZone("UTC"));

                            Calendar start = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
                            Calendar end = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);

//                            end.set(Calendar.HOUR_OF_DAY, 0);
//                            start.set(Calendar.HOUR_OF_DAY, 0);

                            try {
                                end.setTime(df_input.parse(cashback_date));
                            } catch (ParseException e) {
                                Log.e("timze", e.toString());
                            }

//                            long startTime = start.getTimeInMillis();
//                            long diffTime = end.getTimeInMillis() - startTime;
//                            long diffDays = TimeUnit.DAYS.convert(diffTime, TimeUnit.MILLISECONDS);

                            int diffDays = Utils.daysBetween(start, end);

                            String message = "Payments with <font color=\"#c53030\">" + payMethod + "</font> will be rewarded by <b>" + cashback_percentage + "%</b> cashback balance within <b>" + diffDays + " days</b>.";
                            tvCampaignRule.setText(Html.fromHtml(message));

                            if (diffDays < 1)
                                campaignRuleHolder.setVisibility(View.GONE);
                        } else {
                            String message = "Payments with <font color=\"#c53030\">" + payMethod + "</font> will be rewarded by <b>" + cashback_percentage + "%</b> cashback balance instantly.";
                            tvCampaignRule.setText(Html.fromHtml(message));
                        }
                    }
                }


                String payMethod = response.getPaymentMethod();

                if (payMethod.equals("cod"))
                    paymentMethods.setText("Cash on Delivery");
                else if (payMethod.equals(""))
                    paymentMethods.setVisibility(View.GONE);
                else {

                    payMethod = payMethod.replaceAll("card", "Card");
                    payMethod = payMethod.replaceAll("balance", "Balance");
                    payMethod = payMethod.replaceAll("bank", "Bank Account");
                    payMethod = payMethod.replaceAll("gift_code", "Gift Code");
                    payMethod = payMethod.replaceAll(",", ", ");
                    payMethod = payMethod.replaceAll("  ", " ");

                    paymentMethods.setText(Utils.capitalize(payMethod));
                }

                shopName.setText(response.getShop().getName());
                shopSlug = response.getShop().getSlug();
                shopAddress.setText(response.getShop().getAddress());
                shopnumber.setText(response.getShop().getContactNumber());

                total_amount = Math.round(Double.parseDouble(response.getTotal()));
                paid_amount = Math.round(Double.parseDouble(response.getPaidAmount()));
                due_amount = total_amount - paid_amount;

                if (due_amount < 1) {
                    makePayment.setVisibility(View.GONE);
                    payParially.setVisibility(View.GONE);
                    payViaGiftCard.setVisibility(View.GONE);
                }

//                if (orderStatus.equals("pending") && paid_amount < 1) {
//                    cancelBtn.setVisibility(View.VISIBLE);
//                    cancelBtn.setOnClickListener(view -> cancelOrder());
//                }


                inflateMenu();

                orderDetailsProducts.clear();
                orderDetailsProductAdapter.notifyDataSetChanged();

                List<OrderItemsItem> orderItemList = response.getOrderItems();

                for (int i = 0; i < orderItemList.size(); i++) {

                    OrderItemsItem orderItem = orderItemList.get(i);
                    String productVariation = "";

                    for (int j = 0; j < orderItem.getVariations().size(); j++) {

                        JsonObject varJ = orderItem.getVariations().get(j).getAsJsonObject();
                        String attr = varJ.get("attribute").getAsString();
                        String variation = varJ.get("attribute_value").getAsString();

                        if (j > 0)
                            productVariation = productVariation + ", " + attr + ": " + variation;
                        else
                            productVariation = attr + ": " + variation;
                    }

                    orderDetailsProducts.add(
                            new OrderDetailsProducts(
                                    orderItem.getItemImages().get(0),
                                    orderItem.getItemName(),
                                    orderItem.getProductSlug(),
                                    orderItem.getOrderTimePrice(),
                                    String.valueOf(orderItem.getQuantity()),
                                    (Math.round(Double.parseDouble(orderItem.getOrderTimePrice())) * orderItem.getQuantity()) + "",
                                    productVariation));

                    orderDetailsProductAdapter.notifyItemInserted(orderDetailsProducts.size());
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


    public void cancelOrder() {

        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_cancel_order);
        dialog.setTitle("Select Cancellation Reason");

        TextView button = dialog.findViewById(R.id.btn);
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);

        assert button != null;
        button.setOnClickListener(view -> {

            if ((radioGroup != null ? radioGroup.getCheckedRadioButtonId() : 0) == -1) {
                Toast.makeText(this, "Select a cancellation reason", Toast.LENGTH_SHORT).show();
                return;
            }

            assert radioGroup != null;
            int index = radioGroup.indexOfChild(dialog.findViewById(radioGroup.getCheckedRadioButtonId()));

            String reason = getResources().getStringArray(R.array.cancelReasons)[index];

            OrderApiHelper.cancelOrder(CredentialManager.getToken(), invoice_no, reason, new ResponseListenerAuth<JsonObject, String>() {
                @Override
                public void onDataFetched(JsonObject response, int statusCode) {

                    updatePage();
                    cancelBtn.setVisibility(View.GONE);
                    dialog.dismiss();
                }

                @Override
                public void onFailed(String errorBody, int errorCode) {
                    Toast.makeText(context, "Can't cancel this order!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthError(boolean logout) {

                }
            });
        });

        dialog.show();

    }

    public void getOrderHistory() {

        orderStatuses.clear();
        OrderApiHelper.getOrderHistories(CredentialManager.getToken(), invoice_no, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                JsonArray list = response.getAsJsonObject("data").getAsJsonArray("histories");

                for (int i = 0; i < list.size(); i++) {
                    JsonObject jsonObject = list.get(i).getAsJsonObject();

                    orderStatuses.add(new OrderStatus(
                            jsonObject.get("date").getAsString(),
                            jsonObject.get("order_status").getAsString(),
                            jsonObject.get("note").getAsString())
                    );

                    adapter.notifyDataSetChanged();
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


    @Override
    public void onBackPressed() {
        finish();
    }


    public void deliveryConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsActivity.this);
        builder.setTitle("Did you receive the product?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            HashMap<String, String> data = new HashMap<>();
            data.put("invoice_no", invoice_no);
            AuthApiHelper.updateProductStatus(data, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                @Override
                public void onDataFetched(retrofit2.Response<JsonObject> response1) {
                    if (response1.code() == 200 || response1.code() == 201) {
                        dialog.hideDialog();
                        Toast.makeText(getApplicationContext(), "Order Updated", Toast.LENGTH_LONG).show();
                    } else {
                        dialog.hideDialog();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailed(int status) {
                    dialog.hideDialog();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                }
            });
        }).setNegativeButton("No", null);

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
