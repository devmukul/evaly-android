package bd.com.evaly.evalyshop.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.order.PayViaBkashActivity;
import bd.com.evaly.evalyshop.adapter.MyVoucherAdapter;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.MyVoucherInfo;
import bd.com.evaly.evalyshop.models.VoucherDetails;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.VolleyMultipartRequest;

import static android.app.Activity.RESULT_OK;





public class VoucherMyListFragment extends Fragment {

    RecyclerView voucherList;
    View view;
    ArrayList<MyVoucherInfo> myVoucherInfos;
    ArrayList<VoucherDetails> voucherDetails;
    MyVoucherAdapter adapter;
    UserDetails userDetails;
    RequestQueue rq;
    ImageView bkash,bank,card;
    BottomSheetBehavior sheetBehavior;
    View mViewBg;
    LinearLayout layoutBottomSheet;
    static VoucherMyListFragment instance;
    EditText amountPay,amountET,transactionID;
    String orderID="",slug="",bankDepositImage="";

    LinearLayout noItem;

    ProgressBar progressBar;

    int attempt = 0;


    Context context;
    String userAgent;

    boolean isImageSelected = false;

    String amount = "0";


    TextView balance;

    public VoucherMyListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_voucher_my_list, container, false);
        voucherList=view.findViewById(R.id.voucher_list);

        context = getContext();
        try {
            userAgent = WebSettings.getDefaultUserAgent(context);
        } catch (Exception e) {
            userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
        }

        voucherList.setLayoutManager(new LinearLayoutManager(context));
        userDetails=new UserDetails(context);
        rq = Volley.newRequestQueue(context);
        myVoucherInfos=new ArrayList<>();
        voucherDetails=new ArrayList<>();
        adapter=new MyVoucherAdapter(context,myVoucherInfos);
        voucherList.setAdapter(adapter);
        balance = view.findViewById(R.id.balance);

        layoutBottomSheet = view.findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        amountPay=view.findViewById(R.id.amountPay);
        bkash=view.findViewById(R.id.bkash);
        bank=view.findViewById(R.id.bank);
        card=view.findViewById(R.id.card);
        mViewBg = view.findViewById(R.id.bg);
        instance=this;
        noItem = view.findViewById(R.id.noItem);

        progressBar = view.findViewById(R.id.progressBar);


        mViewBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                mViewBg.setVisibility(View.GONE);
            }
        });



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

        bkash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialog();
            }
        });

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Connecting to server...", Toast.LENGTH_SHORT).show();

                paymentViaCard();
            }
        });

        bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               // Toast.makeText(context, "Uploading Bank Receipt system is coming in next update.", Toast.LENGTH_LONG).show();


