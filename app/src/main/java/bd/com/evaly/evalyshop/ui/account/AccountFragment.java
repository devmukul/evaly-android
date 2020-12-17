package bd.com.evaly.evalyshop.ui.account;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.FragmentAccountBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.user.UserModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.token.ChatApiHelper;
import bd.com.evaly.evalyshop.ui.auth.ChangePasswordActivity;
import bd.com.evaly.evalyshop.ui.balance.BalanceFragment;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.user.editProfile.EditProfileActivity;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateViews();
        clickListeners();
        getMessageCount();
        updateUserDetails();
    }

    private void updateViews() {
        if (CredentialManager.getUserData() != null) {
            binding.name.setText(CredentialManager.getUserData().getFullName());
        }
    }


    private void clickListeners() {

        binding.llAppointment.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.appointmentFragment);
        });

        binding.btn1Image.setOnClickListener(view -> {
            NavHostFragment.findNavController(this).navigate(R.id.orderListBaseFragment);
        });

        binding.btn1Title.setOnClickListener(view -> {
            NavHostFragment.findNavController(this).navigate(R.id.orderListBaseFragment);
        });


        binding.notification.setOnClickListener(view -> {
            NavHostFragment.findNavController(this).navigate(R.id.notificationFragment);
        });

        binding.transactionHistory.setOnClickListener(view -> {
            NavHostFragment.findNavController(this).navigate(R.id.transactionHistory);
        });

        binding.btn2Image.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivity(intent);
        });

        binding.btn2Title.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivity(intent);
        });


        binding.btn3Image.setOnClickListener(view -> {
            NavHostFragment.findNavController(this).navigate(R.id.addressFragment);
        });

        binding.btn3Title.setOnClickListener(view -> {
            NavHostFragment.findNavController(this).navigate(R.id.addressFragment);
        });


        binding.changePassword.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        binding.changeLanguage.setOnClickListener(view -> {

            AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
            CharSequence[] items = new CharSequence[]{"English", "বাংলা"};

            int selectedPos = 0;
            if (CredentialManager.getLanguage().equalsIgnoreCase("bn"))
                selectedPos = 1;

            adb.setSingleChoiceItems(items, selectedPos, (d, n) -> {
                Locale myLocale;
                if (n == 1) {
                    CredentialManager.setLanguage("BN");
                    myLocale = new Locale("BN");
                } else {
                    CredentialManager.setLanguage("EN");
                    myLocale = new Locale("EN");
                }
                Locale.setDefault(myLocale);
                android.content.res.Configuration config = new android.content.res.Configuration();
                config.locale = myLocale;
                getContext().getResources().updateConfiguration(config,
                        getContext().getResources().getDisplayMetrics());

                startActivity(new Intent(getContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                getActivity().finish();
            });
            adb.setNegativeButton(R.string.cancel, null);
            adb.setTitle(R.string.select_language);
            adb.show();
        });

        binding.balance.setOnClickListener(view -> {
            BalanceFragment balanceFragment = BalanceFragment.newInstance();
            balanceFragment.show(getParentFragmentManager(), "balance");
        });

        binding.btn4Image.setOnClickListener(v -> {
            openEconnect();
        });

        binding.btn4Title.setOnClickListener(v -> {
            openEconnect();
        });

        binding.toolbar.getMenu().getItem(0).setOnMenuItemClickListener(menuItem -> {
            AppController.logout(getActivity());
            return false;
        });
        binding.toolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
    }

    private void openEconnect() {

        try {
            Intent launchIntent = new Intent("bd.com.evaly.econnect.OPEN_MAINACTIVITY");
            launchIntent.putExtra("to", "OPEN_CHAT_LIST");
            launchIntent.putExtra("user", CredentialManager.getUserName());
            launchIntent.putExtra("password", CredentialManager.getPassword());
            launchIntent.putExtra("userInfo", new Gson().toJson(CredentialManager.getUserData()));
            startActivity(launchIntent);
        } catch (android.content.ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "bd.com.evaly.econnect")));
            } catch (android.content.ActivityNotFoundException anfe) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "bd.com.evaly.econnect")));
                } catch (Exception e3) {
                    ToastUtils.show("Couldn't open eConnect, please install from Google Playstore");
                }
            }
        } catch (Exception ee) {
            ToastUtils.show("Couldn't open eConnect, please install from Google Playstore");
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (CredentialManager.getUserData() != null) {
            if (CredentialManager.getUserData().getImageSm() != null) {
                Glide.with(this)
                        .asBitmap()
                        .load(CredentialManager.getUserData().getImageSm())
                        .skipMemoryCache(true)
                        .fitCenter()
                        .placeholder(R.drawable.user_image)
                        .centerCrop()
                        .apply(new RequestOptions().override(200, 200))
                        .into(binding.picture);
            }
            binding.name.setText(CredentialManager.getUserData().getFullName());
            binding.phoneNumber.setText(CredentialManager.getUserName());
        }
    }


    private void getMessageCount() {

        if (Calendar.getInstance().getTimeInMillis() - CredentialManager.getMessageCounterLastUpdated() < 600000) {
            updateHotCount(CredentialManager.getMessageCount());
            return;
        }

        ChatApiHelper.getMessageCount(new ResponseListenerAuth<CommonDataResponse<String>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<String> response, int statusCode) {
                updateHotCount(response.getCount());
                CredentialManager.setMessageCounterLastUpdated();
                CredentialManager.setMessageCount(response.getCount());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });

    }

    private void updateUserDetails() {

        AuthApiHelper.getUserProfile(CredentialManager.getToken(), new ResponseListenerAuth<CommonDataResponse<UserModel>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<UserModel> response, int statusCode) {
                if (response.getData() != null)
                    CredentialManager.saveUserData(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });


    }

    private void updateHotCount(int count) {
        if (count > 0) {
            binding.messageCount.setVisibility(View.VISIBLE);
            binding.messageCount.setText(String.format("%d", count));
        } else
            binding.messageCount.setVisibility(View.GONE);
    }
}
