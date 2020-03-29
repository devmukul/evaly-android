package bd.com.evaly.evalyshop.ui.shop.models;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ShopModelCategoryBinding;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.ui.shop.ShopViewModel;
import bd.com.evaly.evalyshop.ui.shop.adapter.ShopCategoryAdapter;

@EpoxyModelClass(layout = R.layout.shop_model_category)
public abstract class ShopCategoryModel extends EpoxyModelWithHolder<ShopCategoryModel.CategoryHolder> {

    @EpoxyAttribute
    public AppCompatActivity activity;
    @EpoxyAttribute
    public Fragment fragment;
    @EpoxyAttribute
    ShopViewModel viewModel;

    @Override
    public void bind(@NonNull CategoryHolder holder) {
        super.bind(holder);
    }


    class CategoryHolder extends EpoxyHolder {

        View itemView;

        private int currentPage = 1;
        private boolean isLoading = false;
        private ArrayList<TabsItem> itemList;
        private ShopCategoryAdapter adapter;
        private int pastVisiblesItems, visibleItemCount, totalItemCount;

        @Override
        protected void bindView(@NonNull View itemView) {
            this.itemView = itemView;

            ShopModelCategoryBinding binding = ShopModelCategoryBinding.bind(itemView);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(true);

            itemList = new ArrayList<>();
            adapter = new ShopCategoryAdapter(activity, itemList, viewModel);

            binding.recyclerView.setAdapter(adapter);

            binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dx > 0) {
                        GridLayoutManager mLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                        visibleItemCount = mLayoutManager.getChildCount();
                        totalItemCount = itemList.size();
                        pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                        if (!isLoading)
                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                isLoading = true;
                                viewModel.loadShopCategories();
                                currentPage++;
                            }
                    }
                }
            });

            viewModel.getShopCategoryListLiveData().observe(fragment.getViewLifecycleOwner(), categoryList -> {

                if (currentPage == 1 && categoryList.size() < 1)
                    binding.ct.setText(" ");
                else if (currentPage == 1 && categoryList.size() < 4) {
                    GridLayoutManager mLayoutManager = new GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false);
                    binding.recyclerView.setLayoutManager(mLayoutManager);
                } else
                    currentPage++;

                binding.shimmerHolder.shimmer.stopShimmer();
                binding.shimmerHolder.shimmer.setVisibility(View.GONE);
                isLoading = false;

                itemList.addAll(categoryList);
                adapter.notifyItemRangeInserted(itemList.size() - categoryList.size(), itemList.size());
            });


            viewModel.getSelectedCategoryLiveData().observe(fragment.getViewLifecycleOwner(), tabsItem -> {
                binding.categoryTitle.setText(tabsItem.getTitle());
                binding.resetBtn.setVisibility(View.VISIBLE);
            });

            binding.resetBtn.setOnClickListener(v -> {
                binding.categoryTitle.setText(R.string.all_products);
                binding.resetBtn.setVisibility(View.GONE);
                viewModel.setOnResetLiveData(true);
            });

            viewModel.loadShopCategories();

        }

    }
}
