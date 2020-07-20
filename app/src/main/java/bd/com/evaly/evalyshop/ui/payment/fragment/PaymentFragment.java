package bd.com.evaly.evalyshop.ui.payment.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;

import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentPaymentBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.order.orderDetails.OrderDetailsModel;
import bd.com.evaly.evalyshop.rest.apiHelper.OrderApiHelper;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;

public class PaymentFragment extends Fragment {

    private FragmentPaymentBinding binding;
    private String invoice_no;
    private double total_amount = 0, paid_amount = 0.0;
    private String shopSlug = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            invoice_no = getArguments().getString("invoice_no");
            if (getArguments().containsKey("shop_slug"))
                shopSlug = getArguments().getString("shop_slug");
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

        Glide.with(this)
                .load(R.drawable.ic_successful_purchase_vector)
                .into(binding.banner);

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });

        binding.makePayment.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putString("invoice_no", invoice_no);
            bundle.putDouble("total_amount", total_amount);
            bundle.putDouble("paid_amount", paid_amount);
            bundle.putBoolean("is_food", shopSlug.contains("food"));
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

    @Override
    public void onResume() {
        super.onResume();
        getOrderDetails();
    }

    public void getOrderDetails() {

        OrderApiHelper.getOrderDetails(CredentialManager.getToken(), invoice_no, new ResponseListenerAuth<OrderDetailsModel, String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataFetched(OrderDetailsModel response, int statusCode) {
                total_amount = Double.parseDouble(response.getTotal());
                paid_amount = Double.parseDouble(response.getPaidAmount());
                binding.totalPrice.setText(String.format("৳ %s", Utils.formatPrice(response.getTotal())));
                binding.paidAmount.setText(String.format("৳ %s", Utils.formatPrice(response.getPaidAmount())));

                double dueAmount = Double.parseDouble(response.getTotal()) - Double.parseDouble(response.getPaidAmount());
                binding.duePrice.setText(String.format(Locale.ENGLISH, "৳ %s",
                        Utils.formatPrice(dueAmount)));

                if (dueAmount == 0)
                    binding.makePayment.setVisibility(View.GONE);
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                ToastUtils.show("Error occurred!");
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    getOrderDetails();
            }
        });
    }
}
