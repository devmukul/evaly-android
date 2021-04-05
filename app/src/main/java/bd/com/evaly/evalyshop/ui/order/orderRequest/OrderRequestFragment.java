package bd.com.evaly.evalyshop.ui.order.orderRequest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import bd.com.evaly.evalyshop.databinding.FragmentOrderRequestsBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.ui.order.orderList.OrderListBaseViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderRequestFragment extends Fragment {

    private OrderListBaseViewModel viewModel;
    private FragmentOrderRequestsBinding binding;
    private OrderRequestListController requestListController;
    private boolean isLoading = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderRequestsBinding.inflate(inflater);
        viewModel = new ViewModelProvider(this).get(OrderListBaseViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupAdapter();
        liveEvents();
        clickListeners();
        viewModel.loadFromApi();
    }

    private void clickListeners() {
        binding.toolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
    }

    private void liveEvents() {
        viewModel.liveData.observe(getViewLifecycleOwner(), orderRequestResponses -> {
            requestListController.setList(orderRequestResponses);
            requestListController.setLoading(false);
            requestListController.requestModelBuild();
        });
    }

    private void setupAdapter() {
        requestListController = new OrderRequestListController();
        requestListController.setFilterDuplicates(true);
        requestListController.setClickListener(invoice -> {

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
