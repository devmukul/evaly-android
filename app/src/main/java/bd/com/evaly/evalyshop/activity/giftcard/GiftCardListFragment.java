package bd.com.evaly.evalyshop.activity.giftcard;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.giftcard.adapter.GiftCardListAdapter;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;


public class GiftCardListFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    ArrayList<GiftCardListItem> itemList;
    GiftCardListAdapter adapter;
    RequestQueue rq;
    static GiftCardListFragment instance;
    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    View mViewBg;
    ViewDialog dialog;
    ImageView image,plus,minus;
    UserDetails userDetails;
    TextView details,name,amount,total;
    EditText quantity;
    int voucherAmount=0;
    Button placeOrder;
    String giftCardSlug="";

    LinearLayout noItem;
    Context context;
    
    BottomSheetDialog bottomSheetDialog;
    BottomSheetBehavior bottomSheetBehavior;
    View bottomSheetInternal;


    public GiftCardListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_giftcard_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        itemList=new ArrayList<>();
        dialog=new ViewDialog(getActivity());

        context = getContext();
        rq = Volley.newRequestQueue(context);
        userDetails=new UserDetails(context);


        initializeBottomSheet();


        noItem = view.findViewById(R.id.noItem);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        instance=this;
        adapter=new GiftCardListAdapter(context, itemList);
        recyclerView.setAdapter(adapter);




        getGiftCardList();

        return view;
    }


    public void initializeBottomSheet(){


        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_gift_cards);

        bottomSheetInternal = bottomSheetDialog.findViewById(android.support.design.R.id.design_bottom_sheet);
        bottomSheetInternal.setPadding(0, 0, 0, 0);

        new KeyboardUtil(getActivity(), bottomSheetInternal);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        layoutBottomSheet = bottomSheetDialog.findViewById(R.id.bottom_sheet);

        image = bottomSheetDialog.findViewById(R.id.image);
        plus = bottomSheetDialog.findViewById(R.id.plus);
        minus = bottomSheetDialog.findViewById(R.id.minus);
        quantity = bottomSheetDialog.findViewById(R.id.quantity);
        details = bottomSheetDialog.findViewById(R.id.details);
        name = bottomSheetDialog.findViewById(R.id.name);
        amount = bottomSheetDialog.findViewById(R.id.amount);
        total = bottomSheetDialog.findViewById(R.id.total);
        placeOrder= bottomSheetDialog.findViewById(R.id.place_order);


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
                createOrder(giftCardSlug);
            }
        });

    }


    public void catchError(){
        try{
            dialog.hideDialog();
        }catch(Exception e){}

        Toast.makeText(context, "Sorry something went wrong(server error). Please try again.", Toast.LENGTH_SHORT).show();

    }

    public void toggleBottomSheet(GiftCardListItem item){



        quantity.setText("1");
        giftCardSlug = item.getSlug();
        getGiftCardDetails(giftCardSlug);


    }

    public void getGiftCardList(){


        String url = UrlUtils.DOMAIN+"cpn/gift-cards/custom/list?page=1";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Gson gson = new Gson();
                            GiftCardListItem item = gson.fromJson(jsonArray.getJSONObject(i).toString(), GiftCardListItem.class);

                            itemList.add(item);
                            
                            adapter.notifyItemInserted(itemList.size());
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
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                if (!userDetails.getToken().equals(""))
                    headers.put("Authorization", "Bearer " + userDetails.getToken());

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


    public static GiftCardListFragment getInstance() {
        return instance;
    }


    public void getGiftCardDetails(String slug){

        if (userDetails.getToken().equals("")){

            Toast.makeText(context, "You need to login first", Toast.LENGTH_SHORT).show();
            return;

        }


        dialog.showDialog();

        initializeBottomSheet();


        String url= UrlUtils.DOMAIN+"cpn/gift-cards/retrieve/"+slug;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {
                        dialog.hideDialog();
                        if(response.getBoolean("success")){

                            Gson gson = new Gson();
                            GiftCardListItem item = gson.fromJson(response.getJSONObject("data").toString(), GiftCardListItem.class);


                            name.setText(item.getName());
                            details.setText(item.getDescription());
                            voucherAmount= item.getPrice();
                            amount.setText("৳ "+item.getPrice());
                            total.setText("৳ " + item.getPrice());


                            if (item.getImageUrl() == null)
                                Glide.with(context).load("https://beta.evaly.com.bd/static/images/gift-card.jpg").placeholder(R.drawable.ic_placeholder_small).into(image);
                            else
                                Glide.with(context).load(item.getImageUrl()).placeholder(R.drawable.ic_placeholder_small).into(image);



                            bottomSheetDialog.show();
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


                        }else{
                            Toast.makeText(context, "Sorry the gift card is not available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                if (!userDetails.getToken().equals(""))
                    headers.put("Authorization", "Bearer " + userDetails.getToken());

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
                return headers;
            }
        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(request);

    }


}
