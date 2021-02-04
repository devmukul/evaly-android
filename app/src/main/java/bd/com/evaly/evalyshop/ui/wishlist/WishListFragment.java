package bd.com.evaly.evalyshop.ui.wishlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListEntity;
import bd.com.evaly.evalyshop.ui.wishlist.adapter.WishListAdapter;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WishListFragment extends Fragment {

    private ArrayList<WishListEntity> wishLists;
    private RecyclerView recyclerView;
    private WishListAdapter adapter;
    private LinearLayoutManager manager;
    private ViewDialog alert;
    private View view;
    private Toolbar mToolbar;
    private WishListViewModel viewModel;


    public WishListFragment() {
        // Required empty public constructor
    }

    public static WishListFragment newInstance() {
        WishListFragment fragment = new WishListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wish_list, container, false);
        viewModel = new ViewModelProvider(this).get(WishListViewModel.class);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToolbar = view.findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });

        wishLists = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycle);
        alert = new ViewDialog(getActivity());


        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter = new WishListAdapter(wishLists, getContext(), new WishListAdapter.WishListListener() {
            @Override
            public void checkEmpty() {
            }

            @Override
            public void delete(String slug) {
                viewModel.deleteBySlug(slug);
            }
        });

        recyclerView.setAdapter(adapter);

        LinearLayout empty = view.findViewById(R.id.empty);
        NestedScrollView scrollView = view.findViewById(R.id.scroller);

        viewModel.liveData.observe(getViewLifecycleOwner(), wishListEntities -> {

            wishLists.clear();
            wishLists.addAll(wishListEntities);
            adapter.notifyDataSetChanged();

            if (wishListEntities.size() > 0) {
                empty.setVisibility(View.GONE);
            } else {
                empty.setVisibility(View.VISIBLE);
                scrollView.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }
}
