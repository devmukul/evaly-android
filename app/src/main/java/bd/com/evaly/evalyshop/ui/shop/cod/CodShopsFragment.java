package bd.com.evaly.evalyshop.ui.shop.cod;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentCodShopsBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.shop.cod.controller.CodShopsController;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.RecyclerSpacingItemDecoration;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CodShopsFragment extends BaseFragment<FragmentCodShopsBinding, CodShopsViewModel> implements CodShopsController.ClickListener {

    private CodShopsController controller;
    private boolean isLoading = true;
    private TextWatcher textWatcher = new TextWatcher() {


        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            String search = binding.search.getText().toString().trim();
            viewModel.setSearch(search.isEmpty() ? null : search);
            viewModel.clearAndLoad();
            controller.setLoading(true);
            isLoading = true;

            if (search.length() > 0)
                binding.clear.setVisibility(View.VISIBLE);
            else
                binding.clear.setVisibility(View.GONE);
        }
    };

    public CodShopsFragment() {
        super(CodShopsViewModel.class, R.layout.fragment_cod_shops);
    }

    @Override
    protected void initViews() {

    }

    @Override
    public void onResume() {
        super.onResume();
        binding.search.addTextChangedListener(textWatcher);
        if (binding.search.getText().toString().trim().length() > 0)
            binding.clear.setVisibility(View.VISIBLE);
        else
            binding.clear.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.search.removeTextChangedListener(textWatcher);
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
        binding.clear.setOnClickListener(view -> {
            binding.search.setText("");
        });
    }

    @Override
    protected void setupRecycler() {
        if (controller == null)
            controller = new CodShopsController();
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
                    viewModel.loadCodShops();
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
