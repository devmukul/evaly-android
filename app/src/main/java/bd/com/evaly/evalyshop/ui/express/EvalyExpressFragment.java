package bd.com.evaly.evalyshop.ui.express;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentEvalyExpressBinding;
import bd.com.evaly.evalyshop.models.shop.shopGroup.ShopsItem;

public class EvalyExpressFragment extends Fragment {

    private FragmentEvalyExpressBinding binding;
    private EvalyExpressAdapter adapter;
    private ArrayList<ShopsItem> itemList;
    private EvalyExpressViewModel viewModel;

    public EvalyExpressFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEvalyExpressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });

        viewModel = new ViewModelProvider(this).get(EvalyExpressViewModel.class);

        itemList = new ArrayList<>();
        adapter = new EvalyExpressAdapter(getContext(), itemList, NavHostFragment.findNavController(this));
        binding.recyclerView.setAdapter(adapter);

        viewModel.getLiveData().observe(getViewLifecycleOwner(), shopGroupResponse -> {

            itemList.clear();
            binding.progressBar.setVisibility(View.GONE);
            if (shopGroupResponse.getShops() == null) {
                binding.layoutNot.setVisibility(View.VISIBLE);
                return;
            }

            itemList.addAll(shopGroupResponse.getShops());
            adapter.notifyDataSetChanged();

            if (itemList.size() == 0) {
                binding.layoutNot.setVisibility(View.VISIBLE);
            } else
                binding.layoutNot.setVisibility(View.GONE);

        });

        binding.progressBar.setVisibility(View.VISIBLE);

        itemList.clear();
        viewModel.getShops("evaly-express", 1);

    }

}
