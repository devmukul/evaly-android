package bd.com.evaly.evalyshop.ui.order.orderDetails;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.badoualy.stepperindicator.StepperIndicator;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.ActivityOrderDetailsBinding;
import bd.com.evaly.evalyshop.databinding.BottomSheetCreateIssueBinding;
import bd.com.evaly.evalyshop.databinding.BottomSheetUpdateOrderAddressBinding;
import bd.com.evaly.evalyshop.databinding.DialogConfirmDeliveryBinding;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.hero.DeliveryHeroResponse;
import bd.com.evaly.evalyshop.models.image.ImageDataModel;
import bd.com.evaly.evalyshop.models.issueNew.category.IssueCategoryModel;
import bd.com.evaly.evalyshop.models.issueNew.create.IssueCreateBody;
import bd.com.evaly.evalyshop.models.issueNew.list.IssueListModel;
import bd.com.evaly.evalyshop.models.order.OrderDetailsProducts;
import bd.com.evaly.evalyshop.models.order.OrderStatus;
import bd.com.evaly.evalyshop.models.order.orderDetails.OrderDetailsModel;
import bd.com.evaly.evalyshop.models.order.orderDetails.OrderItemsItem;
import bd.com.evaly.evalyshop.models.order.payment.ParitalPaymentModel;
import bd.com.evaly.evalyshop.models.order.updateAddress.UpdateOrderAddressRequest;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.GiftCardApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ImageApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.IssueApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.issue.IssuesActivity;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.order.orderDetails.adapter.OrderDetailsProductAdapter;
import bd.com.evaly.evalyshop.ui.order.orderDetails.adapter.OrderStatusAdapter;
import bd.com.evaly.evalyshop.ui.order.orderDetails.refund.RefundBottomSheet;
import bd.com.evaly.evalyshop.ui.payment.bottomsheet.PaymentBottomSheet;
import bd.com.evaly.evalyshop.util.Balance;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.RealPathUtil;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.evaly.evalypaymentlibrary.builder.PaymentWebBuilder;
import bd.evaly.evalypaymentlibrary.listener.PaymentListener;
import bd.evaly.evalypaymentlibrary.model.PurchaseRequestInfo;

public class OrderDetailsActivity extends BaseActivity implements PaymentBottomSheet.PaymentOptionListener, PaymentListener {

    private ActivityOrderDetailsBinding binding;
    private double total_amount = 0.0, paid_amount = 0.0, due_amount = 0.0;
    private String shopSlug = "";
    private String invoice_no = "";
    private String orderStatus = "pending", paymentStatus = "unpaid", paymentMethod = "";
    private StepperIndicator indicator;
    private ArrayList<OrderStatus> orderStatuses;
    private OrderStatusAdapter adapter;
    private ArrayList<OrderDetailsProducts> orderDetailsProducts;
    private OrderDetailsProductAdapter orderDetailsProductAdapter;
    private ViewDialog dialog;
    private BottomSheetDialog bottomSheetDialog;
    private String imageUrl;
    private MenuItem cancelMenuItem;
    private MenuItem refundMenuItem;
    private List<IssueCategoryModel> categoryList;
    private OrderDetailsModel orderDetailsModel;
    private PaymentWebBuilder paymentWebBuilder;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private String paymetMethods;
    private String paymentMessage;
    private boolean showCodConfirmDialog = false;
    private String deliveryChargeText = null;
    private String deliveryChargeApplicable = null;
    private OrderDetailsViewModel viewModel;
    private boolean isRefundEligible = false;
    private String disabledPaymentMethods = "", disabledPaymentMethodText = "", nagadBadgeText = "", nagadDescription = "";

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
            ToastUtils.show("Can't request refund, reload the page");
        } else {
            RefundBottomSheet refundBottomSheet = RefundBottomSheet.newInstance(invoice_no, orderStatus, paymentMethod, paymentStatus, isRefundEligible);
            refundBottomSheet.show(getSupportFragmentManager(), "Refund");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        // For Payment Listener

        viewModel = new ViewModelProvider(this).get(OrderDetailsViewModel.class);
        setupRemoteConfig();
        paymentWebBuilder = new PaymentWebBuilder(OrderDetailsActivity.this);
        paymentWebBuilder.setPaymentListener(this);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(R.string.order_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.orderId.setText(invoice_no);


        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.recycle.setLayoutManager(manager);
        binding.productList.setLayoutManager(new LinearLayoutManager(this));
        orderStatuses = new ArrayList<>();
        adapter = new OrderStatusAdapter(orderStatuses, this);
        binding.recycle.setAdapter(adapter);
        orderDetailsProducts = new ArrayList<>();
        orderDetailsProductAdapter = new OrderDetailsProductAdapter(this, orderDetailsProducts);
        binding.productList.setAdapter(orderDetailsProductAdapter);

        binding.scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == 0)
                getSupportActionBar().setElevation(0);
            else
                getSupportActionBar().setElevation(4f);
        });

        binding.shopInfo.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailsActivity.this, MainActivity.class);
            intent.putExtra("type", 3);
            intent.putExtra("shop_slug", shopSlug);
            intent.putExtra("category", "root");
            startActivity(intent);
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            invoice_no = extras.getString("orderID");
            binding.orderId.setText("#" + invoice_no);
            getOrderDetails();
