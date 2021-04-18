package bd.com.evaly.evalyshop.ui.followedShops;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentFollowedShopsBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.followedShops.controller.FollowedShopsController;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.RecyclerSpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FollowedShopsFragment extends BaseFragment<FragmentFollowedShopsBinding, FollowedShopsViewModel> implements FollowedShopsController.ClickListener {

    private FollowedShopsController controller;
    private boolean isLoading = true;

    public FollowedShopsFragment() {
        super(FollowedShopsViewModel.class, R.layout.fragment_followed_shops);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void liveEventsObservers() {
        viewModel.liveList.observe(getViewLifecycleOwner(), shopListResponses -> {
            controller.setList(shopListResponses);
            controller.setLoading(false);
            controller.requestModelBuild();
            binding.progressBar.setVisibility(View.GONE);
            isLoading = false;
        });
    }

    @Override
    protected void clickListeners() {
        binding.toolbar.setNavigationOnClickListener(backPressClickListener);
    }

    @Override
    protected void setupRecycler() {
        if (controller == null)
            controller = new FollowedShopsController();
        controller.setClickListener(this);
        controller.setSpanCount(3);
        binding.recyclerView.setAdapter(controller.getAdapter());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setSpanSizeLookup(controller.getSpanSizeLookup());
        binding.recyclerView.setLayoutManager(layoutManager);
        int spacing = (int) Utils.convertDpToPixel(10, getActivity());
        RecyclerSpacingItemDecoration recyclerSpacingItemDecoration = new RecyclerSpacingItemDecoration(3, spacing, true);
        binding.recyclerView.addItemDecoration(recyclerSpacingItemDecoration);
        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    isLoading = true;
                    viewModel.loadShops();
                    controller.setLoading(true);
                }
            }
        });
    }

    @Override
    public void onClick(String name, String slug) {
        Bundle bundle = new Bundle();
        bundle.putString("shop_slug", slug);
        bundle.putString("shop_name", name);
        navController.navigate(R.id.shopFragment, bundle);
    }
}
