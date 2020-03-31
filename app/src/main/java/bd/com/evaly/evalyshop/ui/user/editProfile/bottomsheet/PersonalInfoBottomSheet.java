package bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetEditPersonalInfoBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.ui.user.editProfile.EditProfileViewModel;

public class PersonalInfoBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetEditPersonalInfoBinding binding;
    private EditProfileViewModel viewModel;

    public static PersonalInfoBottomSheet newInstance() {
        PersonalInfoBottomSheet instance = new PersonalInfoBottomSheet();
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetEditPersonalInfoBinding.inflate(inflater, container, false);
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

        UserModel userModel = CredentialManager.getUserData();

        binding.firstName.setText(userModel.getFirst_name());
        binding.lastName.setText(userModel.getLast_name());

        if (userModel.getGender().equals("male"))
            binding.genderGroup.check(R.id.checkMale);
        else if ((userModel.getGender().equals("female")))
            binding.genderGroup.check(R.id.checkFemale);
        else
            binding.genderGroup.check(R.id.checkGenderOther);

        binding.save.setOnClickListener(v -> {
            String firstName = binding.firstName.getText().toString().trim();
            String lastName = binding.lastName.getText().toString().trim();
            String gender = "male";

            if (binding.genderGroup.getCheckedRadioButtonId() == R.id.checkMale)
                gender = "male";
            else if (binding.genderGroup.getCheckedRadioButtonId() == R.id.checkFemale)
                gender = "female";
            else
                gender = "other";

            if (firstName.equals("")) {
                Toast.makeText(getContext(), "Please enter your first name", Toast.LENGTH_SHORT).show();
                return;
            } else if (lastName.equals("")) {
                Toast.makeText(getContext(), "Please enter your last name", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, String> body = new HashMap<>();
            body.put("first_name", firstName);
            body.put("last_name", lastName);
            body.put("gender", gender);

            viewModel.setUserData(body);
            dismiss();
        });

    }

}
