package bd.com.evaly.evalyshop.ui.refundSettlement.modals;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomsheetEditMfsSettlementAccountBinding;
import bd.com.evaly.evalyshop.models.refundSettlement.Bank;
import bd.com.evaly.evalyshop.models.refundSettlement.RefundSettlementResponse;
import bd.com.evaly.evalyshop.models.refundSettlement.request.Account;
import bd.com.evaly.evalyshop.models.refundSettlement.request.BankAccountRequest;
import bd.com.evaly.evalyshop.models.refundSettlement.request.MFSAccountRequest;
import bd.com.evaly.evalyshop.ui.base.BaseBottomSheetFragment;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditSettlementAccountBottomSheet extends BaseBottomSheetFragment<BottomsheetEditMfsSettlementAccountBinding, EditSettlementAccountViewModel> {


    private MainViewModel mainViewModel;

    public EditSettlementAccountBottomSheet() {
        super(EditSettlementAccountViewModel.class, R.layout.bottomsheet_edit_mfs_settlement_account);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentInputBottomSheetDialog);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    }

    @Override
    protected void initViews() {
        binding.otpView.requestFocusOTP();
    }

    @Override
    protected void liveEventsObservers() {

        viewModel.responseLiveData.observe(getViewLifecycleOwner(), refundSettlementResponse -> {
            mainViewModel.refundSettlementUpdated.setValue(refundSettlementResponse);
            dismissAllowingStateLoss();
        });

        viewModel.typeLiveData.observe(getViewLifecycleOwner(), type -> {
            if (type.equalsIgnoreCase("bank")) {
                binding.llBankInfoHolder.setVisibility(View.VISIBLE);
                binding.mfsHolder.setVisibility(View.GONE);
                if (viewModel.accountsModel != null && viewModel.accountsModel.getBank() != null) {
                    Bank bank = viewModel.accountsModel.getBank();
                    binding.etBankName.setText(bank.getBankName());
                    binding.etAccountName.setText(bank.getAccountName());
                    binding.etAccountNumber.setText(bank.getAccountNumber());
                    binding.etBranch.setText(bank.getBranchName());
                    binding.etBranchRouting.setText(bank.getRoutingNumber());
                }
            } else {
                binding.llBankInfoHolder.setVisibility(View.GONE);
                binding.mfsHolder.setVisibility(View.VISIBLE);
                binding.title.setText(type + " Account");
                if (viewModel.accountsModel != null) {
                    if (type.equalsIgnoreCase("bkash"))
                        binding.etNumber.setText(viewModel.accountsModel.getBkash());
                    else if (type.equalsIgnoreCase("nagad"))
                        binding.etNumber.setText(viewModel.accountsModel.getNagad());
                }
            }
        });
    }

    @Override
    protected void clickListeners() {

        binding.submitBtn.setOnClickListener(view -> {
            String type = viewModel.typeLiveData.getValue();
            String otp = binding.otpView.getOtp();
            if (type == null) {
                ToastUtils.show("Please reload the page");
                return;
            }

            if (otp == null || otp.isEmpty() || otp.length() < 5) {
                ToastUtils.show("Please enter OTP");
                return;
            }

            if (type.equalsIgnoreCase("bank")) {

                String bankName = binding.etBankName.getText().toString().trim();
                String branchName = binding.etBranch.getText().toString().trim();
                String routingNumber = binding.etBranchRouting.getText().toString();
                String accountName = binding.etAccountName.getText().toString().trim();
                String accountNumber = binding.etAccountNumber.getText().toString().trim();

                String errorMessage = "";
                if (bankName.equals(""))
                    errorMessage = "Please enter bank name.";
                else if (branchName.equals(""))
                    errorMessage = "Please enter branch name";
                else if (routingNumber.equals(""))
                    errorMessage = "Please enter routing number.";
                else if (accountName.equals(""))
                    errorMessage = "Please enter account name.";
                else if (accountNumber.equals(""))
                    errorMessage = "Please enter account number.";

                if (!errorMessage.equals("")) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    return;
                }

                BankAccountRequest body = new BankAccountRequest();
                Account bankAccount = new Account();
                bankAccount.setAccountName(accountName);
                bankAccount.setBankName(bankName);
                bankAccount.setAccountNumber(accountNumber);
                bankAccount.setBranchName(branchName);
                bankAccount.setRoutingNumber(routingNumber);
                body.setAccount(bankAccount);
                body.setOtp(otp);

                viewModel.submitBankInfo(body);

            } else {
                String account = binding.etNumber.getText().toString().trim();
                if (account.equals("")) {
                    Toast.makeText(getContext(), "Please enter your " + type + " account number.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Utils.isValidNumber(account)) {
                    Toast.makeText(getContext(), "Please enter valid " + type + " account number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                MFSAccountRequest body = new MFSAccountRequest();
                body.setOtp(otp);
                body.setAccount(account);

                viewModel.submitMFSInfo(body);
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialogz -> {
            BottomSheetDialog dialog = (BottomSheetDialog) dialogz;
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (getContext() != null && bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
                BottomSheetBehavior.from(bottomSheet).setHideable(true);
            }
        });
        return bottomSheetDialog;
    }

}
