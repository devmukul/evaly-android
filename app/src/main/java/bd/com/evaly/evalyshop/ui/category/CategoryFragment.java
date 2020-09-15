package bd.com.evaly.evalyshop.ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentCategoryBinding;
import bd.com.evaly.evalyshop.rest.ApiClient;
import bd.com.evaly.evalyshop.ui.category.controllers.RootCategoryController;
import bd.com.evaly.evalyshop.ui.category.controllers.SubCategoryController;
import bd.com.evaly.evalyshop.ui.shop.ShopViewModelFactory;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;
    private CategoryViewModel viewModel;
    private RootCategoryController categoryController;
    private SubCategoryController productController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.setTitle("Categories");
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });
        inflateSearchMenu();
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        categoryController = new RootCategoryController();
        categoryController.setViewModel(viewModel);
        categoryController.setFilterDuplicates(true);

        productController = new SubCategoryController();
        productController.setViewModel(viewModel);
        productController.setLoadingMore(true);
        productController.setFilterDuplicates(true);

        binding.rvCategory.setAdapter(categoryController.getAdapter());
        binding.rvProducts.setAdapter(productController.getAdapter());

        viewModelLiveDataObservers();

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.rvProducts.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManagerCategory = new LinearLayoutManager(getContext());
        binding.rvCategory.setLayoutManager(layoutManagerCategory);

    }

    private void inflateSearchMenu() {
        binding.toolbar.inflateMenu(R.menu.search_btn);
        MenuItem item = binding.toolbar.getMenu().findItem(R.id.action_search);

        binding.toolbar.setOnMenuItemClickListener(item1 -> {
            switch (item1.getItemId()) {
                case R.id.action_search:
                    Bundle bundle = new Bundle();
                    NavHostFragment.findNavController(CategoryFragment.this).navigate(R.id.shopSearchActivity, bundle);
            }
            return true;
        });

    }

    private void viewModelLiveDataObservers() {

        viewModel.getSubCategoryLiveData().observe(getViewLifecycleOwner(), tabsItems -> {
            categoryController.setLoadingMore(false, true);
            categoryController.addCategoryData(tabsItems, true);
        });

        viewModel.getSelectedCategoryLiveData().observe(getViewLifecycleOwner(), tabsItem -> {
            binding.selectedBrd.setVisibility(View.GONE);
            String categorySlug = tabsItem.getSlug();
            viewModel.setRootCategory(categorySlug);
            productController.setCategoryTitle(tabsItem.getName());
            productController.clear();
            productController.setLoadingMore(true);
        });

        binding.allCategories.setOnClickListener(view -> {
            ApiClient.getUnsafeOkHttpClient().dispatcher().cancelAll();
            productController.clear();
            viewModel.setRootCategory(null);
            productController.setCategoryTitle(null);
            productController.setLoadingMore(true);
            binding.selectedBrd.setVisibility(View.VISIBLE);
            categoryController.deselectCategory();
        });


    }
}
