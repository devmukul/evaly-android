package bd.com.evaly.evalyshop.ui.main;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.badge.BadgeDrawable;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.data.roomdb.ProviderDatabase;
import bd.com.evaly.evalyshop.databinding.ActivityMainBinding;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.basic.TextBottomSheetFragment;
import bd.com.evaly.evalyshop.ui.cart.CartActivity;
import bd.com.evaly.evalyshop.ui.followedShops.FollowedShopActivity;
import bd.com.evaly.evalyshop.ui.menu.ContactActivity;
import bd.com.evaly.evalyshop.ui.networkError.UnderMaintenanceActivity;
import bd.com.evaly.evalyshop.ui.payment.builder.PaymentWebBuilder;
import bd.com.evaly.evalyshop.ui.payment.listener.PaymentListener;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ToastUtils;
import dagger.hilt.android.AndroidEntryPoint;

import static androidx.navigation.ui.NavigationUI.onNavDestinationSelected;

@AndroidEntryPoint
public class MainActivity extends BaseActivity implements AppController.AppEvent {

    public boolean isLaunchActivity = true;

    @Inject
    PreferenceRepository preferenceRepository;
    @Inject
    ApiRepository apiRepository;
    @Inject
    FirebaseRemoteConfig mFirebaseRemoteConfig;

