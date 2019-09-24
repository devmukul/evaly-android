package bd.com.evaly.evalyshop.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.VoucherDetailsAdapter;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.models.VoucherDetails;


public class VoucherListFragment extends Fragment {

    View view;
    RecyclerView voucherList,voucherList2;
    ArrayList<VoucherDetails> voucherDetails,voucherDetails2;
    VoucherDetailsAdapter voucherDetailsAdapter,voucherDetailsAdapter2;
    RequestQueue rq;
    static VoucherListFragment instance;
    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    View mViewBg;
    ViewDialog dialog;
    ImageView voucher1,voucher2,image,plus,minus;
    UserDetails userDetails;
    RelativeLayout rel1,rel2;
    TextView details,name,amount,total;
    EditText quantity;
    int voucherAmount=0;
    Button placeOrder;
    String voucherSlug="";

    LinearLayout noItem;


    Context context;

    int attempt = 0;
    String userAgent;


    public VoucherListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_voucher_list, container, false);
        voucherList=view.findViewById(R.id.voucher_list);
        voucherDetails=new ArrayList<>();
        dialog=new ViewDialog(getActivity());
        //dialog.showDialog();

        context = getContext();



        try {
            userAgent = WebSettings.getDefaultUserAgent(context);
        } catch (Exception e) {
            userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
        }

        layoutBottomSheet = view.findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        voucher1=view.findViewById(R.id.voucher1);
        voucher2=view.findViewById(R.id.voucher2);
        image=view.findViewById(R.id.image);
        rel1=view.findViewById(R.id.rel1);
        rel2=view.findViewById(R.id.rel2);
        mViewBg = view.findViewById(R.id.bg);
        plus=view.findViewById(R.id.plus);
        minus=view.findViewById(R.id.minus);
        quantity=view.findViewById(R.id.quantity);
        details=view.findViewById(R.id.details);
        name=view.findViewById(R.id.name);
        amount=view.findViewById(R.id.amount);
        voucherList2=view.findViewById(R.id.voucher_list2);
        total=view.findViewById(R.id.total);
        placeOrder=view.findViewById(R.id.place_order);
        voucherDetails2=new ArrayList<>();
        rq = Volley.newRequestQueue(context);
        userDetails=new UserDetails(context);
        noItem = view.findViewById(R.id.noItem);


        mViewBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                mViewBg.setVisibility(View.GONE);
            }
        });

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED: {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        mViewBg.setVisibility(View.VISIBLE);
                    }

                        case BottomSheetBehavior.STATE_EXPANDED: {
                        mViewBg.setVisibility(View.VISIBLE);
                    }

                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        mViewBg.setVisibility(View.GONE);
                        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                        try {
                            dialog.hideDialog();
                        }catch (Exception e){}


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

        voucherList.setLayoutManager(new LinearLayoutManager(context));
        voucherList2.setLayoutManager(new LinearLayoutManager(context));
        instance=this;
        voucherDetailsAdapter=new VoucherDetailsAdapter(context,voucherDetails);
        voucherDetailsAdapter2=new VoucherDetailsAdapter(context,voucherDetails2);
        voucherList.setAdapter(voucherDetailsAdapter);
        voucherList2.setAdapter(voucherDetailsAdapter2);


        checkIfVoucherIsAvailable();


        // voucherList2.setVisibility(View.GONE);

        rel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rel1.setBackgroundResource(R.drawable.box_selected);
                rel2.setBackgroundResource(R.drawable.border_box);
                //voucherList.setVisibility(View.VISIBLE);
                //voucherList2.setVisibility(View.GONE);
            }
        });

        rel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rel2.setBackgroundResource(R.drawable.box_selected);
                rel1.setBackgroundResource(R.drawable.border_box);
               // voucherList.setVisibility(View.GONE);
               // voucherList2.setVisibility(View.VISIBLE);
            }
        });

        //rel1.performClick();

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quan=Integer.parseInt(quantity.getText().toString());
                total.setText("৳ " + (quan*voucherAmount));
                quan+=1;
                quantity.setText(quan+"");
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quan=Integer.parseInt(quantity.getText().toString());
                quan-=1;
                if(quan<1){
                    quan=1;
                }
                total.setText("৳ " + (quan*voucherAmount));
                quantity.setText(quan+"");
            }
        });

        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    int quan=Integer.parseInt(s.toString());
                    total.setText("৳ " +(quan*voucherAmount));
                }catch(Exception e){

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOrder(voucherSlug);
            }
        });

        return view;
    }



    public void get150Offline(){

        String json = "{\"count\":6,\"next\":null,\"previous\":null,\"results\":[{\"id\":6,\"name\":\"1,50,000\",\"thumnail_image\":\"https://s3-ap-southeast-1.amazonaws.com/media.evaly.com.bd/media/2019-07-06_140341.375157blob\",\"slug\":\"150000\",\"value\":150.0,\"amount\":100000.0,\"status\":\"Active\"},{\"id\":5,\"name\":\"75,000 TK\",\"thumnail_image\":\"https://s3-ap-southeast-1.amazonaws.com/media.evaly.com.bd/media/2019-07-06_140305.007453blob\",\"slug\":\"75000-tk\",\"value\":150.0,\"amount\":50000.0,\"status\":\"Active\"},{\"id\":4,\"name\":\"4,500 TK\",\"thumnail_image\":\"https://s3-ap-southeast-1.amazonaws.com/media.evaly.com.bd/media/2019-07-06_140243.385154blob\",\"slug\":\"4500-tk\",\"value\":150.0,\"amount\":3000.0,\"status\":\"Active\"},{\"id\":3,\"name\":\"7,500 TK\",\"thumnail_image\":\"https://s3-ap-southeast-1.amazonaws.com/media.evaly.com.bd/media/2019-07-06_140215.873024blob\",\"slug\":\"7500-tk\",\"value\":150.0,\"amount\":5000.0,\"status\":\"Active\"},{\"id\":2,\"name\":\"15,000 TK\",\"thumnail_image\":\"https://s3-ap-southeast-1.amazonaws.com/media.evaly.com.bd/media/2019-07-06_140153.192431blob\",\"slug\":\"15000-tk\",\"value\":150.0,\"amount\":10000.0,\"status\":\"Active\"},{\"id\":1,\"name\":\"30,000 TK.\",\"thumnail_image\":\"https://s3-ap-southeast-1.amazonaws.com/media.evaly.com.bd/media/2019-07-06_140106.550497blob\",\"slug\":\"30000-tk\",\"value\":150.0,\"amount\":20000.0,\"status\":\"Active\"}]}";


        try {

            JSONObject response = new JSONObject(json);

            JSONArray jsonArray = response.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject ob = jsonArray.getJSONObject(i);
                voucherDetails.add(new VoucherDetails(ob.getString("name"),ob.getString("thumnail_image"),ob.getString("slug"),ob.getString("status"),
                        ob.getInt("id"),ob.getInt("value"),ob.getInt("amount")));
                voucherDetailsAdapter.notifyItemInserted(voucherDetails.size());
            }

            get200Offline();


        } catch (JSONException e) {
            e.printStackTrace();
            catchError();
        }


    }

    public void get200Offline(){

        String json = "{\"count\":4,\"next\":null,\"previous\":null,\"results\":[{\"id\":10,\"name\":\"5,000 TK\",\"thumnail_image\":\"https://s3-ap-southeast-1.amazonaws.com/media.evaly.com.bd/media/2019-07-06_140642.854505blob\",\"slug\":\"5000-tk\",\"value\":200.0,\"amount\":2500.0,\"status\":\"Active\"},{\"id\":9,\"name\":\"10,000 TK\",\"thumnail_image\":\"https://s3-ap-southeast-1.amazonaws.com/media.evaly.com.bd/media/2019-07-06_140610.442063blob\",\"slug\":\"10000-tk\",\"value\":200.0,\"amount\":5000.0,\"status\":\"Active\"},{\"id\":8,\"name\":\"20,000 TK\",\"thumnail_image\":\"https://s3-ap-southeast-1.amazonaws.com/media.evaly.com.bd/media/2019-07-06_140542.840231blob\",\"slug\":\"20000-tk\",\"value\":200.0,\"amount\":10000.0,\"status\":\"Active\"},{\"id\":7,\"name\":\"40,000 TK\",\"thumnail_image\":\"https://s3-ap-southeast-1.amazonaws.com/media.evaly.com.bd/media/2019-07-06_140517.148928blob\",\"slug\":\"40000-tk\",\"value\":200.0,\"amount\":20000.0,\"status\":\"Active\"}]}";


        try {

            JSONObject response = new JSONObject(json);

            JSONArray jsonArray = response.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject ob = jsonArray.getJSONObject(i);
                voucherDetails2.add(new VoucherDetails(ob.getString("name"),ob.getString("thumnail_image"),ob.getString("slug"),ob.getString("status"),
                        ob.getInt("id"),ob.getInt("value"),ob.getInt("amount")));
                voucherDetailsAdapter2.notifyItemInserted(voucherDetails2.size());
            }


        } catch (JSONException e) {
            e.printStackTrace();
            catchError();
        }


    }





    public void get150VoucherData(){
        String url="https://api-prod.evaly.com.bd/pay/voucher-variants/?voucher_slug=evaly-1919-150";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {
                        dialog.hideDialog();
                        Log.d("voucher_data",response.toString());
                        JSONArray jsonArray = response.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);
                            voucherDetails.add(new VoucherDetails(ob.getString("name"),ob.getString("thumnail_image"),ob.getString("slug"),ob.getString("status"),
                                    ob.getInt("id"),ob.getInt("value"),ob.getInt("amount")));
                            voucherDetailsAdapter.notifyItemInserted(voucherDetails.size());
                        }
                        // get200VoucherData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        catchError();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                catchError();
            }
        });
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(request);
    }

    public void get200VoucherData(){
        String url="https://api-prod.evaly.com.bd/pay/voucher-variants/?voucher_slug=evaly-1919-200";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {
                        dialog.hideDialog();
                        Log.d("voucher_data",response.toString());
                        JSONArray jsonArray = response.getJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject ob = jsonArray.getJSONObject(i);
                            voucherDetails2.add(new VoucherDetails(ob.getString("name"),ob.getString("thumnail_image"),ob.getString("slug"),ob.getString("status"),
                                    ob.getInt("id"),ob.getInt("value"),ob.getInt("amount")));
                            voucherDetailsAdapter2.notifyItemInserted(voucherDetails2.size());
                        }


                        if(voucherDetails.size() < 1 || voucherDetails2.size() < 1){

                            noItem.setVisibility(View.VISIBLE);

                        } else {


                            noItem.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        catchError();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                catchError();
            }
        });
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(request);
    }














    public void checkIfVoucherIsAvailable(){
        String url="https://api-prod.evaly.com.bd/pay/vouchers/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {

                        int count = response.getInt("count");


                        if(count < 1){

                            voucherList.setVisibility(View.GONE);
                            voucherList2.setVisibility(View.GONE);
                            noItem.setVisibility(View.VISIBLE);

                        } else {


                            noItem.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        catchError();

                        voucherList.setVisibility(View.GONE);
                        voucherList2.setVisibility(View.GONE);
                        noItem.setVisibility(View.VISIBLE);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error.printStackTrace();



                if(attempt < 3) {
                    attempt++;
                    checkIfVoucherIsAvailable();
                } else {

                    try {
                       // Toast.makeText(context, "Server error!", Toast.LENGTH_SHORT).show();

                        voucherList.setVisibility(View.GONE);
                        voucherList2.setVisibility(View.GONE);
                        noItem.setVisibility(View.VISIBLE);
                    } catch (Exception e){}

                }


            }
        });
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(request);
    }
















    public void toggleBottomSheet(String slug) {
        quantity.setText("1");
        voucherSlug=slug;
        getVoucherDetails(slug);

    }

    public static VoucherListFragment getInstance() {
        return instance;
    }

    public void catchError(){
        try{
            dialog.hideDialog();
        }catch(Exception e){

        }
        Toast.makeText(context, "Sorry something went wrong(server error). Please try again.", Toast.LENGTH_SHORT).show();
//        Intent intent=new Intent(context,MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
    }

    public void getVoucherDetails(String slug){
        dialog.showDialog();
        String url="https://api-prod.evaly.com.bd/pay/variants/details/"+slug+"/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {
                        dialog.hideDialog();
                        if(response.getBoolean("success")){
                            JSONObject ob=response.getJSONObject("data");
                            Log.d("voucher_details",ob.toString());
                            name.setText(ob.getString("name"));
                            details.setText(ob.getString("description"));
                            //ob.getString("thumnail_image");
                            //ob.getString("slug");
                            //ob.getString("status");
                            //ob.getInt("value");
                            voucherAmount=ob.getInt("amount");
                            amount.setText("৳ "+ob.getInt("amount"));
                            total.setText("৳ " + ob.getInt("amount"));
                            Glide.with(context).load(ob.getString("thumnail_image")).placeholder(R.drawable.ic_placeholder_small).into(image);

                            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            mViewBg.setVisibility(View.VISIBLE);

//                            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
//
//                            } else {
//                                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                                mViewBg.setVisibility(View.GONE);
//                            }
//




                        }else{
                            //Toast.makeText(context, "Sorry the voucher is not available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        catchError();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                catchError();
            }
        });
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(request);
    }

    public void createOrder(String slug){
        String url="https://api-prod.evaly.com.bd/pay/voucher-orders/";

        dialog.showDialog();

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
            parameters.put("voucher_variant_id",slug);
            int q=Integer.parseInt(quantity.getText().toString());
            String str[]=total.getText().toString().split(" ");
            int t=Integer.parseInt(str[1]);
            parameters.put("quantity",q);
            parameters.put("total_price",t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                dialog.hideDialog();

                try{
                    Log.d("voucher_buy",response.toString());
                    if(response.getBoolean("success")){
                        Toast.makeText(context, "Voucher order placed successfully", Toast.LENGTH_SHORT).show();
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


                dialog.hideDialog();
                Toast.makeText(context, "Server error, try again", Toast.LENGTH_SHORT).show();

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


        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(request);
    }





}
