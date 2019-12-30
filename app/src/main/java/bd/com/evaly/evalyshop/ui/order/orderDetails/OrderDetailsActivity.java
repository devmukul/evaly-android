package bd.com.evaly.evalyshop.ui.order.orderDetails;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
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
import java.util.concurrent.TimeUnit;

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
import bd.com.evaly.evalyshop.models.order.payment.ParitalPaymentModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.GiftCardApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.chat.viewmodel.ImageUploadView;
import bd.com.evaly.evalyshop.ui.issue.IssuesActivity;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.order.PayViaBkashActivity;
import bd.com.evaly.evalyshop.ui.order.PayViaCard;
import bd.com.evaly.evalyshop.ui.order.orderDetails.adapter.OrderDetailsProductAdapter;
import bd.com.evaly.evalyshop.ui.order.orderDetails.adapter.OrderStatusAdapter;
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

    private static final int SCROLL_DIRECTION_UP = -1;
    private NestedScrollView scrollView;
    String shopSlug="";

    String invoice_no="";
    double total_amount = 0.0, paid_amount = 0.0, due_amount = 0.0;
    UserDetails userDetails;
    StepperIndicator indicator;
    TextView shopName,shopAddress,shopnumber,username,userAddress,userNumber,totalPriceTextView,paidAmountTextView,duePriceTextView, tvCampaignRule;
    TextView orderNumber,orderDate,paymentMethods,balance;
    RecyclerView recyclerView,orderList;
    ArrayList<OrderStatus> orderStatuses;
    OrderStatusAdapter adapter;
    ArrayList<OrderDetailsProducts> orderDetailsProducts;
    OrderDetailsProductAdapter orderDetailsProductAdapter;
    ViewDialog dialog;
    TextView makePayment, payParially, full_or_partial;
    RelativeLayout shopInfo;
    TextView payViaGiftCard;
    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet, campaignRuleHolder;
    View mViewBg;
    TextView amountToPayView, evalyPayText;
    ImageView bkash,cards,evalyPay;
    BottomSheetDialog bottomSheetDialog;


    Context context;
    RequestQueue queue;

    String shopGroup = "";
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        queue= Volley.newRequestQueue(context);


        campaignRuleHolder = findViewById(R.id.campaignRuleHolder);
        tvCampaignRule = findViewById(R.id.tvCampaignRule);
        full_or_partial = findViewById(R.id.full_or_partial);
        scrollView = findViewById(R.id.scroll);
        shopName=findViewById(R.id.billfromName);
        shopAddress=findViewById(R.id.billfromAddress);
        shopnumber=findViewById(R.id.billfromPhone);
        username=findViewById(R.id.billtoName);
        userAddress=findViewById(R.id.billtoAddress);
        userNumber=findViewById(R.id.billtoPhone);
        totalPriceTextView=findViewById(R.id.total_price);
        paidAmountTextView=findViewById(R.id.paid_amount);
        duePriceTextView=findViewById(R.id.due_price);
        orderNumber=findViewById(R.id.order_id);
        orderNumber.setText(invoice_no);
        orderDate=findViewById(R.id.order_date);
        paymentMethods=findViewById(R.id.payment_method);
        recyclerView=findViewById(R.id.recycle);
        balance=findViewById(R.id.balance);
        orderList=findViewById(R.id.product_list);
        shopInfo=findViewById(R.id.shop_info);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        orderList.setLayoutManager(new LinearLayoutManager(this));
        userDetails=new UserDetails(this);
        orderStatuses=new ArrayList<>();
        adapter=new OrderStatusAdapter(orderStatuses,this);
        recyclerView.setAdapter(adapter);
        orderDetailsProducts=new ArrayList<>();
        orderDetailsProductAdapter=new OrderDetailsProductAdapter(this,orderDetailsProducts);
        orderList.setAdapter(orderDetailsProductAdapter);

        mViewBg = findViewById(R.id.bg);

        // bottom sheet
        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        amountToPayView = findViewById(R.id.amountPay);
        bkash = findViewById(R.id.bkash);
        cards = findViewById(R.id.card);
        evalyPay = findViewById(R.id.evaly_pay);
        evalyPayText = findViewById(R.id.evalyPayText);

        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        mViewBg.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        mViewBg.setVisibility(View.VISIBLE);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        mViewBg.setVisibility(View.GONE);
                        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        mViewBg.setOnClickListener(v -> {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            mViewBg.setVisibility(View.GONE);

        });


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

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            invoice_no=extras.getString("orderID");
            Log.d("order_id",invoice_no);

            orderNumber.setText("#"+invoice_no);

            getOrderDetails();
        }

        balance.setText(Html.fromHtml("Balance: <b>৳ "+userDetails.getBalance() + "</b>"));

        getOrderHistory();

        indicator = findViewById(R.id.indicator);
        indicator.setStepCount(6);

        dialog = new ViewDialog(this);

        dialog.showDialog();
        makePayment = findViewById(R.id.makePayment);

        payParially = findViewById(R.id.payPartially);

        makePayment.setOnClickListener(v -> {
            double amountToPay = total_amount - paid_amount;
            amountToPayView.setText((int)amountToPay + "");
            full_or_partial.setVisibility(View.GONE);
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });


        payParially.setOnClickListener(v -> addPartialPayDialog());

        payViaGiftCard = findViewById(R.id.payViaGiftCard);
        payViaGiftCard.setOnClickListener(v -> dialogGiftCardPayment());

        evalyPay.setOnClickListener(v -> {
            double amountToPay = total_amount - paid_amount;

            if (amountToPayView.getText().toString().trim().equals("")){
                Toast.makeText(context, "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            } else if (Double.parseDouble(amountToPayView.getText().toString()) > amountToPay) {
                Toast.makeText(context, "Your entered amount is larger than the due amount", Toast.LENGTH_SHORT).show();
                return;
            } else if (amountToPayView.getText().toString().equals("0")){
                Toast.makeText(context, "Amount can't be zero", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Double.parseDouble(amountToPayView.getText().toString()) > Double.parseDouble(userDetails.getBalance())){
                Toast.makeText(context, "Insufficient Evaly balance (৳ "+userDetails.getBalance()+")", Toast.LENGTH_SHORT).show();
                return;
            }

            makePartialPayment(invoice_no, amountToPayView.getText().toString());
        });


        bkash.setOnClickListener(v -> {
            double amountToPay = total_amount - paid_amount;

            if (amountToPayView.getText().toString().trim().equals("")){
                Toast.makeText(context, "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            } else if (Double.parseDouble(amountToPayView.getText().toString()) > amountToPay) {
                Toast.makeText(context, "Your entered amount is larger than the due amount", Toast.LENGTH_SHORT).show();
                return;
            } else if (amountToPayView.getText().toString().equals("0")){
                Toast.makeText(context, "Amount can't be zero", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(OrderDetailsActivity.this, PayViaBkashActivity.class);
            intent.putExtra("amount", amountToPayView.getText().toString());
            intent.putExtra("invoice_no", invoice_no);
            intent.putExtra("context", "order_payment");
            startActivityForResult(intent,10002);

            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        cards.setOnClickListener(v -> {
            double amountToPay = total_amount - paid_amount;

            if (amountToPayView.getText().toString().trim().equals("")){
                Toast.makeText(context, "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            } else if (Double.parseDouble(amountToPayView.getText().toString()) > amountToPay) {
                Toast.makeText(context, "Your entered amount is larger than the due amount", Toast.LENGTH_SHORT).show();
                return;
            } else if (amountToPayView.getText().toString().equals("0")){
                Toast.makeText(context, "Amount can't be zero", Toast.LENGTH_SHORT).show();
                return;
            }

            double amToPay = Double.parseDouble(amountToPayView.getText().toString());

            addBalanceViaCard(invoice_no, String.valueOf((int) amToPay));

            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        });

    }

    @OnClick(R.id.tvViewIssue)
    void viewIssues(){
        startActivity(new Intent(OrderDetailsActivity.this, IssuesActivity.class).putExtra("invoice", invoice_no));
    }

    @OnClick(R.id.tvReport)
    void report(){
        OrderIssueModel model = new OrderIssueModel();

        bottomSheetDialog = new BottomSheetDialog(OrderDetailsActivity.this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.report_view);

        Spinner spinner = bottomSheetDialog.findViewById(R.id.spnDelivery);
        EditText etDescription = bottomSheetDialog.findViewById(R.id.etDescription);
        Button btnSubmit = bottomSheetDialog.findViewById(R.id.btnSubmit);
        ImageView ivClose = bottomSheetDialog.findViewById(R.id.ivClose);
        LinearLayout addPhoto = bottomSheetDialog.findViewById(R.id.addPhoto);

        List<String> options = new ArrayList<>();
        List<OrderIssueModel> list = Constants.getDelivaryIssueList();
        for (int i = 0; i<list.size(); i++){
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
            if (etDescription.getText().toString().trim().isEmpty()){
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
                if (response.code() == 200 || response.code() == 201){
                    Toast.makeText(getApplicationContext(), "Your issue has been submitted, you will be notified shortly", Toast.LENGTH_LONG).show();
                    bottomSheetDialog.dismiss();
                } else if (response.code() == 401){
                    submitIssue(model, bottomSheetDialog);
                }else {
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


    public void addPartialPayDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.WideDialog));

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_partial_pay, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();

        Button d_submit = dialogView.findViewById(R.id.submit);

        final EditText amount = dialogView.findViewById(R.id.amount);


        alertDialog.getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        alertDialog.show();


        d_submit.setOnClickListener(v -> {

            if (amount.getText().toString().equals("")){
                Toast.makeText(context, "Please enter an amount.", Toast.LENGTH_SHORT).show();
                return;
            }

            double partial_amount = Double.parseDouble(amount.getText().toString());

            if (partial_amount > total_amount){
                Toast.makeText(context, "You have entered an amount that is larger than your due amount.", Toast.LENGTH_LONG).show();
                return;
            }


            double userBalance = Double.parseDouble(userDetails.getBalance());

            if (partial_amount <= userBalance){
                makePartialPayment(invoice_no, String.valueOf((int) partial_amount));
                Logger.d(partial_amount);
            } else {

                alertDialog.dismiss();

                //Toast.makeText(context, "Insufficient Balance, pay the rest amount.", Toast.LENGTH_SHORT).show();
                double amountToPay = partial_amount - userBalance;

                amountToPayView.setText(amountToPay+"");
                full_or_partial.setText("Partial Pay");

                Handler handler = new Handler();
                handler.postDelayed(() -> sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED), 500);
            }
        });
    }




    public void dialogGiftCardPayment(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.WideDialog));

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_pay_with_gift_card, null);
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();

        Button d_submit = dialogView.findViewById(R.id.submit);

        final EditText amount = dialogView.findViewById(R.id.amount);
        final EditText code = dialogView.findViewById(R.id.code);


        amount.setText((int)due_amount+"");


        alertDialog.getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        alertDialog.show();


        d_submit.setOnClickListener(v -> {
            if (amount.getText().toString().equals("")){
                Toast.makeText(context, "Please enter an amount.", Toast.LENGTH_SHORT).show();
                return;
            } else if (code.getText().toString().equals("")){
                Toast.makeText(context, "Please enter gift card coupon code.", Toast.LENGTH_SHORT).show();
                return;
            }
            double partial_amount = Double.parseDouble(amount.getText().toString());

            if (partial_amount > total_amount){
                Toast.makeText(context, "You have entered an amount that is larger than your due amount.", Toast.LENGTH_LONG).show();
                return;
            }

            makePaymentViaGiftCard(code.getText().toString(), invoice_no, String.valueOf((int) partial_amount));

        });
    }



    public void makePaymentViaGiftCard(String giftCode, String invoice, String amount) {

        HashMap<String, String> payload = new HashMap<>();

        payload.put("invoice_no", invoice);
        payload.put("gift_code", giftCode);
        payload.put("amount", amount);

        GiftCardApiHelper.payWithGiftCard(CredentialManager.getToken(), payload, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                dialog.hideDialog();

                Toast.makeText(OrderDetailsActivity.this, response.get("message").getAsString(), Toast.LENGTH_LONG).show();

                if(response.has("success")) {

                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        finish();
                        startActivity(getIntent());
                    }, 1000);
                }

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                Toast.makeText(OrderDetailsActivity.this,"Payment unsuccessful!", Toast.LENGTH_LONG).show();
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


    @Override
    public void onResume(){
        super.onResume();
        Balance.update(this, false);
        checkCardBalance();

    }

    public void checkCardBalance(){

        AuthApiHelper.getUserInfoPay(CredentialManager.getToken(), CredentialManager.getUserName(), new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                response = response.getAsJsonObject("data");

                if (response.get("gift_card_balance").getAsDouble() < 1)
                    payViaGiftCard.setVisibility(View.GONE);

                balance.setText(Html.fromHtml("Balance: <b>৳ "+response.get("balance").getAsString() + "</b>"));

            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }


    public void makePartialPayment(String invoice, String amount){

        dialog.showDialog();

        ParitalPaymentModel model = new ParitalPaymentModel();

        model.setInvoice_no(invoice);
        model.setAmount(Integer.parseInt(amount));

        OrderApiHelper.makePartialPayment(CredentialManager.getToken(), model, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                dialog.hideDialog();

                Toast.makeText(OrderDetailsActivity.this,response.get("message").getAsString(), Toast.LENGTH_SHORT).show();

                if(response.get("success").getAsBoolean()){

                    // it means all payment done

                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {

                        dialog.hideDialog();
                        finish();
                        startActivity(getIntent());

                    }, 1000);
                }

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



    public void addBalanceViaCard(String invoice, String amount) {

        if (balance.equals("")){
            Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.showDialog();

        HashMap<String, String> payload = new HashMap<>();

        payload.put("amount", amount);
        payload.put("context", "order_payment");
        payload.put("context_reference", invoice);


        OrderApiHelper.payViaCard(CredentialManager.getToken(), payload, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                dialog.hideDialog();

                String purl = response.get("payment_gateway_url").getAsString();

                Intent intent = new Intent(OrderDetailsActivity.this, PayViaCard.class);
                intent.putExtra("url", purl);
                startActivityForResult(intent,10002);

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void getOrderDetails(){

        OrderApiHelper.getOrderDetails(CredentialManager.getToken(), invoice_no, new ResponseListenerAuth<OrderDetailsModel, String>() {
            @Override
            public void onDataFetched(OrderDetailsModel response, int statusCode) {

                dialog.hideDialog();

                String orderStatus = response.getOrderStatus().toLowerCase();

                if(orderStatus.equals("pending")){
                    indicator.setCurrentStep(1);
                }else if(orderStatus.equals("confirmed")){
                    indicator.setCurrentStep(2);
                }else if(orderStatus.equals("processing")){
                    indicator.setCurrentStep(3);
                }else if(orderStatus.equals("picked")){
                    indicator.setCurrentStep(4);
                }else if(orderStatus.equals("shipped")){
                    indicator.setCurrentStep(5);
                }else if(orderStatus.equals("delivered")){
                    indicator.setCurrentStep(6);
                }


                if (!response.isDeliveryConfirmed() && response.isDeliveryConfirmationRequired())
                    deliveryConfirmationDialog();


                orderDate.setText(Utils.formattedDateFromString("", "yyyy-MM-d", response.getDate()));

                if(response.getOrderStatus().toLowerCase().equals("cancel")){

                    StepperIndicator indicatorCancelled = findViewById(R.id.indicatorCancelled);
                    indicatorCancelled.setVisibility(View.VISIBLE);
                    indicatorCancelled.setCurrentStep(6);
                    indicator.setDoneIcon(getDrawable(R.drawable.ic_close_smallest));
                    indicator.setVisibility(View.GONE);
                    makePayment.setVisibility(View.GONE);
                    payParially.setVisibility(View.GONE);
                    payViaGiftCard.setVisibility(View.GONE);

                } else if(response.getOrderStatus().toLowerCase().equals("delivered")){

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

                if (response.getCampaignRules().size()>0){

                    JsonObject campaignRuleObject = response.getCampaignRules().get(0);

                    double cashback_percentage = campaignRuleObject.get("cashback_percentage").getAsDouble();

                    if (cashback_percentage>0) {

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

                            Calendar start = Calendar.getInstance(TimeZone.getTimeZone("GMT-6"), Locale.ENGLISH);
                            Calendar end = Calendar.getInstance(TimeZone.getTimeZone("GMT-6"), Locale.ENGLISH);

                            end.set(Calendar.HOUR_OF_DAY, 0);
                            start.set(Calendar.HOUR_OF_DAY, 0);

                            try {
                                end.setTime(df_input.parse(cashback_date));
                            } catch (ParseException e) {
                                Log.e("timze", e.toString());
                            }

                            long startTime = start.getTimeInMillis();
                            long diffTime = end.getTimeInMillis() - startTime;

                            long diffDays = TimeUnit.DAYS.convert(diffTime, TimeUnit.MILLISECONDS) + 1;

                            String message = "Payments with <font color=\"#c53030\">" + payMethod + "</font> will be rewarded by <b>" + cashback_percentage + "%</b> cashback balance within <b>" + diffDays + " days</b>.";
                            tvCampaignRule.setText(Html.fromHtml(message));
                        } else {

                            String message = "Payments with <font color=\"#c53030\">" + payMethod + "</font> will be rewarded by <b>" + cashback_percentage + "%</b> cashback balance instantly.";
                            tvCampaignRule.setText(Html.fromHtml(message));

                        }

                    }
                }


                String payMethod = response.getPaymentMethod();

                if(payMethod.equals("cod"))
                    paymentMethods.setText("Cash on Delivery");
                else if (payMethod.equals(""))
                    paymentMethods.setText("None");
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


                orderDetailsProducts.clear();
                orderDetailsProductAdapter.notifyDataSetChanged();


                List<OrderItemsItem> orderItemList = response.getOrderItems();

                for(int i=0;i<orderItemList.size();i++){

                    OrderItemsItem orderItem = orderItemList.get(i);
                    String productVariation = "";

                    for(int j = 0; j < orderItem.getVariations().size(); j++) {

                        JsonObject varJ = orderItem.getVariations().get(j).getAsJsonObject();
                        String attr = varJ.get("attribute").getAsString();
                        String variation = varJ.get("attribute_value").getAsString();

                        if (j > 0)
                            productVariation = productVariation + ", " +attr + ": "+ variation;
                        else
                            productVariation = attr + ": "+ variation;
                    }

                    orderDetailsProducts.add(
                            new OrderDetailsProducts(
                                    orderItem.getItemImages().get(0),
                                    orderItem.getItemName(),
                                    orderItem.getProductSlug(),
                                    orderItem.getOrderTimePrice(),
                                    String.valueOf(orderItem.getQuantity()),
                                    (Math.round(Double.parseDouble(orderItem.getOrderTimePrice()))* orderItem.getQuantity())+"",
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



    public void getOrderHistory(){

        orderStatuses.clear();
        adapter.notifyDataSetChanged();
        OrderApiHelper.getOrderHistories(CredentialManager.getToken(), invoice_no, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {

                JsonArray list  = response.getAsJsonObject("data").getAsJsonArray("histories");

                for (int i=0; i < list.size(); i++) {

                    JsonObject jsonObject = list.get(i).getAsJsonObject();

                    orderStatuses.add(new OrderStatus(
                            jsonObject.get("date").getAsString(),
                            jsonObject.get("order_status").getAsString(),
                            jsonObject.get("note").getAsString())
                    );

                }
                adapter.notifyDataSetChanged();
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



    public void deliveryConfirmationDialog(){


        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsActivity.this);
        builder.setTitle("Did you receive the product?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            HashMap<String, String> data = new HashMap<>();
            data.put("invoice_no", invoice_no);
            AuthApiHelper.updateProductStatus(data, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                @Override
                public void onDataFetched(retrofit2.Response<JsonObject> response1) {
                    if (response1.code() == 200 || response1.code() == 201){
                        dialog.hideDialog();
                        Toast.makeText(getApplicationContext(), "Order Updated", Toast.LENGTH_LONG).show();
                    }else {
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