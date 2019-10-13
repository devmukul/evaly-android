package bd.com.evaly.evalyshop.activity.giftcard;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ProgressBar;
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
import bd.com.evaly.evalyshop.activity.EditProfileActivity;
import bd.com.evaly.evalyshop.activity.giftcard.adapter.GiftCardListAdapter;
import bd.com.evaly.evalyshop.activity.giftcard.adapter.GiftCardListPurchasedAdapter;
import bd.com.evaly.evalyshop.activity.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.activity.orderDetails.PayViaBkashActivity;
import bd.com.evaly.evalyshop.activity.orderDetails.PayViaCard;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListPurchasedItem;
import bd.com.evaly.evalyshop.util.KeyboardUtil;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;


public class GiftCardMyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{


    View view;
    RecyclerView recyclerView;
    ArrayList<GiftCardListPurchasedItem> itemList;
    GiftCardListPurchasedAdapter adapter;
    RequestQueue rq;
    static GiftCardMyFragment instance;
    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;
    ViewDialog dialog;
    ImageView image,plus,minus;
    UserDetails userDetails;
    TextView details,name,amount,total;
    EditText quantity;
    int voucherAmount=0;
    Button placeOrder;
    String giftCardInvoice="";

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
    ImageView bkash,cards;


    SwipeRefreshLayout swipeLayout;

    @Override
    public void onRefresh() {

        itemList.clear();
        adapter.notifyDataSetChanged();
        currentPage = 1;
        swipeLayout.setRefreshing(false);

        getGiftCardList();


    }

    public GiftCardMyFragment() {
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
        noItem = view.findViewById(R.id.noItem);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);

        instance = this;
        adapter = new GiftCardListPurchasedAdapter(context, itemList,1);
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





    public void catchError(){
        try{
            dialog.hideDialog();
        }catch(Exception e){}

        Toast.makeText(context, "Sorry something went wrong(server error). Please try again.", Toast.LENGTH_SHORT).show();

    }

    public void toggleBottomSheet(GiftCardListPurchasedItem item){

        giftCardInvoice = item.getInvoiceNo();


        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_gift_card_redeem);

        bottomSheetInternal = bottomSheetDialog.findViewById(android.support.design.R.id.design_bottom_sheet);
        bottomSheetInternal.setPadding(0, 0, 0, 0);

        new KeyboardUtil(getActivity(), bottomSheetInternal);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        layoutBottomSheet = bottomSheetDialog.findViewById(R.id.bottom_sheet);


        Button button = bottomSheetDialog.findViewById(R.id.button);
        EditText email = bottomSheetDialog.findViewById(R.id.email);
        TextView details = bottomSheetDialog.findViewById(R.id.details);


        email.setText(userDetails.getEmail());

        if (userDetails.getEmail().equals(""))
            button.setText("ADD EMAIL FIRST");
        else
            button.setText("REDEEM");


        button.setOnClickListener(view1 -> {


            if (userDetails.getEmail().equals("")) {
                context.startActivity(new Intent(context, EditProfileActivity.class));
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
            else
                redeemCard(giftCardInvoice);


        });



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

        String url = UrlUtils.DOMAIN+"cpn/gift-card-orders?show=gifts&page="+currentPage;

        Log.d("json url", url);


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(String) null,
                response -> {
                    try {

                        Log.d("json response", response.toString());

                        loading = true;
                        progressBar.setVisibility(View.GONE);

                        JSONArray jsonArray = response.getJSONArray("data");

                        if (currentPage == 1)
                            progressContainer.setVisibility(View.GONE);


                        if (jsonArray.length() == 0 && currentPage == 1){
                            noItem.setVisibility(View.VISIBLE);
                            TextView noText = view.findViewById(R.id.noText);
                            noText.setText("You have no gift cards");
                        } else {

                            recyclerView.setVisibility(View.VISIBLE);
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            Gson gson = new Gson();

                            GiftCardListPurchasedItem item = gson.fromJson(jsonArray.getJSONObject(i).toString(), GiftCardListPurchasedItem.class);
                            itemList.add(item);
                            adapter.notifyItemInserted(itemList.size());

                            try {



                                Log.d("json", "added");


                            }catch (Exception e){

                                Log.d("json exc", e.toString());

                            }

                        }

                        currentPage++;

                    } catch (JSONException e) {
                        e.printStackTrace();
                        catchError();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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


    public static GiftCardMyFragment getInstance() {
        return instance;
    }


    public void redeemCard(String invoice_no){

        String url= UrlUtils.DOMAIN + "cpn/gift-card-orders/gift-code/retrieve";

        dialog.showDialog();

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("invoice_no", invoice_no);

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
