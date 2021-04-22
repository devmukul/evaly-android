package bd.com.evaly.evalyshop.ui.auth.changePassword;

import android.content.Intent;
import android.text.InputType;
import android.view.MenuItem;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ActivityChangePasswordBinding;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.util.ToastUtils;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChangePasswordActivity extends BaseActivity<ActivityChangePasswordBinding, ChangePasswordViewModel> {

    private ViewDialog dialog;
    private boolean isCurrentShowing, isNewShowing, isNewConfirmShowing;

    public ChangePasswordActivity(){
        super(ChangePasswordViewModel.class, R.layout.activity_change_password);
    }

    @Override
    protected void initViews() {
        getSupportActionBar().setElevation(0f);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dialog = new ViewDialog(this);
    }

    @Override
    protected void liveEventsObservers() {
        viewModel.loadingDialog.observe(this, aBoolean -> {
            if (aBoolean)
                dialog.showDialog();
            else
                dialog.hideDialog();
        });

        viewModel.loginSuccess.observe(this, aVoid -> {
            Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "signin");
            startActivity(intent);
            finishAffinity();
        });
    }

    @Override
    protected void clickListeners() {
        binding.showCurrentPass.setOnClickListener(v -> {
            if (!isCurrentShowing) {
                isCurrentShowing = true;
                binding.currentPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.showCurrentPass.setImageResource(R.drawable.ic_visibility_off);
            } else {
                isCurrentShowing = false;
                binding.currentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.showCurrentPass.setImageResource(R.drawable.ic_visibility);
            }
        });

        binding.showNewPass.setOnClickListener(v -> {
            if (!isNewShowing) {
                isNewShowing = true;
                binding.newPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.showNewPass.setImageResource(R.drawable.ic_visibility_off);
            } else {
                isNewShowing = false;
                binding.newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.showNewPass.setImageResource(R.drawable.ic_visibility);
            }
        });

        binding.showNewPassConfirm.setOnClickListener(v -> {
            if (!isNewConfirmShowing) {
                isNewConfirmShowing = true;
                binding.newPasswordConfirmation.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.showNewPassConfirm.setImageResource(R.drawable.ic_visibility_off);
            } else {
                isNewConfirmShowing = false;
                binding.newPasswordConfirmation.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.showNewPassConfirm.setImageResource(R.drawable.ic_visibility);
            }
        });

        binding.change.setOnClickListener(v -> {
            String oldPass = binding.currentPassword.getText().toString();
            String newPass = binding.newPassword.getText().toString();
            String confirmPass = binding.newPasswordConfirmation.getText().toString();
            if (oldPass.equals("")) {
                ToastUtils.show("Please enter your current password");
            } else if (newPass.equals("")) {
                ToastUtils.show("Please enter your new password");
            } else if (confirmPass.equals("")) {
                ToastUtils.show("Please confirm your new password");
            } else if (!confirmPass.equals(newPass)) {
                ToastUtils.show("Password didn't match. Please enter your new password again.");
            } else if (!Utils.isStrongPassword(confirmPass).equals("yes")) {
                ToastUtils.show(Utils.isStrongPassword(confirmPass));
            } else {
                viewModel.updatePassword(oldPass, newPass);
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
