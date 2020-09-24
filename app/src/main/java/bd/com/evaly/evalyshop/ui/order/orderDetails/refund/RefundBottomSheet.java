package bd.com.evaly.evalyshop.ui.order.orderDetails.refund;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.orhanobut.logger.Logger;

import java.util.HashMap;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetRefundRequestBinding;
import bd.com.evaly.evalyshop.databinding.ConfirmOtpViewBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsViewModel;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class RefundBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetRefundRequestBinding binding;
    private String invoice_no;
    private String order_status;
    private String payment_method;
    private String payment_status;
    private ViewDialog dialog;
    private boolean is_eligible = false;
    private int selectedOtp = 0;
    private Dialog otpAlert;
    private OrderDetailsViewModel orderDetailsViewModel;

    public static RefundBottomSheet newInstance(String invoiceNo, String orderStatus, String paymentMethod, String paymentStatus, boolean is_eligible) {

        RefundBottomSheet instance = new RefundBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putString("invoice_no", invoiceNo.toLowerCase());
        bundle.putString("order_status", orderStatus.toLowerCase());
        bundle.putString("payment_method", paymentMethod.toLowerCase());
        bundle.putString("payment_status", paymentStatus.toLowerCase());
        bundle.putBoolean("is_eligible", is_eligible);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetRefundRequestBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            invoice_no = getArguments().getString("invoice_no");
            order_status = getArguments().getString("order_status");
            payment_method = getArguments().getString("payment_method");
            payment_status = getArguments().getString("payment_status");
            is_eligible = getArguments().getBoolean("is_eligible");
        }

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderDetailsViewModel = new ViewModelProvider(requireActivity()).get(OrderDetailsViewModel.class);

        dialog = new ViewDialog(getActivity());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner_default);

        if (!is_eligible) {
            spinnerAdapter.add("Evaly Account");
        }

        if (Utils.canRefundToCard(payment_method))
            spinnerAdapter.add("Debit/Credit Card");
        else {
            spinnerAdapter.add("bKash");
            spinnerAdapter.add("Bank");
        }

        binding.spRefundOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String paymentType = spinnerAdapter.getItem(position);
                if (paymentType == null)
                    return;

                if (paymentType.equals("Evaly Account") || paymentType.equals("Debit/Credit Card")) {
                    binding.llBkashHolder.setVisibility(View.GONE);
                    binding.llBankInfoHolder.setVisibility(View.GONE);
                } else if (paymentType.equals("bKash")) {
                    binding.llBkashHolder.setVisibility(View.VISIBLE);
                    binding.llBankInfoHolder.setVisibility(View.GONE);
                } else if (paymentType.equals("Bank")) {
                    binding.llBkashHolder.setVisibility(View.GONE);
                    binding.llBankInfoHolder.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spRefundOption.setAdapter(spinnerAdapter);

        binding.submitBtn.setOnClickListener(v -> {

            int selectedPosition = binding.spRefundOption.getSelectedItemPosition();
            String paymentType = spinnerAdapter.getItem(selectedPosition);

            HashMap<String, String> body = new HashMap<>();
            body.put("invoice_no", invoice_no.toUpperCase());

            if (paymentType.equals("Evaly Account")) {
                body.put("refund_type", "Balance");
            } else if (paymentType.equals("bKash")) {
                String bkashNumber = binding.etbKashNumber.getText().toString().trim();
                if (bkashNumber.equals("")) {
                    Toast.makeText(getContext(), "Please enter bKash number.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Utils.isValidNumber(bkashNumber)) {
                    Toast.makeText(getContext(), "Please enter valid bKash number.", Toast.LENGTH_SHORT).show();
                    return;
                }
                body.put("refund_type", "Bkash");
                body.put("bkash_account", bkashNumber);
            } else if (paymentType.equals("Bank")) {
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
                body.put("refund_type", "Bank");
                body.put("bank_name", bankName);
                body.put("branch_name", branchName);
                body.put("branch_routing_number", routingNumber);
                body.put("account_name", accountName);
                body.put("account_number", accountNumber);
            } else if (paymentType.equals("Debit/Credit Card"))
                body.put("refund_type", "Card");


            requestRefund(body);
        });
    }


    private void deleteRefundTransaction() {

        OrderApiHelper.deleteRefundTransaction(invoice_no, new ResponseListenerAuth<CommonDataResponse<String>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<String> response, int statusCode) {
                if (response.getSuccess() && statusCode == 202) {
                    submitOtp();
                    dismissAllowingStateLoss();
                } else
                    ToastUtils.show(response.getMessage());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    deleteRefundTransaction();

            }
        });

    }

    private void requestRefund(HashMap<String, String> body) {

        dialog.showDialog();

        OrderApiHelper.requestRefund(CredentialManager.getToken(), body, new ResponseListenerAuth<CommonDataResponse<String>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<String> response, int statusCode) {
                Logger.d(statusCode);
                dialog.hideDialog();
                if (statusCode == 202) {
                    otpAlert = new Dialog(getActivity(), R.style.FullWidthTransparentDialog);
                    otpAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    otpAlert.setCancelable(true);
                    final ConfirmOtpViewBinding dialogConfirmDeliveryBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.confirm_otp_view, null, false);

                    dialogConfirmDeliveryBinding.verify.setOnClickListener(v -> {
                        if (dialogConfirmDeliveryBinding.code.getText().toString().trim().equals("")) {
                            ToastUtils.show("Please enter captcha code");
                        } else {
                            dialog.showDialog();
                            selectedOtp = Integer.parseInt(dialogConfirmDeliveryBinding.code.getText().toString());
                        }

                        if (is_eligible)
                            deleteRefundTransaction();
                        else {
                            dismissAllowingStateLoss();
                            submitOtp();
                        }
                    });

                    otpAlert.setContentView(dialogConfirmDeliveryBinding.getRoot());
                    otpAlert.show();
                } else {
                    if (getContext() != null) {
                        dialog.hideDialog();
                        Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                        if (response.getSuccess())
                            onSuccess();
                    }
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                Logger.d(errorBody);
                if (getContext() != null) {
                    Logger.d(errorBody);
                    dialog.hideDialog();
                    Toast.makeText(getContext(), "Couldn't request refund, try again later", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    requestRefund(body);
            }
        });
    }

    private void submitOtp() {
        HashMap<String, Integer> otpBody = new HashMap<>();
        otpBody.put("otp_token", selectedOtp);
        OrderApiHelper.requestRefundConfirmOTP(CredentialManager.getToken(), invoice_no.toUpperCase(), otpBody, new ResponseListenerAuth<CommonDataResponse<String>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<String> response, int statusCode) {
                if (response.getSuccess()) {
                    if (otpAlert != null && otpAlert.isShowing())
                        otpAlert.dismiss();
                    orderDetailsViewModel.setRefreshPage();
                }
                dialog.hideDialog();
                ToastUtils.show(response.getMessage());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialog.hideDialog();
                Toast.makeText(getActivity().getApplicationContext(), R.string.something_wrong, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }

    private void onSuccess() {
        if (getContext() != null) {
            if (isVisible()) {
                if (getActivity() != null) {
                    ((OrderDetailsActivity) getActivity()).updatePage();
                    dismiss();
                }
            }
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog dialogz = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = dialogz.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheet != null;
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
            BottomSheetBehavior.from(bottomSheet).setHideable(true);
        });
        return bottomSheetDialog;
    }


}
