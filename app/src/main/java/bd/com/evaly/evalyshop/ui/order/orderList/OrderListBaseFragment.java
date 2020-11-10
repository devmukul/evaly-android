package bd.com.evaly.evalyshop.ui.order.orderList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentOrderListBaseBinding;
import bd.com.evaly.evalyshop.ui.order.orderList.adapter.OrderListTabAdapter;

public class OrderListBaseFragment extends Fragment {

    private FragmentOrderListBaseBinding binding;

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

    }

}
