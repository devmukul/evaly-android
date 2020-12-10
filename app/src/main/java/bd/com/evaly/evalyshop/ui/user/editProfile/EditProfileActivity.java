package bd.com.evaly.evalyshop.ui.user.editProfile;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.ProviderDatabase;
import bd.com.evaly.evalyshop.data.roomdb.userInfo.UserInfoDao;
import bd.com.evaly.evalyshop.data.roomdb.userInfo.UserInfoEntity;
import bd.com.evaly.evalyshop.databinding.ActivityEditProfileBinding;
import bd.com.evaly.evalyshop.databinding.DialogContinueAsBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.image.ImageDataModel;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.ImageApiHelper;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet.EmailInfoBottomSheet;
import bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet.EmploymentInfoBottomSheet;
import bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet.ParentsInfoBottomSheet;
import bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet.PersonalInfoBottomSheet;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ImageUtils;
import bd.com.evaly.evalyshop.util.RealPathUtil;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;


public class EditProfileActivity extends BaseActivity {

    private Context context;
    private ActivityEditProfileBinding binding;
    private EditProfileViewModel viewModel;
    private boolean fromOtherApp = false;
    private boolean isLoggedAgain = false;
    private ViewDialog dialog;

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
        dialog = new ViewDialog(this);
        handleIntent();

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


    private void handleIntent() {

        try {
            if (getIntent() != null && getIntent().hasExtra("user")) {
                if (getIntent().getStringExtra("user").equalsIgnoreCase(CredentialManager.getUserName())) {
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

                        if (CredentialManager.getToken().equals(""))
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

        AuthApiHelper.login(payload, new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int code) {
                dialog.hideDialog();
                switch (code) {
                    case 200:
                    case 201:
                    case 202:
                        response = response.get("data").getAsJsonObject();
                        String token = response.get("access_token").getAsString();
                        CredentialManager.saveToken(token);
                        CredentialManager.saveRefreshToken(response.get("refresh_token").getAsString());
                        CredentialManager.saveUserName(getIntent().getStringExtra("user"));
                        CredentialManager.savePassword(getIntent().getStringExtra("password"));
                        updateUserInfo();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Incorrect phone number or password. Please try again! ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(String body, int status) {
                dialog.hideDialog();
                ToastUtils.show(body);
            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }


    public void updateUserInfo() {
        dialog.showDialog();
        AuthApiHelper.getUserProfile(CredentialManager.getToken(), new ResponseListenerAuth<CommonDataResponse<UserModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<UserModel> response, int statusCode) {
                dialog.hideDialog();
                CredentialManager.saveUserData(response.getData());
                ProviderDatabase providerDatabase = ProviderDatabase.getInstance(context);
                UserInfoDao userInfoDao = providerDatabase.userInfoDao();

                Executors.newSingleThreadExecutor().execute(() -> {
                    UserInfoEntity entity = new UserInfoEntity();
                    entity.setToken(CredentialManager.getToken());
                    entity.setRefreshToken(CredentialManager.getRefreshToken());
                    entity.setName(response.getData().getFullName());
                    entity.setImage(response.getData().getImageSm());
                    entity.setUsername(CredentialManager.getUserName());
                    entity.setPassword(CredentialManager.getPassword());
                    userInfoDao.insert(entity);
                });
                finish();
                startActivity(getIntent());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                dialog.hideDialog();
                ToastUtils.show("Couldn't login, please try again");
                finish();
            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    updateUserInfo();
            }
        });

    }


    private void updateProfileData() {
        UserModel userModel = CredentialManager.getUserData();
        if (CredentialManager.getUserData() == null && (getIntent() != null && getIntent().hasExtra("user"))) {
            return;
        } else if (CredentialManager.getUserData() == null) {
            Toast.makeText(this, "Profile information not found, please logout and login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setProfilePic();
        String fullName = CredentialManager.getUserData().getFullName();
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
            binding.dateOfBirth.setText(Utils.formattedDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd/MM/yyyy", userModel.getBirthDate()));
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

                HashMap<String, String> body = new HashMap<>();
                body.put("profile_pic_url", response.getData().getUrl());
                body.put("image_sm", response.getData().getUrlSm());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
