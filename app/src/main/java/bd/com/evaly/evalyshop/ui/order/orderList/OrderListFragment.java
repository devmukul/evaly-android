package bd.com.evaly.evalyshop.ui.order.orderList;


import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentOrderListBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.ui.order.orderList.controller.OrderListController;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderListFragment extends BaseFragment<FragmentOrderListBinding, OrderListViewModel> implements OrderListController.ClickListener {

    private OrderListController controller;
    private String statusType = "all";
    private boolean isLoading = true;

    public OrderListFragment() {
        super(OrderListViewModel.class, R.layout.fragment_order_list);
    }

    public static OrderListFragment getInstance(String type) {
        OrderListFragment myFragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    protected void initViews() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("type"))
            statusType = bundle.getString("type");
    }

    @Override
    protected void clickListeners() {

    }

    @Override
    protected void liveEventsObservers() {
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

    @Override
    protected void setupRecycler() {

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
        controller.requestModelBuild();
    }

    @Override
    public void onClick(String invoice) {
        Intent intent = new Intent(getContext(), OrderDetailsActivity.class);
        intent.putExtra("orderID", invoice);
        getContext().startActivity(intent);
    }
}
