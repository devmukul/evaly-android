package bd.com.evaly.evalyshop.ui.giftcard;

import android.view.View;

import com.google.android.material.tabs.TabLayout;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.FragmentGiftcardsBinding;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.base.BaseViewPagerAdapter;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GiftCardFragment extends BaseFragment<FragmentGiftcardsBinding, GiftCardViewModel> {

    @Inject
    PreferenceRepository preferenceRepository;

    private BaseViewPagerAdapter pager;

    public GiftCardFragment() {
        super(GiftCardViewModel.class, R.layout.fragment_giftcards);
    }

    @Override
    protected void initViews() {

        pager = new BaseViewPagerAdapter(getParentFragmentManager());
        pager.addFragment(new GiftCardListFragment(), "STORE");

        if (!preferenceRepository.getToken().equals("")) {
            pager.addFragment(new GiftCardMyFragment(), "MY GIFTS");
            pager.addFragment(new GiftCardPurchasedFragment(), "PURCHASED");
        }

        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.viewPager.setAdapter(pager);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        binding.viewPager.setOffscreenPageLimit(1);

        binding.balance.setVisibility(View.GONE);
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.balanceLiveData.observe(getViewLifecycleOwner(), balanceResponse -> {
            binding.balance.setText(String.format("Gift Card: ৳ %s", balanceResponse.getGiftCardBalance()));
            binding.balance.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void clickListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
    }

}
