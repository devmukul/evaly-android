package bd.com.evaly.evalyshop.ui.payment.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Objects;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentPaymentBinding;
import bd.com.evaly.evalyshop.ui.order.PayViaBkashActivity;
import bd.com.evaly.evalyshop.ui.order.PayViaCard;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.ui.payment.bottomsheet.PaymentBottomSheetNavigator;
import bd.com.evaly.evalyshop.ui.payment.bottomsheet.PaymentBottomSheetViewModel;

public class PaymentFragment extends Fragment implements PaymentBottomSheetNavigator {

    private FragmentPaymentBinding binding;
    private String invoice_no;
    private double total_amount = 0, paid_amount = 0.0;
    private PaymentBottomSheetViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PaymentBottomSheetViewModel.class);
        if (getArguments() != null) {
            invoice_no = getArguments().getString("invoice_no");
            total_amount = getArguments().getDouble("total_amount");
            paid_amount = getArguments().getDouble("paid_amount");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPaymentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if ((total_amount % 1) == 0)
            binding.amountPay.setText(String.format("%d", (int) (total_amount - paid_amount)));
        else
            binding.amountPay.setText(String.format("%s", total_amount - paid_amount));

        // evaly pay
        binding.evalyPay.setOnClickListener(v -> {
            try {
                Double.parseDouble(binding.amountPay.getText().toString());
            } catch (Exception e) {
                Toast.makeText(getContext(), "Please enter valid amount.", Toast.LENGTH_SHORT).show();
                return;
            }
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
                Toast.makeText(getContext(), "Insufficient Evaly Balance (৳ " + userDetails.getBalance() + ")", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.evalyPay.setEnabled(false);
            viewModel.makePartialPayment(invoice_no, binding.amountPay.getText().toString());
        });

        binding.bkash.setOnClickListener(v -> {
            try {
                Double.parseDouble(binding.amountPay.getText().toString());
            } catch (Exception e) {
                Toast.makeText(getContext(), "Please enter valid amount.", Toast.LENGTH_SHORT).show();
                return;
            }
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
            // TODO

            Toast.makeText(getContext(), "Opening bKash payment gateway!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), PayViaBkashActivity.class);
            intent.putExtra("amount", binding.amountPay.getText().toString());
            intent.putExtra("invoice_no", invoice_no);
            intent.putExtra("context", "order_payment");
            Objects.requireNonNull(getActivity()).startActivityForResult(intent, 10002);

        });

        binding.card.setOnClickListener(v -> {
            try {
                Double.parseDouble(binding.amountPay.getText().toString());
            } catch (Exception e) {
                Toast.makeText(getContext(), "Please enter valid amount.", Toast.LENGTH_SHORT).show();
                return;
            }
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

        binding.continueShopping.setOnClickListener(view1 -> {
            NavHostFragment.findNavController(this).navigate(R.id.homeFragment);
        });
    }


    @Override
    public void onPaymentSuccess(String message) {

        binding.evalyPay.setEnabled(true);

        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            if (isVisible()) {
                if (getActivity() != null) {
                    ((OrderDetailsActivity) getActivity()).updatePage();
                    // TODO
                }
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
                if (isVisible() && !isRemoving() && !isDetached())
                    // TODO
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), PayViaCard.class);
                    intent.putExtra("url", url);
                    getActivity().startActivityForResult(intent, 10002);
                }
            }
        }
    }
}
