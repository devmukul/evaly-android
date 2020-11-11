package bd.com.evaly.evalyshop.ui.order.orderList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayoutMediator;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentOrderListBaseBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.ui.order.orderList.adapter.OrderListTabAdapter;
import bd.com.evaly.evalyshop.ui.order.orderList.controller.OrderRequestListController;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderListBaseFragment extends Fragment {

    @Inject
    OrderListBaseViewModel viewModel;
    private FragmentOrderListBaseBinding binding;
    private OrderRequestListController requestListController;
    private boolean isLoading = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderListBaseBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OrderListTabAdapter pager = new OrderListTabAdapter(this);

        binding.pager.setAdapter(pager);

        pager.addFragment(OrderListFragment.getInstance("all"), getString(R.string.all));
        pager.addFragment(OrderListFragment.getInstance("pending"), getString(R.string.pending));
        pager.addFragment(OrderListFragment.getInstance("confirmed"), getString(R.string.confirmed));
        pager.addFragment(OrderListFragment.getInstance("processing"), getString(R.string.processing));
        pager.addFragment(OrderListFragment.getInstance("picked"), getString(R.string.picked));
        pager.addFragment(OrderListFragment.getInstance("shipped"), getString(R.string.shipped));
        pager.addFragment(OrderListFragment.getInstance("delivered"), getString(R.string.delivered));
        pager.addFragment(OrderListFragment.getInstance("cancel"), getString(R.string.cancelled));

        pager.notifyDataSetChanged();

        new TabLayoutMediator(binding.tabs, binding.pager,
                (tab, position) -> tab.setText(pager.getTitle(position))
        ).attach();

        binding.toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        setupBottomSheet();

    }

    private void setupBottomSheet() {
        BottomSheetBehavior behavior = BottomSheetBehavior.from(binding.orderRequestBottomSheet);
        behavior.setPeekHeight(280);

        if (requestListController == null)
            requestListController = new OrderRequestListController();
        requestListController.setFilterDuplicates(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    viewModel.loadFromApi();
                    requestListController.setLoading(true);
                    requestListController.requestModelBuild();
                }
            }
        });

        binding.recyclerView.setAdapter(requestListController.getAdapter());

        viewModel.liveData.observe(getViewLifecycleOwner(), orderRequestResponses -> {
            if (orderRequestResponses.size() > 0)
                binding.orderRequestBottomSheet.setVisibility(View.VISIBLE);
            else
                binding.orderRequestBottomSheet.setVisibility(View.GONE);
            requestListController.setLoading(false);
            requestListController.setList(orderRequestResponses);
            requestListController.requestModelBuild();
            binding.orderRequestCount.setText(orderRequestResponses.size() + "");
        });

    }

}
