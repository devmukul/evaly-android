package bd.com.evaly.evalyshop.ui.refundSettlement;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentRefundSettlementBinding;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RefundSettlementFragment extends BaseFragment<FragmentRefundSettlementBinding, RefundSettlementViewModel> {

    public RefundSettlementFragment() {
        super(RefundSettlementViewModel.class, R.layout.fragment_refund_settlement);
    }

    @Override
    protected void initViews() {
        hideKeyboard();
    }

    private void hideKeyboard() {
        if (getActivity() != null) {
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && getView() != null)
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    @Override
    protected void liveEventsObservers() {

        viewModel.bankAccount.observe(getViewLifecycleOwner(), bank -> {
            if (bank == null) {
                binding.bankName.setText("N/A");
                binding.accountName.setText("N/A");
                binding.accountNo.setText("N/A");
                binding.branchName.setText("N/A");
                binding.routingNo.setText("N/A");
            } else {
                binding.bankName.setText(bank.getBankName());
                binding.accountName.setText(bank.getAccountName());
                binding.accountNo.setText(bank.getAccountNumber());
                binding.branchName.setText(bank.getBranchName());
                binding.routingNo.setText(bank.getRoutingNumber());
            }
        });

        viewModel.bkashAccount.observe(getViewLifecycleOwner(), s -> {
            if (s == null || s.isEmpty())
                binding.bkashAccount.setText("Not provided");
            else
                binding.bkashAccount.setText(s);
        });

        viewModel.nagadAccount.observe(getViewLifecycleOwner(), s -> {
            if (s == null || s.isEmpty())
                binding.nagadAccount.setText("Not provided");
            else
                binding.nagadAccount.setText(s);
        });
    }

    @Override
    protected void clickListeners() {
        binding.toolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
    }

}
