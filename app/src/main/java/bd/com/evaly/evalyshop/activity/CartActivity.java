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
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.orderDetails.AddBalanceActivity;
import bd.com.evaly.evalyshop.activity.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.adapter.CartAdapter;
import bd.com.evaly.evalyshop.models.CartItem;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
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
    Button btnBottomSheet;

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
    int paymentMethod = 2;

    String userAgent;
    boolean isCheckedFromAdapter = false;

    double totalPriceDouble = 0;

    CompoundButton.OnCheckedChangeListener selectAllListener;

    BottomSheetDialog bottomSheetDialog;
    View bottomSheetView;

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

        checkout = findViewById(R.id.button);
        selectAll = findViewById(R.id.checkBox);
        alert=new ViewDialog(CartActivity.this);

        manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        adapter = new CartAdapter(itemList,context, db);
        recyclerView.setAdapter(adapter);

        // bottom sheet

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_checkout, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        btnBottomSheet = bottomSheetView.findViewById(R.id.bs_button);


        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean selected=false;
                for (int i = 0; i < itemList.size(); i++){
                    if(itemList.get(i).isSelected()){
//                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                        mViewBg.setVisibility(View.VISIBLE);

                        bottomSheetDialog.show();

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



        TextView privacyText = bottomSheetView.findViewById(R.id.privacyText);

        privacyText.setText(Html.fromHtml("I agree to the <a href=\"https://evaly.com.bd/about/terms-conditions\">Terms & Conditions</a> and <a href=\"https://evaly.com.bd/about/purchasing-policy\">Purchasing Policy</a> of Evaly."));
        privacyText.setMovementMethod(LinkMovementMethod.getInstance());

        CheckBox checkBox = bottomSheetView.findViewById(R.id.checkBox);


        selectAll.setOnCheckedChangeListener(selectAllListener);
        btnBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(userDetails.getToken().equals("")) {
                    startActivity(new Intent(CartActivity.this, SignInActivity.class));
                    return;
                }

                if (!checkBox.isChecked()){
                    Toast.makeText(CartActivity.this, "You must accept terms & conditions and purchasing policy to place an order.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (addressSwitch.isChecked() && customAddress.getText().toString().equals("")){
                    Toast.makeText(context, "Please enter address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (totalPriceDouble < 500){
                    Toast.makeText(context, "You can't order below 500 Tk.", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!Utils.isValidNumber(contact_number.getText().toString())){
                    Toast.makeText(context, "Please enter a correct phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                 placeOrder(generateOrderJson(), dialog);

                //generateOrderJson();


            }
        });

        // bottom sheet

        contact_number = bottomSheetView.findViewById(R.id.contact_number);
        addressSwitch = bottomSheetView.findViewById(R.id.addressSwitch);
        customAddress = bottomSheetView.findViewById(R.id.customAddress);
        addressSpinner = bottomSheetView.findViewById(R.id.spinner);

        contact_number.setText(userDetails.getPhone());
        spinnerArray = new ArrayList<String>();
        spinnerArrayID = new ArrayList<Integer>();

        customAddress.setText(userDetails.getJsonAddress());

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

        ImageView cod = bottomSheetView.findViewById(R.id.cod);
        ImageView evalyPay = bottomSheetView.findViewById(R.id.evaly_pay);


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

        getCartList();

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
                if(itemList.size() == 0){
                    Toast.makeText(context, "No item is available in cart to delete", Toast.LENGTH_SHORT).show();
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

            totalPriceDouble = 0;

            cartItem=true;
            while(res.moveToNext()){
                itemList.add(new CartItem(res.getString(0),res.getString(1),res.getString(2),res.getString(3),res.getInt(4),res.getLong(5),res.getString(6),res.getInt(7),true, res.getString(8), res.getString(9)));

                totalPriceDouble +=  res.getInt(4);


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
        totalPriceDouble = 0;

        for (int i = 0; i < listAdapter.size(); i++){
            if(listAdapter.get(i).isSelected()){
                totalPrice += listAdapter.get(i).getPrice() * listAdapter.get(i).getQuantity();
                totalPriceDouble += listAdapter.get(i).getPrice() * listAdapter.get(i).getQuantity();
            }
        }

        TextView priceView = findViewById(R.id.totalPrice);
        priceView.setText("৳ "+totalPrice);

        TextView priceView2 = bottomSheetView.findViewById(R.id.bs_price);
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

        super.onBackPressed();

    }

    public void placeOrder(JSONObject payload, ViewDialog alert){

        String url = UrlUtils.BASE_URL+"custom/order/create/";

        Log.d("json order url", url);

        alert.showDialog();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                Log.d("json order", response.toString());



                try {

                    if (response.getJSONArray("data").length() < 1) {
                        dialog.hideDialog();
                        // Toast.makeText(context, "Order couldn't be placed", Toast.LENGTH_SHORT).show();

                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();

                        return;
                    } else {

                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();

                        orderPlaced();
                        dialog.hideDialog();

                    }

                } catch (Exception e){

                }


                try {

                    JSONArray data = response.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {

                        JSONObject item = data.getJSONObject(i);
                        String invoice = item.getString("invoice_no");
                        Intent intent = new Intent(context, OrderDetailsActivity.class);
                        intent.putExtra("orderID", invoice);
                        startActivity(intent);

                        //makePayment(invoice, alert, response.length()-1, i);
                    }


                } catch (Exception e) {

                    Log.e("json exception", e.toString());
                    Toast.makeText(context, "Couldn't place order, might be a server error.", Toast.LENGTH_SHORT).show();

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
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
                    Logger.d(fromShopJson);
                    // add "bought from shop" data to json (comes from db)
                    //items.optJSONObject(index).put("shop_product_item", new JSONObject(fromShopJson));

                    JSONObject itemsObject = new JSONObject();  // get the current object from items array

                    // put quantity
                    itemsObject.put("quantity", adapterItems.get(i).getQuantity());

                    try{
                        JSONObject sellerJson = new JSONObject(fromShopJson);
                        String item_id = sellerJson.getString("shop_item_id");
                        itemsObject.put("shop_item_id", item_id);

                        // september date for pendrive

                        String currentDateString = "08/31/2019 1:00:00";
                        Date septemberDate;

                        String currentDateString2 = "09/20/2019 1:00:00";
                        Date septemberDate2;

                        SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
                        septemberDate = sd.parse(currentDateString);
                        septemberDate2 = sd.parse(currentDateString2);

                        if (sellerJson.getString("shop_slug").equals("evaly-amol-1")) {


                            Date joinDate = Utils.formattedDateFromString("", userDetails.getCreatedAt());

                            if (joinDate.after(septemberDate) && joinDate.before(septemberDate2)) {
                                items.put(itemsObject);

                            } else {
                                Toast.makeText(context, "10 Tk pendrive offer is only available to users who joined after 1st September, 2019", Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                            items.put(itemsObject);

                    } catch (Exception e){
                    }

                    index++;
                }
            }

            Logger.d(obj);

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

        bottomSheetDialog.hide();

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
