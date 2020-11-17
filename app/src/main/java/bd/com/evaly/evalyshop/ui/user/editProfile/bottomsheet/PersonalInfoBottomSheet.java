package bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet;

import android.app.DatePickerDialog;
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

import java.util.Calendar;
import java.util.HashMap;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetEditPersonalInformationBinding;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.profile.PersonalInfoRequest;
import bd.com.evaly.evalyshop.models.profile.UserInfoResponse;
import bd.com.evaly.evalyshop.ui.user.editProfile.EditProfileViewModel;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.Utils;

public class PersonalInfoBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetEditPersonalInformationBinding binding;
    DatePickerDialog.OnDateSetListener datePickerListener = (datePicker, year, month, day) -> {
        String date = String.format("%d-%01d-%01d", year, ++month, day);
        binding.dateOfBirth.setText(date);
    };
    private EditProfileViewModel viewModel;

    public static PersonalInfoBottomSheet newInstance() {
        PersonalInfoBottomSheet instance = new PersonalInfoBottomSheet();
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetEditPersonalInformationBinding.inflate(inflater, container, false);
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
            binding.firstName.setText(userModel.getFirstName());
            binding.lastName.setText(userModel.getLastName());
            binding.phone.setText(userModel.getPhoneNumber());
            binding.dateOfBirth.setText(userModel.getBirthDate());

            if (userModel.getGender().equals("male"))
                binding.genderGroup.check(R.id.checkMale);
            else if ((userModel.getGender().equals("female")))
                binding.genderGroup.check(R.id.checkFemale);
            else
                binding.genderGroup.check(R.id.checkGenderOther);
        }

        binding.dateOfBirth.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = 2000;
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    getContext(),
                    datePickerListener,
                    year, month, day
            );

            dialog.show();
        });

        binding.save.setOnClickListener(v -> {
            String firstName = binding.firstName.getText().toString().trim();
            String lastName = binding.lastName.getText().toString().trim();
            String dateOfBirth = binding.dateOfBirth.getText().toString().trim();
            String contact = binding.phone.getText().toString().trim();
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
            } else if (dateOfBirth.equals("")) {
                Toast.makeText(getContext(), "Please enter your date of birth", Toast.LENGTH_SHORT).show();
                return;
            }

            PersonalInfoRequest body = new PersonalInfoRequest();
            body.setFirstName(firstName);
            body.setLastName(lastName);
            body.setBirthDate(dateOfBirth);
            body.setContact(contact);
            body.setGender(gender);

//            HashMap<String, String> body = new HashMap<>();
//            body.put("first_name", firstName);
//            body.put("last_name", lastName);
//            body.put("birth_date", dateOfBirth);
//            body.put("contact", contact);
//            body.put("gender", gender);
//            viewModel.setUserData(body);


            viewModel.setUserData(Utils.objectToHashMap(body));

            HashMap<String, String> data = new HashMap<>();
            data.put("user", CredentialManager.getUserName());
            data.put("host", Constants.XMPP_HOST);
            data.put("name", "FN");
            data.put("content", firstName + " " + lastName);
            viewModel.updateToXMPP(data);
            dismissAllowingStateLoss();
        });

    }

}
