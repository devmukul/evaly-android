package bd.com.evaly.evalyshop.ui.payment.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentPaymentBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.order.orderDetails.OrderDetailsModel;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.util.Utils;

public class PaymentFragment extends Fragment {

    private FragmentPaymentBinding binding;
    private String invoice_no;
    private double total_amount = 0, paid_amount = 0.0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        getOrderDetails();

        binding.makePayment.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putString("invoice_no", invoice_no);
            bundle.putDouble("invoice_no", total_amount);
            bundle.putDouble("invoice_no", paid_amount);
            NavHostFragment.findNavController(this).navigate(R.id.paymentBottomSheet, bundle);
        });

        binding.viewDetails.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putString("orderID", invoice_no);
            NavHostFragment.findNavController(this).navigate(R.id.orderDetailsActivity, bundle);
        });

        binding.continueShopping.setOnClickListener(view1 -> {
            NavHostFragment.findNavController(this).navigate(R.id.homeFragment);
        });
    }


    public void getOrderDetails() {

        OrderApiHelper.getOrderDetails(CredentialManager.getToken(), invoice_no, new ResponseListenerAuth<OrderDetailsModel, String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataFetched(OrderDetailsModel response, int statusCode) {
                binding.totalPrice.setText(String.format("৳ %s", Utils.formatPrice(response.getTotal())));
                binding.paidAmount.setText(String.format("৳ %s", Utils.formatPrice(response.getPaidAmount())));
                binding.paidAmount.setText(String.format(Locale.ENGLISH, "৳ %s",
                        Utils.formatPrice((Double.parseDouble(response.getTotal())) - Math.round(Double.parseDouble(response.getPaidAmount())))));
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                Toast.makeText(getContext(), "Error occurred!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    getOrderDetails();
            }
        });
    }


}
