package bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet;

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

import java.util.HashMap;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetEditAddressBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.ui.user.editProfile.EditProfileViewModel;

public class AddressBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetEditAddressBinding binding;
    private EditProfileViewModel viewModel;

    public static AddressBottomSheet newInstance() {
        AddressBottomSheet instance = new AddressBottomSheet();
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetEditAddressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserModel userModel = CredentialManager.getUserData();
        binding.address.setText(userModel.getAddresses());

        binding.save.setOnClickListener(v -> {
            String address = binding.address.getText().toString().trim();
            HashMap<String, String> body = new HashMap<>();
            body.put("address", address);
            viewModel.setUserData(body);
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialog);
        if (getActivity() != null)
            viewModel = new ViewModelProvider(getActivity()).get(EditProfileViewModel.class);
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
