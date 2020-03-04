package bd.com.evaly.evalyshop.ui.shop.delivery;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetDeliveryOptionsBinding;
import bd.com.evaly.evalyshop.models.shop.shopDetails.ShopDeliveryOption;
import bd.com.evaly.evalyshop.ui.shop.delivery.adapter.DeliveryOptionAdapter;

public class DeliveryBottomSheetFragment extends BottomSheetDialogFragment {


    private BottomSheetDeliveryOptionsBinding binding;
    private ArrayList<ShopDeliveryOption> list = new ArrayList<>();

    public static DeliveryBottomSheetFragment newInstance(List<ShopDeliveryOption> deliveryOptionList) {
        DeliveryBottomSheetFragment fragment = new DeliveryBottomSheetFragment();

        Bundle bundle = new Bundle();

        if (deliveryOptionList != null)
            bundle.putSerializable("list", new ArrayList(deliveryOptionList));
        else
            bundle.putSerializable("list", new ArrayList());

        fragment.setArguments(bundle);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            if (getArguments().getSerializable("list") != null)
                list.addAll((ArrayList<ShopDeliveryOption>) getArguments().getSerializable("list"));

        Logger.d(getArguments().getSerializable("list"));

        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetDeliveryOptionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DeliveryOptionAdapter adapter = new DeliveryOptionAdapter(list, getContext());
        binding.recyclerView.setAdapter(adapter);

        if (list.size() == 0)
            binding.not.setVisibility(View.VISIBLE);
        else
            binding.not.setVisibility(View.GONE);

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
