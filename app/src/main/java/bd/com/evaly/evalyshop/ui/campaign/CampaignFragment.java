package bd.com.evaly.evalyshop.ui.campaign;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentCampaignBinding;
import bd.com.evaly.evalyshop.listener.PaginationScrollListener;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.ui.campaign.adapter.CampaignAdapter;
import bd.com.evaly.evalyshop.util.ToastUtils;

public class CampaignFragment extends Fragment implements CampaignNavigator {

    private FragmentCampaignBinding binding;
    private CampaignViewModel viewModel;
    private View mRootView;
    private List<CampaignItem> items;
    private CampaignAdapter adapter;
    private NavController navController;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean isLoading = false;

    public static CampaignFragment newInstance() {
        final CampaignFragment fragment = new CampaignFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_campaign, container, false);
        binding.setViewModel(viewModel);
        mRootView = binding.getRoot();
        navController = NavHostFragment.findNavController(this);
        return mRootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CampaignViewModel.class);
        viewModel.setNavigator(this);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupToolbar();
        initRecycler();
        liveEventsObserver();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });
        binding.toolbar.inflateMenu(R.menu.menu_search);
        MenuItem searchItem = binding.toolbar.getMenu().findItem(R.id.action_search);
        SearchView searchView;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setQueryHint("Search campaigns...");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    items.clear();
                    viewModel.clear();
                    viewModel.setSearch(query);
                    viewModel.loadCampaigns();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });

            searchView.setOnCloseListener(() -> {
                items.clear();
                adapter.notifyDataSetChanged();
                viewModel.clear();
                viewModel.setSearch(null);
                viewModel.loadCampaigns();
                return false;
            });
        }
    }

    private void liveEventsObserver() {
        viewModel.getLiveList().observe(getViewLifecycleOwner(), list -> {
            isLoading = false;
            binding.progressBar.setVisibility(View.GONE);
            if (list.size() == 0) {
                binding.recyclerView.setVisibility(View.GONE);
                binding.layoutNot.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.layoutNot.setVisibility(View.GONE);
            }
            items.addAll(list);
            adapter.notifyItemRangeInserted(items.size() - list.size(), list.size());
        });
    }


    private void initRecycler() {
        items = new ArrayList<>();
        adapter = new CampaignAdapter(getContext(), items, item -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("model", item);
            navController.navigate(R.id.campaignShopFragment, bundle);
        });
        binding.recyclerView.setAdapter(adapter);

        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(manager) {
            @Override
            public void loadMoreItem() {
                if (!isLoading) {
                    // viewModel.loadCampaigns();
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onListLoaded(List<CampaignItem> list) {

    }

    @Override
    public void onListFailed(String errorBody, int errorCode) {
        if (getActivity() == null || getActivity().isFinishing() || binding == null)
            return;
        ToastUtils.show("Error occurred!");
        binding.progressBar.setVisibility(View.INVISIBLE);
    }
}

