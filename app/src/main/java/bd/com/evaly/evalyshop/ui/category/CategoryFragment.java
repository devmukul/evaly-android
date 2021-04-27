package bd.com.evaly.evalyshop.ui.category;

import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentCategoryBinding;
import bd.com.evaly.evalyshop.rest.ApiClient;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.category.controllers.RootCategoryController;
import bd.com.evaly.evalyshop.ui.category.controllers.SubCategoryController;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoryFragment extends BaseFragment<FragmentCategoryBinding, CategoryViewModel> {

    private RootCategoryController categoryController;
    private SubCategoryController subCategoryController;

    public CategoryFragment() {
        super(CategoryViewModel.class, R.layout.fragment_category);
    }

    @Override
    protected void initViews() {
        binding.toolbar.setTitle("Categories");
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        inflateSearchMenu();

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

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        binding.rvProducts.setLayoutManager(layoutManager);
        layoutManager.setSpanSizeLookup(subCategoryController.getSpanSizeLookup());

        LinearLayoutManager layoutManagerCategory = new LinearLayoutManager(getContext());
        binding.rvCategory.setLayoutManager(layoutManagerCategory);

        if (viewModel.getRootCategory() == null)
            binding.selectedBrd.setVisibility(View.VISIBLE);
        else
            binding.selectedBrd.setVisibility(View.GONE);

    }


    @Override
    protected void clickListeners() {
        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });
    }


    @Override
    protected void liveEventsObservers() {

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
            binding.allCategories.setBackgroundColor(ContextCompat.getColor(binding.allCategories.getContext(), R.color.rootCategoryBg));
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
            binding.allCategories.setBackgroundColor(ContextCompat.getColor(binding.allCategories.getContext(), R.color.fff));
            categoryController.deselectCategory();
            viewModel.loadTopCategories();
        });
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
}
