package bd.com.evaly.evalyshop.ui.order.orderList;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import bd.com.evaly.evalyshop.databinding.FragmentOrderListBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.ui.order.orderList.controller.OrderListController;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderListFragment extends Fragment implements OrderListController.ClickListener {

    private OrderListViewModel viewModel;
    private FragmentOrderListBinding binding;
    private OrderListController controller;
    private String statusType = "all";
    private boolean isLoading = true;

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
        binding = FragmentOrderListBinding.inflate(inflater);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("type"))
            statusType = bundle.getString("type");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(statusType, OrderListViewModel.class);
        initRecycler();
        liveEvents();
    }

    private void liveEvents() {
        viewModel.liveData.observe(getViewLifecycleOwner(), orderListItems -> {
            isLoading = false;
            controller.setList(orderListItems);
            controller.setLoading(false);
            controller.requestModelBuild();
        });

        viewModel.hideLoading.observe(getViewLifecycleOwner(), aBoolean -> {
            isLoading = false;
            controller.setLoading(false);
            controller.requestModelBuild();
        });
    }

    private void initRecycler() {
        if (controller == null)
            controller = new OrderListController();
        controller.setFilterDuplicates(true);
        controller.setClickListener(this);
        binding.recycle.setAdapter(controller.getAdapter());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recycle.setLayoutManager(layoutManager);
        binding.recycle.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    viewModel.getOrderData();
                    isLoading = true;
                    controller.setLoading(true);
                    controller.requestModelBuild();
                }
            }
        });
        controller.setClickListener(invoice -> ToastUtils.show("You will get notification/SMS when your order is accepted, after that your can pay.", true));
        controller.requestModelBuild();
    }

    @Override
    public void onClick(String invoice) {
        Intent intent = new Intent(getContext(), OrderDetailsActivity.class);
        intent.putExtra("orderID", invoice);
        getContext().startActivity(intent);
    }
}
