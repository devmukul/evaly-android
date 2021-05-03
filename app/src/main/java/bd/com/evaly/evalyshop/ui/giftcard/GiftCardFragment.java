package bd.com.evaly.evalyshop.ui.giftcard;

import android.view.View;

import com.google.android.material.tabs.TabLayout;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.FragmentGiftcardsBinding;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.base.BaseViewPagerAdapter;
import bd.com.evaly.evalyshop.ui.giftcard.giftCardList.GiftCardListFragment;
import bd.com.evaly.evalyshop.ui.giftcard.myGiftCard.GiftCardMyFragment;
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

        if (bundle != null && bundle.containsKey("type")) {
            String type = bundle.getString("type");
            if (type == null)
                type = "";
            if (type.equals("my"))
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1));
            else if (type.equals("purchased"))
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(2));
        }
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
