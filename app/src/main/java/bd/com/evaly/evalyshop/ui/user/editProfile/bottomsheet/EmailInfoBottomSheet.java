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

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetEditEmailAddressBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.profile.EmailInfoRequest;
import bd.com.evaly.evalyshop.models.profile.UserInfoResponse;
import bd.com.evaly.evalyshop.ui.user.editProfile.EditProfileViewModel;
import bd.com.evaly.evalyshop.util.ToastUtils;

public class EmailInfoBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetEditEmailAddressBinding binding;
    private EditProfileViewModel viewModel;

    public static EmailInfoBottomSheet newInstance() {
        EmailInfoBottomSheet instance = new EmailInfoBottomSheet();
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetEditEmailAddressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentInputBottomSheetDialog);

        if (getActivity() != null)
            viewModel = new ViewModelProvider(getActivity()).get(EditProfileViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(d -> {
            BottomSheetDialog dialog = (BottomSheetDialog) d;
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
                BottomSheetBehavior.from(bottomSheet).setHideable(true);
            }
        });

        return bottomSheetDialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserInfoResponse userModel = CredentialManager.getUserInfo();

        binding.primaryEmail.setText(userModel.getPrimaryEmail());
        binding.otherEmail.setText(userModel.getOtherEmail());

        binding.save.setOnClickListener(v -> {
            String primaryEmail = binding.primaryEmail.getText().toString().trim();
            String otherEmail = binding.otherEmail.getText().toString().trim();

            String error = null;
            if (primaryEmail.equals(""))
                error = "Please enter your primary email";

            if (error != null) {
                ToastUtils.show(error);
                return;
            }

            EmailInfoRequest body = new EmailInfoRequest();
            body.setPrimaryEmail(primaryEmail);
            if (!otherEmail.equals(""))
                body.setOtherEmail(otherEmail);
        });

    }

}
