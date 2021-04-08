package bd.com.evaly.evalyshop.ui.refundSettlement;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentRefundSettlementBinding;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RefundSettlementFragment extends BaseFragment<FragmentRefundSettlementBinding, RefundSettlementViewModel> {

    private MainViewModel mainViewModel;

    public RefundSettlementFragment() {
        super(RefundSettlementViewModel.class, R.layout.fragment_refund_settlement);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
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
        mainViewModel.refundSettlementUpdated.observe(getViewLifecycleOwner(), accounts -> {
            if (accounts.getBkash() != null && !accounts.getBkash().isEmpty())
                viewModel.bkashAccount.setValue(accounts.getBkash());
            else if (accounts.getNagad() != null && !accounts.getNagad().isEmpty())
                viewModel.nagadAccount.setValue(accounts.getNagad());
            else if (accounts.getBank() != null && accounts.getBank().getAccountName() != null && !accounts.getBank().getAccountName().isEmpty())
                viewModel.bankAccount.setValue(accounts.getBank());
        });

        viewModel.bankAccount.observe(getViewLifecycleOwner(), bank -> {
            if (bank == null) {
                binding.bankName.setText("N/A");
                binding.accountName.setText("N/A");
                binding.accountNo.setText("N/A");
                binding.branchName.setText("N/A");
                binding.routingNo.setText("N/A");
            } else {

                if (bank.getBankName().isEmpty())
                    binding.bankName.setText("N/A");
                else
                    binding.bankName.setText(bank.getBankName());

                if (bank.getAccountName().isEmpty())
                    binding.accountName.setText("N/A");
                else
                    binding.accountName.setText(bank.getAccountName());

                if (bank.getAccountNumber().isEmpty())
                    binding.accountNo.setText("N/A");
                else
                    binding.accountNo.setText(bank.getAccountNumber());

                if (bank.getBranchName().isEmpty())
                    binding.branchName.setText("N/A");
                else
                    binding.branchName.setText(bank.getBranchName());

                if (bank.getRoutingNumber().isEmpty())
                    binding.routingNo.setText("N/A");
                else
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
        binding.toolbar.setNavigationOnClickListener(view -> {
            getActivity().onBackPressed();
        });

        binding.editBkash.setOnClickListener(view -> {
            openModal("bKash");
        });

        binding.editNagad.setOnClickListener(view -> {
            openModal("Nagad");
        });

        binding.editBank.setOnClickListener(view -> {
            openModal("Bank");
        });
    }

    private void openModal(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putSerializable("model", viewModel.accountsModel);
        navController.navigate(R.id.editSettlementAccountBottomSheet, bundle);
    }

}
