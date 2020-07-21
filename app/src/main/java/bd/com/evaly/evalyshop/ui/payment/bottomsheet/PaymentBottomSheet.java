package bd.com.evaly.evalyshop.ui.payment.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetPaymentBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.payment.PaymentMethodModel;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.ui.payment.bottomsheet.controller.PaymentMethodController;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.ViewDialog;

public class PaymentBottomSheet extends BottomSheetDialogFragment implements PaymentBottomSheetNavigator {

    private static PaymentOptionListener paymentOptionRedirceListener;
    private PaymentBottomSheetViewModel viewModel;
    private BottomSheetPaymentBinding binding;
    private String invoice_no, enteredAmount;
    private double total_amount = 0, paid_amount = 0.0;
    private AppCompatActivity activityInstance;
    private PaymentMethodController controller;
    private ViewDialog dialog;
    private boolean isFood = false;

    public static PaymentBottomSheet newInstance(String invoiceNo,
                                                 double totalAmount,
                                                 double paidAmount,
                                                 boolean isFood,
                                                 PaymentOptionListener paymentOptionListener) {
        PaymentBottomSheet instance = new PaymentBottomSheet();
        paymentOptionRedirceListener = paymentOptionListener;
        Bundle bundle = new Bundle();
        bundle.putString("invoice_no", invoiceNo);
        bundle.putDouble("total_amount", totalAmount);
        bundle.putDouble("paid_amount", paidAmount);
        bundle.putBoolean("is_food", isFood);
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_payment, container, false);
        // binding.setViewModel(viewModel);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PaymentBottomSheetViewModel.class);
        viewModel.setNavigator(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialog);

        if (getArguments() != null) {
            invoice_no = getArguments().getString("invoice_no");
            total_amount = getArguments().getDouble("total_amount");
            paid_amount = getArguments().getDouble("paid_amount");
            if (getArguments().containsKey("is_food"))
                isFood = getArguments().getBoolean("is_food");
        }
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


    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = new ViewDialog(getActivity());
        activityInstance = (AppCompatActivity) getActivity();
        controller = new PaymentMethodController();
        controller.setActivity(activityInstance);
        controller.setFocusListener(object -> {
            binding.amountPay.clearFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(binding.amountPay.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        });

        binding.amountPay.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.amountPay.clearFocus();
            }
            return false;
        });

        binding.recyclerView.setAdapter(controller.getAdapter());
        setPaymentMethodViewData();
        setAmountText();

        binding.confirm.setOnClickListener(view1 -> {
            onPaymentConfirmOnClick();
        });

    }


    private void onPaymentConfirmOnClick() {
        double amountToPay = total_amount - paid_amount;
        enteredAmount = binding.amountPay.getText().toString().trim();
        double enteredAmountDouble = 0;

        try {
            enteredAmountDouble = Double.parseDouble(enteredAmount);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Please enter valid amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredAmount.equals("")) {
            Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        } else if (enteredAmountDouble > amountToPay) {
            Toast.makeText(getContext(), "Your entered amount is larger than the due amount", Toast.LENGTH_SHORT).show();
            return;
        } else if (binding.amountPay.getText().toString().equals("0")) {
            Toast.makeText(getContext(), "Amount can't be zero", Toast.LENGTH_SHORT).show();
            return;
        }

        PaymentMethodModel method = controller.getSelectedMethod();

        if (method == null) {
            ToastUtils.show("Please select a payment method");
            return;
        }
        if (method.getName().equals("Evaly Balance")) {
            if (Double.parseDouble(binding.amountPay.getText().toString()) > CredentialManager.getBalance()) {
                Toast.makeText(getContext(), "Insufficient Evaly Balance (৳ " + CredentialManager.getBalance() + ")", Toast.LENGTH_SHORT).show();
                dialog.hideDialog();
                return;
            }
            dialog.showDialog();
            viewModel.makePartialPayment(invoice_no, binding.amountPay.getText().toString());
        } else if (method.getName().equalsIgnoreCase("bKash")) {
            dismiss();
            Toast.makeText(getContext(), "Opening bKash payment gateway!", Toast.LENGTH_SHORT).show();
            paymentOptionRedirceListener.onPaymentRedirect(BuildConfig.BKASH_URL, enteredAmount, invoice_no);
        } else if (method.getName().equalsIgnoreCase("Cards")) {
            Toast.makeText(getContext(), "Opening to payment gateway!", Toast.LENGTH_SHORT).show();
            viewModel.payViaCard(invoice_no, enteredAmount);
        } else {
            ToastUtils.show("Payment is not possible, please try again");
        }
    }

    private void setAmountText() {
        if ((total_amount % 1) == 0)
            binding.amountPay.setText(String.format("%d", (int) (total_amount - paid_amount)));
        else
            binding.amountPay.setText(String.format("%s", total_amount - paid_amount));

    }

    private void setPaymentMethodViewData() {
        List<PaymentMethodModel> methodList = new ArrayList<>();

        String balanceText = "You can pay up to 60% of the order amount using Evaly Balance.";
        if (isFood)
            balanceText = "For express food shops, you can pay up to 100% of the order amount using Evaly Balance.";

        methodList.add(new PaymentMethodModel(
                "Evaly Balance",
                balanceText,
                R.drawable.payment_icon_evaly,
                false));

        methodList.add(new PaymentMethodModel(
                "bKash",
                "Pay from your bKash account using \nbKash payment gateway.", R.drawable.payment_bkash_square,
                false));

        methodList.add(new PaymentMethodModel(
                "Cards",
                "Pay from your debit/visa/master card using \nSSL payment gateway.", R.drawable.payment_cards,
                false));

        controller.loadData(methodList, true);
    }

    @Override
    public void onPaymentSuccess(String message) {
        if (getActivity() != null && !getActivity().isDestroyed() && !getActivity().isFinishing()) {
            dialog.hideDialog();
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            if (isVisible()) {
                if (activityInstance != null && activityInstance instanceof OrderDetailsActivity && !activityInstance.isDestroyed() && !activityInstance.isFinishing())
                    ((OrderDetailsActivity) getActivity()).updatePage();
                else {
                    NavHostFragment.findNavController(this).popBackStack(R.id.paymentFragment, true);
                    Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
                    intent.putExtra("orderID", invoice_no);
                    startActivity(intent);
                }
                dismissAllowingStateLoss();
            }
        }
    }

    @Override
    public void onPaymentFailed(String message) {
        dialog.hideDialog();
        ToastUtils.show(message);
    }

    @Override
    public void payViaCard(String url) {

        if (getContext() != null) {
            if (url == null || url.equals(""))
                Toast.makeText(getContext(), "Unable to make payment!", Toast.LENGTH_SHORT).show();
            else {
                if (isVisible() && !isRemoving() && !isDetached())
                    dismiss();
                if (getActivity() != null) {
                    paymentOptionRedirceListener.onPaymentRedirect(url, enteredAmount, invoice_no);
                }
            }
        }
    }

    public interface PaymentOptionListener {
        void onPaymentRedirect(String url, String amount, String invoiceNo);
    }


}
