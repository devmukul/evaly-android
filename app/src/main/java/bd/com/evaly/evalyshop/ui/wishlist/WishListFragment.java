package bd.com.evaly.evalyshop.ui.wishlist;

import android.graphics.Color;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListDao;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListEntity;
import bd.com.evaly.evalyshop.ui.wishlist.adapter.WishListAdapter;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class WishListFragment extends Fragment {

    private ArrayList<WishListEntity> wishLists;
    private RecyclerView recyclerView;
    private WishListAdapter adapter;
    private LinearLayoutManager manager;
    private ViewDialog alert;
    private View view;
    private Toolbar mToolbar;
    private AppDatabase appDatabase;
    private WishListDao wishListDao;

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

        appDatabase = AppDatabase.getInstance(getContext());
        wishListDao = appDatabase.wishListDao();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wish_list, container, false);

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
                Executors.newSingleThreadExecutor().execute(() -> wishListDao.deleteBySlug(slug));
            }
        });

        recyclerView.setAdapter(adapter);

        LinearLayout empty = view.findViewById(R.id.empty);
        NestedScrollView scrollView = view.findViewById(R.id.scroller);

        wishListDao.getAllLive().observe(this, wishListEntities -> {

            wishLists.clear();
            wishLists.addAll(wishListEntities);
            adapter.notifyDataSetChanged();

            if (wishListEntities.size() > 0) {
                empty.setVisibility(View.GONE);
            } else {
                empty.setVisibility(View.VISIBLE);
                scrollView.setBackgroundColor(Color.WHITE);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }


}
