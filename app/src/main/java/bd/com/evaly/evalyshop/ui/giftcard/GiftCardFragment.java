package bd.com.evaly.evalyshop.ui.giftcard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.databinding.FragmentGiftcardsBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.pay.BalanceResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewPagerAdapter;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GiftCardFragment extends Fragment {

    @Inject
    ApiRepository apiRepository;
    private FragmentGiftcardsBinding binding;
    private BaseViewPagerAdapter pager;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGiftcardsBinding.inflate(inflater);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        pager = new BaseViewPagerAdapter(getParentFragmentManager());
        pager.addFragment(new GiftCardListFragment(), "STORE");

        if (!CredentialManager.getToken().equals("")) {
            pager.addFragment(new GiftCardMyFragment(), "MY GIFTS");
            pager.addFragment(new GiftCardPurchasedFragment(), "PURCHASED");
        }

        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.viewPager.setAdapter(pager);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        binding.viewPager.setOffscreenPageLimit(1);

        binding.balance.setVisibility(View.GONE);

        updateBalance();

        binding.toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());

    }


    public void updateBalance() {

        apiRepository.getBalance(CredentialManager.getToken(), CredentialManager.getUserName(), new ResponseListenerAuth<CommonDataResponse<BalanceResponse>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<BalanceResponse> response, int statusCode) {
                binding.balance.setText(String.format("Gift Card: ৳ %s", response.getData().getGiftCardBalance()));
                binding.balance.setVisibility(View.VISIBLE);
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
