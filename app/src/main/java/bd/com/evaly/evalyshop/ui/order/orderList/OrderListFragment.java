package bd.com.evaly.evalyshop.ui.order.orderList;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.order.OrderListItem;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.ui.order.orderList.adapter.OrderAdapter;

public class OrderListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<OrderListItem> orders;
    private OrderAdapter adapter;
    private LinearLayout notOrdered;
    private int currentPage = 1, errorCounter = 0;
    private boolean loading = true;
    private ProgressBar progressBar;
    private NestedScrollView nestedSV;
    private View view;
    private Context context;
    private String statusType = "all";
    private View dummyView;

    public OrderListFragment() {
        // Required empty public constructor
    }

    public static OrderListFragment getInstance(String type) {

        OrderListFragment myFragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_list, container, false);
        context = getContext();
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("type"))
            statusType = bundle.getString("type");
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dummyView = view.findViewById(R.id.dummyView);
        recyclerView = view.findViewById(R.id.recycle);
        notOrdered = view.findViewById(R.id.not_order);
        progressBar = view.findViewById(R.id.progressBar);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        orders = new ArrayList<>();
        adapter = new OrderAdapter(context, orders);
        recyclerView.setAdapter(adapter);

        showProgressView();

        getOrderData(currentPage);

        nestedSV = view.findViewById(R.id.relativeLayout);

        nestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                showProgressView();
                getOrderData(++currentPage);
            }
        });

    }

    void showProgressView() {
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideProgressView() {
        progressBar.setVisibility(View.INVISIBLE);
    }


    public void getOrderData(int page) {

        if (page == 1)
            dummyView.setVisibility(View.VISIBLE);

        OrderApiHelper.getOrderList(CredentialManager.getToken(), page, statusType, new ResponseListenerAuth<CommonResultResponse<List<OrderListItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<OrderListItem>> response, int statusCode) {

                dummyView.setVisibility(View.GONE);

                hideProgressView();
                if (response != null) {
                    if (response.getCount() == 0 && page == 1) {
                        notOrdered.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        if (getContext() != null)
                            nestedSV.setBackgroundColor(nestedSV.getContext().getResources().getColor(R.color.fff));
                    } else {
                        notOrdered.setVisibility(View.GONE);
                        orders.addAll(response.getData());
                        adapter.notifyItemRangeInserted(orders.size() - response.getData().size(), response.getData().size());
                    }
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                hideProgressView();
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    getOrderData(page);
                else if (getActivity() != null) {
                    AppController.logout(getActivity());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }


}
