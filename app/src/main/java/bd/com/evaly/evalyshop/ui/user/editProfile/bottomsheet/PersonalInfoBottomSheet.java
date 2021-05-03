package bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.BottomSheetEditPersonalInformationBinding;
import bd.com.evaly.evalyshop.models.profile.PersonalInfoRequest;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.ui.base.BaseBottomSheetFragment;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import bd.com.evaly.evalyshop.ui.user.editProfile.EditProfileViewModel;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.Utils;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PersonalInfoBottomSheet extends BaseBottomSheetFragment<BottomSheetEditPersonalInformationBinding, BaseViewModel> {

    @Inject
    PreferenceRepository preferenceRepository;
    private String dateOfBirth = "";
    private EditProfileViewModel commonViewModel;

    public PersonalInfoBottomSheet() {
        super(BaseViewModel.class, R.layout.bottom_sheet_edit_personal_information);
    }

    public static PersonalInfoBottomSheet newInstance() {
        PersonalInfoBottomSheet instance = new PersonalInfoBottomSheet();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialog);

        if (getActivity() != null)
            commonViewModel = new ViewModelProvider(getActivity()).get(EditProfileViewModel.class);
    }


    @Override
    protected void initViews() {
        UserModel userModel = preferenceRepository.getUserData();

        if (userModel != null) {
            binding.firstName.setText(userModel.getFirstName());
            binding.lastName.setText(userModel.getLastName());
            binding.phone.setText(userModel.getContact());

            if (userModel.getBirthDate() == null)
                binding.dateOfBirth.setText(R.string.not_provided);
            else {
                dateOfBirth = Utils.formattedDateFromString("", "dd/MM/yyyy", userModel.getBirthDate());
                binding.dateOfBirth.setText(dateOfBirth);
            }
            if (userModel.getGender().equalsIgnoreCase("male"))
                binding.genderGroup.check(R.id.checkMale);
            else if ((userModel.getGender().equalsIgnoreCase("female")))
                binding.genderGroup.check(R.id.checkFemale);
        }


    }

    @Override
    protected void liveEventsObservers() {

    }

    @Override
    protected void clickListeners() {
        binding.dateOfBirth.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Select your birth date");
            MaterialDatePicker<Long> picker = builder.build();
            picker.addOnPositiveButtonClickListener(selection -> {
                String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat formatter = new SimpleDateFormat(inputFormat);
                formatter.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection);
                dateOfBirth = formatter.format(calendar.getTime());
                binding.dateOfBirth.setText(Utils.formattedDateFromString("", "dd/MM/yyyy", dateOfBirth));
            });
            picker.show(getParentFragmentManager(), picker.toString());
        });

        binding.save.setOnClickListener(v -> {
            String firstName = binding.firstName.getText().toString().trim();
            String lastName = binding.lastName.getText().toString().trim();
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
            } else if (!Utils.isValidNumber(contact)) {
                Toast.makeText(getContext(), "Please enter your valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            PersonalInfoRequest body = new PersonalInfoRequest();
            body.setFirstName(firstName);
            body.setLastName(lastName);
            body.setBirthDate(dateOfBirth);
            body.setContact(contact);
            body.setGender(gender);
            commonViewModel.setUserData(Utils.objectToHashMap(body));

            HashMap<String, String> data = new HashMap<>();
            data.put("user", preferenceRepository.getUserName());
            data.put("host", Constants.XMPP_HOST);
            data.put("name", "FN");
            data.put("content", firstName + " " + lastName);
            commonViewModel.updateToXMPP(data);
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
