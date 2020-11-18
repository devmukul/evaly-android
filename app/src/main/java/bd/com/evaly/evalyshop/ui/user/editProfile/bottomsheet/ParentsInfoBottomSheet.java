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
import bd.com.evaly.evalyshop.databinding.BottomSheetEditParentInfoBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.profile.ParentInfoRequest;
import bd.com.evaly.evalyshop.models.profile.UserInfoResponse;
import bd.com.evaly.evalyshop.ui.user.editProfile.EditProfileViewModel;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;

public class ParentsInfoBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetEditParentInfoBinding binding;
    private EditProfileViewModel viewModel;

    public static ParentsInfoBottomSheet newInstance() {
        ParentsInfoBottomSheet instance = new ParentsInfoBottomSheet();
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetEditParentInfoBinding.inflate(inflater, container, false);
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

        if (userModel != null) {
            binding.fatherName.setText(userModel.getFatherName());
            binding.fatherPhoneNumber.setText(userModel.getFatherPhoneNumber());
            binding.motherName.setText(userModel.getMotherName());
            binding.motherPhoneNumber.setText(userModel.getMotherPhoneNumber());
        }

        binding.save.setOnClickListener(v -> {
            String fatherName = binding.fatherName.getText().toString().trim();
            String fatherPhoneNumber = binding.fatherPhoneNumber.getText().toString().trim();
            String motherName = binding.motherName.getText().toString().trim();
            String motherPhoneNumber = binding.motherPhoneNumber.getText().toString().trim();
            String error = null;
            if (fatherName.equals(""))
                error = "Please enter your father's name";
            else if (fatherPhoneNumber.equals(""))
                error = "Please enter your father's phone number";
            else if (motherName.equals(""))
                error = "Please enter your mother's name";
            else if (motherPhoneNumber.equals(""))
                error = "Please enter your mother's phone number";
            else if (!Utils.isValidNumber(fatherPhoneNumber))
                error = "Please enter your father's valid phone number";
            else if (!Utils.isValidNumber(motherPhoneNumber))
                error = "Please enter your mother's valid phone number";

            if (error != null) {
                ToastUtils.show(error);
                return;
            }

            ParentInfoRequest body = new ParentInfoRequest();
            body.setFatherName(fatherName);
            body.setFatherPhoneNumber(fatherPhoneNumber);
            body.setMotherName(motherName);
            body.setMotherPhoneNumber(motherPhoneNumber);


            viewModel.addUserData(Utils.objectToHashMap(body));
            dismissAllowingStateLoss();

//            HashMap<String, String> data = new HashMap<>();
//            data.put("father_name", fatherName);
//            data.put("father_phone_number", fatherPhoneNumber);
//            data.put("mother_name", motherName);
//            data.put("mother_phone_number", motherPhoneNumber);
//
//            viewModel.setUserData(data);
        });

    }

}
