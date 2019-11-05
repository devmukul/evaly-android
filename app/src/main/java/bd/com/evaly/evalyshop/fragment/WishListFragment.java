package bd.com.evaly.evalyshop.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.adapter.WishListAdapter;
import bd.com.evaly.evalyshop.models.WishList;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.database.DbHelperWishList;

public class WishListFragment extends Fragment {


    DbHelperWishList db;
    ArrayList<WishList> wishLists;
    RecyclerView recyclerView;
    WishListAdapter adapter;
    LinearLayoutManager manager;
    ViewDialog alert;
    View view;

    MainActivity activity;

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

        activity = (MainActivity) getActivity();


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


        wishLists=new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycle);
        alert = new ViewDialog(getActivity());


        manager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter=new WishListAdapter(wishLists,getContext());
        recyclerView.setAdapter(adapter);




//        view.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//                    //((MainActivity) getActivity()).finish();
//                    if(getFragmentManager().getBackStackEntryCount() > 0) {
//                        getFragmentManager().popBackStack();
//                    }else {
//                        ((MainActivity) getActivity()).finish();
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });


    }


    @Override
    public void onResume(){

        super.onResume();
        getWishList();

    }


    public void getWishList(){
        db=new DbHelperWishList(getContext());
        Cursor res=db.getData();
        if(res.getCount()==0){

            LinearLayout empty = view.findViewById(R.id.empty);

            empty.setVisibility(View.VISIBLE);

            Button button = view.findViewById(R.id.button);

            NestedScrollView scrollView = view.findViewById(R.id.scroller);
            scrollView.setBackgroundColor(Color.WHITE);



        }else{
            while(res.moveToNext()){
                wishLists.add(new WishList(res.getString(0),res.getString(1),res.getString(2),res.getString(3),res.getString(4),res.getLong(5)));
//                Collections.sort(wishLists, new Comparator<WishList>(){
//                    public int compare(WishList o1, WishList o2) {
//                        return o2.getTime() > o1.getTime();
//                    }
//                });
                adapter.notifyItemInserted(wishLists.size());
            }
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
