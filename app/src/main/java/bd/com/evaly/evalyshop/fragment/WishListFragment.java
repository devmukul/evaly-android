package bd.com.evaly.evalyshop.fragment;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.WishListAdapter;
import bd.com.evaly.evalyshop.models.WishList;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.database.DbHelperWishList;

public class WishListFragment extends Fragment {


    private DbHelperWishList db;
    private ArrayList<WishList> wishLists;
    private RecyclerView recyclerView;
    private WishListAdapter adapter;
    private LinearLayoutManager manager;
    private ViewDialog alert;
    private View view;
    private Toolbar mToolbar;

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
        view =  inflater.inflate(R.layout.fragment_wish_list, container, false);

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

        wishLists=new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycle);
        alert = new ViewDialog(getActivity());


        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter = new WishListAdapter(wishLists,getContext(), () -> checkListEmpty());

        recyclerView.setAdapter(adapter);



    }


    @Override
    public void onResume(){

        super.onResume();
        getWishList();

    }


    public void getWishList(){

        wishLists.clear();
        adapter.notifyDataSetChanged();

        db = new DbHelperWishList(getContext());
        Cursor res = db.getData();

        while(res.moveToNext()){
            wishLists.add(new WishList(res.getString(0),res.getString(1),res.getString(2),res.getString(3),res.getString(4),res.getLong(5)));
            adapter.notifyItemInserted(wishLists.size());

        }


        checkListEmpty();



    }


    private void checkListEmpty(){


        LinearLayout empty = view.findViewById(R.id.empty);
        NestedScrollView scrollView = view.findViewById(R.id.scroller);
        Button button = view.findViewById(R.id.button);

        if (wishLists.size() > 0) {
            empty.setVisibility(View.GONE);
           // scrollView.setBackgroundColor(Color.parseColor("#fafafa"));
        } else {


            empty.setVisibility(View.VISIBLE);
            scrollView.setBackgroundColor(Color.WHITE);

        }

    }


    @Override
    public void onDestroy() {
        if(db!=null){
            db.close();
        }
        super.onDestroy();
    }
}
