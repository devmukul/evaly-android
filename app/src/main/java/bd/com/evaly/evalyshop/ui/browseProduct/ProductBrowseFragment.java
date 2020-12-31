package bd.com.evaly.evalyshop.ui.browseProduct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;

import bd.com.evaly.evalyshop.databinding.FragmentProductBrowseBinding;
import bd.com.evaly.evalyshop.ui.browseProduct.controller.ProductBrowseController;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProductBrowseFragment extends Fragment {

    private FragmentProductBrowseBinding binding;
    private BrowseProductViewModel viewModel;
    private ProductBrowseController controller;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BrowseProductViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProductBrowseBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupTabs();
        clickListeners();
        setupRecycler();
        liveEvents();
    }

    private void setupRecycler() {
        if (controller == null)
            controller = new ProductBrowseController();
        controller.setFilterDuplicates(true);
        binding.recyclerView.setAdapter(controller.getAdapter());
    }


    private void setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String type = tab.getText().toString();
                viewModel.setSelectedType(type);
                viewModel.loadFromApi();
                if (type.toLowerCase().contains("products")) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    binding.recyclerView.setLayoutManager(layoutManager);
                } else {
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
                    binding.recyclerView.setLayoutManager(gridLayoutManager);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void clickListeners() {

    }

    private void liveEvents() {

        viewModel.liveList.observe(getViewLifecycleOwner(), baseModels -> {
            controller.setList(baseModels);
            controller.setLoadingMore(false);
            controller.requestModelBuild();
        });
    }
}
