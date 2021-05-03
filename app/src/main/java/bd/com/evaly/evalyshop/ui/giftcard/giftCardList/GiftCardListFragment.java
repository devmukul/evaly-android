package bd.com.evaly.evalyshop.ui.giftcard.giftCardList;


import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.FragmentGiftcardListBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.giftcard.GiftCardListItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.giftcard.adapter.GiftCardListAdapter;
import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class GiftCardListFragment extends BaseFragment<FragmentGiftcardListBinding, GiftCardListViewModel> implements SwipeRefreshLayout.OnRefreshListener, GiftCardListAdapter.ClickListener {

    @Inject
    PreferenceRepository preferenceRepository;
    @Inject
    ApiRepository apiRepository;
    @Inject
    FirebaseRemoteConfig firebaseRemoteConfig;

    private ArrayList<GiftCardListItem> itemList;
    private GiftCardListAdapter adapter;
    private boolean loading = true;

    public GiftCardListFragment() {
        super(GiftCardListViewModel.class, R.layout.fragment_giftcard_list);
    }

    @Override
    public void onRefresh() {
        viewModel.clear();
        itemList.clear();
        adapter.notifyDataSetChanged();
        binding.swipeContainer.setRefreshing(false);
        getGiftCardList(true);
    }

    @Override
    protected void initViews() {
        binding.swipeContainer.setOnRefreshListener(this);
        itemList = new ArrayList<>();
        getGiftCardList(false);
    }

    @Override
    protected void liveEventsObservers() {

        viewModel.liveList.observe(getViewLifecycleOwner(), list -> {
            loading = false;
            binding.progressBar.setVisibility(View.GONE);
            itemList.clear();
            itemList.addAll(list);
            adapter.notifyDataSetChanged();

            if (viewModel.getCurrentPage() <= 2)
                binding.progressContainer.setVisibility(View.GONE);

            if (list.size() == 0 && viewModel.getCurrentPage() <= 2)
                binding.noItem.setVisibility(View.VISIBLE);
        });

    }

    @Override
    protected void clickListeners() {

    }

    @Override
    protected void setupRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(manager);
        adapter = new GiftCardListAdapter(getContext(), itemList);
        adapter.setClickListener(this);
        binding.recyclerView.setAdapter(adapter);

        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(manager) {
            @Override
            public void loadMoreItem() {
                if (!loading) {
                    getGiftCardList(true);
                }
            }
        });
    }

    public void getGiftCardList(boolean loadFromApi) {

        loading = true;

        if (viewModel.getCurrentPage() == 1) {
            binding.progressContainer.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        } else {
            binding.progressContainer.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }

        if (loadFromApi)
            viewModel.getGiftCardList();
    }

    @Override
    public void onClick(GiftCardListItem item) {
        Bundle bundle = new Bundle();
        bundle.putString("slug", item.getSlug());
        navController.navigate(R.id.giftCardDetailsBottomSheet, bundle);
    }
}
