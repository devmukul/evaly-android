package bd.com.evaly.evalyshop.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.orderDetails.AddBalanceActivity;
import bd.com.evaly.evalyshop.adapter.CartAdapter;
import bd.com.evaly.evalyshop.models.CartItem;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.database.DbHelperCart;

public class CartActivity extends BaseActivity {

    /*
        H. M. Tamim
        24/Jun/2019
     */

    DbHelperCart db;
    ArrayList<CartItem> itemList;
    RecyclerView recyclerView;
    CartAdapter adapter;
    LinearLayoutManager manager;
    ImageView back,person;
    Context context;

    CheckBox selectAll;
    Button checkout;
    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    Button btnBottomSheet;
    View mViewBg;

    EditText customAddress, contact_number;
    Switch addressSwitch;
    Spinner addressSpinner;
    ArrayAdapter<String> spinnerArrayAdapter;
    int spinnerPosition;
    ArrayList<String> spinnerArray;

    ArrayList<Integer> spinnerArrayID;
    UserDetails userDetails;
    ViewDialog dialog;
    boolean cartItem=false;
    ViewDialog alert;
    int paymentMethod = -1;

    String userAgent;
    boolean isCheckedFromAdapter = false;

    CompoundButton.OnCheckedChangeListener selectAllListener;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_cart);
        //getSupportActionBar().setElevation(0);


        context = this;
        try {
            userAgent = WebSettings.getDefaultUserAgent(this);
        } catch (Exception e) {
            userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
        }
        dialog = new ViewDialog(this);

        getSupportActionBar().setElevation(4f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Shopping Cart");

        db = new DbHelperCart(context);
        userDetails = new UserDetails(context);

        itemList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycle);

        contact_number = findViewById(R.id.contact_number);
        checkout = findViewById(R.id.button);
        selectAll = findViewById(R.id.checkBox);
        mViewBg = findViewById(R.id.bg);
        alert=new ViewDialog(CartActivity.this);

        manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        adapter = new CartAdapter(itemList,context, db);
        recyclerView.setAdapter(adapter);

        // bottom sheet

        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        btnBottomSheet = findViewById(R.id.bs_button);


        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);


        getCartList();

        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean selected=false;
                for (int i = 0; i < itemList.size(); i++){
                    if(itemList.get(i).isSelected()){
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        mViewBg.setVisibility(View.VISIBLE);
                        selected=true;
                        break;
                    }
                }
                if(!selected){
                    Toast.makeText(context, "Please select item from cart", Toast.LENGTH_SHORT).show();
                }
                // generateOrderJson();

            }
        });






        selectAllListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i = 0; i < itemList.size(); i++)
                    itemList.get(i).setSelected(isChecked);

                adapter.notifyDataSetChanged();
            }
        };


        selectAll.setOnCheckedChangeListener(selectAllListener);







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


        btnBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (paymentMethod == -1){
                    Toast.makeText(context, "Please select payment method", Toast.LENGTH_SHORT).show();
                    return;

                }

                if(userDetails.getToken().equals("")) {
                    startActivity(new Intent(CartActivity.this, SignInActivity.class));
                    return;
                }

                if (addressSwitch.isChecked() && customAddress.getText().toString().equals("")){

                    Toast.makeText(context, "Please enter address.", Toast.LENGTH_SHORT).show();
                    return;


                }


                 placeOrder(generateOrderJson(), dialog);

                //generateOrderJson();



            }
        });



        // address spinner


        contact_number.setText(userDetails.getPhone());

        addressSpinner = findViewById(R.id.spinner);



        spinnerArray = new ArrayList<String>();

        spinnerArrayID = new ArrayList<Integer>();

        try{

            String addressString = userDetails.getJsonAddress();

            Log.d("json", addressString);

            JSONArray addresses = new JSONArray(addressString);

            for (int j = 0; j < addresses.length(); j++){


                if(j == 4)
                    break;

                JSONObject item = addresses.getJSONObject(j);

                spinnerArray.add(item.getString("address"));
                spinnerArrayID.add(item.getInt("id"));


            }

            Log.d("json", spinnerArray.size()+"");
            spinnerArrayAdapter = new ArrayAdapter<String>(CartActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            addressSpinner.setAdapter(spinnerArrayAdapter);
           // alert.showDialog();


        } catch (Exception e){


        }




        addressSwitch = findViewById(R.id.addressSwitch);

        customAddress = findViewById(R.id.customAddress);

        addressSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    customAddress.setVisibility(View.VISIBLE);
                    addressSpinner.setVisibility(View.GONE);

                } else{

                    customAddress.setVisibility(View.GONE);
                    addressSpinner.setVisibility(View.VISIBLE);

                }

            }
        });

        addressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                spinnerPosition = arg2; //Here is your selected position

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                spinnerPosition = 0;
            }

        });


        if(spinnerArray.size() < 1){
            addressSwitch.setChecked(true);
        }

        // updateAddress();





        ImageView cod = findViewById(R.id.cod);
        ImageView evalyPay = findViewById(R.id.evaly_pay);


        // select payment method

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                paymentMethod = 1;

                Log.d("json p", "cod");

            }
        });


        cod.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.performClick();
                }
            }
        });


        evalyPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                paymentMethod = 2;

                Log.d("json p", "evaly pay");

            }
        });


        evalyPay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.performClick();
                }
            }
        });



    }

    public void updateAddress(){
        String url="https://api-prod.evaly.com.bd/api/user/detail/"+userDetails.getUserName()+"/";
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


                    JSONObject ob=response.getJSONObject("user");
                    userDetails.setUserName(ob.getString("username"));
                    userDetails.setPhone(ob.getString("username"));
                    userDetails.setFirstName(ob.getString("first_name"));
                    userDetails.setLastName(ob.getString("last_name"));



                    JSONArray addressArray=response.getJSONArray("addresses");

                    if(userDetails.getJsonAddress().equals(addressArray.toString()))
                        Log.d("json", "yes same address");
                    else {



                        userDetails.setJsonAddress(addressArray.toString());

                        spinnerArray.clear();
                        spinnerArrayID.clear();
                        spinnerArrayAdapter.notifyDataSetChanged();


                        for (int i = 0; i < addressArray.length(); i++) {
                            if (i == 4) {

                                break;
                            }
                            spinnerArray.add(addressArray.getJSONObject(i).getString("address"));
                            spinnerArrayID.add(addressArray.getJSONObject(i).getInt("id"));
                            spinnerArrayAdapter.notifyDataSetChanged();
                        }

                        if (spinnerArray.size() < 1) {
                            addressSwitch.setChecked(true);
                        }
                    }


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
        RequestQueue queue= Volley.newRequestQueue(CartActivity.this);
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


    public void uncheckSelectAllBtn(boolean isChecked){


        if(!isChecked) {


            selectAll.setOnCheckedChangeListener(null);
            selectAll.setChecked(false);
            selectAll.setOnCheckedChangeListener(selectAllListener);
        }
        else {

            boolean isAllSelected = true;

            for (int i = 0; i < adapter.getItemList().size(); i++){
                if(!adapter.getItemList().get(i).isSelected()){
                    isAllSelected = false;
                    break;
                }
            }


            selectAll.setOnCheckedChangeListener(null);
            selectAll.setChecked(isAllSelected);
            selectAll.setOnCheckedChangeListener(selectAllListener);


        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:
                if(!cartItem){
                    Toast.makeText(context, "No available cart item to delete", Toast.LENGTH_SHORT).show();
                }else{
                    new AlertDialog.Builder(this)
                            .setMessage("Are you sure you want to delete the selected products from the cart?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {


                                    ArrayList<CartItem> listAdapter = adapter.getItemList();



                                    for (int i = 0; i < listAdapter.size(); i++){
                                        if(listAdapter.get(i).isSelected()){
                                            db.deleteData(listAdapter.get(i).getId());

                                        }
                                    }

                                    getCartList();




                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getCartList(){
        adapter.notifyItemRangeRemoved(0, itemList.size());
        itemList.clear();
        Cursor res=db.getData();
        if(res.getCount()==0){


            LinearLayout empty = findViewById(R.id.empty);
            LinearLayout cal = findViewById(R.id.cal);

            recyclerView.setVisibility(View.GONE);


            cal.setVisibility(View.GONE);

            empty.setVisibility(View.VISIBLE);

            Button button = findViewById(R.id.button);
            button.setVisibility(View.GONE);

            NestedScrollView scrollView = findViewById(R.id.scroller);
            scrollView.setBackgroundColor(Color.WHITE);


        }else{
            cartItem=true;
            while(res.moveToNext()){
                itemList.add(new CartItem(res.getString(0),res.getString(1),res.getString(2),res.getString(3),res.getInt(4),res.getLong(5),res.getString(6),res.getInt(7),true, res.getString(8), res.getString(9)));
//                Collections.sort(itemList, new Comparator<WishList>(){
//                    public int compare(WishList o1, WishList o2) {
//                        return o2.getTime() > o1.getTime();
//                    }
//                });
                adapter.notifyItemInserted(itemList.size());
                updateCartFromRecycler();
            }

             adapter.notifyDataSetChanged();
        }
    }


    public void updateCartFromRecycler(){


        ArrayList<CartItem> listAdapter = adapter.getItemList();
        int totalPrice = 0;


        for (int i = 0; i < listAdapter.size(); i++){
            if(listAdapter.get(i).isSelected()){
                totalPrice += listAdapter.get(i).getPrice() * listAdapter.get(i).getQuantity();
            }
        }

        TextView priceView = findViewById(R.id.totalPrice);
        priceView.setText("৳ "+totalPrice);

        TextView priceView2 = findViewById(R.id.bs_price);
        priceView2.setText("৳ "+totalPrice);


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delete_btn, menu);
        return true;
    }


    @Override
    public void onBackPressed(){

        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){

            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            mViewBg.setVisibility(View.GONE);

            return;
        }

        super.onBackPressed();


    }





    public void makePayment(String invoice, ViewDialog alert, int total, int current){

        String url="https://api.evaly.com.bd/pay/transactions/payment/order/";

        Log.d("json order url", url);


        Toast.makeText(context,"Payment is processing", Toast.LENGTH_SHORT).show();

        JSONObject payload = new JSONObject();

        try{
            payload.put("invoice_no", invoice);
        } catch (Exception e){


        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                Log.d("json payment res", response.toString());


                alert.hideDialog();
                orderPlaced();

                if(response.has("reason")){

                    try {


                        Double due = response.getDouble("due");
                        String invoice = response.getString("note");

                        Intent intent = new Intent(context, AddBalanceActivity.class);

                        intent.putExtra("due", due);
                        intent.putExtra("invoice", invoice);

                        startActivity(intent);

                        alert.hideDialog();
                        orderPlaced();

                    } catch (Exception e){


                    }



                } else {

                    if (total == current) {

                        // it means all payment done
                        alert.hideDialog();
                        orderPlaced();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                dialog.hideDialog();
                alert.hideDialog();
                orderPlaced();

                //Toast.makeText(context, "Error occurred during payment. Might be insufficient balance.", Toast.LENGTH_SHORT).show();
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
                // headers.put("Content-Length", data.length()+"");
                return headers;
            }




        };
        RequestQueue queue= Volley.newRequestQueue(CartActivity.this);
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



    public void placeOrder(JSONObject payload, ViewDialog alert){

        String url="https://api.evaly.com.bd/core/custom/order/create/";

        Log.d("json order url", url);

        alert.showDialog();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                Log.d("json order", response.toString());

                try {
                    if (!response.getBoolean("success")) {
                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                        dialog.hideDialog();
                        return;
                    }

                } catch (Exception e){

                }


                try {



                    if (response.getJSONArray("data").length() < 1) {
                        Toast.makeText(context, "Couldn't place holder.", Toast.LENGTH_SHORT).show();
                        dialog.hideDialog();
                        return;
                    }

                } catch (Exception e){

                }

                if(paymentMethod == 2) {


                    try {


                        JSONArray data = response.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {

                            JSONObject item = data.getJSONObject(i);

//                            String payment_status = item.getString("payment_status");
//                            String payment_method = item.getString("payment_method");
//                            String paid_amount= item.getString("paid_amount");
//
//                            Double paid_amount_double = 0.0;
//
//                            try{
//
//                                paid_amount_double = Double.parseDouble(paid_amount);
//
//                            } catch (Exception e){
//
//                            }
//
//                            Double total_price = item.getDouble("total_price");



                            String invoice = item.getString("invoice_no");

                            makePayment(invoice, alert, response.length()-1, i);



                        }


                    } catch (Exception e) {

                        Log.e("json exception", e.toString());
                        Toast.makeText(context, "Couldn't place order, might be a server error.", Toast.LENGTH_SHORT).show();


                    }
                } else {


                    orderPlaced();

                }




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());

                Toast.makeText(context, "Couldn't place holder, might be a server error.", Toast.LENGTH_SHORT).show();
                dialog.hideDialog();

                // orderPlaced();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());
                // headers.put("Host", "api-prod.evaly.com.bd");
                // headers.put("Content-Length", data.length()+"");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                return headers;
            }




        };
        RequestQueue queue= Volley.newRequestQueue(CartActivity.this);
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










    private JSONObject generateOrderJson(){


        JSONObject obj = new JSONObject();

        try {

            JSONObject addressObj = new JSONObject();

            if(!addressSwitch.isChecked()) {

                addressObj.put("address", JSONObject.NULL);
                addressObj.put("address_id", spinnerArrayID.get(spinnerPosition));

                Log.d("json", ""+spinnerArrayID.get(spinnerPosition));

            } else {



                addressObj.put("customer_address", customAddress.getText().toString());
                // addressObj.put("address_id", JSONObject.NULL);

            }





//            if(paymentMethod == 1)
//                obj.put("payment_method", "cod");
//            else if(paymentMethod == 2)

            obj.put("payment_method", "evaly_pay");

            // get items jsonArray

            obj.put("order_items", new JSONArray());
            obj.put("customer_address", customAddress.getText().toString());
            if (contact_number.getText().toString().equals(""))
                obj.put("contact_number", userDetails.getUserName());
            else
                obj.put("contact_number", contact_number.getText().toString());



            JSONArray items = obj.getJSONArray("order_items");


            int index=0;


            ArrayList<CartItem> adapterItems = adapter.getItemList();


            for(int i=0; i < adapterItems.size(); i++) {




                if(adapterItems.get(i).isSelected()) {

                    String fromShopJson = adapterItems.get(i).getSellerJson();

                    // Log.d("json seller", fromShopJson);


                    // add "bought from shop" data to json (comes from db)
                    //items.optJSONObject(index).put("shop_product_item", new JSONObject(fromShopJson));


                    JSONObject itemsObject = new JSONObject();  // get the current object from items array


                    // put quantity
                    itemsObject.put("quantity", adapterItems.get(i).getQuantity());



                    try{


                        JSONObject sellerJson = new JSONObject(fromShopJson);

                        String item_id = sellerJson.getString("shop_item_id");

                        itemsObject.put("shop_item_id", item_id);


                        items.put(itemsObject);

                    } catch (Exception e){


                    }



                    index++;
                }
            }

            largeLog("json", obj.toString());

        } catch (Exception e) {
            Log.e("json", "Could not parse malformed JSON: "+e.toString());
        }


        return obj;



    }



















    private JSONObject generateOrderJsonOld(){


        JSONObject obj = new JSONObject();

        try {

            JSONObject addressObj = new JSONObject();

            if(!addressSwitch.isChecked()) {

                addressObj.put("address", JSONObject.NULL);
                addressObj.put("address_id", spinnerArrayID.get(spinnerPosition));

                Log.d("json", ""+spinnerArrayID.get(spinnerPosition));

            } else {

                addressObj.put("address", customAddress.getText().toString());
                addressObj.put("address_id", JSONObject.NULL);

            }


            if(paymentMethod == 1)
                obj.put("payment_method", "cod");
            else if(paymentMethod == 2)
                obj.put("payment_method", "evaly_pay");

            // get items jsonArray

            obj.put("items", new JSONArray());
            obj.put("customer_address", addressObj);


            JSONArray items = obj.getJSONArray("items");


            int index=0;


            ArrayList<CartItem> adapterItems = adapter.getItemList();


            for(int i=0; i < adapterItems.size(); i++) {




                if(adapterItems.get(i).isSelected()) {

                    String fromShopJson = adapterItems.get(i).getSellerJson();

                    // Log.d("json seller", fromShopJson);

                    items.put(new JSONObject());

                    // add "bought from shop" data to json (comes from db)
                    items.optJSONObject(index).put("shop_product_item", new JSONObject(fromShopJson));


                    JSONObject itemsObject = items.getJSONObject(index);  // get the current object from items array


                    // put quantity
                    itemsObject.put("quantity", adapterItems.get(i).getQuantity());


                    JSONObject shop_product_item = itemsObject.getJSONObject("shop_product_item"); // get shop_product_item jsonObject
                    // product_item
                    //shop_product_item.put("product_item", new JSONObject());


                    JSONObject product_item = shop_product_item.getJSONObject("product_item"); // get shop_product_item product_item

                    // put product details (comes from db) - don't need now as shop has that
                    //product_item.put("product", new JSONObject(productJson));


                    JSONObject product = product_item.getJSONObject("product");

                    // put varying_options (come from db)
                    // product_item.put("varying_options", new JSONArray());


                    index++;
                }
            }

            largeLog("json", obj.toString());

        } catch (Exception e) {
            Log.e("json", "Could not parse malformed JSON: "+e.toString());
        }


        return obj;



    }


    public void orderPlaced(){


        dialog.hideDialog();

        ArrayList<CartItem> listAdapter = adapter.getItemList();

        for (int i = 0; i < listAdapter.size(); i++){
            if(listAdapter.get(i).isSelected()){
                db.deleteData(listAdapter.get(i).getId());

            }
        }

        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mViewBg.setVisibility(View.GONE);
        getCartList();
        Toast.makeText(context, "Your order has been placed!", Toast.LENGTH_LONG).show();

    }


    public String LoadData(String inFile) {
        String tContents = "";

        try {
            InputStream stream = getAssets().open(inFile);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);
        } catch (IOException e) {
            // Handle exceptions here
        }

        return tContents;

    }


    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }

    @Override
    protected void onDestroy() {
        if(db!=null){
            db.close();
        }
        super.onDestroy();
    }
}
