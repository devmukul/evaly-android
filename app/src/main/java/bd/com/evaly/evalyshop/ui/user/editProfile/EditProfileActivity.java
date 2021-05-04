package bd.com.evaly.evalyshop.ui.user.editProfile;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.ActivityEditProfileBinding;
import bd.com.evaly.evalyshop.databinding.DialogContinueAsBinding;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet.EmailInfoBottomSheet;
import bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet.EmploymentInfoBottomSheet;
import bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet.ParentsInfoBottomSheet;
import bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet.PersonalInfoBottomSheet;
import bd.com.evaly.evalyshop.util.ImageUtils;
import bd.com.evaly.evalyshop.util.RealPathUtil;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditProfileActivity extends BaseActivity<ActivityEditProfileBinding, EditProfileViewModel> {

    @Inject
    ApiRepository apiRepository;
    @Inject
    PreferenceRepository preferenceRepository;
    private Context context;
    private boolean fromOtherApp = false;
    private boolean isLoggedAgain = false;
    private ViewDialog dialog;

    public EditProfileActivity() {
        super(EditProfileViewModel.class, R.layout.activity_edit_profile);
    }

    @Override
    protected void preBind() {
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initViews() {
        context = this;
        dialog = new ViewDialog(this);
        handleIntent();
        binding.editPicture.bringToFront();
        updateProfileData();
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.getInfoSavedStatus().observe(this, aBoolean -> {
            if (aBoolean) {
                viewModel.setInfoSavedStatus(false);
                updateProfileData();
                Toast.makeText(EditProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.autoLogin.observe(this, aBoolean -> {
            finish();
            if (aBoolean)
            startActivity(getIntent());
        });

        viewModel.dialogToggle.observe(this, aBoolean -> {
            if (aBoolean)
                dialog.showDialog();
            else
                dialog.hideDialog();
        });
    }

    @Override
    protected void clickListeners() {
        View.OnClickListener uploadListener = v -> openImageSelector();

        binding.editPicture.setOnClickListener(uploadListener);
        binding.editPicture.setOnClickListener(uploadListener);

        binding.editPersonalInfo.setOnClickListener(v -> {
            PersonalInfoBottomSheet bottomSheet = PersonalInfoBottomSheet.newInstance();
            bottomSheet.show(getSupportFragmentManager(), "Edit Personal Info");
        });

        binding.editEmailInfo.setOnClickListener(v -> {
            EmailInfoBottomSheet bottomSheet = EmailInfoBottomSheet.newInstance();
            bottomSheet.show(getSupportFragmentManager(), "Edit Email Info");
        });

        binding.editEmploymentInfo.setOnClickListener(v -> {
            EmploymentInfoBottomSheet bottomSheet = EmploymentInfoBottomSheet.newInstance();
            bottomSheet.show(getSupportFragmentManager(), "Edit Employment Info");
        });

        binding.editParentsInfo.setOnClickListener(v -> {
            ParentsInfoBottomSheet bottomSheet = ParentsInfoBottomSheet.newInstance();
            bottomSheet.show(getSupportFragmentManager(), "Edit Parent Info");
        });
    }

    private void handleIntent() {

        try {
            if (getIntent() != null && getIntent().hasExtra("user")) {
                if (getIntent().getStringExtra("user").equalsIgnoreCase(preferenceRepository.getUserName())) {
                    fromOtherApp = true;
                } else {
                    if (getIntent() != null && getIntent().getStringExtra("userInfo") != null) {
                        fromOtherApp = true;
                        UserModel userModel = new Gson().fromJson(getIntent().getStringExtra("userInfo"), UserModel.class);
                        final Dialog dialog = new Dialog(this, R.style.FullWidthTransparentDialog);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(true);
                        final DialogContinueAsBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_continue_as, null, false);
                        if (userModel.getProfilePicUrl() != null)
                            Glide.with(this)
                                    .load(userModel.getProfilePicUrl())
                                    .placeholder(R.drawable.user_image)
                                    .into(dialogBinding.picture);

                        if (preferenceRepository.getToken().equals(""))
                            dialogBinding.desciption.setText("You are not logged to an account. Click on the button to login automatically.");
                        else
                            dialogBinding.desciption.setText("You are logged to a different account. Click on the button to switch account automatically.");

                        dialogBinding.buttonText.setText(Html.fromHtml("Continue as <b>" + userModel.getFullName() + "</b>"));
                        dialogBinding.loginWithEvaly.setOnClickListener(v -> {
                            loginWithEvaly();
                            isLoggedAgain = true;
                            dialog.dismiss();
                        });
                        dialog.setOnDismissListener(dialogInterface -> {
                            if (!isLoggedAgain)
                                finish();
                        });
                        dialog.setContentView(dialogBinding.getRoot());
                        dialog.show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void loginWithEvaly() {

        ViewDialog dialog = new ViewDialog(this);
        dialog.showDialog();
        HashMap<String, String> payload = new HashMap<>();

        payload.put("username", getIntent().getStringExtra("user"));
        payload.put("password", getIntent().getStringExtra("password"));

        viewModel.login(payload);
    }

    private void updateProfileData() {
        UserModel userModel = preferenceRepository.getUserData();
        if (preferenceRepository.getUserData() == null && (getIntent() != null && getIntent().hasExtra("user"))) {
            return;
        } else if (preferenceRepository.getUserData() == null) {
            Toast.makeText(this, "Profile information not found, please logout and login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setProfilePic();
        String fullName = preferenceRepository.getUserData().getFullName();
        binding.firstName.setText(fullName);
        binding.userNameTop.setText(fullName);
        binding.gender.setText(Utils.capitalize(userModel.getGender()));

        if (userModel.getGender() == null || userModel.getGender().equals(""))
            binding.gender.setText(R.string.not_provided);
        binding.primaryEmail.setText(((userModel.getEmail() == null) ||
                (userModel.getEmail() != null && userModel.getEmail().equals(""))) ? "No email address provided" : userModel.getEmail());

        binding.contactNumber.setText(((userModel.getContacts() == null) ||
                (userModel.getContacts() != null && userModel.getContacts().equals(""))) ? "No phone number provided" : userModel.getContacts());

        if (userModel.getContact() != null)
            binding.contactNumber.setText(userModel.getContact());
        if (userModel.getBirthDate() != null)
            binding.dateOfBirth.setText(Utils.formattedDateFromString("", "dd/MM/yyyy", userModel.getBirthDate()));
        if (userModel.getParentsInfo().getFatherName() != null)
            binding.fatherInfo.setText(String.format("%s%s", userModel.getParentsInfo().getFatherName(), userModel.getParentsInfo().getFatherPhoneNumber() != null ?
                    String.format(", %s", userModel.getParentsInfo().getFatherPhoneNumber()) : ""));
        if (userModel.getParentsInfo().getMotherName() != null)
            binding.motherInfo.setText(String.format("%s%s", userModel.getParentsInfo().getMotherName(), userModel.getParentsInfo().getMotherPhoneNumber() != null ?
                    String.format(", %s", userModel.getParentsInfo().getMotherPhoneNumber()) : ""));
        if (userModel.getOccupation() != null)
            binding.occupation.setText(userModel.getOccupation());
        if (userModel.getOrganization() != null)
            binding.organization.setText(userModel.getOrganization());
        if (userModel.getPrimaryEmail() != null)
            binding.primaryEmail.setText(userModel.getPrimaryEmail());
        if (userModel.getOtherEmail() != null)
            binding.otherEmail.setText(userModel.getOtherEmail());
    }

    private void setProfilePic() {
        if (preferenceRepository.getUserData().getProfilePicUrl() != null && !isFinishing())
            Glide.with(this)
                    .asBitmap()
                    .load(preferenceRepository.getUserData().getProfilePicUrl())
                    .skipMemoryCache(true)
                    .fitCenter()
                    .optionalCenterCrop()
                    .placeholder(R.drawable.half_dp_bg_light)
                    .apply(new RequestOptions().override(500, 500))
                    .into(binding.picture);
    }

    private void openImageSelector() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    8000);

        } else openSelector();
    }

    private void openSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        try {
            startActivityForResult(intent, 1001);
        } catch (Exception e) {
            ToastUtils.show("Can't open image picker");
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 8000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openSelector();
            else Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            String imagePath = RealPathUtil.getRealPath(context, selectedImage);
            try {
                String destinationDirectoryPath = context.getCacheDir().getPath() + File.separator + "images";

                Bitmap bitmap = BitmapFactory.decodeFile(destinationDirectoryPath);
                Glide.with(this)
                        .asBitmap()
                        .load(bitmap)
                        .skipMemoryCache(true)
                        .fitCenter()
                        .optionalCenterCrop()
                        .apply(new RequestOptions().override(500, 500))
                        .into(binding.picture);

                uploadPicture(bitmap);

            } catch (Exception e) {
                Toast.makeText(context, "Error occurred while uploading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadPicture(Bitmap bitmap) {
        dialog.showDialog();
        viewModel.uploadPicture(bitmap);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
