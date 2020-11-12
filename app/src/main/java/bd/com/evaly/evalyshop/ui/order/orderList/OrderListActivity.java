package bd.com.evaly.evalyshop.ui.order.orderList;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.cart.CartFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderListActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        FragmentManager fm = getSupportFragmentManager();
        OrderListBaseFragment fragment = new OrderListBaseFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragmentHolder,fragment,"order list");
        ft.commit();

    }
}
