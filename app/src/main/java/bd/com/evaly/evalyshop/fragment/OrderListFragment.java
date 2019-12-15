package bd.com.evaly.evalyshop.fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.OrderAdapter;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.models.Orders;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;

public class OrderListFragment extends Fragment {


    private UserDetails userDetails;
    private RecyclerView recyclerView;
    private ArrayList<Orders> orders;
    private OrderAdapter adapter;
    private LinearLayout notOrdered;
    private int currentPage = 1,errorCounter=0;
    private boolean loading = true;
    private ProgressBar progressBar;
    private NestedScrollView nestedSV;
    private View view;
    private Context context;
    private String statusType = "all";

    public static OrderListFragment getInstance(String type){

        OrderListFragment myFragment = new OrderListFragment();

        Bundle args = new Bundle();
        args.putString("type", type);
        myFragment.setArguments(args);

        return myFragment;

    }


    public OrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for context fragment
        view = inflater.inflate(R.layout.fragment_order_list, container, false);

        context = getContext();

        Bundle bundle = getArguments();

        if (bundle != null) {
            if (bundle.containsKey("type"))
                statusType = bundle.getString("type");
        }

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView=view.findViewById(R.id.recycle);
        notOrdered=view.findViewById(R.id.not_order);
        progressBar = view.findViewById(R.id.progressBar);
        LinearLayoutManager manager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        orders=new ArrayList<>();
        adapter=new OrderAdapter(context,orders);
        recyclerView.setAdapter(adapter);


        userDetails=new UserDetails(context);

        showProgressView();

        getOrderData(currentPage);


        nestedSV = view.findViewById(R.id.relativeLayout);

        if (nestedSV != null) {

            nestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

                 if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {

                            try {

                                showProgressView();
                                getOrderData(++currentPage);

                            } catch (Exception e) {
                                Log.e("load more product", e.toString());
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
            url = UrlUtils.BASE_URL+"custom/orders/?page="+currentPagez;
        else
            url = UrlUtils.BASE_URL+"custom/orders/?page="+currentPagez+"&order_status="+statusType;

        Log.d("json", url);

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters, response -> {
            Log.d("json", response.toString());
            errorCounter=0;

            hideProgressView();

            try {
                JSONArray jsonArray = response.getJSONArray("results");
                if(jsonArray.length()==0){
                    notOrdered.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(R.drawable.ic_emptycart_new)
                            .apply(new RequestOptions().override(700, 700))
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

                hideProgressView();
                //getOrderData(currentPage);

            }
        }, error -> {
            Log.e("onErrorResponse", error.toString());

            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                if (error.networkResponse.statusCode == 401) {

                    AuthApiHelper.refreshToken(getActivity(), new DataFetchingListener<retrofit2.Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(retrofit2.Response<JsonObject> response) {
                            getOrderData(currentPagez);
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });

                }
            }

            if(errorCounter==0 || orders.size() < 1){
                if(errorCounter==5){
                    hideProgressView();
                    Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();

                }else{
                    getOrderData(currentPage);
                    errorCounter++;
                }
            }else{
                progressBar.setVisibility(View.GONE);
                hideProgressView();
            }

            //getOrderData(currentPage);

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());

                return headers;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(context);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
}
