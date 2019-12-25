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
import android.webkit.WebSettings;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.badoualy.stepperindicator.StepperIndicator;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.issue.IssuesActivity;
import bd.com.evaly.evalyshop.ui.order.orderDetails.adapter.OrderDetailsProductAdapter;
import bd.com.evaly.evalyshop.ui.order.orderDetails.adapter.OrderStatusAdapter;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.OrderDetailsProducts;
import bd.com.evaly.evalyshop.models.OrderStatus;
import bd.com.evaly.evalyshop.models.order.OrderIssueModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.ui.order.PayViaBkashActivity;
import bd.com.evaly.evalyshop.ui.order.PayViaCard;
import bd.com.evaly.evalyshop.util.Balance;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.RealPathUtil;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.ui.chat.viewmodel.ImageUploadView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderDetailsActivity extends BaseActivity {

    private static final int SCROLL_DIRECTION_UP = -1;

    private NestedScrollView scrollView;
    String shopSlug="", shopCategory="";

    // for payment
    String invoice_no="";

    double total_amount = 0.0, paid_amount = 0.0, due_amount = 0.0;

    UserDetails userDetails;
    StepperIndicator indicator;
    TextView shopName,shopAddress,shopnumber,username,userAddress,userNumber,totalPriceTextView,paidAmountTextView,duePriceTextView;
    TextView orderNumber,orderDate,paymentMethods,balance;
    RecyclerView recyclerView,orderList;
    ArrayList<OrderStatus> orderStatuses;
    OrderStatusAdapter adapter;
    ArrayList<OrderDetailsProducts> orderDetailsProducts;
    OrderDetailsProductAdapter orderDetailsProductAdapter;
    ViewDialog dialog;
    TextView makePayment, payParially, full_or_partial;
    RelativeLayout shopInfo;

    String userAgent;

    TextView payViaGiftCard;


    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    Button btnBottomSheet;
    View mViewBg;
    TextView amountToPayView, evalyPayText;
    ImageView bkash,cards,evalyPay;

    BottomSheetDialog bottomSheetDialog;


    int paymentMethod = -1;

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

        try {
            userAgent = WebSettings.getDefaultUserAgent(this);
        } catch (Exception e) {
            userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
        }

        queue= Volley.newRequestQueue(context);


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


        // update balance
        Balance.update(this, false);

        mViewBg = findViewById(R.id.bg);

        // bottom sheet
        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        amountToPayView = findViewById(R.id.amountPay);
        bkash = findViewById(R.id.bkash);
        cards = findViewById(R.id.card);
        evalyPay = findViewById(R.id.evaly_pay);
        evalyPayText = findViewById(R.id.evalyPayText);

        final TextView cardText = findViewById(R.id.gatewayText);

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
        //indicator.setCurrentStep(3);

        dialog = new ViewDialog(this);

        dialog.showDialog();


        makePayment = findViewById(R.id.makePayment);

        payParially = findViewById(R.id.payPartially);

        makePayment.setOnClickListener(v -> {


            double amountToPay = total_amount - paid_amount;

            double userBalance = Double.parseDouble(userDetails.getBalance());


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
        Logger.d("CLicked");
        OrderIssueModel model = new OrderIssueModel();
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View view = LayoutInflater.from(this).inflate(R.layout.report_view, null, false);
//        builder.setView(view);
//        builder.setCancelable(false);

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
                    Logger.d("=---==========------");
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    });
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
                handler.postDelayed(new Runnable() {
                    public void run() {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }, 500);

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


            double userBalance = Double.parseDouble(userDetails.getBalance());


            makePaymentViaGiftCard(code.getText().toString(), invoice_no, String.valueOf((int) partial_amount));



        });
    }

    public void makePaymentViaGiftCard(String giftCode, String invoice, String amount){

        String url= UrlUtils.DOMAIN+"pay/transactions/payment/order/gift-code/";

        dialog.showDialog();
        Log.d("json order url", url);
        Toast.makeText(this,"Payment is processing", Toast.LENGTH_SHORT).show();

        JSONObject payload = new JSONObject();

        try{
            payload.put("invoice_no", invoice);
            payload.put("gift_code", giftCode);
            payload.put("amount", amount);
        } catch (Exception e){


        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload, response -> {

            Log.d("json payment res", response.toString());

            if(!response.has("success")){

                try {
                    dialog.hideDialog();
                    Toast.makeText(OrderDetailsActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();

                } catch (Exception e){
                }

            } else {


                try {
                    // it means all payment done
                    Toast.makeText(OrderDetailsActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                } catch (Exception e){

                }

                final Handler handler = new Handler();
                handler.postDelayed(() -> {

                    dialog.hideDialog();
                    finish();
                    startActivity(getIntent());

                }, 1500);

            }
        }, error -> {


            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                if (error.networkResponse.statusCode == 401){

                AuthApiHelper.refreshToken(OrderDetailsActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                    @Override
                    public void onDataFetched(retrofit2.Response<JsonObject> response) {
                        makePaymentViaGiftCard(giftCode, invoice, amount);
                    }

                    @Override
                    public void onFailed(int status) {

                    }
                });

                return;

            }}

            dialog.hideDialog();
            Log.e("onErrorResponse", error.toString());

            //Toast.makeText(OrderDetailsActivity.this,"Insufficient balance!", Toast.LENGTH_LONG).show();

            Toast.makeText(OrderDetailsActivity.this,"Payment unsuccessful!", Toast.LENGTH_LONG).show();

            try {
                String responseBody = new String(error.networkResponse.data, "utf-8");
                JSONObject data = new JSONObject(responseBody);
                Toast.makeText(OrderDetailsActivity.this, data.getString("message"), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
            }




        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", CredentialManager.getToken());
                return headers;
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }


    @Override
    public void onResume(){
        super.onResume();
        Balance.update(this, false);
        checkCardBalance();

    }

    public void checkCardBalance(){

        String url=UrlUtils.BASE_URL_AUTH+"user-info-pay/"+userDetails.getUserName()+"/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), response -> {
            Log.d("onResponse", response.toString());
            try {

                TextView payViaGiftCard = findViewById(R.id.payViaGiftCard);
                response = response.getJSONObject("data");
                if (response.getDouble("gift_card_balance") < 1)
                    payViaGiftCard.setVisibility(View.GONE);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("onErrorResponse", error.toString());

            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                if (error.networkResponse.statusCode == 401){

                AuthApiHelper.refreshToken(OrderDetailsActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                    @Override
                    public void onDataFetched(retrofit2.Response<JsonObject> response) {
                        checkCardBalance();
                    }

                    @Override
                    public void onFailed(int status) {

                    }
                });

                return;

            }}

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", CredentialManager.getToken());
                return headers;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue= Volley.newRequestQueue(context);
        queue.add(request);
    }


    public void makePartialPayment(String invoice, String amount){

        String url= UrlUtils.DOMAIN+"pay/transactions/payment/order/";

        dialog.showDialog();
        Log.d("json order url", url);
        Toast.makeText(this,"Partial payment is processing", Toast.LENGTH_SHORT).show();

        JSONObject payload = new JSONObject();

        try{
            payload.put("invoice_no", invoice);
            payload.put("amount", Integer.parseInt(amount));
        } catch (Exception e){


        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload, response -> {


            Log.d("json payment res", response.toString());

            if(!response.has("success")){

                try {

                    dialog.hideDialog();
                    shouldAddBalance();

                } catch (Exception e){
                }

            } else {

                // it means all payment done
                Toast.makeText(OrderDetailsActivity.this,"Partial payment successful!", Toast.LENGTH_LONG).show();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        dialog.hideDialog();
                        finish();
                        startActivity(getIntent());

                    }
                }, 1500);

            }
        }, error -> {

            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                if (error.networkResponse.statusCode == 401){

                AuthApiHelper.refreshToken(OrderDetailsActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                    @Override
                    public void onDataFetched(retrofit2.Response<JsonObject> response) {
                        makePartialPayment(invoice, amount);
                    }

                    @Override
                    public void onFailed(int status) {

                    }
                });
                return;

            }}
            dialog.hideDialog();
            Log.e("onErrorResponse", error.toString());

            //Toast.makeText(OrderDetailsActivity.this,"Insufficient balance!", Toast.LENGTH_LONG).show();
            shouldAddBalance();

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", CredentialManager.getToken());
                // headers.put("Host", "api-prod.evaly.com.bd");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                return headers;
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }



    public void addBalanceViaCard(String invoice, String amount) {

        String url = UrlUtils.DOMAIN+"pay/pg";
        Log.d("json order url", url);
        JSONObject payload = new JSONObject();

        if (balance.equals("")){
            Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            payload.put("amount", amount);
            payload.put("context", "order_payment");
            payload.put("context_reference", invoice);

        } catch (Exception e){

        }

        dialog.showDialog();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload, response -> {


            Log.d("json payload", payload.toString());
            Log.d("json response", response.toString());

            dialog.hideDialog();

            try {

                String purl = response.getString("payment_gateway_url");


                Log.d("json response", purl);

                Intent intent = new Intent(OrderDetailsActivity.this, PayViaCard.class);
                intent.putExtra("url", purl);
                startActivityForResult(intent,10002);


            }catch (Exception e){


            }

        }, error -> {
            Log.e("onErrorResponse", error.toString());

            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                if (error.networkResponse.statusCode == 401){

                AuthApiHelper.refreshToken(OrderDetailsActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                    @Override
                    public void onDataFetched(retrofit2.Response<JsonObject> response) {
                        addBalanceViaCard(invoice, amount);
                    }
                    @Override
                    public void onFailed(int status) {
                    }
                });
                return;
            }}
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", CredentialManager.getToken());
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }


    public void shouldAddBalance(){
        Snackbar.make(findViewById(android.R.id.content), "Insufficient balance!", Snackbar.LENGTH_LONG)
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

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

                String destinationDirectoryPath = context.getCacheDir().getPath() + File.separator + "images";

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


//                    if (selectedImage != null && bottomSheetDialog.isShowing()) {
//                        Glide.with(this)
//                                .asBitmap()
//                                .load(selectedImage)
//                                .skipMemoryCache(true)
//                                .fitCenter()
//                                .optionalCenterCrop()
//                                .placeholder(R.drawable.half_dp_bg_light)
//                                .apply(new RequestOptions().override(300, 300))
//                                .into((ImageView) bottomSheetDialog.findViewById(R.id.postImage));
//                    }
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



    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }


    public void getOrderDetails(){
        String url=UrlUtils.BASE_URL+"custom/orders/"+invoice_no+"/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), response -> {
            dialog.hideDialog();

            // brand grand days only full payment through bKash

            try{

                if(response.getJSONArray("shop_groups").toString().contains("grandbranddays")){
                    payParially.setVisibility(View.GONE);
                    shopGroup = response.getJSONArray("shop_groups").toString();
                }

            }catch(Exception e){}


            try{
                if(response.getString("order_status").toLowerCase().equals("pending")){
                    indicator.setCurrentStep(1);
                }else if(response.getString("order_status").toLowerCase().equals("confirmed")){
                    indicator.setCurrentStep(2);
                }else if(response.getString("order_status").toLowerCase().equals("processing")){
                    indicator.setCurrentStep(3);
                }else if(response.getString("order_status").toLowerCase().equals("picked")){
                    indicator.setCurrentStep(4);
                }else if(response.getString("order_status").toLowerCase().equals("shipped")){
                    indicator.setCurrentStep(5);
                }else if(response.getString("order_status").toLowerCase().equals("delivered")){
                    indicator.setCurrentStep(6);
                }
            }catch(Exception e){}

            boolean delivery_confirmed, delivery_confirmation_required;
            try {
                delivery_confirmation_required = response.getBoolean("delivery_confirmation_required");
                delivery_confirmed = response.getBoolean("delivery_confirmed");

                if (!delivery_confirmed && delivery_confirmation_required){
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
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                orderDate.setText(Utils.formattedDateFromString("", "yyyy-MM-d", response.getString("date")));
            } catch (Exception e){

                // json exception check :p
                try {
                    orderDate.setText(response.getString("date"));
                } catch (Exception ee){}
            }


            try{

                if(response.getString("order_status").toLowerCase().equals("cancel")){
                    StepperIndicator indicatorCancelled = findViewById(R.id.indicatorCancelled);
                    indicatorCancelled.setVisibility(View.VISIBLE);
                    indicatorCancelled.setCurrentStep(6);
                    indicator.setDoneIcon(getDrawable(R.drawable.ic_close_smallest));
                    indicator.setVisibility(View.GONE);
                    makePayment.setVisibility(View.GONE);
                    payParially.setVisibility(View.GONE);
                    payViaGiftCard.setVisibility(View.GONE);
                }
            }catch(Exception e){}

            try{
                if(response.getString("order_status").toLowerCase().equals("delivered")){
                    makePayment.setVisibility(View.GONE);
                    payParially.setVisibility(View.GONE);
                    payViaGiftCard.setVisibility(View.GONE);
                }
            }catch(Exception e) {}

            try{
                username.setText(response.getJSONObject("customer").getString("first_name")+" "+response.getJSONObject("customer").getString("last_name"));
            }catch(Exception e){}

            try{
                userAddress.setText(response.getString("customer_address"));
            }catch(Exception e){
                userAddress.setText("");
            }

            try{
                userNumber.setText(response.getString("contact_number"));
            }catch(Exception e){}

            try{
                totalPriceTextView.setText("৳ " + Math.round(Double.parseDouble(response.getString("total"))));
            }catch(Exception e){}

            try{
                paidAmountTextView.setText("৳ " + Math.round(Double.parseDouble(response.getString("paid_amount"))));
            }catch(Exception e){}

            try{
                duePriceTextView.setText("৳ " + (Math.round(Double.parseDouble(response.getString("total"))) - Math.round(Double.parseDouble(response.getString("paid_amount"))))+"");

            }catch(Exception e){}


            try{
                if(response.getString("payment_method").equals("cod")){
                    paymentMethods.setText("Cash on Delivery");
                }else{
                    paymentMethods.setText(response.getString("Evaly Pay"));
                }
            }catch(Exception e){

            }
            try{
                shopName.setText(response.getJSONObject("shop").getString("name"));
            }catch(Exception e){
                Log.d("shop_error",e.getMessage());
            }

            try{
                shopSlug=response.getJSONObject("shop").getString("slug");
            }catch(Exception e){}

            try{
                shopAddress.setText(response.getJSONObject("shop").getString("address"));
            }catch(Exception e){}

            try{
                shopnumber.setText(response.getJSONObject("shop").getString("contact_number"));
            }catch(Exception e){}


            try {
                total_amount = Math.round(Double.parseDouble(response.getString("total")));
                paid_amount = Math.round(Double.parseDouble(response.getString("paid_amount")));
                due_amount = total_amount - paid_amount;

                if (due_amount < 1) {
                    makePayment.setVisibility(View.GONE);
                    payParially.setVisibility(View.GONE);
                    payViaGiftCard.setVisibility(View.GONE);
                }
            } catch (Exception e) {
            }


            try{

                orderDetailsProducts.clear();
                orderDetailsProductAdapter.notifyDataSetChanged();

                    //Log.d("product_image",ob.getJSONObject("shop_product_item").getJSONObject("product_item").getJSONObject("product").getString("thumbnail"));

                    JSONArray jsonArray = response.getJSONArray("order_items");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject ob = jsonArray.getJSONObject(i);
                        Log.d("object_details",ob.toString());

                        String productVariation = "";

                        try{

                            for(int j = 0; j < ob.getJSONArray("variations").length(); j++) {

                                JSONObject varJ = ob.getJSONArray("variations").getJSONObject(j);
                                String attr = varJ.getString("attribute");
                                String variation = varJ.getString("attribute_value");

                                if (j > 0)
                                    productVariation = productVariation + ", " +attr + ": "+ variation;
                                else
                                    productVariation = attr + ": "+ variation;

                            }
                        } catch (Exception e){

                            Log.e("json error", e.toString());
                        }

                        try{
                            orderDetailsProducts.add(
                                    new OrderDetailsProducts(
                                            ob.getJSONArray("item_images").getString(0),
                                            ob.getString("item_name"),
                                            ob.getString("product_slug"),
                                            ob.getString("order_time_price"),
                                            ob.getString("quantity"),
                                            (Math.round(Double.parseDouble(ob.getString("order_time_price")))*Double.parseDouble(ob.getString("quantity")))+"",
                                            productVariation));
                            orderDetailsProductAdapter.notifyItemInserted(orderDetailsProducts.size());
                        }catch(Exception e){

                        }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("onErrorResponse", error.toString());


            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                if (error.networkResponse.statusCode == 401){

                AuthApiHelper.refreshToken(OrderDetailsActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                    @Override
                    public void onDataFetched(retrofit2.Response<JsonObject> response) {
                        getOrderDetails();
                    }

                    @Override
                    public void onFailed(int status) {

                    }
                });

                return;

            }}

            dialog.hideDialog();
            Toast.makeText(OrderDetailsActivity.this, "Error occured while grabing order details", Toast.LENGTH_SHORT).show();

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", CredentialManager.getToken());
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public void getOrderHistory(){
        String url=UrlUtils.BASE_URL+"orders/histories/"+invoice_no+"/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("json", response.toString());

                try {

                    JSONArray list  = response.getJSONObject("data").getJSONArray("histories");

                    for (int i=0; i < list.length(); i++) {

                        orderStatuses.add(new OrderStatus(
                                list.getJSONObject(i).getString("date"),
                                list.getJSONObject(i).getString("order_status"),
                                list.getJSONObject(i).getString("note"))
                        );
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();

            }
        }, error -> {
            Log.e("onErrorResponse", error.toString());

            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                if (error.networkResponse.statusCode == 401){

                AuthApiHelper.refreshToken(OrderDetailsActivity.this, new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                    @Override
                    public void onDataFetched(retrofit2.Response<JsonObject> response) {
                        getOrderHistory();
                    }

                    @Override
                    public void onFailed(int status) {

                    }
                });

                return;

            }}

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", CredentialManager.getToken());

                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
