package bd.com.evaly.evalyshop.ui.wishlist;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.wishlist.WishList;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.wishlist.adapter.WishListAdapter;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.database.DbHelperWishList;

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
        adapter=new WishListAdapter(wishLists,this, () -> {

        });
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
            NestedScrollView scrollView = findViewById(R.id.scroller);
            scrollView.setBackgroundColor(Color.WHITE);

        }else{
            while(res.moveToNext()){
                wishLists.add(new WishList(res.getString(0),res.getString(1),res.getString(2),res.getString(3),res.getString(4),res.getLong(5)));
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
