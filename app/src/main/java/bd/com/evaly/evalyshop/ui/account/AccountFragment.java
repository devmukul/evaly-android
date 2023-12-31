package bd.com.evaly.evalyshop.ui.account;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.Locale;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.FragmentAccountBinding;
import bd.com.evaly.evalyshop.ui.auth.changePassword.ChangePasswordActivity;
import bd.com.evaly.evalyshop.ui.balance.BalanceFragment;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.evalyPoint.EvalyPointsViewModel;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.user.editProfile.EditProfileActivity;
import bd.com.evaly.evalyshop.util.BindingUtils;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AccountFragment extends BaseFragment<FragmentAccountBinding, AccountViewModel> {

    @Inject
    PreferenceRepository preferenceRepository;
    private EvalyPointsViewModel pointsViewModel;

    public AccountFragment() {
        super(AccountViewModel.class, R.layout.fragment_account);
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pointsViewModel = new ViewModelProvider(this).get(EvalyPointsViewModel.class);
    }

    @Override
    protected void initViews() {
        updateViews();
    }

    @Override
    protected void liveEventsObservers() {
        pointsViewModel.pointsLiveData.observe(getViewLifecycleOwner(), integer -> {
            BindingUtils.bindPointsView(binding.pointGraph, integer, true);
        });
        viewModel.messageCount.observe(getViewLifecycleOwner(), integer -> {
            updateHotCount(integer);
        });
    }

    private void updateViews() {
        if (preferenceRepository.getUserData() != null) {
            binding.name.setText(preferenceRepository.getUserData().getFullName());
        }
    }

    @Override
    protected void clickListeners() {

        binding.pointsHolder.setOnClickListener(view -> navController.navigate(R.id.evalyPointsFragment));

        binding.refundSettlement.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.preOtpFragment);
        });

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
            if (preferenceRepository.getLanguage().equalsIgnoreCase("bn"))
                selectedPos = 1;

            adb.setSingleChoiceItems(items, selectedPos, (d, n) -> {
                Locale myLocale;
                if (n == 1) {
                    preferenceRepository.setLanguage("BN");
                    myLocale = new Locale("BN");
                } else {
                    preferenceRepository.setLanguage("EN");
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
            AppController.onLogoutEvent();
            return false;
        });
        binding.toolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
    }

    private void openEconnect() {

        try {
            Intent launchIntent = new Intent("bd.com.evaly.econnect.OPEN_MAINACTIVITY");
            launchIntent.putExtra("to", "OPEN_CHAT_LIST");
            launchIntent.putExtra("user", preferenceRepository.getUserName());
            launchIntent.putExtra("password", preferenceRepository.getPassword());
            launchIntent.putExtra("userInfo", new Gson().toJson(preferenceRepository.getUserData()));
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
        if (preferenceRepository.getUserData() != null) {
            if (preferenceRepository.getUserData().getProfilePicUrl() != null) {
                Glide.with(this)
                        .asBitmap()
                        .load(preferenceRepository.getUserData().getProfilePicUrl())
                        .skipMemoryCache(true)
                        .fitCenter()
                        .placeholder(R.drawable.user_image)
                        .centerCrop()
                        .apply(new RequestOptions().override(200, 200))
                        .into(binding.picture);
            }
            binding.name.setText(preferenceRepository.getUserData().getFullName());
            binding.phoneNumber.setText(preferenceRepository.getUserName());
        }
    }

    private void updateHotCount(Integer count) {
        if (count != null && count > 0) {
            binding.messageCount.setVisibility(View.VISIBLE);
            binding.messageCount.setText(String.format("%d", count));
        } else
            binding.messageCount.setVisibility(View.GONE);
    }
}
