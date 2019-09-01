package bd.com.evaly.evalyshop.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.badoualy.stepperindicator.StepperIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.OrderDetailsProductAdapter;
import bd.com.evaly.evalyshop.adapter.OrderStatusAdapter;
import bd.com.evaly.evalyshop.models.OrderDetailsProducts;
import bd.com.evaly.evalyshop.models.OrderStatus;
import bd.com.evaly.evalyshop.util.Balance;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class OrderDetailsActivity extends BaseActivity {

    private static final int SCROLL_DIRECTION_UP = -1;

    private NestedScrollView scrollView;
    String shopSlug="",shopCategory="";

    // for payment
    String invoice_no="";

    Double total_amount = 0.0, paid_amount = 0.0, due_amount = 0.0;

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
    TextView makePayment, payParially;
    RelativeLayout shopInfo;

    String userAgent;


    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    Button btnBottomSheet;
    View mViewBg;
    TextView amountToPayView;
    ImageView bkash,cards;


    int paymentMethod = -1;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        try {
            userAgent = WebSettings.getDefaultUserAgent(this);
        } catch (Exception e) {
            userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
        }


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
        Balance.update(this);

        mViewBg = findViewById(R.id.bg);

        // bottom sheet
        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        amountToPayView = findViewById(R.id.amountPay);
        bkash = findViewById(R.id.bkash);
        cards = findViewById(R.id.card);

        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);




        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
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


        mViewBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                mViewBg.setVisibility(View.GONE);

            }
        });


        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
               if (scrollY == 0)
                   getSupportActionBar().setElevation(0);
               else
                   getSupportActionBar().setElevation(4f);
            }
        });

        shopInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailsActivity.this, MainActivity.class);
                intent.putExtra("type", 3);
                intent.putExtra("shop_slug", shopSlug);
                intent.putExtra("category", "root");
                startActivity(intent);
            }
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

        makePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(OrderDetailsActivity.this)
                        .setMessage("Do you sure you want to make payment?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                double userBalance = Double.parseDouble(userDetails.getBalance());

                                if (total_amount <= userBalance){
                                    makePartialPayment(invoice_no, String.valueOf(total_amount));
                                } else {


                                    Toast.makeText(context, "Insufficient Balance, pay the rest amount.", Toast.LENGTH_SHORT).show();

                                    double amountToPay = total_amount - userBalance;

                                    amountToPayView.setText(amountToPay+"");
                                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                }

                            }})
                        .setNegativeButton(android.R.string.no, null).show();



            }
        });


        payParially.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addPartialPayDialog();


            }
        });




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


        d_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (amount.getText().toString().equals("")){
                    Toast.makeText(context, "Please enter an amount.", Toast.LENGTH_SHORT).show();
                    return;
                }



                double partial_amount = Double.parseDouble(amount.getText().toString());
                double userBalance = Double.parseDouble(userDetails.getBalance());

                if (total_amount <= userBalance){
                    makePartialPayment(invoice_no, String.valueOf(total_amount));
                } else {


                    Toast.makeText(context, "Insufficient Balance, pay the rest amount.", Toast.LENGTH_SHORT).show();

                    double amountToPay = total_amount - userBalance;

                    amountToPayView.setText(amountToPay+"");
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }




                makePartialPayment(invoice_no, amount.getText().toString());
                alertDialog.dismiss();


            }
        });
    }





    @Override
    public void onResume(){

        super.onResume();

        getBalance();



    }







    public void makePartialPayment(String invoice, String amount){

        String url="https://api.evaly.com.bd/pay/transactions/payment/order/";

        dialog.showDialog();


        Log.d("json order url", url);


        Toast.makeText(this,"Partial payment is processing", Toast.LENGTH_SHORT).show();

        JSONObject payload = new JSONObject();

        try{
            payload.put("invoice_no", invoice);
            payload.put("amount", Integer.parseInt(amount));
        } catch (Exception e){


        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hideDialog();
                Log.e("onErrorResponse", error.toString());

                //Toast.makeText(OrderDetailsActivity.this,"Insufficient balance!", Toast.LENGTH_LONG).show();

                shouldAddBalance();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());
                // headers.put("Host", "api-prod.evaly.com.bd");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                // headers.put("Content-Length", data.length()+"");
                return headers;
            }

        };
        RequestQueue queue= Volley.newRequestQueue(this);
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        queue.add(request);
    }


    public void shouldAddBalance(){



        Snackbar.make(findViewById(android.R.id.content), "Insufficient balance!", Snackbar.LENGTH_LONG)
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();



    }




    public void getBalance(){
        String url="https://api-prod.evaly.com.bd/pay/balance/"+userDetails.getUserName()+"/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("onResponse", response.toString());
                try {

                    userDetails.setBalance(response.getString("balance"));

                    balance.setText(Html.fromHtml("Balance: <b>৳ "+ response.getString("balance") + "</b>"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());
                // headers.put("Host", "api-prod.evaly.com.bd");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                return headers;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(request);
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
        String url="https://api.evaly.com.bd/core/custom/orders/"+invoice_no+"/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.hideDialog();
                largeLog("order_details",response.toString());
                try {
                    try{
                        if(response.getString("order_status").toLowerCase().equals("pending")){
                            indicator.setCurrentStep(1);
                        }else if(response.getString("order_status").toLowerCase().equals("confirmed")){
                            indicator.setCurrentStep(2);
                        }else if(response.getString("status").toLowerCase().equals("processing")){
                            indicator.setCurrentStep(3);
                        }else if(response.getString("order_status").toLowerCase().equals("picked")){
                            indicator.setCurrentStep(4);
                        }else if(response.getString("order_status").toLowerCase().equals("shipped")){
                            indicator.setCurrentStep(5);
                        }else if(response.getString("order_status").toLowerCase().equals("delivered")){
                            indicator.setCurrentStep(6);
                        }
                    }catch(Exception e){

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


                        }
                    }catch(Exception e){

                    }


                    try{
                        if(response.getString("order_status").toLowerCase().equals("delivered")){
                            makePayment.setVisibility(View.GONE);
                            payParially.setVisibility(View.GONE);
                        }
                    }catch(Exception e) {

                    }



                        try{
                            username.setText(response.getJSONObject("customer").getString("first_name")+" "+response.getJSONObject("customer").getString("last_name"));
                        }catch(Exception e){

                        }
                        try{
                            userAddress.setText(response.getString("customer_address"));
                        }catch(Exception e){
                            userAddress.setText("");
                        }
                        try{
                            userNumber.setText(response.getJSONObject("customer").getString("username"));
                        }catch(Exception e){

                        }
                        try{
                            totalPriceTextView.setText("৳ " + String.format("%.2f", Double.parseDouble(response.getString("total"))));
                        }catch(Exception e){

                        }
                        try{
                            paidAmountTextView.setText("৳ " + String.format("%.2f", Double.parseDouble(response.getString("paid_amount"))));
                        }catch(Exception e){

                        }
                        try{
                            duePriceTextView.setText("৳ " + String.format("%.2f",(Double.parseDouble(response.getString("total"))-Double.parseDouble(response.getString("paid_amount")))));
                        }catch(Exception e){

                        }

                        total_amount = Double.parseDouble(response.getString("total"));
                        paid_amount = Double.parseDouble(response.getString("paid_amount"));
                        due_amount = total_amount-paid_amount;

                        if(due_amount<1)
                        {
                            makePayment.setVisibility(View.GONE);
                            payParially.setVisibility(View.GONE);
                        }

                        if(paid_amount > 1){


                            makePayment.setVisibility(View.GONE);

                        }
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
                        }catch(Exception e){

                        }
                        try{
                            shopAddress.setText(response.getJSONObject("shop").getString("address"));
                        }catch(Exception e){

                        }
                        try{
                            shopnumber.setText(response.getJSONObject("shop").getString("contact_number"));
                        }catch(Exception e){

                        }
                        //Log.d("product_image",ob.getJSONObject("shop_product_item").getJSONObject("product_item").getJSONObject("product").getString("thumbnail"));



                        String productVariation = "";


                        JSONArray jsonArray = response.getJSONArray("order_items");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject ob = jsonArray.getJSONObject(i);
                            Log.d("object_details",ob.toString());


                        try{


                            JSONObject productItem = ob.getJSONObject("shop_product_item");
                            JSONObject product_item = productItem.getJSONObject("product_item"); // get shop_product_item product_item

                            if(product_item.getJSONArray("varying_options").length() > 0) {

                                String attr = product_item.getJSONArray("varying_options").getJSONArray(0).getJSONObject(0).getJSONObject("attribute").getString("name");
                                String variation = product_item.getJSONArray("varying_options").getJSONArray(0).getJSONObject(0).getJSONObject("option").getString("value");
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
                                            (Double.parseDouble(ob.getString("order_time_price"))*Double.parseDouble(ob.getString("quantity")))+"",
                                            productVariation));
                            orderDetailsProductAdapter.notifyItemInserted(orderDetailsProducts.size());
                        }catch(Exception e){

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());

                dialog.hideDialog();
                Toast.makeText(OrderDetailsActivity.this, "Error occured while grabing order details", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());
               // headers.put("Host", "api.evaly.com.bd");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                //headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                return headers;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(OrderDetailsActivity.this);
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        queue.add(request);
    }

    public void getOrderHistory(){
        String url="https://api.evaly.com.bd/core/orders/histories/"+invoice_no+"/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("json", response.toString());

                try {

                    JSONArray list  = response.getJSONObject("data").getJSONArray("histories");

                    for (int i=0; i < list.length(); i++) {
                        try {
                            orderDate.setText(Utils.formattedDateFromString("", "yyyy-MM-d", list.getJSONObject(0).getString("date")));
                        } catch (Exception e){

                            orderDate.setText(list.getJSONObject(0).getString("date"));


                        }
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());
                headers.put("Host", "api.evaly.com.bd");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                return headers;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(OrderDetailsActivity.this);
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
