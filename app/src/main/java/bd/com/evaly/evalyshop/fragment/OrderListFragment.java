package bd.com.evaly.evalyshop.fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.OrderListActivity;
import bd.com.evaly.evalyshop.adapter.OrderAdapter;
import bd.com.evaly.evalyshop.util.Orders;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class OrderListFragment extends Fragment {


    UserDetails userDetails;
    RecyclerView recyclerView;
    ArrayList<Orders> orders;
    OrderAdapter adapter;
    LinearLayout notOrdered;
    ViewDialog alert;
    int currentPage = 1,errorCounter=0;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;

    ProgressBar progressBar;
    NestedScrollView nestedSV;

    String userAgent;


    View view;


    Context context;

    String statusType = "all";


    public OrderListFragment() {
        // Required empty public constructor
    }


    public OrderListFragment(String statusType) {
        this.statusType = statusType;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for context fragment
        view = inflater.inflate(R.layout.fragment_order_list, container, false);

        context = getContext();

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        try {
            userAgent = WebSettings.getDefaultUserAgent(context);
        } catch (Exception e) {
            userAgent = "Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/75.0.3770.101 Mobile Safari/537.36";
        }

        recyclerView=view.findViewById(R.id.recycle);
        notOrdered=view.findViewById(R.id.not_order);
        progressBar = view.findViewById(R.id.progressBar);
        LinearLayoutManager manager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        orders=new ArrayList<>();
        adapter=new OrderAdapter(context,orders);
        recyclerView.setAdapter(adapter);


        alert = new ViewDialog(getActivity());

        userDetails=new UserDetails(context);
        alert.showDialog();
        getOrderData(currentPage);


        nestedSV = view.findViewById(R.id.relativeLayout);

        if (nestedSV != null) {

            nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    String TAG = "nested_sync";
//
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {

                        try {

                            showProgressView();
                            getOrderData(++currentPage);

                        } catch (Exception e) {
                            Log.e("load more product", e.toString());
                        }


                    }
                }
            });
        }
        
        
    }



    void showProgressView() {
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideProgressView() {
        progressBar.setVisibility(View.INVISIBLE);
    }


    public void getOrderData(int currentPagez){


        String url;

        if(statusType.equals("all"))
            url = "https://api.evaly.com.bd/core/custom/orders/?page="+currentPagez;
        else
            url = "https://api.evaly.com.bd/core/custom/orders/?page="+currentPagez+"&order_status="+statusType;

        Log.d("json", url);

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("json", response.toString());
                errorCounter=0;

                alert.hideDialog();
                hideProgressView();

                try {
                    JSONArray jsonArray = response.getJSONArray("results");
                    if(jsonArray.length()==0){
                        notOrdered.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(R.drawable.ic_emptycart)
                                .apply(new RequestOptions().override(600, 600))
                                .into((ImageView) view.findViewById(R.id.noImage));
                        recyclerView.setVisibility(View.GONE);
                        nestedSV.setBackgroundColor(Color.WHITE);
                    }else{
                        notOrdered.setVisibility(View.GONE);
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject ob = jsonArray.getJSONObject(i);
                            orders.add(new Orders(
                                    ob.getString("date"),
                                    ob.getString("order_status"),
                                    ob.getString("invoice_no"),
                                    "mobile",
                                    ob.getString("payment_method"),
                                    ob.getString("payment_status")
                            ));

                            Log.d("order_response", "Inserted");

                        }

                        adapter.notifyDataSetChanged();

                    }
                } catch (Exception e) {
                    e.printStackTrace();


                    alert.hideDialog();
                    hideProgressView();
                    //getOrderData(currentPage);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());


//
//                alert.hideDialog();
//                hideProgressView();

                //Toast.makeText(OrderListActivity.this, "Server error, trying to fetch data again.", Toast.LENGTH_LONG).show();

                if(errorCounter==0 || orders.size() < 1){
                    if(errorCounter==5){
                        hideProgressView();
                        Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }else{
                        getOrderData(currentPage);
                        errorCounter++;
                    }
                }else{
                    progressBar.setVisibility(View.GONE);
                    alert.hideDialog();
                    hideProgressView();
                }

                //getOrderData(currentPage);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());
                headers.put("Host", "api.evaly.com.bd");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                //headers.put("Referer", "https://evaly.com.bd/order/my-orders?invoice_no=EVL668365508");
                //headers.put("User-Agent", userAgent);
                return headers;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(context);
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
}
