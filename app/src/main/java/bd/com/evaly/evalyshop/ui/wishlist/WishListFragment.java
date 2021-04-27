package bd.com.evaly.evalyshop.ui.wishlist;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListEntity;
import bd.com.evaly.evalyshop.databinding.FragmentWishListBinding;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.wishlist.adapter.WishListAdapter;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WishListFragment extends BaseFragment<FragmentWishListBinding, WishListViewModel> {

    private ArrayList<WishListEntity> wishLists;
    private WishListAdapter adapter;
    private LinearLayoutManager manager;
    private ViewDialog alert;

    public WishListFragment() {
        super(WishListViewModel.class, R.layout.fragment_wish_list);
    }

    @Override
    protected void initViews() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        wishLists = new ArrayList<>();
        alert = new ViewDialog(getActivity());
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.liveData.observe(getViewLifecycleOwner(), wishListEntities -> {
            wishLists.clear();
            wishLists.addAll(wishListEntities);
            adapter.notifyDataSetChanged();

            if (wishListEntities.size() > 0) {
                binding.empty.setVisibility(View.GONE);
            } else {
                binding.empty.setVisibility(View.VISIBLE);
                binding.scroller.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });
    }

    @Override
    protected void clickListeners() {
        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });
    }

    @Override
    protected void setupRecycler() {
        manager = new LinearLayoutManager(getContext());
        binding.recycle.setLayoutManager(manager);
        adapter = new WishListAdapter(wishLists, getContext(), new WishListAdapter.WishListListener() {
            @Override
            public void checkEmpty() {
            }

            @Override
            public void delete(String slug) {
                viewModel.deleteBySlug(slug);
            }
        });

        binding.recycle.setAdapter(adapter);

    }

}
