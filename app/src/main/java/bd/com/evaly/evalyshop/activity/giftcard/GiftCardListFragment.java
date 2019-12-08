package bd.com.evaly.evalyshop.activity.giftcard;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.CartActivity;
import bd.com.evaly.evalyshop.activity.giftcard.adapter.GiftCardListAdapter;
import bd.com.evaly.evalyshop.activity.newsfeed.NewsfeedActivity;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;


public class GiftCardListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    View view;
    RecyclerView recyclerView;
    ArrayList<GiftCardListItem> itemList;
    GiftCardListAdapter adapter;
    RequestQueue rq;
    static GiftCardListFragment instance;

    ViewDialog dialog;
    ImageView image,plus,minus;
    UserDetails userDetails;
    TextView details,name,amount,total,cardValue;
    EditText quantity, phoneNumber;
    int voucherAmount=0;
    Button placeOrder;
    String giftCardSlug="";

    LinearLayout noItem;
    Context context;

    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetBehavior bottomSheetBehavior;
    View bottomSheetInternal;

    LinearLayout progressContainer;
    ProgressBar progressBar;
    int currentPage;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;



    SwipeRefreshLayout swipeLayout;

    @Override
    public void onRefresh() {

        itemList.clear();
        adapter.notifyDataSetChanged();
        currentPage = 1;
        swipeLayout.setRefreshing(false);

        getGiftCardList();


    }


    public GiftCardListFragment() {
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
        adapter=new GiftCardListAdapter(context, itemList);
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


        getGiftCardList();

        return view;
    }


    public void initializeBottomSheet(){


        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_gift_cards);

        bottomSheetInternal = bottomSheetDialog.findViewById(android.support.design.R.id.design_bottom_sheet);
        bottomSheetInternal.setPadding(0, 0, 0, 0);

        // new KeyboardUtil(getActivity(), bottomSheetInternal);
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
        cardValue = bottomSheetDialog.findViewById(R.id.cardValue);

        total = bottomSheetDialog.findViewById(R.id.total);
        placeOrder= bottomSheetDialog.findViewById(R.id.place_order);
        phoneNumber = bottomSheetDialog.findViewById(R.id.phone);



        TextView privacyText = bottomSheetDialog.findViewById(R.id.privacyText);

        privacyText.setText(Html.fromHtml("I agree to the <a href=\"https://evaly.com.bd/about/terms-conditions\">Terms & Conditions</a> and <a href=\"https://evaly.com.bd/about/purchasing-policy\">Purchasing Policy</a> of Evaly."));
        privacyText.setMovementMethod(LinkMovementMethod.getInstance());

        CheckBox checkBox = bottomSheetDialog.findViewById(R.id.checkBox);


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


                if (phoneNumber.getText().toString().equals(userDetails.getUserName())){
                    Toast.makeText(context,"You can't buy gift cards for yourself", Toast.LENGTH_LONG).show();
                    return;
                }

                if (phoneNumber.getText().toString().equals("")){
                    Toast.makeText(context,"Please enter a number", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!Utils.isValidNumber(phoneNumber.getText().toString())){
                    Toast.makeText(context, "Please enter a correct phone number", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (Integer.parseInt(quantity.getText().toString()) > 10){
                    Toast.makeText(context,"Quantity must be less than 10", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!checkBox.isChecked()){
                    Toast.makeText(context, "You must accept terms & conditions and purchasing policy to place an order.", Toast.LENGTH_LONG).show();
                    return;
                }


                createOrder(giftCardSlug);
            }
        });

    }


    public void catchError(){
        try{
            dialog.hideDialog();
        }catch(Exception e){}

        Toast.makeText(context, "Sorry something went wrong. Please try again.", Toast.LENGTH_SHORT).show();

    }

    public void toggleBottomSheet(GiftCardListItem item){



        quantity.setText("1");
        giftCardSlug = item.getSlug();
        getGiftCardDetails(giftCardSlug);


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

        String url = UrlUtils.DOMAIN+"cpn/gift-cards/custom/list?page="+currentPage;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {


                        Log.d("json gift", response.toString());

                        loading = true;
                        progressBar.setVisibility(View.GONE);

                        JSONArray jsonArray = response.getJSONArray("data");

                        if (currentPage == 1)
                            progressContainer.setVisibility(View.GONE);

                        if (jsonArray.length() == 0 && currentPage == 1){
                            noItem.setVisibility(View.VISIBLE);
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            Gson gson = new Gson();
                            try {

                                GiftCardListItem item = gson.fromJson(jsonArray.getJSONObject(i).toString(), GiftCardListItem.class);
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
                    if (error.networkResponse.statusCode == 401) {
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

                    }
                }

                error.printStackTrace();
                progressContainer.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
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
                            cardValue.setText("৳ " + item.getValue());

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

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    if (error.networkResponse.statusCode == 401){

                    AuthApiHelper.refreshToken(getActivity(), new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                            getGiftCardDetails(slug);
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

        String url= UrlUtils.DOMAIN + "cpn/gift-card-orders/place/";

        dialog.showDialog();

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("to", phoneNumber.getText().toString().trim());
            parameters.put("gift_card", slug);
            int q=Integer.parseInt(quantity.getText().toString());
            parameters.put("quantity",q);

        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                dialog.hideDialog();

                try{

                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.hide();

                    startActivity(getActivity().getIntent());

                    getActivity().finish();


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
                            createOrder(slug);
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });

                    return;

                }}

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