//                return;

                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_upload_image_large);

                buildBankDialog(bitmap);



            }
        });

        balance.setText(Html.fromHtml("Holding balance: <b>Updating</b>"));



        if (userDetails.getToken().equals("")){


            noItem.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);


            ((TextView) view.findViewById(R.id.noText)).setText("Please login first to see your claimed vouchers");

        } else {
            getVoucherInfo("1");
            holdingAmount();
        }


        //holdingAmount();
        return view;
    }

    public void toggleBottomSheet(String a,String b,int amount) {
        orderID=a;
        slug=b;
        amountPay.setText(amount+"");
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            mViewBg.setVisibility(View.VISIBLE);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            mViewBg.setVisibility(View.GONE);
        }
    }

    public static VoucherMyListFragment getInstance() {
        return instance;
    }

    public void getVoucherInfo(String page){
        String url="https://api.evaly.com.bd/pay/voucher-orders/?page="+page;
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {

        }

        Log.d("voucher_response", url);


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("voucher_response",response.toString());

                progressBar.setVisibility(View.GONE);


                try {
                    if(response.getInt("count")==0){
                        voucherList.setVisibility(View.GONE);
                        noItem.setVisibility(View.VISIBLE);
                    }else{
                        JSONArray jsonArray = response.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);
                            MyVoucherInfo info=new MyVoucherInfo();
                            info.setInvoiceNumber(ob.getString("invoice_no"));
                            info.setCustomerNumber(ob.getString("customer"));
                            JSONObject variant=ob.getJSONObject("voucher_variant");
                            info.setVoucherName(variant.getString("name"));
                            info.setVoucherDescription(variant.getString("description"));
                            info.setVoucherImage(variant.getString("thumnail_image"));
                            info.setVoucherSlug(variant.getString("slug"));
                            info.setValue(variant.getInt("value"));
                            info.setAmount(variant.getInt("amount"));
                            info.setStatus(variant.getString("status"));
                            JSONObject voucher=ob.getJSONObject("voucher");
                            info.setApplicableDate(voucher.getString("applicable_date"));
                            info.setStartDate(voucher.getString("start_date"));
                            info.setEndDate(voucher.getString("end_date"));
                            info.setApplicable(ob.getBoolean("applicable"));
                            info.setTotalPrice(ob.getInt("total_price"));
                            info.setTotalPaid(ob.getInt("total_paid"));
                            info.setClaimAmount(ob.getInt("claim_amount"));
                            info.setApplyStatus(ob.getString("apply_status"));
                            info.setQuantity(ob.getInt("quantity"));
                            info.setStatus(ob.getString("status"));
                            info.setPaymentStatus(ob.getString("payment_status"));
                            info.setDate(ob.getString("date"));
                            myVoucherInfos.add(info);
                            adapter.notifyItemInserted(myVoucherInfos.size());
                        }

                        if(response.getInt("count") > 10 && page.equals("1")){


                            getVoucherInfo("2");
                        } else if(response.getInt("count") > 20 && page.equals("2")){


                            getVoucherInfo("3");
                        }





                    if(myVoucherInfos.size() < 1){

                            noItem.setVisibility(View.VISIBLE);

                        } else {


                            noItem.setVisibility(View.GONE);
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


                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    if (error.networkResponse.statusCode == 401){

                    AuthApiHelper.refreshToken(getActivity(), new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                            getVoucherInfo(page);
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });

                    return;

                }}


                if(page.equals("1")) {

                    if (attempt < 3) {
                        attempt++;
                        getVoucherInfo(page);
                    } else {


                        try {

                            Toast.makeText(context, "Server error, try again", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                        }
                    }
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", CredentialManager.getToken());
                // headers.put("Host", "api-prod.evaly.com.bd");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                return headers;
            }
        };
        request.setShouldCache(false);

        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rq.add(request);
    }

    public void paymentViaCard(){
        String url="https://api-prod.evaly.com.bd/pay/pg";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
            int a=Integer.parseInt(amountPay.getText().toString());
            parameters.put("amount",a);
            parameters.put("context","voucher_payment");
            parameters.put("context_reference",orderID);
        } catch (Exception e) {

        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String purl = response.getString("payment_gateway_url");


                    Utils.CustomTab(purl, context);


                }catch (Exception e){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    if (error.networkResponse.statusCode == 401){

                    AuthApiHelper.refreshToken(getActivity(), new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                            paymentViaCard();
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });

                    return;

                }}

                Log.e("onErrorResponse", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", CredentialManager.getToken());
                // headers.put("Host", "api-prod.evaly.com.bd");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                return headers;
            }
        };
        request.setShouldCache(false);
        rq.add(request);
    }

    public void paymentViaBank(String image){



        ProgressDialog dialog = ProgressDialog.show(context, "",
                "Updating data...", true);

        String url="https://api-prod.evaly.com.bd/pay/bank_deposit/";
        JSONObject parameters = new JSONObject();



        try {

            int a = Integer.parseInt(amount);

            parameters.put("amount", amount);
        } catch (Exception e){

            Toast.makeText(context, "Invalid amount, enter only numbers!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            parameters.put("context","voucher_payment");
            parameters.put("context_reference",orderID);
            parameters.put("bank_receipt_copy",image);        //put image url
        } catch (Exception e) {

            Toast.makeText(context, "Error occured!", Toast.LENGTH_SHORT).show();
            return;

        }


        Log.d("json param", parameters.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{

                    dialog.dismiss();
                    Log.d("json set image",response.toString());

                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();

                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    mViewBg.setVisibility(View.GONE);

                    getActivity().finish();
                    startActivity(getActivity().getIntent());


                }catch(Exception e){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    if (error.networkResponse.statusCode == 401){

                    AuthApiHelper.refreshToken(getActivity(), new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                            paymentViaBank(image);
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });

                    return;

                }}

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", CredentialManager.getToken());
                // headers.put("Host", "api-prod.evaly.com.bd");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                return headers;
            }
        };
        request.setShouldCache(false);
        rq.add(request);
    }

    public void paymentViaBkash(){
        String url="https://api-prod.evaly.com.bd/pay/bkash_transaction/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
            int a=Integer.parseInt(amountET.getText().toString());
            parameters.put("amount",a);
            parameters.put("context","voucher_payment");
            parameters.put("context_reference",orderID);
            parameters.put("bkash_payment_id",transactionID.getText().toString());
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.getBoolean("success")){
                        Toast.makeText(context, "Voucher payment completed successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        if(!response.getString("message").equals("")){
                            String cap = response.getString("message").substring(0, 1).toUpperCase() + response.getString("message").substring(1);
                            Toast.makeText(context,cap, Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(context, "Sorry something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    getActivity().finish();
                    startActivity(getActivity().getIntent());

                }catch(Exception e){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    if (error.networkResponse.statusCode == 401){

                    AuthApiHelper.refreshToken(getActivity(), new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                            paymentViaBkash();
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });

                    return;

                }}

            }
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
        request.setShouldCache(false);
        rq.add(request);
    }

    public void buildDialog(){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_voucher_payment, null);
        amountET =  dialogView.findViewById(R.id.amount);
        amountET.setText(amountPay.getText().toString());
        transactionID =  dialogView.findViewById(R.id.transaction);
        Button submit =  dialogView.findViewById(R.id.buttonSubmit);
        Button cancel =  dialogView.findViewById(R.id.buttonCancel);
        TextView howBkash  =dialogView.findViewById(R.id.howBkash);


        howBkash.setPaintFlags(howBkash.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        howBkash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PayViaBkashActivity.class));
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentViaBkash();
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }





    public void buildBankDialog(Bitmap bitmap){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_bank_receipt, null);
        amountET =  dialogView.findViewById(R.id.amount);
        amountET.setText(amountPay.getText().toString());


        transactionID =  dialogView.findViewById(R.id.transaction);
        Button submit =  dialogView.findViewById(R.id.buttonSubmit);
        Button cancel =  dialogView.findViewById(R.id.buttonCancel);

        ImageView selectImage = dialogView.findViewById(R.id.upload);

        if(isImageSelected)
            selectImage.setImageBitmap(bitmap);



        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Bank Deposit Photo"),1000);
                dialogBuilder.dismiss();

            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isImageSelected){

                    Toast.makeText(context, "Please select your bank receipt image", Toast.LENGTH_LONG).show();
                    return;

                }


                amount = amountPay.getText().toString();

                if (amount.equals(""))
                {
                    Toast.makeText(context, "Please enter amount.", Toast.LENGTH_LONG).show();
                    return;

                }


                uploadBankDepositImage(bitmap);

                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }


    public void holdingAmount(){
        String url= UrlUtils.BASE_URL_AUTH+"user-info-pay/"+userDetails.getUserName();
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {

        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{


                     JSONObject data = response.getJSONObject("data");

                    Log.d("holding_balance",data.toString());

                    int bala = 0;

                    try {

                        bala = (int) data.getInt("holding_balance");

                    } catch (Exception e) {

                    }

                    balance.setText(Html.fromHtml("Holding balance: <b>৳ " +bala +"</b>"));


                    balance.setVisibility(View.VISIBLE);

                }catch(Exception e){

                    balance.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    if (error.networkResponse.statusCode == 401){

                    AuthApiHelper.refreshToken(getActivity(), new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                            holdingAmount();
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });

                    return;

                }}

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", CredentialManager.getToken());
                // headers.put("Host", "api-prod.evaly.com.bd");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                return headers;
            }
        };
        request.setShouldCache(false);
        rq.add(request);
    }

    public void claimVoucher(String invoiceNumber){
        String url="https://api-prod.evaly.com.bd/pay/transactions/apply/order/voucher/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
            parameters.put("invoice_no",invoiceNumber);
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    Log.d("claim_amount_check",response.toString());
                }catch(Exception e){

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
                headers.put("Authorization", CredentialManager.getToken());
                // headers.put("Host", "api-prod.evaly.com.bd");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                return headers;
            }
        };
        request.setShouldCache(false);
        rq.add(request);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());


                isImageSelected = true;



                buildBankDialog(bitmap);

                //uploadBankDepositImage(bitmap);

                //
                // ByteArrayOutputStream ba=new ByteArrayOutputStream();
                //bitmap.compress(Bitmap.CompressFormat.JPEG,100,ba);
                //byte[] imgBytes=ba.toByteArray();
                //bankDepositImage=Base64.encodeToString(imgBytes,Base64.DEFAULT);


                //Log.d("json ", bankDepositImage);

                //uploadBankDepositImage(bankDepositImage);

            }catch(Exception e){

            }
        }
    }






    private void uploadBankDepositImage(final Bitmap bitmap) {

        ProgressDialog dialog = ProgressDialog.show(context, "",
                "Uploading image...", true);

        RequestQueue rQueue;
        String url="https://api-prod.evaly.com.bd/api/evaly_images/"; // dev mode, don't use it

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {

                            dialog.dismiss();

                            Log.d("json image",new String(response.data));


                            try {
                                JSONObject jsonObject = new JSONObject(new String(response.data));

                                String image = jsonObject.getString("image");

                                paymentViaBank(image);


//                                Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//
//                                jsonObject.toString().replace("\\\\","");


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    if (error.networkResponse.statusCode == 401){

                                AuthApiHelper.refreshToken(getActivity(), new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                                    @Override
                                    public void onDataFetched(retrofit2.Response<JsonObject> response) {
                                        uploadBankDepositImage(bitmap);
                                    }

                                    @Override
                        public void onFailed(int status) {

                        }
                    });

                    return;

                }}

                            dialog.dismiss();
                            Toast.makeText(context, "Image upload error", Toast.LENGTH_SHORT).show();
                        }
                    }) {


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    // params.put("tags", "ccccc");  add string parameters
                    return params;
                }


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();

                    headers.put("Authorization", CredentialManager.getToken());
                    // headers.put("Host", "api-prod.evaly.com.bd");
                    headers.put("Origin", "https://evaly.com.bd");
                    headers.put("Referer", "https://evaly.com.bd/");
                    headers.put("User-Agent", userAgent);

                    return headers;
                }

                /*
                 *pass files using below method
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    long imagename = System.currentTimeMillis();
                    params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                    return params;
                }
            };


            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue = Volley.newRequestQueue(context);
            rQueue.add(volleyMultipartRequest);
        }

        public byte[] getFileDataFromDrawable(Bitmap bitmap) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }









}
