package bd.com.evaly.evalyshop.ui.balance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import bd.com.evaly.evalyshop.databinding.BalanceFragmentBinding;

public class BalanceFragment extends BottomSheetDialogFragment {

    private BalanceViewModel mViewModel;
    private BalanceFragmentBinding binding;

    public static BalanceFragment newInstance() {
        return new BalanceFragment();
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
        mViewModel = ViewModelProviders.of(this).get(BalanceViewModel.class);


        mViewModel.getData().observe(this, balanceModel -> {


        });

    }

}
