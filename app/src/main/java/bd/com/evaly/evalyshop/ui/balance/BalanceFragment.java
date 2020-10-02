package bd.com.evaly.evalyshop.ui.balance;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BalanceFragmentBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;

public class BalanceFragment extends BottomSheetDialogFragment {

    private BalanceViewModel mViewModel;
    private BalanceFragmentBinding binding;

    public static BalanceFragment newInstance() {
        return new BalanceFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BalanceFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BalanceViewModel.class);

        binding.balanceHolder.setVisibility(View.GONE);
        binding.balanceHolder2.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        mViewModel.getData().observe(this, balanceModel -> {

            binding.balanceHolder.setVisibility(View.VISIBLE);
            binding.balanceHolder2.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);

            binding.tvBalance.setText(String.format("৳ %s", balanceModel.getBalance()));
            binding.tvHoldingBalance.setText(String.format("৳ %s", balanceModel.getHolding_balance()));
            binding.tvGiftCardBalance.setText(String.format("৳ %s", balanceModel.getGift_card_balance()));
            binding.tvCashbackBalance.setText(String.format("৳ %s", balanceModel.getCashback_balance()));

            if (balanceModel.getCashback_balance() == 0)
                binding.claimCashback.setVisibility(View.GONE);
            else
                binding.claimCashback.setVisibility(View.VISIBLE);

            CredentialManager.setBalance(balanceModel.getBalance());
        });

        mViewModel.updateBalance();

        binding.claimCashback.setOnClickListener(view -> mViewModel.claimCashback());

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog dialogz = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = dialogz.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
            BottomSheetBehavior.from(bottomSheet).setHideable(true);
        });
        return bottomSheetDialog;
    }

}