    private AlertDialog exitDialog;
    private AlertDialog.Builder exitDialogBuilder;
    private NavController navController;
    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppController.getInstance().getPreferenceRepository().isDarkMode())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);

        AppController.setAppEvent(this);

        if (preferenceRepository.getLanguage().equalsIgnoreCase("bn"))
            changeLanguage("BN");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (!preferenceRepository.getToken().equals("") &&
                (preferenceRepository.getUserData() == null ||
                        preferenceRepository.getUserName().equals("") ||
                        preferenceRepository.getPassword().equals(""))) {
            AppController.onLogoutEvent();
            return;
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        if (!preferenceRepository.getToken().isEmpty() && !preferenceRepository.isUserRegistered())
            viewModel.registerXMPP();

        setupNavigation();
        setupBottomNav();
        checkRemoteConfig();
        setupFirebase();
        liveEvents();
        setupDialogs();
        handleNotificationNavigation(getIntent());
        handleOtherIntent();
    }

    private void transparentStatusBarColor() {
        if (getWindow() != null && Build.VERSION.SDK_INT > 23) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void resetStatusBarColor() {
        if (getWindow() != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                int flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                getWindow().getDecorView().setSystemUiVisibility(0);
                if (!preferenceRepository.isDarkMode())
                    getWindow().getDecorView().setSystemUiVisibility(flags);
                getWindow().setStatusBarColor(getColor(R.color.fff));
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                getWindow().getDecorView().setSystemUiVisibility(0);
                getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.black));
            }
        }
    }

    private void setupDialogs() {
        exitDialogBuilder = new AlertDialog.Builder(MainActivity.this)
                .setMessage("Are you sure you want to close the app?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    exitDialog.dismiss();
                    finish();
                })
                .setNegativeButton("No", null);
        exitDialog = exitDialogBuilder.create();

        if (!isConnected(MainActivity.this))
            buildDialog(MainActivity.this).show();
    }


    public void changeLanguage(String lang) {
        Locale myLocale;
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

    }

    public void showDarkModeDialog() {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        CharSequence[] items = new CharSequence[]{"Dark", "Light"};

        int selectedPos = 0;
        if (!preferenceRepository.isDarkMode())
            selectedPos = 1;

        adb.setSingleChoiceItems(items, selectedPos, (d, n) -> {
            preferenceRepository.setDarkMode(n == 0);
            startActivity(new Intent(MainActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finishAffinity();
        });
        adb.setNegativeButton(R.string.cancel, null);
        adb.setTitle("Choose Theme");
        adb.show();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNotificationNavigation(intent);
    }

    private void liveEvents() {

        viewModel.onLogoutResponse.observe(this, aBoolean -> {
            ProviderDatabase providerDatabase = ProviderDatabase.getInstance(getApplicationContext());
            providerDatabase.userInfoDao().deleteAll();
            startActivity(new Intent(this, SignInActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });

        viewModel.getBackOnClick().observe(this, aBoolean -> {
            if (aBoolean)
                onBackPressed();
        });

        viewModel.getDrawerOnClick().observe(this, aBoolean -> {
            if (aBoolean)
                binding.drawerLayout.openDrawer(Gravity.START);
        });

    }

    private void setupBottomNav() {

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    if (item.getItemId() == R.id.accountFragment) {
                        if (navController == null)
                            return false;
                        if (preferenceRepository.getToken().equals(""))
                            startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        else
                            return onNavDestinationSelected(item, navController);
                        return false;
                    } else if (item.getItemId() == R.id.homeFragment) {
                        navController.navigate(R.id.action_homeFragment_Pop);
                        return false;
                    } else
                        return onNavDestinationSelected(item, navController);
                });

        BadgeDrawable wishListBadge = binding.bottomNavigationView.getOrCreateBadge(R.id.wishListFragment);
        if (wishListBadge != null)
            wishListBadge.setVisible(false);

        viewModel.wishListLiveCount.observe(this, integer -> {
            if (wishListBadge != null) {
                if (integer == 0) {
                    wishListBadge.clearNumber();
                    wishListBadge.setVisible(false);
                } else {
                    wishListBadge.setVisible(true);
                    wishListBadge.setNumber(integer);
                }
            }
        });

        BadgeDrawable cartBadge = binding.bottomNavigationView.getOrCreateBadge(R.id.cartFragment);
        if (cartBadge != null)
            cartBadge.setVisible(false);

        viewModel.cartLiveCount.observe(this, integer -> {
            if (cartBadge != null) {
                if (integer == 0) {
                    cartBadge.clearNumber();
                    cartBadge.setVisible(false);
                } else {
                    cartBadge.setVisible(true);
                    cartBadge.setNumber(integer);
                }
            }
        });

    }

    private void setupFirebase() {
        String email = preferenceRepository.getUserName();
        String strNew = email.replaceAll("[^A-Za-z0-9]", "");

        FirebaseMessaging.getInstance().subscribeToTopic("all_user");

        if (!preferenceRepository.getToken().equals("")) {
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.BUILD + "_" + strNew);
            FirebaseMessaging.getInstance().subscribeToTopic("USER." + preferenceRepository.getUserName());
        }
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.BUILD + "_all_user");
    }

    private void handleOtherIntent() {
        Intent data = getIntent();

        if (data.hasExtra("type")) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            int type = data.getIntExtra("type", 1);
            Bundle bundle = new Bundle();

            if (type == 2) {
                isLaunchActivity = false;
                bundle.putInt("type", type);
                bundle.putString("brand_slug", data.getStringExtra("brand_slug"));
                bundle.putString("brand_name", data.getStringExtra("brand_name"));
                bundle.putString("category", data.getStringExtra("category"));
                bundle.putString("image_url", data.getStringExtra("image_url"));
                navController.navigate(R.id.brandFragment, bundle);

            } else if (type == 3 || type == 6) {

                isLaunchActivity = false;
                bundle.putInt("type", type);
                bundle.putString("shop_slug", data.getStringExtra("shop_slug"));
                bundle.putString("shop_name", data.getStringExtra("shop_name"));
                bundle.putString("logo_image", data.getStringExtra("logo_image"));
                bundle.putString("category", data.getStringExtra("category"));
                bundle.putString("groups", data.getStringExtra("groups"));
                bundle.putString("campaign_slug", data.getStringExtra("campaign_slug"));
                navController.navigate(R.id.shopFragment, bundle);

            } else if (type == 4) {

                isLaunchActivity = false;
                Bundle bundle2 = new Bundle();
                bundle2.putInt("type", 1);
                bundle2.putString("slug", data.getStringExtra("category_slug"));
                bundle2.putString("category", "root");
                navController.navigate(R.id.browseProductFragment, bundle);
            }
        }

        if (data.hasExtra("fromBalance")) {
            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.accountFragment);
        }
    }

    private void setupNavigation() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.createDeepLink();
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getLabel() != null && !destination.getLabel().toString().toLowerCase().contains("bottomsheet")) {
                if (destination.getId() == R.id.campaignFragment || destination.getId() == R.id.campaignDetails)
                    transparentStatusBarColor();
                else {
                    resetStatusBarColor();
                }
            }
        });
    }

    private void handleNotificationNavigation(Intent intent) {
        if (intent.hasExtra("notification_type") && intent.getStringExtra("notification_type") != null &&
                intent.hasExtra("resource_id") && intent.getStringExtra("resource_id") != null) {

            String notificationType = intent.getStringExtra("notification_type");
            String resourceId = intent.getStringExtra("resource_id");

            switch (notificationType) {
                case "deeplink":
                    navigateToDeepLink(resourceId);
                    break;
                case "large_text":
                    showLargeTextBottomSheet(resourceId);
                    break;
                case "playstore":
                    openPlaystoreByPackage(resourceId);
                    break;
                case "url":
                    openUrl(resourceId);
                    break;
            }
        }
    }

    private void navigateToDeepLink(String url) {
        try {
            navController.navigate(Uri.parse(url));
        } catch (Exception e) {

        }
    }

    private void showLargeTextBottomSheet(String text) {
        TextBottomSheetFragment textBottomSheetFragment = TextBottomSheetFragment.newInstance(text);
        textBottomSheetFragment.show(getSupportFragmentManager(), "Large text");
    }

    private void openUrl(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
        }
    }

    private void checkRemoteConfig() {
        mFirebaseRemoteConfig
                .fetchAndActivate()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        boolean isForce = mFirebaseRemoteConfig.getBoolean("force_update");
                        boolean shouldLogout = mFirebaseRemoteConfig.getBoolean("logout_on_force_update");
                        boolean isUnderMaintenance = mFirebaseRemoteConfig.getBoolean("under_maintenance");
                        String underMaintenanceMessage = mFirebaseRemoteConfig.getString("under_maintenance_message");

                        int latestVersion = (int) mFirebaseRemoteConfig.getLong("latest_version");
                        int versionCode = BuildConfig.VERSION_CODE;

                        if (isUnderMaintenance) {
                            Intent intent = new Intent(MainActivity.this, UnderMaintenanceActivity.class);
                            intent.putExtra("text", underMaintenanceMessage);
                            startActivity(intent);
                            return;
                        }

                        if (versionCode < latestVersion && isForce) {
                            if (shouldLogout) {
                                preferenceRepository.clearAll();
                            }
                            update(false);
                        } else if (versionCode < latestVersion)
                            update(true);
                    }
                });

    }


    private void getMessageCount(TextView messageCount) {

        if (Calendar.getInstance().getTimeInMillis() - preferenceRepository.getMessageCounterLastUpdated() < 600000) {
            updateHotCount(messageCount, preferenceRepository.getMessageCount());
            return;
        }

        apiRepository.getMessageCount(new ResponseListener<CommonDataResponse<String>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<String> response, int statusCode) {
                updateHotCount(messageCount, response.getCount());
                preferenceRepository.setMessageCounterLastUpdated();
                preferenceRepository.setMessageCount(response.getCount());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });

    }

    private void updateHotCount(TextView messageCount, int count) {
        if (count > 0) {
            messageCount.setVisibility(View.VISIBLE);
            messageCount.setText(String.format("%d", count));
        } else
            messageCount.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (!isLaunchActivity) {
            if (!(navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.shopQuickViewFragment)) {
                finish();
                super.onBackPressed();
            }
        }

        if (navController.getCurrentDestination() != null) {
            if (navController.getCurrentDestination().getId() == R.id.homeFragment) {
                if (isLaunchActivity)
                    exitDialog.show();
                else
                    finish();
            } else
                super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Menu menu = binding.bottomNavigationView.getMenu();
        MenuItem item = menu.getItem(0);
        item.setChecked(true);
        setupDrawerMenu();
    }

    private void setupDrawerMenu() {

        if (preferenceRepository.getToken().equals("") || preferenceRepository.getUserData() == null) {
            binding.drawerLayout.removeView(binding.navView);

            binding.drawerLayout.removeView(binding.navView2);
            binding.drawerLayout.addView(binding.navView2);
            binding.navView2.setNavigationItemSelectedListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.nav_wishlist:
                        navController.navigate(R.id.wishListFragment);
                        break;
                    case R.id.nav_contact:
                        startActivity(new Intent(MainActivity.this, ContactActivity.class));
                        break;
                    case R.id.nav_sign_in:
                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        break;
                    case R.id.nav_home:
                        finish();
                        this.startActivity(getIntent());
                        this.overridePendingTransition(0, 0);
                        break;
                    case R.id.nav_dark_mode:
                        showDarkModeDialog();
                        break;
                    case R.id.nav_terms_conditions:
                        openTermsConditions();
                        break;
                }
                new Handler().postDelayed(() -> binding.drawerLayout.closeDrawer(GravityCompat.START), 150);
                return true;
            });

        } else {
            binding.drawerLayout.removeView(binding.navView2);
            binding.drawerLayout.removeView(binding.navView);
            binding.drawerLayout.addView(binding.navView);

            TextView userNameNavHeader = binding.navView.getHeaderView(0).findViewById(R.id.userNameNavHeader);
            TextView phoneNavHeader = binding.navView.getHeaderView(0).findViewById(R.id.phone);
            userNameNavHeader.setText(preferenceRepository.getUserData().getFullName());
            phoneNavHeader.setText(preferenceRepository.getUserName());

            ImageView profilePicNav = binding.navView.getHeaderView(0).findViewById(R.id.profilePicNav);

            Glide.with(this)
                    .asBitmap()
                    .load(preferenceRepository.getUserData().getProfilePicUrl())
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.user_image)
                    .fitCenter()
                    .centerCrop()
                    .apply(new RequestOptions().override(200, 200))
                    .into(profilePicNav);

            TextView tvMessageCount = binding.navView.getMenu().findItem(R.id.nav_messages).getActionView().findViewById(R.id.count);
            getMessageCount(tvMessageCount);

            binding.navView.setNavigationItemSelectedListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.nav_wishlist:
                        navController.navigate(R.id.wishListFragment);
                        break;
                    case R.id.nav_contact:
                        startActivity(new Intent(MainActivity.this, ContactActivity.class));
                        break;
                    case R.id.nav_home:
                        finish();
                        startActivity(getIntent());
                        break;
                    case R.id.nav_account:
                        navController.navigate(R.id.accountFragment);
                        break;
                    case R.id.nav_orders:
                        navController.navigate(R.id.orderListBaseFragment);
                        break;
                    case R.id.nav_cart:
                        startActivity(new Intent(MainActivity.this, CartActivity.class));
                        break;
