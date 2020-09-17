package bd.com.evaly.evalyshop.ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentCategoryBinding;
import bd.com.evaly.evalyshop.rest.ApiClient;
import bd.com.evaly.evalyshop.ui.category.controllers.RootCategoryController;
import bd.com.evaly.evalyshop.ui.category.controllers.SubCategoryController;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;
    private CategoryViewModel viewModel;
    private RootCategoryController categoryController;
    private SubCategoryController subCategoryController;

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

        if (categoryController == null)
            categoryController = new RootCategoryController();
        categoryController.setViewModel(viewModel);
        categoryController.setFilterDuplicates(true);

        if (subCategoryController == null)
            subCategoryController = new SubCategoryController();
        subCategoryController.setNavController(NavHostFragment.findNavController(this));
        subCategoryController.setViewModel(viewModel);
        subCategoryController.setLoadingMore(true);
        subCategoryController.setFilterDuplicates(true);

        binding.rvCategory.setAdapter(categoryController.getAdapter());
        binding.rvProducts.setAdapter(subCategoryController.getAdapter());

        viewModelLiveDataObservers();

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.rvProducts.setLayoutManager(layoutManager);
        layoutManager.setSpanSizeLookup(subCategoryController.getSpanSizeLookup());

        LinearLayoutManager layoutManagerCategory = new LinearLayoutManager(getContext());
        binding.rvCategory.setLayoutManager(layoutManagerCategory);

        if (viewModel.getRootCategory() == null)
            binding.selectedBrd.setVisibility(View.VISIBLE);
        else
            binding.selectedBrd.setVisibility(View.GONE);

    }

    private void inflateSearchMenu() {
//        binding.toolbar.inflateMenu(R.menu.search_btn);
//        MenuItem item = binding.toolbar.getMenu().findItem(R.id.action_search);
//
//        binding.toolbar.setOnMenuItemClickListener(item1 -> {
//            switch (item1.getItemId()) {
//                case R.id.action_search:
//                    Bundle bundle = new Bundle();
//                    NavHostFragment.findNavController(CategoryFragment.this).navigate(R.id.shopSearchActivity, bundle);
//            }
//            return true;
//        });

    }

    private void viewModelLiveDataObservers() {

        viewModel.getRootCategoryLiveData().observe(getViewLifecycleOwner(), tabsItems -> {
            categoryController.setLoadingMore(false, true);
            categoryController.addCategoryData(tabsItems, true);
        });

        viewModel.getSubCategoryLiveData().observe(getViewLifecycleOwner(), tabsItems -> {
            subCategoryController.setLoadingMore(false);
            subCategoryController.addData(tabsItems);
            subCategoryController.requestModelBuild();
        });

        viewModel.getSelectedCategoryLiveData().observe(getViewLifecycleOwner(), tabsItem -> {
            binding.selectedBrd.setVisibility(View.GONE);
            String categorySlug = tabsItem.getSlug();
            viewModel.setRootCategory(categorySlug);
            viewModel.loadSubCategories();
            subCategoryController.setCategoryTitle(tabsItem.getName());
            subCategoryController.clear();
            subCategoryController.setLoadingMore(true);
        });

        binding.allCategories.setOnClickListener(view -> {
            ApiClient.getUnsafeOkHttpClient().dispatcher().cancelAll();
            subCategoryController.clear();
            viewModel.setRootCategory(null);
            subCategoryController.setCategoryTitle(null);
            subCategoryController.setLoadingMore(true);
            binding.selectedBrd.setVisibility(View.VISIBLE);
            categoryController.deselectCategory();
            viewModel.loadTopCategories();
        });
    }
}
