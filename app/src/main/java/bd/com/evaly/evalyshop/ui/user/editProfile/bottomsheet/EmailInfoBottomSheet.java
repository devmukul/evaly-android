package bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.BottomSheetEditEmailAddressBinding;
import bd.com.evaly.evalyshop.models.profile.EmailInfoRequest;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.ui.base.BaseBottomSheetFragment;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import bd.com.evaly.evalyshop.ui.user.editProfile.EditProfileViewModel;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EmailInfoBottomSheet extends BaseBottomSheetFragment<BottomSheetEditEmailAddressBinding, BaseViewModel> {

    @Inject
    PreferenceRepository preferenceRepository;
    private EditProfileViewModel viewModel;


    public EmailInfoBottomSheet() {
        super(BaseViewModel.class, R.layout.bottom_sheet_edit_email_address);
    }

    public static EmailInfoBottomSheet newInstance() {
        EmailInfoBottomSheet instance = new EmailInfoBottomSheet();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentInputBottomSheetDialog);
        if (getActivity() != null)
            viewModel = new ViewModelProvider(getActivity()).get(EditProfileViewModel.class);
    }

    @Override
    protected void initViews() {
        UserModel userModel = preferenceRepository.getUserData();

        if (userModel != null) {
            binding.primaryEmail.setText(userModel.getPrimaryEmail());
            binding.otherEmail.setText(userModel.getOtherEmail());
        }
    }

    @Override
    protected void liveEventsObservers() {

    }

    @Override
    protected void clickListeners() {
        binding.save.setOnClickListener(v -> {
            String primaryEmail = binding.primaryEmail.getText().toString().trim();
            String otherEmail = binding.otherEmail.getText().toString().trim();

            String error = null;
            if (primaryEmail.equals(""))
                error = "Please enter your primary email";
            else if (!Utils.isEmailValid(primaryEmail))
                error = "Please enter valid primary email";
            else if (!otherEmail.isEmpty() && !Utils.isEmailValid(otherEmail))
                error = "Please enter valid secondary email";

            if (error != null) {
                ToastUtils.show(error);
                return;
            }

            EmailInfoRequest body = new EmailInfoRequest();
            body.setPrimaryEmail(primaryEmail);
            body.setEmail(primaryEmail);
            if (!otherEmail.equals(""))
                body.setOtherEmail(otherEmail);
            ToastUtils.show("Updating email addresses");
            viewModel.setUserData(Utils.objectToHashMap(body));
            dismissAllowingStateLoss();
        });
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
}
