package bd.com.evaly.evalyshop.ui.refundSettlement.preOtp;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

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
        showKeyboard();
    }

    private void showKeyboard() {
        binding.otpView.postDelayed(() -> {
            binding.otpView.requestFocusOTP();
            if (getActivity() != null) {
                InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imgr != null)
                    imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }, 100);
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.settlementResponse.observe(getViewLifecycleOwner(), refundSettlementResponse -> {
            if (refundSettlementResponse == null) {
                binding.otpView.showError();
            } else {
                binding.otpView.clearFocus();
                navController.popBackStack();
                Bundle bundle = new Bundle();
                bundle.putSerializable("model", refundSettlementResponse);
                navController.navigate(R.id.refundSettlementFragment, bundle);
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
