package bd.com.evaly.evalyshop.ui.order.orderDetails.payment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.PaymentBottomSheetFragmentBinding;
import bd.com.evaly.evalyshop.ui.order.PayViaBkashActivity;
import bd.com.evaly.evalyshop.ui.order.PayViaCard;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.util.UserDetails;

public class PaymentBottomSheet extends BottomSheetDialogFragment implements PaymentBottomSheetNavigator {

    private PaymentBottomSheetViewModel viewModel;
    private PaymentBottomSheetFragmentBinding binding;
    private String invoice_no;
    private double total_amount = 0, paid_amount = 0.0;
    private UserDetails userDetails;

    public static PaymentBottomSheet newInstance(String invoiceNo, double totalAmount, double paidAmount) {
        PaymentBottomSheet instance = new PaymentBottomSheet();

        Bundle bundle = new Bundle();
        bundle.putString("invoice_no", invoiceNo);
        bundle.putDouble("total_amount", totalAmount);
        bundle.putDouble("paid_amount", paidAmount);

        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.payment_bottom_sheet_fragment, container, false);
        binding.setViewModel(viewModel);

        if (getArguments() != null) {
            invoice_no = getArguments().getString("invoice_no");
            total_amount = getArguments().getDouble("total_amount");
            paid_amount = getArguments().getDouble("paid_amount");
        }

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(PaymentBottomSheetViewModel.class);
        viewModel.setNavigator(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
        userDetails = new UserDetails(getContext());
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

        if ((total_amount % 1) == 0)
            binding.amountPay.setText(String.format("%d", (int) (total_amount - paid_amount)));
        else
            binding.amountPay.setText(String.format("%s", total_amount - paid_amount));

        // evaly pay
        binding.evalyPay.setOnClickListener(v -> {

            if (binding.amountPay.getText().toString().trim().equals("")) {
                Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            } else if (Double.parseDouble(binding.amountPay.getText().toString()) > Double.parseDouble(binding.amountPay.getText().toString())) {
                Toast.makeText(getContext(), "Your entered amount is larger than the due amount", Toast.LENGTH_SHORT).show();
                return;
            } else if (binding.amountPay.getText().toString().equals("0")) {
                Toast.makeText(getContext(), "Amount can't be zero", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Double.parseDouble(binding.amountPay.getText().toString()) > Double.parseDouble(userDetails.getBalance())) {
                Toast.makeText(getContext(), "Insufficient Evaly balance (৳ " + userDetails.getBalance() + ")", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.evalyPay.setEnabled(false);

            viewModel.makePartialPayment(invoice_no, binding.amountPay.getText().toString());

        });

        binding.bkash.setOnClickListener(v -> {
            double amountToPay = total_amount - paid_amount;

            if (binding.amountPay.getText().toString().trim().equals("")) {
                Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            } else if (Double.parseDouble(binding.amountPay.getText().toString()) > amountToPay) {
                Toast.makeText(getContext(), "Your entered amount is larger than the due amount", Toast.LENGTH_SHORT).show();
                return;
            } else if (binding.amountPay.getText().toString().equals("0")) {
                Toast.makeText(getContext(), "Amount can't be zero", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.bkash.setEnabled(false);
            dismiss();
            Toast.makeText(getContext(), "Opening bKash payment gateway!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), PayViaBkashActivity.class);
            intent.putExtra("amount", binding.amountPay.getText().toString());
            intent.putExtra("invoice_no", invoice_no);
            intent.putExtra("context", "order_payment");

            Objects.requireNonNull(getActivity()).startActivityForResult(intent, 10002);

        });

        binding.card.setOnClickListener(v -> {

            double amountToPay = total_amount - paid_amount;

            if (binding.amountPay.getText().toString().trim().equals("")) {
                Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
                return;
            } else if (Double.parseDouble(binding.amountPay.getText().toString()) > amountToPay) {
                Toast.makeText(getContext(), "Your entered amount is larger than the due amount", Toast.LENGTH_SHORT).show();
                return;
            } else if (binding.amountPay.getText().toString().equals("0")) {
                Toast.makeText(getContext(), "Amount can't be zero", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.card.setEnabled(false);
            double amToPay = Double.parseDouble(binding.amountPay.getText().toString());

            Toast.makeText(getContext(), "Opening to payment gateway!", Toast.LENGTH_SHORT).show();
            viewModel.payViaCard(invoice_no, String.valueOf((int) amToPay));

        });
    }

    @Override
    public void onPaymentSuccess(String message) {

        binding.evalyPay.setEnabled(true);

        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            if (isVisible()) {
                if (getActivity() != null)
                    ((OrderDetailsActivity) getActivity()).updatePage();
                dismiss();
            }
        }
    }

    @Override
    public void onPaymentFailed(String message) {

        binding.evalyPay.setEnabled(true);
        if (getContext() != null)
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void payViaCard(String url) {

        if (getContext() != null) {
            if (url.equals(""))
                Toast.makeText(getContext(), "Unable to make payment!", Toast.LENGTH_SHORT).show();
            else {
                dismiss();
                Intent intent = new Intent(getActivity(), PayViaCard.class);
                intent.putExtra("url", url);
                Objects.requireNonNull(getActivity()).startActivityForResult(intent, 10002);
            }
        }
    }
}
