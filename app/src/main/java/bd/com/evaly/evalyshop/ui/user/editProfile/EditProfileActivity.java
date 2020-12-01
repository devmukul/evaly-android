package bd.com.evaly.evalyshop.ui.user.editProfile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ActivityEditProfileBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.image.ImageDataModel;
import bd.com.evaly.evalyshop.models.profile.UserInfoResponse;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.rest.apiHelper.ImageApiHelper;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet.EmailInfoBottomSheet;
import bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet.EmploymentInfoBottomSheet;
import bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet.ParentsInfoBottomSheet;
import bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet.PersonalInfoBottomSheet;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ImageUtils;
import bd.com.evaly.evalyshop.util.RealPathUtil;


public class EditProfileActivity extends BaseActivity {

    private Context context;
    private ActivityEditProfileBinding binding;
    private EditProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);

        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        updateProfileData();

        context = this;

        binding.editPicture.bringToFront();

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

        viewModel.getInfoSavedStatus().observe(this, aBoolean -> {
            if (aBoolean) {
                viewModel.setInfoSavedStatus(false);
                updateProfileData();
                Toast.makeText(EditProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void updateProfileData() {

        UserModel userModel = CredentialManager.getUserData();
        UserInfoResponse userInfoModel = CredentialManager.getUserInfo();

        if (CredentialManager.getUserData() == null || CredentialManager.getUserInfo() == null) {
            Toast.makeText(this, "Profile information not found, please logout and login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setProfilePic();

        String fullName = CredentialManager.getUserData().getFullName();

        binding.firstName.setText(fullName);
        binding.userNameTop.setText(fullName);

        binding.primaryEmail.setText(((userModel.getEmail() == null) ||
                (userModel.getEmail() != null && userModel.getEmail().equals(""))) ? "No email address provided" : userModel.getEmail());

        binding.contactNumber.setText(((userModel.getContacts() == null) ||
                (userModel.getContacts() != null && userModel.getContacts().equals(""))) ? "No phone number provided" : userModel.getContacts());

        if (userInfoModel.getPhoneNumber() != null)
            binding.contactNumber.setText(userInfoModel.getPhoneNumber());
        if (userInfoModel.getBirthDate() != null)
            binding.dateOfBirth.setText(userInfoModel.getBirthDate());
        if (userInfoModel.getFatherName() != null)
            binding.fatherInfo.setText(String.format("%s%s", userInfoModel.getFatherName(), userInfoModel.getFatherPhoneNumber() != null ?
                    String.format(", %s", userInfoModel.getFatherPhoneNumber()) : ""));
        if (userInfoModel.getMotherName() != null)
            binding.motherInfo.setText(String.format("%s%s", userInfoModel.getMotherName(), userInfoModel.getMotherPhoneNumber() != null ?
                    String.format(", %s", userInfoModel.getMotherPhoneNumber()) : ""));
        if (userInfoModel.getOccupation() != null)
            binding.occupation.setText(userInfoModel.getOccupation());
        if (userInfoModel.getOrganization() != null)
            binding.organization.setText(userInfoModel.getOrganization());
        if (userInfoModel.getPrimaryEmail() != null)
            binding.primaryEmail.setText(userInfoModel.getPrimaryEmail());
        if (userInfoModel.getOtherEmail() != null)
            binding.otherEmail.setText(userInfoModel.getOtherEmail());
    }

    private void setProfilePic() {
        if (CredentialManager.getUserData().getProfilePicUrl() != null)
            Glide.with(this)
                    .asBitmap()
                    .load(CredentialManager.getUserData().getProfilePicUrl())
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
        startActivityForResult(intent, 1001);
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
                try {

                    File cImage = compressImage(data.getData(), Bitmap.CompressFormat.JPEG, 60, destinationDirectoryPath);
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

                } catch (Exception ignored) {
                }

            } catch (Exception e) {
                Toast.makeText(context, "Error occurred while uploading image", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private File compressImage(Uri path, Bitmap.CompressFormat compressFormat, int quality, String destinationPath) throws IOException {
        File file = new File(destinationPath).getParentFile();
        if (!file.exists()) file.mkdirs();

        try (FileOutputStream fileOutputStream = new FileOutputStream(destinationPath)) {
            ImageUtils.getCorrectlyOrientedImage(EditProfileActivity.this, path).compress(compressFormat, quality, fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File(destinationPath);
    }


    private void uploadPicture(Bitmap bitmap) {

        ProgressDialog dialog = ProgressDialog.show(EditProfileActivity.this, "",
                "Uploading image...", true);

        ImageApiHelper.uploadImage(bitmap, new ResponseListenerAuth<CommonDataResponse<ImageDataModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<ImageDataModel> response, int statusCode) {

                if (dialog != null && dialog.isShowing() && !isFinishing() && !isDestroyed())
                    dialog.dismiss();

                UserModel userModel = CredentialManager.getUserData();
                userModel.setProfilePicUrl(response.getData().getUrl());
                userModel.setImageSm(response.getData().getUrlSm());

                HashMap<String, String> body = new HashMap<>();
                body.put("profile_pic_url", response.getData().getUrl());
                viewModel.setUserData(body);

                HashMap<String, String> data = new HashMap<>();
                body.put("user", CredentialManager.getUserName());
                body.put("host", Constants.XMPP_HOST);
                body.put("name", "IMAGE_URL");
                body.put("content", response.getData().getUrl());

                viewModel.updateToXMPP(data);

                setProfilePic();
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                Toast.makeText(EditProfileActivity.this, "Upload error, try again!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    uploadPicture(bitmap);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

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