//                    case R.id.nav_voucher:
//                        startActivity(new Intent(MainActivity.this, VoucherActivity.class));
//                        break;
                    case R.id.nav_messages:
                        openEconnect();
                        break;
                    case R.id.nav_followed_shops:
                        Intent inf = new Intent(MainActivity.this, FollowedShopActivity.class);
                        inf.putExtra("title", "Followed Shops");
                        inf.putExtra("slug", "shop-subscriptions");
                        startActivity(inf);
                        break;
                    case R.id.nav_dark_mode:
                        showDarkModeDialog();
                        break;
                    case R.id.nav_terms_conditions:
                        openTermsConditions();
                        break;
                }
                new Handler().postDelayed(() -> binding.drawerLayout.closeDrawer(GravityCompat.START), 150);
                return true;
            });
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void openTermsConditions() {
        PaymentWebBuilder paymentWebBuilder = new PaymentWebBuilder(this);
        paymentWebBuilder.setToolbarTitle(getString(R.string.terms_amp_conditions));
        paymentWebBuilder.setPaymentListener(new PaymentListener() {
            @Override
            public void onPaymentSuccess(HashMap<String, String> values) {

            }

            @Override
            public void onPaymentFailure(HashMap<String, String> values) {

            }

            @Override
            public void onPaymentSuccess(String message) {

            }
        });
        paymentWebBuilder.loadPaymentURL(BuildConfig.WEB_URL + "about/terms-conditions?is_mobile=true", "adsaaswetrs", null);
    }

    private void openEconnect() {
        Intent launchIntent = new Intent("bd.com.evaly.econnect.OPEN_MAINACTIVITY");
        try {
            if (launchIntent != null) {
                launchIntent.putExtra("to", "OPEN_CHAT_LIST");
                launchIntent.putExtra("user", preferenceRepository.getUserName());
                launchIntent.putExtra("password", preferenceRepository.getPassword());
                launchIntent.putExtra("userInfo", new Gson().toJson(preferenceRepository.getUserData()));
                startActivity(launchIntent);
            }
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "bd.com.evaly.econnect")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "bd.com.evaly.econnect")));
            } catch (Exception e4) {
                ToastUtils.show("Please install eConnect app from Playstore");
            }
        } catch (Exception e5) {
            ToastUtils.show("Please install eConnect app from Playstore");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (exitDialog != null && exitDialog.isShowing()) {
            exitDialog.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        if (exitDialog != null && exitDialog.isShowing()) {
            exitDialog.cancel();
        }
        super.onDestroy();
    }

    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public AlertDialog.Builder buildDialog(final Context c) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        final String[] a = {"Turn on Wi-Fi", "Turn on Mobile Data"};
        final int[] select = new int[1];
        builder.setSingleChoiceItems(a, 0, (dialogInterface, i) -> {
            if (i == 0)
                select[0] = 1;
            else if (i == 1)
                select[0] = 2;
        });
        builder.setPositiveButton("OK", (dialog, which) -> {
            if (select[0] == 1) {
                WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);
                Toast.makeText(MainActivity.this, "Turning on WiFi...", Toast.LENGTH_SHORT).show();

            } else if (select[0] == 2) {
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            } else {
                WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);
                Toast.makeText(MainActivity.this, "Turning on WiFi...", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        return builder;
    }

    private void update(boolean isCancelable) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle("New update available!");
        builder.setMessage("Please update your app");
        builder.setCancelable(isCancelable);
        builder.setPositiveButton("Update", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            finish();
            final String appPackageName = getPackageName();
            openPlaystoreByPackage(appPackageName);
        });

        if (isCancelable)
            builder.setNegativeButton("No", ((dialogInterface, i) -> dialogInterface.dismiss()));

        android.app.AlertDialog dialog = builder.create();
        if (!isFinishing() && !isDestroyed())
            dialog.show();
    }


    private void openPlaystoreByPackage(String appPackageName) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @Override
    public void onLogout() {
        if (viewModel != null)
            viewModel.onLogoutFromServer();
    }

}
