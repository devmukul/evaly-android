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
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.ActivityEditProfileNewBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.image.ImageDataModel;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.rest.apiHelper.ImageApiHelper;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet.AddressBottomSheet;
import bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet.ContactInfoBottomSheet;
import bd.com.evaly.evalyshop.ui.user.editProfile.bottomsheet.PersonalInfoBottomSheet;
import bd.com.evaly.evalyshop.util.ImageUtils;
import bd.com.evaly.evalyshop.util.RealPathUtil;
import bd.com.evaly.evalyshop.util.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.util.xmpp.XMPPService;
import bd.com.evaly.evalyshop.util.xmpp.XmppCustomEventListener;


public class EditProfileActivity extends BaseActivity {

    private Context context;
    private AppController mChatApp = AppController.getInstance();
    private XMPPHandler xmppHandler;
    private ActivityEditProfileNewBinding binding;
    private EditProfileViewModel viewModel;

    private XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {
        public void onConnected() {
            xmppHandler = AppController.getmService().xmpp;
        }

        public void onLoggedIn() {
            xmppHandler = AppController.getmService().xmpp;
            xmppHandler.updateUserInfo(CredentialManager.getUserData());
        }

        public void onUpdateUserSuccess() {
        }

        public void onUpdateUserFailed(String error) {
            xmppHandler.disconnect();
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditProfileNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;


        binding.editPicture.bringToFront();

        View.OnClickListener uploadListener = v -> openImageSelector();

        binding.editPicture.setOnClickListener(uploadListener);
        binding.editPicture.setOnClickListener(uploadListener);

        binding.editPersonalInfo.setOnClickListener(v -> {
            PersonalInfoBottomSheet bottomSheet = PersonalInfoBottomSheet.newInstance();
            bottomSheet.show(getSupportFragmentManager(), "Edit Personal Info");
        });

        binding.editContactInfo.setOnClickListener(v -> {
            ContactInfoBottomSheet bottomSheet = ContactInfoBottomSheet.newInstance();
            bottomSheet.show(getSupportFragmentManager(), "Edit Contact Info");
        });

        binding.editAddress.setOnClickListener(v -> {
            AddressBottomSheet bottomSheet = AddressBottomSheet.newInstance();
            bottomSheet.show(getSupportFragmentManager(), "Edit Address");
        });

        viewModel.getInfoSavedStatus().observe(this, aBoolean -> {
            if (aBoolean) {
                mChatApp.getEventReceiver().setListener(xmppCustomEventListener);
                startXmppService();
                viewModel.setInfoSavedStatus(false);
                updateProfileData();
            }
        });

        updateProfileData();

        setProfilePic();
    }


    private void  updateProfileData(){


        UserModel userModel = CredentialManager.getUserData();

        binding.firstName.setText(String.format("%s %s", userModel.getFirst_name(), userModel.getLast_name()));
        binding.email.setText(userModel.getEmail());
        binding.phone.setText(userModel.getContacts());

        if (userModel.getAddresses().equals(""))
            binding.address.setHint("Add an address");
        else
            binding.address.setText(userModel.getAddresses());

    }

    private void setProfilePic() {
        if (CredentialManager.getUserData().getProfile_pic_url() != null)
            Glide.with(this)
                    .asBitmap()
                    .load(CredentialManager.getUserData().getProfile_pic_url())
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

                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();

                UserModel userModel = CredentialManager.getUserData();
                userModel.setProfile_pic_url(response.getData().getUrl());
                userModel.setImage_sm(response.getData().getUrlSm());

                HashMap<String, String> body = new HashMap<>();
                body.put("profile_pic_url", response.getData().getUrl());
                viewModel.setUserData(body);

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

    private void startXmppService() {
        if (!XMPPService.isServiceRunning) {
            Intent intent = new Intent(this, XMPPService.class);
            mChatApp.UnbindService();
            mChatApp.BindService(intent);
        } else {
            xmppHandler = AppController.getmService().xmpp;
            if (!xmppHandler.isConnected()) {
                xmppHandler.connect();
            } else {
                xmppHandler.updateUserInfo(CredentialManager.getUserData());
            }
        }
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
