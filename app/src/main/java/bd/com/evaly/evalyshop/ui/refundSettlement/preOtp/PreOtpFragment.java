package bd.com.evaly.evalyshop.ui.refundSettlement.preOtp;

import org.jetbrains.annotations.NotNull;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentPreOtpRefundSettlementBinding;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;
import in.aabhasjindal.otptextview.OTPListener;

@AndroidEntryPoint
public class PreOtpFragment extends BaseFragment<FragmentPreOtpRefundSettlementBinding, PreOtpViewModel> {

    public PreOtpFragment() {
        super(PreOtpViewModel.class, R.layout.fragment_pre_otp_refund_settlement);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void liveEventsObservers() {
        viewModel.settlementResponse.observe(getViewLifecycleOwner(), refundSettlementResponse -> {
            if (refundSettlementResponse == null) {
                binding.otpView.showError();
            } else {
                // navigate
                navController.popBackStack();
                navController.navigate(R.id.refundSettlementFragment);
            }
        });

    }

    @Override
    protected void clickListeners() {
        binding.toolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
        binding.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(@NotNull String s) {
                viewModel.getSettlementAccounts(s);
            }
        });

    }

}