//
//            if (extras.containsKey("show_cod_confirmation_dialog"))
//                showCodConfirmationDialog();
        }
        binding.balance.setText(Html.fromHtml(getString(R.string.evaly_bal) + ": <b>৳ " + Utils.formatPrice(CredentialManager.getBalance()) + "</b>"));

        getOrderHistory();

        indicator = findViewById(R.id.indicator);
        indicator.setStepCount(6);

        dialog = new ViewDialog(this);
        dialog.showDialog();

        binding.makePayment.setOnClickListener(v -> {
            if (orderDetailsModel == null || orderDetailsModel.getAllowed_payment_methods() == null) {
                ToastUtils.show("Cash on delivery only");
                return;
            }
            PaymentBottomSheet paymentBottomSheet = PaymentBottomSheet.newInstance(invoice_no,
                    total_amount,
                    paid_amount,
                    shopSlug.contains("food"),
                    orderDetailsModel.getAllowed_payment_methods(),
                    paymentMessage,
                    disabledPaymentMethods,
                    disabledPaymentMethodText,
                    nagadBadgeText,
                    nagadDescription,
                    this,
                    orderDetailsModel.isApplyDeliveryCharge(),
                    orderDetailsModel.getDeliveryCharge());
            paymentBottomSheet.show(getSupportFragmentManager(), "payment");
        });

