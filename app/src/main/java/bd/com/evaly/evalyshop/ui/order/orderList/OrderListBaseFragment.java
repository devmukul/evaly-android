package bd.com.evaly.evalyshop.ui.order.orderList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.FragmentOrderListBaseBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.orderRequest.OrderRequestResponse;
import bd.com.evaly.evalyshop.ui.order.orderList.adapter.OrderListTabAdapter;
import bd.com.evaly.evalyshop.ui.order.orderRequest.OrderRequestListController;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderListBaseFragment extends Fragment {

    @Inject
    OrderListBaseViewModel viewModel;
    private FragmentOrderListBaseBinding binding;
    private OrderRequestListController requestListController;
    private boolean isLoading = true;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderListBaseBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(this);
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

        binding.orderRequestHolder.setOnClickListener(view1 -> {
            if (getActivity() != null && getContext() != null && navController != null)
                navController.navigate(R.id.orderRequestFragment);
        });

    }

    private void setupBottomSheet() {
        binding.orderRequestBottomSheet.bringToFront();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(binding.orderRequestBottomSheet);
        behavior.setPeekHeight(370);

        binding.orderRequestBottomSheet.setOnClickListener(view -> {
            behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        });

        if (requestListController == null)
            requestListController = new OrderRequestListController();
        requestListController.setFilterDuplicates(true);
        requestListController.setClickListener(invoice -> ToastUtils.show("You will get notification/SMS when your order is accepted, after that your can pay.", true));

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
            isLoading = false;
            if (orderRequestResponses.size() > 0) {
                binding.hotlistHot.setVisibility(View.VISIBLE);
                binding.hotlistHot.setText(orderRequestResponses.size() + "");
            } else
                binding.hotlistHot.setVisibility(View.GONE);

            List<OrderRequestResponse> pendingList = new ArrayList<>();
            for (OrderRequestResponse item : orderRequestResponses) {
                if (item.getStatus() != null && item.getStatus().equalsIgnoreCase("pending"))
                    pendingList.add(item);
            }

            if (pendingList.size() > 0) {
                binding.orderRequestBottomSheet.setVisibility(View.VISIBLE);
                binding.orderRequestCount.setText(pendingList.size() + "");
            } else {
                binding.orderRequestBottomSheet.setVisibility(View.GONE);
            }
            requestListController.setLoading(false);
            requestListController.setList(orderRequestResponses);
            requestListController.requestModelBuild();
        });

        viewModel.logoutLiveData.observe(getViewLifecycleOwner(), aVoid -> AppController.logout(getActivity()));

    }

}
