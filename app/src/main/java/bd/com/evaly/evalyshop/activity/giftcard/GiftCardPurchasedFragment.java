package bd.com.evaly.evalyshop.activity.giftcard;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.giftcard.adapter.GiftCardListPurchasedAdapter;
import bd.com.evaly.evalyshop.activity.orderDetails.PayViaBkashActivity;
import bd.com.evaly.evalyshop.activity.orderDetails.PayViaCard;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListPurchasedItem;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.VolleyMultipartRequest;

import static android.app.Activity.RESULT_OK;


public class GiftCardPurchasedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    View view;
    RecyclerView recyclerView;
    ArrayList<GiftCardListPurchasedItem> itemList;
    GiftCardListPurchasedAdapter adapter;
    RequestQueue rq;
    static GiftCardPurchasedFragment instance;
    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    ViewDialog dialog;
    ImageView image,plus,minus;
    UserDetails userDetails;
    TextView details,name,total;
    EditText quantity;
    int voucherAmount=0;
    Button placeOrder;
    String giftCardInvoice="";
    String amount;

    LinearLayout noItem;
    Context context;

    BottomSheetDialog bottomSheetDialog;
    BottomSheetBehavior bottomSheetBehavior;
    View bottomSheetInternal;

    LinearLayout progressContainer;
    ProgressBar progressBar;
    int currentPage;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    TextView amountToPayView;
    ImageView bkash,cards, bank;


    SwipeRefreshLayout swipeLayout;

    @Override
    public void onRefresh() {

        itemList.clear();
        adapter.notifyDataSetChanged();
        currentPage = 1;
        swipeLayout.setRefreshing(false);

        getGiftCardList();


    }


    @Override
    public void onResume(){


        super.onResume();

        itemList.clear();
        adapter.notifyDataSetChanged();
        currentPage = 1;

        getGiftCardList();


    }

    public GiftCardPurchasedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_giftcard_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        swipeLayout = view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        itemList=new ArrayList<>();
        dialog=new ViewDialog(getActivity());

        context = getContext();
        rq = Volley.newRequestQueue(context);
        userDetails=new UserDetails(context);

        progressContainer = view.findViewById(R.id.progressContainer);
        progressBar = view.findViewById(R.id.progressBar);
        currentPage = 1;

        initializeBottomSheet();


        noItem = view.findViewById(R.id.noItem);


        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);

        instance=this;
        adapter=new GiftCardListPurchasedAdapter(context, itemList, 0);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = manager.getChildCount();
                    totalItemCount = manager.getItemCount();
                    pastVisiblesItems = manager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {

                            getGiftCardList();
                        }
                    } }
            }
        });



        return view;
    }


    private boolean isImageSelected = false;


    public void buildBankDialog(Bitmap bitmap){
        final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_bank_receipt, null);
        TextView amountET =  dialogView.findViewById(R.id.amount);


        double amToPay = Double.parseDouble(amountToPayView.getText().toString());

        amountET.setText((int)amToPay+"");

        amountET.setVisibility(View.GONE);


        Button submit =  dialogView.findViewById(R.id.buttonSubmit);
        Button cancel =  dialogView.findViewById(R.id.buttonCancel);

        ImageView selectImage = dialogView.findViewById(R.id.upload);

        if(isImageSelected) {
            selectImage.setImageBitmap(bitmap);
        }


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
                    return; }


                if (amount.equals("")) {
                    Toast.makeText(context, "Please enter amount.", Toast.LENGTH_LONG).show();
                    return; }

                uploadBankDepositImage(bitmap);

                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }


    public void paymentViaBank(String image){

        ProgressDialog dialog = ProgressDialog.show(context, "",
                "Updating data...", true);

        String url= UrlUtils.DOMAIN+"pay/bank_deposit/";
        JSONObject parameters = new JSONObject();

        try {
            int a = Integer.parseInt(amount);
            parameters.put("amount", a);
        } catch (Exception e){
            Toast.makeText(context, "Invalid amount, enter only numbers!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            parameters.put("context","gift_card_order_payment");
            parameters.put("context_reference",giftCardInvoice);
            parameters.put("bank_receipt_copy",image);
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
                    Log.d("json set image", response.toString());
                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
                headers.put("Authorization", CredentialManager.getToken());
                return headers;
            }
        };
        request.setShouldCache(false);
        rq.add(request);
    }


    private void uploadBankDepositImage(final Bitmap bitmap) {

        ProgressDialog dialog = ProgressDialog.show(context, "",
                "Uploading image...", true);
        RequestQueue rQueue;
        String url= UrlUtils.BASE_URL+"image/upload";

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        dialog.dismiss();
                        Log.d("json image",new String(response.data));
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            jsonObject = jsonObject.getJSONObject("data");
                            String image = jsonObject.getString("url");
                            paymentViaBank(image);

//                          Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                          jsonObject.toString().replace("\\\\","");


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
                        Toast.makeText(context, "Image upload error, might be too large image", Toast.LENGTH_SHORT).show();
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
                return headers;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };


        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                isImageSelected = true;
                buildBankDialog(bitmap);

                //uploadBankDepositImage(bitmap);
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







    public void initializeBottomSheet(){

        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_gift_card_payment);

        bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        bottomSheetInternal.setPadding(0, 0, 0, 0);

        new KeyboardUtil(getActivity(), bottomSheetInternal);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        layoutBottomSheet = bottomSheetDialog.findViewById(R.id.bottom_sheet);

        amountToPayView = bottomSheetDialog.findViewById(R.id.amountPay);
        bkash = bottomSheetDialog.findViewById(R.id.bkash);
        cards = bottomSheetDialog.findViewById(R.id.card);
        bank = bottomSheetDialog.findViewById(R.id.bank);

        TextView full_or_partial = bottomSheetDialog.findViewById(R.id.full_or_partial);
        full_or_partial.setVisibility(View.GONE);

        bkash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                Intent intent = new Intent(context, PayViaBkashActivity.class);
                intent.putExtra("amount", amountToPayView.getText().toString());
                intent.putExtra("invoice_no", giftCardInvoice);
                intent.putExtra("context", "gift_card_order_payment");
                startActivityForResult(intent,10002);
            }
        });


        cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                double amToPay = Double.parseDouble(amountToPayView.getText().toString());
                addBalanceViaCard(giftCardInvoice, String.valueOf((int) amToPay));
            }
        });


        bank.setOnClickListener(v -> {

            isImageSelected = false;

            amount = amountToPayView.getText().toString();

            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ic_upload_image_large);
            buildBankDialog(bitmap);

        });

    }


    public void catchError(){
        try{
            dialog.hideDialog();
        }catch(Exception e){}

        Toast.makeText(context, "Sorry something went wrong(server error). Please try again.", Toast.LENGTH_SHORT).show();

    }

    public void toggleBottomSheet(GiftCardListPurchasedItem item){

        initializeBottomSheet();

        giftCardInvoice = item.getInvoiceNo();

        amountToPayView.setText(item.getTotal()+"");
        amount = item.getTotal()+"";
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.show();

    }

    public void getGiftCardList(){

        loading = false;


        if (currentPage == 1){
            progressContainer.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else  {

            progressContainer.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

        }

        String url = UrlUtils.DOMAIN+"cpn/gift-card-orders?show=purchased&page="+currentPage;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {

                        loading = true;
                        progressBar.setVisibility(View.GONE);

                        JSONArray jsonArray = response.getJSONArray("data");

                        if (currentPage == 1)
                            progressContainer.setVisibility(View.GONE);


                        if (jsonArray.length() == 0 && currentPage == 1){
                            noItem.setVisibility(View.VISIBLE);
                            TextView noText = view.findViewById(R.id.noText);
                            noText.setText("You haven't purchased any gift card yet");
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            Gson gson = new Gson();

                            try {

                                GiftCardListPurchasedItem item = gson.fromJson(jsonArray.getJSONObject(i).toString(), GiftCardListPurchasedItem.class);
                                itemList.add(item);
                                adapter.notifyItemInserted(itemList.size());
                            }catch (Exception e){}

                        }

                        currentPage++;

                    } catch (JSONException e) {
                        e.printStackTrace();
                        catchError();
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
                            getGiftCardList();
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });

                    return;

                }}

                error.printStackTrace();
                progressContainer.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                if (!userDetails.getToken().equals(""))
                    headers.put("Authorization", CredentialManager.getToken());

                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(request);
    }


    public static GiftCardPurchasedFragment getInstance() {
        return instance;
    }







    public void addBalanceViaCard(String invoice, String amount) {

        String url = UrlUtils.DOMAIN + "pay/pg";

        Log.d("json order url", url);

        JSONObject payload = new JSONObject();


        if (amountToPayView.getText().toString().equals("")){
            Toast.makeText(context, "Enter amount", Toast.LENGTH_SHORT).show();
            return;
        }


        try {
            payload.put("amount", amount);
            payload.put("context", "gift_card_order_payment");
            payload.put("context_reference", invoice);

        } catch (Exception e){

        }



        dialog.showDialog();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.hideDialog();

                try {


                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                    String purl = response.getString("payment_gateway_url");
                    Intent intent = new Intent(context, PayViaCard.class);
                    intent.putExtra("url", purl);
                    startActivityForResult(intent,10002);


                }catch (Exception e){


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
                            addBalanceViaCard(invoice, amount);
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
                return headers;
            }




        };

        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(request);
    }


}
