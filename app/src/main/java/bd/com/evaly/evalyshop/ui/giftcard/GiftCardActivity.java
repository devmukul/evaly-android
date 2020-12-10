package bd.com.evaly.evalyshop.ui.giftcard;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.pay.BalanceResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.PaymentApiHelper;
import bd.com.evaly.evalyshop.ui.base.BaseViewPagerAdapter;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GiftCardActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private BaseViewPagerAdapter pager;
    private TextView balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giftcards);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.gift_cards);


        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewpager);
        balance = findViewById(R.id.balance);

        pager = new BaseViewPagerAdapter(getSupportFragmentManager());
        pager.addFragment(new GiftCardListFragment(), "STORE");

        if (!CredentialManager.getToken().equals("")) {
            pager.addFragment(new GiftCardMyFragment(), "MY GIFTS");
            pager.addFragment(new GiftCardPurchasedFragment(), "PURCHASED");
        }

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(pager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(1);

        balance.setVisibility(View.GONE);

        updateBalance();

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


    public void updateBalance() {

        PaymentApiHelper.getBalance(CredentialManager.getToken(), CredentialManager.getUserName(), new ResponseListenerAuth<CommonDataResponse<BalanceResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<BalanceResponse> response, int statusCode) {
                balance.setText(String.format("Gift Card: ৳ %s", response.getData().getGiftCardBalance()));
                balance.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

}