//        binding.confirmOrder.setOnClickListener(view -> new AlertDialog.Builder(OrderDetailsActivity.this)
//                .setTitle("Do you want to confirm this order for Cash On Delivery?")
//                .setPositiveButton("Yes", (dialogInterface, i) -> makeCashOnDelivery(invoice_no)).setNegativeButton("NO", null)
//                .show());

        binding.btnToggleTimeline.setOnClickListener(v -> {
            if (adapter.isShowAll()) {
                adapter.setShowAll(false);
                binding.btnToggleTimeline.setText("Show All");
            } else {
                adapter.setShowAll(true);
                binding.btnToggleTimeline.setText("Show Less");
            }
            adapter.notifyDataSetChanged();
        });

        binding.payViaGiftCard.setOnClickListener(view -> dialogGiftCardPayment());

        getDeliveryHero();
        binding.confirmDelivery.setOnClickListener(v -> confirmDeliveryDialog());
        liveEvents();
        clickListeners();
    }

    private void clickListeners() {
        binding.updateDeliveryAddress.setOnClickListener(view -> updateDeliveryAddressDialog());
        binding.tvViewIssue.setOnClickListener(view -> viewIssues());
        binding.tvReport.setOnClickListener(view -> report());
    }

    private void liveEvents() {
        viewModel.getRefundEligibilityLiveData().observe(this, response -> {
            if (response.getSuccess())
                isRefundEligible = true;
            if (refundMenuItem == null)
                return;
            refundMenuItem.setVisible(true);
        });

        viewModel.getRefundDeleteLiveData().observe(this, stringCommonDataResponse -> {

        });

        viewModel.getRefreshPage().observe(this, aBoolean -> {
            getOrderHistory();
            getOrderDetails();
        });

        viewModel.getUpdateAddress().observe(this, response -> {
            ToastUtils.show(response.getMessage());
            getOrderDetails();
            getOrderHistory();
        });
    }

    private void showCodConfirmationDialog() {

        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_cod_confirmation);

        ImageView banner = dialog.findViewById(R.id.banner);
        ImageView close = dialog.findViewById(R.id.close);
        TextView confirm = dialog.findViewById(R.id.confirm);
        Glide.with(this)
                .load(R.drawable.ic_cod_banner)
                .into(banner);

        close.setOnClickListener(v -> {
            dialog.dismiss();
        });
        confirm.setOnClickListener(v -> {
            dialog.dismiss();
            makeCashOnDelivery(invoice_no);
        });

        dialog.show();
    }

    public void makePartialPayment(String invoice, String amount) {

        ParitalPaymentModel model = new ParitalPaymentModel();

        model.setInvoice_no(invoice);
        model.setAmount(Double.parseDouble(amount));

        OrderApiHelper.makePartialPayment(CredentialManager.getToken(), model, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                getOrderDetails();
                getOrderHistory();

                if (response != null) {
                    ToastUtils.show(response.get("message").toString());
                } else {
                    ToastUtils.show("Payment failed, try again later");
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                ToastUtils.show("Payment failed, try again later");
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    makePartialPayment(invoice, amount);
            }
        });

    }


    public void makeCashOnDelivery(String invoice) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Confirming order...");
        dialog.show();

        HashMap<String, String> data = new HashMap<>();
        data.put("invoice_no", invoice);
        OrderApiHelper.makeCashOnDelivery(CredentialManager.getToken(), data, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                dialog.dismiss();
                if (response != null) {
                    if (response.get("success").getAsBoolean()) {
                        getOrderDetails();
                        getOrderHistory();
                    }
                    ToastUtils.show(response.get("message").toString());
                } else {
                    ToastUtils.show("Order confirmation failed, try again later");
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialog.dismiss();
                ToastUtils.show("Order confirmation failed, try again later");
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    makeCashOnDelivery(invoice);
            }
        });
    }


    private void setupRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(800)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        checkRemoteConfig();
    }

    private void checkRemoteConfig() {
        mFirebaseRemoteConfig
                .fetchAndActivate()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        paymentMessage = mFirebaseRemoteConfig.getString("evaly_pay_text");
                        deliveryChargeApplicable = mFirebaseRemoteConfig.getString("delivery_charge_applicable");
                        deliveryChargeText = mFirebaseRemoteConfig.getString("delivery_charge_text");
                        disabledPaymentMethods = mFirebaseRemoteConfig.getString("disabled_payment_methods");
                        disabledPaymentMethodText = mFirebaseRemoteConfig.getString("disabled_payment_text");
                        nagadBadgeText = mFirebaseRemoteConfig.getString("nagad_badge_text");
                        nagadDescription = mFirebaseRemoteConfig.getString("nagad_description");
                        mFirebaseRemoteConfig.getString("order_details_banner_url");
                        if (!mFirebaseRemoteConfig.getString("order_details_banner_url").equals("")) {
                            binding.paymentBannerHolder.setVisibility(View.VISIBLE);
                            Glide.with(this)
                                    .load(mFirebaseRemoteConfig.getString("order_details_banner_url"))
                                    .into(binding.paymentBanner);
                        }
                    }
                });
    }

    @SuppressLint("DefaultLocale")
    private void confirmDeliveryDialog() {
        final Dialog dialog = new Dialog(this, R.style.FullWidthTransparentDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        final DialogConfirmDeliveryBinding dialogConfirmDeliveryBinding = DataBindingUtil.inflate(LayoutInflater.from(OrderDetailsActivity.this), R.layout.dialog_confirm_delivery, null, false);

        Random rand = new Random();
        String captchaCode = String.format(Locale.ENGLISH, "%04d", rand.nextInt(10000));
        dialogConfirmDeliveryBinding.captchaCode.setText(captchaCode);

        dialogConfirmDeliveryBinding.verify.setOnClickListener(v -> {
            if (dialogConfirmDeliveryBinding.code.getText().toString().trim().equals(""))
                ToastUtils.show("Please enter captcha code");
            else if (!dialogConfirmDeliveryBinding.code.getText().toString().trim().equals(captchaCode))
                ToastUtils.show("You have entered wrong captcha code");
            else
                requestConfirmDelivery(dialog);
        });

        dialog.setContentView(dialogConfirmDeliveryBinding.getRoot());
        dialog.show();
    }


    @SuppressLint("DefaultLocale")
    void updateDeliveryAddressDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(OrderDetailsActivity.this, R.style.BottomSheetDialogTheme);
        final BottomSheetUpdateOrderAddressBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottom_sheet_update_order_address, null, false);

        dialogBinding.address.setText(orderDetailsModel.getCustomerAddress());
        dialogBinding.save.setOnClickListener(v -> {
            if (dialogBinding.address.getText().toString().trim().length() < 5) {
                ToastUtils.show("Please enter valid address");
                return;
            }
            UpdateOrderAddressRequest body = new UpdateOrderAddressRequest();
            body.setAddress(dialogBinding.address.getText().toString().trim());
            body.setInvoiceNo(invoice_no);

            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage("Are you sure you want to change delivery address?")
                    .setPositiveButton(android.R.string.yes, (dialog1, which) -> {
                        viewModel.updateOrderAddress(body);
                        dialog.dismiss();
                    })
                    .setNegativeButton(android.R.string.no, (dialogInterface, i) -> {

                    })
                    .show();

        });
        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
    }

    private void requestConfirmDelivery(Dialog alertDialog) {

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Confirming delivery...");
        dialog.show();

        OrderApiHelper.confirmDelivery(CredentialManager.getToken(), invoice_no, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                dialog.dismiss();

                if (response.has("message"))
                    ToastUtils.show(response.get("message").getAsString());

                if (response.has("success") && response.get("success").getAsBoolean() && alertDialog != null && alertDialog.isShowing()) {
                    updatePage();
                    alertDialog.dismiss();
                }

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialog.dismiss();
                ToastUtils.show("Error occurred! Try again later");
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    requestConfirmDelivery(alertDialog);

            }
        });


    }

    void viewIssues() {
        startActivity(new Intent(OrderDetailsActivity.this, IssuesActivity.class).putExtra("invoice", invoice_no));
    }

    private IssueCategoryModel getCategoryModelByName(String name) {
        for (IssueCategoryModel item : categoryList) {
            if (item.getName().equals(name))
                return item;
        }
        return null;
    }

    void report() {
        IssueCreateBody model = new IssueCreateBody();
        bottomSheetDialog = new BottomSheetDialog(OrderDetailsActivity.this, R.style.BottomSheetDialogTheme);

        BottomSheetCreateIssueBinding dialogBinding = BottomSheetCreateIssueBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(dialogBinding.getRoot());

        List<String> options = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, options);
        dialogBinding.spnDelivery.setAdapter(adapter);
        dialog.showDialog();

        IssueApiHelper.getCategories(new ResponseListenerAuth<CommonDataResponse<List<IssueCategoryModel>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<IssueCategoryModel>> response, int statusCode) {
                dialog.hideDialog();
                bottomSheetDialog.show();
                categoryList = response.getData();
                for (IssueCategoryModel item : categoryList) {
                    if (orderDetailsModel.getOrderStatus().equals("pending")) {
                        if (item.getName().equals("Bank Payment") || item.getName().equals("Payment"))
                            options.add(item.getName());
                    } else
                        options.add(item.getName());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialog.hideDialog();
                ToastUtils.show(getResources().getString(R.string.something_wrong));
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

        dialogBinding.spnDelivery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                model.setCategory(getCategoryModelByName(options.get(i)).getId());
                String catName = options.get(i).toLowerCase();
                if (catName.contains("payment") || catName.contains("bank") || catName.contains("cashback") || catName.contains("return"))
                    model.setPriority("urgent");
                else
                    model.setPriority("medium");

                String paymentType = getCategoryModelByName(options.get(i)).getName();
                if (paymentType == null)
                    return;

                if (paymentType.equals("Balance Refund") || paymentType.equals("Card Refund")) {
                    dialogBinding.llBkashHolder.setVisibility(View.GONE);
                    dialogBinding.llBankInfoHolder.setVisibility(View.GONE);
                } else if (paymentType.equals("bKash Refund") || paymentType.equals("Nagad Refund")) {
                    dialogBinding.llBkashHolder.setVisibility(View.VISIBLE);
                    dialogBinding.llBankInfoHolder.setVisibility(View.GONE);
                    dialogBinding.descriptionHolder.setVisibility(View.GONE);
                } else if (paymentType.equals("Bank Refund")) {
                    dialogBinding.llBkashHolder.setVisibility(View.GONE);
                    dialogBinding.llBankInfoHolder.setVisibility(View.VISIBLE);
                    dialogBinding.descriptionHolder.setVisibility(View.GONE);
                } else {
                    dialogBinding.llBkashHolder.setVisibility(View.GONE);
                    dialogBinding.llBankInfoHolder.setVisibility(View.GONE);
                    dialogBinding.descriptionHolder.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dialogBinding.addPhoto.setOnClickListener(view -> openImageSelector());

        dialogBinding.btnSubmit.setOnClickListener(view -> {
            if (orderDetailsModel == null || orderDetailsModel.getShop() == null) {
                ToastUtils.show("Please reload the page");
                return;
            }

            model.setChannel("customer_app");
            model.setContext("order");
            model.setCustomer(CredentialManager.getUserName());
            model.setInvoiceNumber(invoice_no);

            model.setSeller(orderDetailsModel.getShop().getName());
            model.setShop(orderDetailsModel.getShop().getSlug());

            if (imageUrl != null && !imageUrl.equals("")) {
                List<String> imageUrls = new ArrayList<>();
                imageUrls.add(imageUrl);
                model.setAttachments(imageUrls);
            }

            int selectedPosition = dialogBinding.spnDelivery.getSelectedItemPosition();

            String paymentType = getCategoryModelByName(options.get(selectedPosition)).getName();

            String description = "";

            if (paymentType.equals("bKash Refund")) {
                String bkashNumber = dialogBinding.etNumber.getText().toString().trim();
                if (bkashNumber.equals("")) {
                    ToastUtils.show("Please enter your bKash account number.");
                    return;
                } else if (!Utils.isValidNumber(bkashNumber)) {
                    ToastUtils.show("Please enter valid bKash account number.");
                    return;
                }
                description = "bKash Account: " + bkashNumber;
            } else if (paymentType.equals("Nagad Refund")) {
                String number = dialogBinding.etNumber.getText().toString().trim();
                if (number.equals("")) {
                    ToastUtils.show("Please enter your Nagad account number.");
                    return;
                } else if (!Utils.isValidNumber(number)) {
                    ToastUtils.show("Please enter valid Nagad account number.");
                    return;
                }
                description = "Nagad Account: " + number;
            } else if (paymentType.equals("Bank Refund")) {
                String bankName = dialogBinding.etBankName.getText().toString().trim();
                String branchName = dialogBinding.etBranch.getText().toString().trim();
                String routingNumber = dialogBinding.etBranchRouting.getText().toString();
                String accountName = dialogBinding.etAccountName.getText().toString().trim();
                String accountNumber = dialogBinding.etAccountNumber.getText().toString().trim();

                String errorMessage = "";
                if (bankName.equals(""))
                    errorMessage = "Please enter bank name.";
                else if (branchName.equals(""))
                    errorMessage = "Please enter branch name";
                else if (routingNumber.equals(""))
                    errorMessage = "Please enter routing number.";
                else if (accountName.equals(""))
                    errorMessage = "Please enter account name.";
                else if (accountNumber.equals(""))
                    errorMessage = "Please enter account number.";

                if (!errorMessage.equals("")) {
                    ToastUtils.show(errorMessage);
                    return;
                }
                description = "Account Name: " + accountName + " \n" +
                        "Account Number: " + accountNumber + " \n" +
                        "Bank Name: " + bankName + "\n" +
                        "Branch Name: " + branchName + "\n" +
                        "Routing Number: " + routingNumber;
            } else
                description = dialogBinding.etDescription.getText().toString();

            model.setAdditionalInfo(description);
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
        dialogBinding.ivClose.setOnClickListener(view -> bottomSheetDialog.dismiss());

    }

    private void submitIssue(IssueCreateBody model, BottomSheetDialog bottomSheetDialog) {

        IssueApiHelper.createIssue(model, new ResponseListenerAuth<CommonDataResponse<IssueListModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<IssueListModel> response, int statusCode) {
                dialog.hideDialog();
                ToastUtils.show("Your issue has been submitted, you will be notified shortly");
                bottomSheetDialog.dismiss();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialog.hideDialog();
                try {
                    Log.e("json parse error", errorBody);
                    JsonObject jsonObject = JsonParser.parseString(errorBody).getAsJsonObject();
                    ToastUtils.show(jsonObject.get("message").getAsString());
                } catch (Exception e) {
                    Log.e("json parse error", e.toString());
                    ToastUtils.show(getResources().getString(R.string.something_wrong));
                }
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    submitIssue(model, bottomSheetDialog);
            }
        });
    }

    private void setPostPic() {
        if (imageUrl != null && bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
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
                ToastUtils.show("Permission denied");
            }
        }
    }

    private void getDeliveryHero() {

        OrderApiHelper.getDeliveryHero(invoice_no, new ResponseListenerAuth<DeliveryHeroResponse, String>() {
            @Override
            public void onDataFetched(DeliveryHeroResponse response, int statusCode) {
                binding.hero.setVisibility(View.VISIBLE);
                binding.heroName.setText(String.format("%s %s", response.getData().getUser().getFirstName(), response.getData().getUser().getLastName()));

                if (!isFinishing() && !isDestroyed())
                    Glide.with(OrderDetailsActivity.this)
                            .load(response.getData().getUser().getProfilePicUrl())
                            .placeholder(R.drawable.user_image)
                            .into(binding.heroPicture);

                binding.heroCall.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + response.getData().getUser().getContact()));
                    startActivity(intent);
                });
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                binding.hero.setVisibility(View.GONE);

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    getDeliveryHero();
            }
        });

    }

    public void dialogGiftCardPayment() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.WideDialog));

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_pay_with_gift_card, null);
        if (dialogView == null)
            return;
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
                ToastUtils.show("Please enter an amount.");
                return;
            } else if (code.getText().toString().equals("")) {
                ToastUtils.show("Please enter gift card coupon code.");
                return;
            }
            double partial_amount = Double.parseDouble(amount.getText().toString());

            if (partial_amount > total_amount) {
                ToastUtils.show("You have entered an amount that is larger than your due amount.");
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
                ToastUtils.show(response.get("message").getAsString());
                if (response.has("success")) {
                    if (response.get("success").getAsBoolean())
                        giftCardSuccessDialog();
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                ToastUtils.show("Payment unsuccessful!");
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

        new AlertDialog.Builder(this)
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
    }

    public void updatePage() {
        binding.scroll.postDelayed(() -> binding.scroll.fullScroll(View.FOCUS_UP), 50);
        Balance.update(this, binding.balance);
        getOrderHistory();
        getOrderDetails();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10002) {
            if (resultCode == Activity.RESULT_OK) {
                updatePage();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // some stuff that will happen if there's no result
            }
        }

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImage = data.getData();
            String imagePath = RealPathUtil.getRealPath(this, selectedImage);
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
                        ToastUtils.show(R.string.something_wrong);
                        return;
                    }

                    dialog.showDialog();

                    ImageApiHelper.uploadImage(bitmap, new ResponseListenerAuth<CommonDataResponse<ImageDataModel>, String>() {
                        @Override
                        public void onDataFetched(CommonDataResponse<ImageDataModel> response, int statusCode) {
                            dialog.hideDialog();
                            imageUrl = response.getData().getUrl();
                            setPostPic();
                        }

                        @Override
                        public void onFailed(String errorBody, int errorCode) {

                        }

                        @Override
                        public void onAuthError(boolean logout) {

                        }
                    });


                } catch (Exception e) {

                }

            } catch (Exception e) {

                Log.d("json image error", e.toString());
                ToastUtils.show("Error occurred while uploading image");
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

                orderDetailsModel = response;

                orderStatus = response.getOrderStatus().toLowerCase();
                paymentMethod = response.getPaymentMethod();
                paymentStatus = response.getPaymentStatus();

                if (paymentStatus.toLowerCase().equals("refund_requested"))
                    binding.paymentStatus.setText("Refund Requested");
                else
                    binding.paymentStatus.setText(Utils.toFirstCharUpperAll(paymentStatus));

                if (paymentStatus.toLowerCase().equals("paid")) {
                    binding.paymentStatus.setBackgroundColor(Color.parseColor("#33d274"));
                    binding.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
                } else if (paymentStatus.toLowerCase().equals("unpaid")) {
                    binding.paymentStatus.setBackgroundColor(Color.parseColor("#f0ac4e"));
                    binding.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
                } else if (paymentStatus.toLowerCase().equals("partial")) {
                    binding.paymentStatus.setBackgroundColor(Color.parseColor("#009688"));
                    binding.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
                } else if (paymentStatus.toLowerCase().equals("refunded")) {
                    binding.paymentStatus.setTextColor(Color.parseColor("#333333"));
                    binding.paymentStatus.setBackgroundColor(Color.parseColor("#eeeeee"));
                    viewModel.checkRefundEligibility(invoice_no);
                } else if (paymentStatus.toLowerCase().equals("refund_requested")) {
                    binding.paymentStatus.setBackgroundColor(Color.parseColor("#c45da8"));
                    binding.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
                }

                binding.deliveryHeroStatus.setText("Assigned for delivery");

                if (orderStatus.equals("pending")) {
                    binding.updateDeliveryAddress.setVisibility(View.VISIBLE);
                    indicator.setCurrentStep(1);
                } else if (orderStatus.equals("confirmed")) {
                    binding.updateDeliveryAddress.setVisibility(View.VISIBLE);
                    indicator.setCurrentStep(2);
                } else if (orderStatus.equals("processing")) {
                    binding.updateDeliveryAddress.setVisibility(View.VISIBLE);
                    indicator.setCurrentStep(3);
                } else if (orderStatus.equals("picked")) {
                    binding.updateDeliveryAddress.setVisibility(View.GONE);
                    indicator.setCurrentStep(4);
                    binding.deliveryHeroStatus.setText("Picked the order for delivery");
                } else if (orderStatus.equals("shipped")) {
                    binding.updateDeliveryAddress.setVisibility(View.GONE);
                    indicator.setCurrentStep(5);
                    binding.deliveryHeroStatus.setText("Picked the order for delivery");
                    binding.updateDeliveryAddress.setVisibility(View.GONE);
                } else if (orderStatus.equals("delivered")) {
                    indicator.setCurrentStep(6);
                    binding.deliveryHeroStatus.setText("Delivered the products");
                }

                if (orderDetailsModel.isApplyDeliveryCharge() && !orderDetailsModel.getOrderStatus().equalsIgnoreCase("delivered")) {
                    binding.llCashCollect.setVisibility(View.VISIBLE);
                    binding.tvDeliveryFee.setText(Html.fromHtml("Please Pay Delivery Fee <b>৳" + orderDetailsModel.getDeliveryCharge() + "</b> Cash to Delivery Hero."));
                } else {
                    binding.llCashCollect.setVisibility(View.GONE);
                }

                if (orderStatus.equals("shipped") && paymentStatus.equals("paid"))
                    binding.confirmDelivery.setVisibility(View.VISIBLE);
                else
                    binding.confirmDelivery.setVisibility(View.GONE);

                if (!response.isDeliveryConfirmed() && response.isDeliveryConfirmationRequired())
                    deliveryConfirmationDialog();

                binding.orderDate.setText(Utils.formattedDateFromString("", "yyyy-MM-d", response.getDate()));


                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
                    Date firstDate = sdf.parse("09/09/2020");
                    Date secondDate = sdf.parse(Utils.formattedDateFromString("", "dd/MM/yyy", response.getDate()));

                    boolean check = false;
                    String shopTitle = response.getShop().getName();
                    if (deliveryChargeApplicable != null) {
                        String[] array = deliveryChargeApplicable.split(",");
                        for (String s : array) {
                            if (shopTitle.toLowerCase().contains(s.toLowerCase())) {
                                check = true;
                                break;
                            }
                        }
                    }

                    if ((secondDate != null && secondDate.after(firstDate)) && check) {
                        binding.vatHolder.setVisibility(View.VISIBLE);
                        binding.deliveryChargeHolder.setVisibility(View.VISIBLE);
                        if (deliveryChargeText != null)
                            binding.deliveryChargeText.setText(deliveryChargeText.replaceAll(" will be", ""));
                    } else {
                        binding.vatHolder.setVisibility(View.GONE);
                        binding.deliveryChargeHolder.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    binding.vatHolder.setVisibility(View.GONE);
                    binding.deliveryChargeHolder.setVisibility(View.GONE);
                }

                if (response.getOrderStatus().toLowerCase().equals("cancel")) {
                    StepperIndicator indicatorCancelled = findViewById(R.id.indicatorCancelled);
                    indicatorCancelled.setVisibility(View.VISIBLE);
                    indicatorCancelled.setCurrentStep(6);
                    indicator.setDoneIcon(getDrawable(R.drawable.ic_close_smallest));
                    indicator.setVisibility(View.GONE);
                    binding.makePayment.setVisibility(View.GONE);
                    binding.confirmOrder.setVisibility(View.GONE);
                    binding.payPartially.setVisibility(View.GONE);
                    binding.payViaGiftCard.setVisibility(View.GONE);
                } else if (response.getOrderStatus().toLowerCase().equals("delivered") || response.getPaymentStatus().toLowerCase().equals("refund_requested") || response.getOrderStatus().toLowerCase().equals("processing") || response.getOrderStatus().toLowerCase().equals("picked") || response.getOrderStatus().toLowerCase().equals("shipped")) {
                    binding.makePayment.setVisibility(View.GONE);
                    binding.payPartially.setVisibility(View.GONE);
                    binding.confirmOrder.setVisibility(View.GONE);
                    binding.payViaGiftCard.setVisibility(View.GONE);
                } else {
                    binding.confirmOrder.setVisibility(View.GONE);
                    if (response.getAllowed_payment_methods() != null && response.getAllowed_payment_methods().length > 0) {
                        binding.makePayment.setVisibility(View.VISIBLE);
                        binding.payPartially.setVisibility(View.GONE);
                        binding.payViaGiftCard.setVisibility(View.GONE);
                        for (int i = 0; i < response.getAllowed_payment_methods().length; i++) {
                            if (response.getAllowed_payment_methods()[i].equalsIgnoreCase("gift_code")) {
                                binding.payViaGiftCard.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        binding.makePayment.setVisibility(View.GONE);
                        binding.payPartially.setVisibility(View.GONE);
                        binding.payViaGiftCard.setVisibility(View.GONE);
                    }

                }

                binding.billtoName1.setText(String.format("%s %s", response.getCustomer().getFirstName(), response.getCustomer().getLastName()));
                binding.billtoAddress1.setText(response.getCustomerAddress());
                binding.billtoPhone.setText(response.getContactNumber());
                binding.totalPrice.setText(String.format("৳ %s", Utils.formatPrice(response.getTotal())));
                binding.paidAmount.setText(String.format("৳ %s", Utils.formatPrice(response.getPaidAmount())));
                binding.duePrice.setText(String.format(Locale.ENGLISH, "৳ %s", Utils.formatPrice((Double.parseDouble(response.getTotal())) - Math.round(Double.parseDouble(response.getPaidAmount())))));

                if (response.getCustomerNote() != null && !response.getCustomerNote().equals("")) {
                    binding.tvCampaignRule.setText(response.getCustomerNote());
                    binding.campaignRuleHolder.setVisibility(View.VISIBLE);
                } else
                    binding.campaignRuleHolder.setVisibility(View.GONE);

                String payMethod = response.getPaymentMethod();

                if (payMethod.equals("cod"))
                    binding.paymentMethod.setText("Cash on Delivery");
                else if (payMethod.equals(""))
                    binding.paymentMethod.setVisibility(View.GONE);
                else {

                    payMethod = payMethod.replaceAll("card", "Card");
                    payMethod = payMethod.replaceAll("binding.balance", "Evaly Account");
                    payMethod = payMethod.replaceAll("bank", "Bank Account");
                    payMethod = payMethod.replaceAll("gift_code", "Gift Code");
                    payMethod = payMethod.replaceAll(",", ", ");
                    payMethod = payMethod.replaceAll("  ", " ");

                    binding.paymentMethod.setText(Utils.capitalize(payMethod));
                }

                binding.billfromName.setText(response.getShop().getName());
                shopSlug = response.getShop().getSlug();
                binding.billfromAddress.setText(response.getShop().getAddress());
                binding.billfromPhone.setText(response.getShop().getContactNumber());

                total_amount = Math.round(Double.parseDouble(response.getTotal()));
                paid_amount = Math.round(Double.parseDouble(response.getPaidAmount()));
                due_amount = total_amount - paid_amount;

                if (due_amount < 1) {
                    binding.makePayment.setVisibility(View.GONE);
                    binding.payPartially.setVisibility(View.GONE);
                    binding.payViaGiftCard.setVisibility(View.GONE);
                }

                if (response.getCampaignRules().size() > 0) {
                    try {
                        if (response.getCampaignRules().get(0).getAsJsonObject().get("category").getAsJsonObject().get("slug").getAsString().equals("pod-1ce6180b") && orderStatus.equals("pending"))
                            binding.makePayment.setVisibility(View.GONE);
                    } catch (Exception ignore) {

                    }
                }

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
                dialog.hideDialog();
                ToastUtils.show("Error occurred!");
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }


    public void cancelOrder() {

        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_cancel_order);
        dialog.setTitle("Select Cancellation Reason");

        TextView button = dialog.findViewById(R.id.btn);
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);

        assert button != null;
        button.setOnClickListener(view -> {

            if ((radioGroup != null ? radioGroup.getCheckedRadioButtonId() : 0) == -1) {
                ToastUtils.show("Select a cancellation reason");
                return;
            }

            assert radioGroup != null;
            int index = radioGroup.indexOfChild(dialog.findViewById(radioGroup.getCheckedRadioButtonId()));

            String reason = getResources().getStringArray(R.array.cancelReasons)[index];

            OrderApiHelper.cancelOrder(CredentialManager.getToken(), invoice_no, reason, new ResponseListenerAuth<JsonObject, String>() {
                @Override
                public void onDataFetched(JsonObject response, int statusCode) {
                    updatePage();
                    binding.cancelBtn.setVisibility(View.GONE);
                    if (dialog.isShowing())
                        dialog.dismiss();
                }

                @Override
                public void onFailed(String errorBody, int errorCode) {
                    ToastUtils.show("Can't cancel this order!");
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

                if (list.size() > 4)
                    binding.btnToggleTimelineHolder.setVisibility(View.VISIBLE);
                else
                    binding.btnToggleTimelineHolder.setVisibility(View.GONE);
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
                    if (response1 == null) {
                        ToastUtils.show(getResources().getString(R.string.something_wrong));
                        return;
                    }
                    if (response1.code() == 200 || response1.code() == 201) {
                        dialog.hideDialog();
                        ToastUtils.show("Order Updated");
                    } else {
                        dialog.hideDialog();
                        ToastUtils.show(getResources().getString(R.string.something_wrong));
                    }
                }

                @Override
                public void onFailed(int status) {
                    dialog.hideDialog();
                    ToastUtils.show(getResources().getString(R.string.something_wrong));
                }
            });
        }).setNegativeButton("No", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onPaymentRedirect(String url, String amount, String invoice_no) {
        String successURL;
        PurchaseRequestInfo purchaseRequestInfo = null;

        if (url.equals(BuildConfig.BKASH_URL)) {
            successURL = Constants.BKASH_SUCCESS_URL;
            paymentWebBuilder.setToolbarTitle(getResources().getString(R.string.bkash_payment));
            purchaseRequestInfo = new PurchaseRequestInfo(CredentialManager.getTokenNoBearer(), amount, invoice_no);
        } else {
            successURL = Constants.SSL_SUCCESS_URL;
            if (url.contains("nagad"))
                paymentWebBuilder.setToolbarTitle("Pay via Nagad");
            else
                paymentWebBuilder.setToolbarTitle(getResources().getString(R.string.pay_via_card));
        }

        paymentWebBuilder.loadPaymentURL(url, successURL, purchaseRequestInfo);
    }

    @Override
    public void onPaymentSuccess(HashMap<String, String> values) {

    }

    @Override
    public void onPaymentFailure(HashMap<String, String> values) {
        ToastUtils.show(R.string.payment_error_message);
    }

    @Override
    public void onPaymentSuccess(String message) {
        updatePage();
        ToastUtils.show(R.string.payment_success_message);
    }

}
