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
import bd.com.evaly.evalyshop.databinding.BottomSheetEditEmploymentInfoBinding;
import bd.com.evaly.evalyshop.models.profile.EmploymentRequest;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.ui.base.BaseBottomSheetFragment;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import bd.com.evaly.evalyshop.ui.user.editProfile.EditProfileViewModel;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EmploymentInfoBottomSheet extends BaseBottomSheetFragment<BottomSheetEditEmploymentInfoBinding, BaseViewModel> {

    @Inject
    PreferenceRepository preferenceRepository;
    private EditProfileViewModel viewModel;

    public EmploymentInfoBottomSheet() {
        super(BaseViewModel.class, R.layout.bottom_sheet_edit_employment_info);
    }

    public static EmploymentInfoBottomSheet newInstance() {
        EmploymentInfoBottomSheet instance = new EmploymentInfoBottomSheet();
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
            binding.occupation.setText(userModel.getOccupation());
            binding.organization.setText(userModel.getOrganization());
        }

    }

    @Override
    protected void liveEventsObservers() {

    }

    @Override
    protected void clickListeners() {
        binding.save.setOnClickListener(v -> {
            String occupation = binding.occupation.getText().toString().trim();
            String organization = binding.organization.getText().toString().trim();

            String error = null;
            if (occupation.equals(""))
                error = "Please enter your occupation";
            else if (organization.equals(""))
                error = "Please enter your organization name";

            if (error != null) {
                ToastUtils.show(error);
                return;
            }

            EmploymentRequest body = new EmploymentRequest();
            body.setOccupation(occupation);
            body.setOrganization(organization);

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
