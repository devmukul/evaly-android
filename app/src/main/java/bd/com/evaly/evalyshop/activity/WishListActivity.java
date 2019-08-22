package bd.com.evaly.evalyshop.activity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.adapter.WishListAdapter;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.database.DbHelperWishList;
import bd.com.evaly.evalyshop.util.WishList;

public class WishListActivity extends BaseActivity
{

    DbHelperWishList db;
    ArrayList<WishList> wishLists;
    RecyclerView recyclerView;
    WishListAdapter adapter;
    LinearLayoutManager manager;
    ViewDialog alert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        //getSupportActionBar().setElevation(0);

        getSupportActionBar().setElevation(4f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Wish List");


        wishLists=new ArrayList<>();
        recyclerView = findViewById(R.id.recycle);
        alert = new ViewDialog(this);


        manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter=new WishListAdapter(wishLists,this);
        recyclerView.setAdapter(adapter);

        getWishList();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getWishList(){
        db=new DbHelperWishList(this);
        Cursor res=db.getData();
        if(res.getCount()==0){

            LinearLayout empty = findViewById(R.id.empty);

            empty.setVisibility(View.VISIBLE);

            Button button = findViewById(R.id.button);

            NestedScrollView scrollView = findViewById(R.id.scroller);
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
    protected void onDestroy() {
        if(db!=null){
            db.close();
        }
        super.onDestroy();
    }
}
