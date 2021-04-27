package bd.com.evaly.evalyshop.ui.cart;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ActivityBlankBinding;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CartActivity extends BaseActivity<ActivityBlankBinding, BaseViewModel> {

    public CartActivity(){
        super(BaseViewModel.class, R.layout.activity_blank);
    }

    @Override
    protected void initViews() {
        FragmentManager fm = getSupportFragmentManager();
        CartFragment fragment = CartFragment.newInstance();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragmentHolder, fragment, "cart");
        ft.commit();
    }

    @Override
    protected void liveEventsObservers() {

    }

    @Override
    protected void clickListeners() {

    }
}
