package bd.com.evaly.evalyshop.ui.cart;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CartActivity extends BaseActivity {

    /*
        H. M. Tamim
        24/Jun/2019
     */


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        FragmentManager fm = getSupportFragmentManager();
        CartFragment fragment = CartFragment.newInstance();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragmentHolder,fragment,"cart");
        ft.commit();

    }
}
