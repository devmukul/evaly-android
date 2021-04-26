package bd.com.evaly.evalyshop.ui.order.orderRequest;

import androidx.recyclerview.widget.LinearLayoutManager;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentOrderRequestsBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.order.orderList.OrderListBaseViewModel;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderRequestFragment extends BaseFragment<FragmentOrderRequestsBinding, OrderListBaseViewModel> {

    private OrderRequestListController requestListController;
    private boolean isLoading = true;

    public OrderRequestFragment() {
        super(OrderListBaseViewModel.class, R.layout.fragment_order_requests);
    }

    @Override
    protected void initViews() {
        viewModel.loadFromApi();
    }

    @Override
    protected void clickListeners() {
        binding.toolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.liveData.observe(getViewLifecycleOwner(), orderRequestResponses -> {
            isLoading = false;
            requestListController.setList(orderRequestResponses);
            requestListController.setLoading(false);
            requestListController.requestModelBuild();
        });
    }

    @Override
    protected void setupRecycler() {

        requestListController = new OrderRequestListController();
        requestListController.setFilterDuplicates(true);
        requestListController.setClickListener(invoice -> {
            ToastUtils.show("You will get notification/SMS when your order is accepted, after that your can pay.", true);
        });
        binding.recycle.setAdapter(requestListController.getAdapter());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recycle.setLayoutManager(layoutManager);
        binding.recycle.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    viewModel.loadFromApi();
                    requestListController.setLoading(true);
                    requestListController.requestModelBuild();
                }
            }
        });
    }
}
