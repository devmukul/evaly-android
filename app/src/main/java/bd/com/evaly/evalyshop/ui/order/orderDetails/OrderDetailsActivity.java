package bd.com.evaly.evalyshop.ui.order.orderDetails;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.badoualy.stepperindicator.StepperIndicator;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ActivityOrderDetailsBinding;
import bd.com.evaly.evalyshop.databinding.BottomSheetUpdateOrderAddressBinding;
import bd.com.evaly.evalyshop.databinding.DialogConfirmDeliveryBinding;
import bd.com.evaly.evalyshop.di.observers.SharedObservers;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.hero.DeliveryHeroResponse;
import bd.com.evaly.evalyshop.models.order.OrderDetailsProducts;
import bd.com.evaly.evalyshop.models.order.OrderStatus;
import bd.com.evaly.evalyshop.models.order.orderDetails.OrderDetailsModel;
import bd.com.evaly.evalyshop.models.order.orderDetails.OrderItemsItem;
import bd.com.evaly.evalyshop.models.order.updateAddress.UpdateOrderAddressRequest;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.issue.IssuesActivity;
import bd.com.evaly.evalyshop.ui.issue.create.CreateIssueBottomSheet;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.order.orderDetails.adapter.OrderDetailsProductAdapter;
import bd.com.evaly.evalyshop.ui.order.orderDetails.adapter.OrderStatusAdapter;
import bd.com.evaly.evalyshop.ui.order.orderDetails.refund.RefundBottomSheet;
import bd.com.evaly.evalyshop.ui.payment.bottomsheet.PaymentBottomSheet;
import bd.com.evaly.evalyshop.ui.payment.giftcard.GiftCardPaymentBottomSheet;
import bd.com.evaly.evalyshop.util.Balance;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.evaly.evalypaymentlibrary.builder.PaymentWebBuilder;
import bd.evaly.evalypaymentlibrary.listener.PaymentListener;
import bd.evaly.evalypaymentlibrary.model.PurchaseRequestInfo;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderDetailsActivity extends BaseActivity implements PaymentBottomSheet.PaymentOptionListener, PaymentListener {

    @Inject
    SharedObservers sharedObservers;
    @Inject
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    private ActivityOrderDetailsBinding binding;
    private double totalAmount = 0.0, paidAmount = 0.0, dueAmount = 0.0;
    private String invoiceNo = "", shopSlug = "", orderStatus = "pending", paymentStatus = "unpaid", paymentMethod = "";
    private String deliveryChargeText = null, deliveryChargeApplicable = null;
    private StepperIndicator indicator;
    private List<OrderStatus> orderStatuses;
    private OrderStatusAdapter orderStatusAdapter;
    private ArrayList<OrderDetailsProducts> orderDetailsProducts;
    private OrderDetailsProductAdapter orderDetailsProductAdapter;
    private ViewDialog dialog;
    private MenuItem cancelMenuItem, refundMenuItem;
    private OrderDetailsModel orderDetailsModel;
    private PaymentWebBuilder paymentWebBuilder;
    private OrderDetailsViewModel viewModel;
    private boolean isRefundEligible = false;
    private ProgressDialog progressDialog;
    private Dialog confirmDeliveryDialog;
    private BottomSheetDialog cancelOrderDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        viewModel = new ViewModelProvider(this).get(OrderDetailsViewModel.class);
        checkRemoteConfig();
        paymentWebBuilder = new PaymentWebBuilder(OrderDetailsActivity.this);
        paymentWebBuilder.setPaymentListener(this);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(R.string.order_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dialog = new ViewDialog(this);
        dialog.showDialog();
        binding.orderId.setText(invoiceNo);

        binding.scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == 0)
                getSupportActionBar().setElevation(0);
            else
                getSupportActionBar().setElevation(4f);
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            invoiceNo = extras.getString("orderID");
            binding.orderId.setText("#" + invoiceNo);
        }

        binding.balance.setText(Html.fromHtml(getString(R.string.evaly_bal) + ": <b>৳ " + Utils.formatPrice(CredentialManager.getBalance()) + "</b>"));
        indicator = findViewById(R.id.indicator);
        indicator.setStepCount(6);

        setupOrderHistoryRecycler();
        setupProductListRecycler();
        liveEvents();
        clickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupProductListRecycler() {
        binding.productList.setLayoutManager(new LinearLayoutManager(this));
        orderDetailsProducts = new ArrayList<>();
        orderDetailsProductAdapter = new OrderDetailsProductAdapter(this, orderDetailsProducts);
        binding.productList.setAdapter(orderDetailsProductAdapter);
    }

    private void setupOrderHistoryRecycler() {
        orderStatuses = new ArrayList<>();
        binding.recycle.setLayoutManager(new LinearLayoutManager(this));
        orderStatusAdapter = new OrderStatusAdapter(orderStatuses, this);
        binding.recycle.setAdapter(orderStatusAdapter);
    }

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
        if (invoiceNo.equals("") || orderStatus.equals("") || paymentMethod.equals("") || paymentStatus.equals("")) {
            ToastUtils.show("Can't request refund, reload the page");
        } else {
            RefundBottomSheet refundBottomSheet = RefundBottomSheet.newInstance(invoiceNo, orderStatus, paymentMethod, paymentStatus, isRefundEligible, orderDetailsModel.getDate());
            refundBottomSheet.show(getSupportFragmentManager(), "Refund");
        }
    }

    private void clickListeners() {
        binding.shopInfo.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailsActivity.this, MainActivity.class);
            intent.putExtra("type", 3);
            intent.putExtra("shop_slug", shopSlug);
            intent.putExtra("category", "root");
            startActivity(intent);
        });

        binding.makePayment.setOnClickListener(v -> {
            if (orderDetailsModel == null || orderDetailsModel.getAllowed_payment_methods() == null) {
                ToastUtils.show("Cash on delivery only");
                return;
            }
            PaymentBottomSheet paymentBottomSheet = PaymentBottomSheet.newInstance(invoiceNo,
                    totalAmount,
                    paidAmount,
                    orderDetailsModel.getAllowed_payment_methods(),
                    orderDetailsModel.isApplyDeliveryCharge(),
                    orderDetailsModel.getDeliveryCharge(), this);
            paymentBottomSheet.show(getSupportFragmentManager(), "payment");
        });

        binding.btnToggleTimeline.setOnClickListener(v -> {
            if (orderStatusAdapter.isShowAll()) {
                orderStatusAdapter.setShowAll(false);
                binding.btnToggleTimeline.setText("Show All");
            } else {
                orderStatusAdapter.setShowAll(true);
                binding.btnToggleTimeline.setText("Show Less");
            }
            orderStatusAdapter.notifyDataSetChanged();
        });

        binding.payViaGiftCard.setOnClickListener(view -> dialogGiftCardPayment());
        binding.withdrawRefund.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage("Are you sure you want to withdraw refund request?")
                    .setPositiveButton(android.R.string.yes, (dialog1, which) -> {
                        viewModel.withdrawRefundRequest(invoiceNo);
                    })
                    .setNegativeButton(android.R.string.no, (dialogInterface, i) -> {

                    })
                    .show();
        });
        binding.confirmDelivery.setOnClickListener(v -> confirmDeliveryDialog());

        binding.updateDeliveryAddress.setOnClickListener(view -> updateDeliveryAddressDialog());
        binding.tvViewIssue.setOnClickListener(view -> viewIssues());
        binding.tvReport.setOnClickListener(v -> {
            CreateIssueBottomSheet bottomSheet = CreateIssueBottomSheet.newInstance(invoiceNo,
                    orderDetailsModel.getOrderStatus(),
                    orderDetailsModel.getShop().getName(),
                    orderDetailsModel.getShop().getSlug());

            bottomSheet.show(getSupportFragmentManager(), "Create issue");
        });
    }

    private void liveEvents() {

        viewModel.balanceLiveData.observe(this, response -> {
            CredentialManager.setBalance(response.getBalance());
            binding.balance.setText(Html.fromHtml(getString(R.string.evaly_bal) + ": <b>৳ " + Utils.formatPrice(response.getBalance()) + "</b>"));
        });

        viewModel.deliveryStatusUpdateLiveData.observe(this, s -> {
            ToastUtils.show(s);
            dialog.hideDialog();
        });

        sharedObservers.giftCardPaymentSuccess.observe(this, aVoid -> updatePage());

        viewModel.orderDetailsLiveData.observe(this, orderDetailsModel -> loadOrderDetails(orderDetailsModel));

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
            viewModel.getOrderDetails();
            viewModel.getOrderHistory();
        });

        viewModel.getUpdateAddress().observe(this, response -> {
            ToastUtils.show(response.getMessage());
            viewModel.getOrderDetails();
            viewModel.getOrderHistory();
        });

        viewModel.deliveryHeroLiveData.observe(this, deliveryHeroResponse -> {
            loadDeliveryHeroInfo(deliveryHeroResponse);
        });

        viewModel.confirmDeliveryLiveData.observe(this, commonDataResponse -> {
            progressDialog.dismiss();
            if (commonDataResponse == null) {
                ToastUtils.show("Couldn't confirm order delivery");
                return;
            }
            ToastUtils.show(commonDataResponse.getMessage());
            updatePage();
        });

        viewModel.confirmDeliveryLiveData.observe(this, response -> {
            dismissProgressBar();
            if (response != null) {
                ToastUtils.show(response.getMessage());
                if (response.getSuccess() && confirmDeliveryDialog.isShowing()) {
                    confirmDeliveryDialog.dismiss();
                }
            }
        });

        viewModel.orderStatusListLiveData.observe(this, list -> {
            orderStatuses.clear();
            orderStatuses.addAll(list);
            orderStatusAdapter.notifyDataSetChanged();
            if (list.size() > 4)
                binding.btnToggleTimelineHolder.setVisibility(View.VISIBLE);
            else
                binding.btnToggleTimelineHolder.setVisibility(View.GONE);
        });

        viewModel.cancelOrderLiveData.observe(this, response -> {
            if (response.getSuccess()) {
                if (cancelOrderDialog != null && cancelOrderDialog.isShowing())
                    cancelOrderDialog.dismiss();
            }
        });
    }

    private void loadOrderDetails(OrderDetailsModel response) {

        dialog.hideDialog();
        orderDetailsModel = response;

        orderStatus = response.getOrderStatus().toLowerCase();
        paymentMethod = response.getPaymentMethod();
        paymentStatus = response.getPaymentStatus().toLowerCase();

        if (paymentStatus.toLowerCase().equals("refund_requested")) {
            binding.paymentStatus.setText("Refund Requested");
            binding.withdrawRefund.setVisibility(View.VISIBLE);
        } else {
            binding.paymentStatus.setText(Utils.toFirstCharUpperAll(paymentStatus));
            binding.withdrawRefund.setVisibility(View.GONE);
        }

        if (paymentStatus.equals("paid")) {
            binding.paymentStatus.setBackgroundColor(Color.parseColor("#33d274"));
            binding.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
        } else if (paymentStatus.equals("unpaid")) {
            binding.paymentStatus.setBackgroundColor(Color.parseColor("#f0ac4e"));
            binding.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
        } else if (paymentStatus.equals("partial")) {
            binding.paymentStatus.setBackgroundColor(Color.parseColor("#009688"));
            binding.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
        } else if (paymentStatus.equals("refunded")) {
            binding.paymentStatus.setTextColor(Color.parseColor("#333333"));
            binding.paymentStatus.setBackgroundColor(Color.parseColor("#eeeeee"));
            viewModel.checkRefundEligibility(invoiceNo);
        } else if (paymentStatus.equals("refund_requested")) {
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

        if (orderDetailsModel.isApplyDeliveryCharge() &&
                !orderStatus.equalsIgnoreCase("delivered")) {
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

        if (orderStatus.equals("cancel")) {
            StepperIndicator indicatorCancelled = findViewById(R.id.indicatorCancelled);
            indicatorCancelled.setVisibility(View.VISIBLE);
            indicatorCancelled.setCurrentStep(6);
            indicator.setDoneIcon(getDrawable(R.drawable.ic_close_smallest));
            indicator.setVisibility(View.GONE);
            binding.confirmOrder.setVisibility(View.GONE);
            hidePaymentButtons();
        } else if (orderStatus.equals("delivered") ||
                response.getPaymentStatus().toLowerCase().equals("refund_requested") ||
                orderStatus.equals("processing") ||
                orderStatus.equals("picked") ||
                orderStatus.equals("shipped")) {
            binding.confirmOrder.setVisibility(View.GONE);
            hidePaymentButtons();
        } else {
            binding.confirmOrder.setVisibility(View.GONE);
            if (response.getAllowed_payment_methods() != null && response.getAllowed_payment_methods().size() > 0) {
                showPaymentButtons();
                binding.payViaGiftCard.setVisibility(View.GONE);
                for (int i = 0; i < response.getAllowed_payment_methods().size(); i++) {
                    if (response.getAllowed_payment_methods().get(i).equalsIgnoreCase("gift_code")) {
                        binding.payViaGiftCard.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            } else
                hidePaymentButtons();

        }

        binding.billtoName1.setText(String.format("%s %s", response.getCustomer().getFirstName(), response.getCustomer().getLastName()));
        binding.billtoAddress1.setText(response.getCustomerAddress());
        binding.billtoPhone.setText(response.getContactNumber());
        binding.totalPrice.setText(Utils.formatPriceSymbol(response.getTotal()));
        binding.paidAmount.setText(Utils.formatPriceSymbol(response.getPaidAmount()));
        binding.duePrice.setText(Utils.formatPriceSymbol((Double.parseDouble(response.getTotal())) - Math.round(Double.parseDouble(response.getPaidAmount()))));

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
            payMethod = payMethod.replaceAll("_", " ");
            payMethod = payMethod.replaceAll(",", ", ");
            payMethod = payMethod.replaceAll("  ", " ");
            binding.paymentMethod.setText(Utils.capitalize(payMethod));
        }

        binding.billfromName.setText(response.getShop().getName());
        shopSlug = response.getShop().getSlug();
        binding.billfromAddress.setText(response.getShop().getAddress());
        binding.billfromPhone.setText(response.getShop().getContactNumber());

        totalAmount = Math.round(Double.parseDouble(response.getTotal()));
        paidAmount = Math.round(Double.parseDouble(response.getPaidAmount()));
        dueAmount = totalAmount - paidAmount;

        if (dueAmount < 1)
            hidePaymentButtons();

        if (response.getCampaignRules().size() > 0) {
            try {
                if (response.getCampaignRules().get(0)
                        .getAsJsonObject().get("category")
                        .getAsJsonObject().get("slug")
                        .getAsString().equals("pod-1ce6180b") && orderStatus.equals("pending")) {
                    binding.makePayment.setVisibility(View.GONE);
                }
            } catch (Exception ignore) {
            }
        }

        inflateMenu();
        updateProducts();
    }

    private void updateProducts() {
        orderDetailsProducts.clear();
        orderDetailsProductAdapter.notifyDataSetChanged();

        List<OrderItemsItem> orderItemList = orderDetailsModel.getOrderItems();

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

    private void hidePaymentButtons() {
        binding.makePayment.setVisibility(View.GONE);
        binding.payViaGiftCard.setVisibility(View.GONE);
        binding.stickyButtons.setVisibility(View.GONE);
        binding.container.setPadding(0, 0, 0, 0);
    }

    private void showPaymentButtons() {
        binding.makePayment.setVisibility(View.VISIBLE);
        binding.payViaGiftCard.setVisibility(View.VISIBLE);
        binding.stickyButtons.setVisibility(View.VISIBLE);
        binding.container.setPadding(0, 0, 0, Utils.convertDpToPixel(70));
    }

    private void dismissProgressBar() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void loadDeliveryHeroInfo(DeliveryHeroResponse response) {
        if (response == null) {
            binding.hero.setVisibility(View.GONE);
            return;
        } else
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

    private void checkRemoteConfig() {
        deliveryChargeApplicable = mFirebaseRemoteConfig.getString("delivery_charge_applicable");
        deliveryChargeText = mFirebaseRemoteConfig.getString("delivery_charge_text");
    }

    @SuppressLint("DefaultLocale")
    private void confirmDeliveryDialog() {
        confirmDeliveryDialog = new Dialog(this, R.style.FullWidthTransparentDialog);
        confirmDeliveryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmDeliveryDialog.setCancelable(true);
        final DialogConfirmDeliveryBinding dialogConfirmDeliveryBinding = DataBindingUtil.inflate(
                LayoutInflater.from(OrderDetailsActivity.this), R.layout.dialog_confirm_delivery, null, false);

        Random rand = new Random();
        String captchaCode = String.format(Locale.ENGLISH, "%04d", rand.nextInt(10000));
        dialogConfirmDeliveryBinding.captchaCode.setText(captchaCode);

        dialogConfirmDeliveryBinding.verify.setOnClickListener(v -> {
            if (dialogConfirmDeliveryBinding.code.getText().toString().trim().equals(""))
                ToastUtils.show("Please enter captcha code");
            else if (!dialogConfirmDeliveryBinding.code.getText().toString().trim().equals(captchaCode))
                ToastUtils.show("You have entered wrong captcha code");
            else
                requestConfirmDelivery();
        });

        confirmDeliveryDialog.setContentView(dialogConfirmDeliveryBinding.getRoot());
        confirmDeliveryDialog.show();
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
            body.setInvoiceNo(invoiceNo);

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

    private void requestConfirmDelivery() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Confirming delivery...");
        progressDialog.show();
        viewModel.confirmDelivery();
    }

    void viewIssues() {
        startActivity(new Intent(OrderDetailsActivity.this, IssuesActivity.class).putExtra("invoice", invoiceNo));
    }

    public void dialogGiftCardPayment() {
        GiftCardPaymentBottomSheet giftCardPaymentBottomSheet = GiftCardPaymentBottomSheet.newInstance(invoiceNo, dueAmount);
        giftCardPaymentBottomSheet.show(getSupportFragmentManager(), "Gift card payment");
    }

    public void updatePage() {
        binding.scroll.postDelayed(() -> binding.scroll.fullScroll(View.FOCUS_UP), 50);
        Balance.update(this, binding.balance);
        viewModel.getOrderHistory();
        viewModel.getOrderDetails();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10002) {
            if (resultCode == Activity.RESULT_OK) {
                updatePage();
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

        if (orderStatus.equals("pending") && paidAmount < 1)
            cancelMenuItem.setVisible(true);
        else
            cancelMenuItem.setVisible(false);
    }

    public void cancelOrder() {
        cancelOrderDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        cancelOrderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cancelOrderDialog.setContentView(R.layout.bottom_sheet_cancel_order);
        cancelOrderDialog.setTitle("Select Cancellation Reason");

        TextView button = cancelOrderDialog.findViewById(R.id.btn);
        RadioGroup radioGroup = cancelOrderDialog.findViewById(R.id.radioGroup);

        assert button != null;
        button.setOnClickListener(view -> {
            if ((radioGroup != null ? radioGroup.getCheckedRadioButtonId() : 0) == -1) {
                ToastUtils.show("Select a cancellation reason");
                return;
            }
            assert radioGroup != null;
            int index = radioGroup.indexOfChild(cancelOrderDialog.findViewById(radioGroup.getCheckedRadioButtonId()));
            String reason = getResources().getStringArray(R.array.cancelReasons)[index];
            viewModel.cancelOrder(reason);
        });
        cancelOrderDialog.show();
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
            data.put("invoice_no", invoiceNo);
            viewModel.updateProductDeliveryStatus(data);
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
            purchaseRequestInfo = new PurchaseRequestInfo(CredentialManager.getTokenNoBearer(), amount, invoice_no, "bKash");
        } else {
            successURL = Constants.SSL_SUCCESS_URL;
            if (url.contains("nagad"))
                paymentWebBuilder.setToolbarTitle("Pay via Nagad");
            else if (url.contains("sebl")) {
                successURL = Constants.SEBL_SUCCESS_URL;
                purchaseRequestInfo = new PurchaseRequestInfo(CredentialManager.getTokenNoBearer(), amount, invoice_no, "sebl");
                paymentWebBuilder.setToolbarTitle(getResources().getString(R.string.visa_master_card));
            } else
                paymentWebBuilder.setToolbarTitle(getResources().getString(R.string.pay_via_card));
        }
        Logger.e(url);
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
