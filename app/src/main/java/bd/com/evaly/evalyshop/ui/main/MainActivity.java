package bd.com.evaly.evalyshop.ui.main;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.Locale;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListDao;
import bd.com.evaly.evalyshop.databinding.ActivityMainBinding;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.xmpp.SignupModel;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.service.XmppConnectionIntentService;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.base.BaseActivity;
import bd.com.evaly.evalyshop.ui.campaign.CampaignShopActivity;
import bd.com.evaly.evalyshop.ui.cart.CartActivity;
import bd.com.evaly.evalyshop.ui.chat.ChatListActivity;
import bd.com.evaly.evalyshop.ui.menu.ContactActivity;
import bd.com.evaly.evalyshop.ui.menu.InviteEarn;
import bd.com.evaly.evalyshop.ui.order.orderList.OrderListActivity;
import bd.com.evaly.evalyshop.ui.user.UserDashboardActivity;
import bd.com.evaly.evalyshop.ui.voucher.VoucherActivity;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.preference.MyPreference;
import bd.com.evaly.evalyshop.util.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.util.xmpp.XMPPService;
import bd.com.evaly.evalyshop.util.xmpp.XmppCustomEventListener;
import retrofit2.Response;

import static androidx.navigation.ui.NavigationUI.onNavDestinationSelected;

public class MainActivity extends BaseActivity {

    public boolean isLaunchActivity = true;
    private AlertDialog exitDialog;
    private AlertDialog.Builder exitDialogBuilder;
    private UserDetails userDetails;
    private AppController mChatApp = AppController.getInstance();
    private XMPPHandler xmppHandler;
    private NavController navController;
    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    private XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {
        @Override
        public void onConnected() {
            if (AppController.getmService() != null) {
                xmppHandler = AppController.getmService().xmpp;
                xmppHandler.setUserPassword(CredentialManager.getUserName(), CredentialManager.getPassword());
                xmppHandler.login();
            }
        }

        public void onLoggedIn() {
            if (xmppHandler != null) {
                CredentialManager.saveUserRegistered(true);
                if (xmppHandler.isLoggedin()) {
                    VCard vCard = xmppHandler.mVcard;
                    if (CredentialManager.getUserData() != null) if (vCard != null) {
                        if (vCard.getFirstName() == null || vCard.getLastName() == null)
                            xmppHandler.updateUserInfo(CredentialManager.getUserData());
                        disconnectXmpp();
                    }
                }
            }
        }

        public void onLoginFailed(String msg) {
            Logger.d(msg);
            if (!msg.contains("already logged in")) {

                if (xmppHandler == null) {
                    if (AppController.getmService() != null)
                        if (AppController.getmService().xmpp != null)
                            xmppHandler = AppController.getmService().xmpp;
                } else {
                    if (xmppHandler.isConnected())
                        xmppHandler.Signup(new SignupModel(CredentialManager.getUserName(), CredentialManager.getPassword(), CredentialManager.getPassword()));
                }

            } else {
                CredentialManager.saveUserRegistered(true);
                disconnectXmpp();
            }
        }

        public void onSignupSuccess() {
            xmppHandler.setUserPassword(CredentialManager.getUserName(), CredentialManager.getPassword());
            xmppHandler.login();
            disconnectXmpp();
        }

        public void onSignupFailed(String msg) {

        }
    };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (CredentialManager.getLanguage().equalsIgnoreCase("bn"))
            changeLanguage("BN");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView userNameNavHeader = binding.navView.getHeaderView(0).findViewById(R.id.userNameNavHeader);
        TextView phoneNavHeader = binding.navView.getHeaderView(0).findViewById(R.id.phone);
        userDetails = new UserDetails(this);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {

        });

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    if (item.getItemId() == R.id.userDashboardActivity) {
                        if (userDetails.getToken().equals(""))
                            startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        else
                            startActivity(new Intent(MainActivity.this, UserDashboardActivity.class));
                        return false;
                    } else if (item.getItemId() == R.id.homeFragment) {

                        // navController.popBackStack(R.id.home_nav_graph, true);
                        navController.navigate(R.id.action_homeFragment_Pop);
                        return false;
                    } else
                        return onNavDestinationSelected(item, navController);
                });


        checkUpdate();

        AppDatabase appDatabase = AppDatabase.getInstance(this);
        WishListDao wishListDao = appDatabase.wishListDao();
        CartDao cartDao = appDatabase.cartDao();


        BottomNavigationItemView itemView = binding.bottomNavigationView.findViewById(R.id.wishListFragment);
        View wishListBadge = LayoutInflater.from(MainActivity.this).inflate(R.layout.bottom_navigation_notification, binding.bottomNavigationView, false);
        TextView wishListCount = wishListBadge.findViewById(R.id.notification);
        itemView.addView(wishListBadge);

        wishListDao.getLiveCount().observe(this, integer -> {
            wishListCount.setText(String.format(Locale.ENGLISH, "%d", integer));
            if (integer == 0)
                wishListBadge.setVisibility(View.GONE);
            else
                wishListBadge.setVisibility(View.VISIBLE);
        });

        BottomNavigationItemView itemView2 = binding.bottomNavigationView.findViewById(R.id.cartFragment);
        View cartBadge = LayoutInflater.from(MainActivity.this).inflate(R.layout.bottom_navigation_notification, binding.bottomNavigationView, false);
        TextView cartCount = cartBadge.findViewById(R.id.notification);
        itemView2.addView(cartBadge);

        cartDao.getLiveCount().observe(this, integer -> {
            cartCount.setText(String.format(Locale.ENGLISH, "%d", integer));
            if (integer == 0)
                cartBadge.setVisibility(View.GONE);
            else
                cartBadge.setVisibility(View.VISIBLE);
        });


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (!isConnected(MainActivity.this)) {
            buildDialog(MainActivity.this).show();
        }

        if (!CredentialManager.getToken().equals("")) {
            if (CredentialManager.getUserName().equals("") || CredentialManager.getPassword().equals(""))
                AppController.logout(MainActivity.this);
            else {
                if (!CredentialManager.getToken().equals("") && !CredentialManager.isUserRegistered())
                    if (AppController.getInstance().isNetworkConnected())
                        AsyncTask.execute(() -> startXmppService());
            }
        }

        String email = CredentialManager.getUserName();
        String strNew = email.replaceAll("[^A-Za-z0-9]", "");

        FirebaseMessaging.getInstance().subscribeToTopic("all_user");

        if (!CredentialManager.getToken().equals(""))
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.BUILD + "_" + strNew).addOnCompleteListener(task -> Logger.d(task.isSuccessful() + " " + Constants.BUILD + "_" + strNew));
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.BUILD + "_all_user").addOnCompleteListener(task -> Logger.d(task.isSuccessful() + " " + Constants.BUILD + "_all_user"));


        if (userDetails.getToken().equals("")) {
            binding.drawerLayout.removeView(binding.navView);
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
                }
                new Handler().postDelayed(() -> binding.drawerLayout.closeDrawer(GravityCompat.START), 150);
                return true;
            });


        } else {

            FirebaseMessaging.getInstance().subscribeToTopic("USER." + userDetails.getUserName());

            userNameNavHeader.setText(userDetails.getFirstName() + " " + userDetails.getLastName());
            phoneNavHeader.setText(userDetails.getUserName());
            binding.drawerLayout.removeView(binding.navView2);
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
                        startActivity(new Intent(MainActivity.this, UserDashboardActivity.class));
                        break;
                    case R.id.nav_orders:
                        startActivity(new Intent(MainActivity.this, OrderListActivity.class));
                        break;
                    case R.id.nav_cart:
                        startActivity(new Intent(MainActivity.this, CartActivity.class));
                        break;
                    case R.id.nav_invite_ref:
                        startActivity(new Intent(MainActivity.this, InviteEarn.class));
                        break;
                    case R.id.nav_voucher:
                        startActivity(new Intent(MainActivity.this, VoucherActivity.class));
                        break;
                    case R.id.nav_messages:
                        startActivity(new Intent(MainActivity.this, ChatListActivity.class));
                        break;
                    case R.id.nav_followed_shops:
                        Intent inf = new Intent(MainActivity.this, CampaignShopActivity.class);
                        inf.putExtra("title", "Followed ShopDetails");
                        inf.putExtra("slug", "shop-subscriptions");
                        startActivity(inf);
                }
                new Handler().postDelayed(() -> binding.drawerLayout.closeDrawer(GravityCompat.START), 150);
                return true;
            });
        }


        viewModel.getBackOnClick().observe(this, aBoolean -> {
            if (aBoolean)
                onBackPressed();
        });
        viewModel.getDrawerOnClick().observe(this, aBoolean -> {
            if (aBoolean)
                binding.drawerLayout.openDrawer(Gravity.START);
        });

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
            Intent ip = new Intent(this, UserDashboardActivity.class);
            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();
            startActivity(ip);
        }

        exitDialogBuilder = new AlertDialog.Builder(MainActivity.this)
                .setMessage("Are you sure you want to close the app?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    exitDialog.dismiss();
                    finish();
                })
                .setNegativeButton("No", null);
        exitDialog = exitDialogBuilder.create();
    }

    public UserDetails getUserDetails() {

        return userDetails;
    }

    private void startXmppService() {
        startService(new Intent(MainActivity.this, XmppConnectionIntentService.class));
    }

    private void disconnectXmpp() {
        XMPPHandler.disconnect();
        stopService(new Intent(MainActivity.this, XMPPService.class));
    }

    @Override
    public void onBackPressed() {
        if (!isLaunchActivity) {
            finish();
            super.onBackPressed();
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

        mChatApp.getEventReceiver().setListener(xmppCustomEventListener);
        if (binding.bottomNavigationView != null) {
            Menu menu = binding.bottomNavigationView.getMenu();
            MenuItem item = menu.getItem(0);
            item.setChecked(true);
        }

        if (userDetails.getToken() != null || !userDetails.getToken().isEmpty()) {
            ImageView profilePicNav = binding.navView.getHeaderView(0).findViewById(R.id.profilePicNav);
            if (!userDetails.getProfilePictureSM().equals("null")) {
                Glide.with(this)
                        .asBitmap()
                        .load(userDetails.getProfilePictureSM())
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.user_image)
                        .fitCenter()
                        .centerCrop()
                        .apply(new RequestOptions().override(200, 200))
                        .into(profilePicNav);
            }
        }

    }

    private void unCheckAllMenuItems(@NonNull final Menu menu) {
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            final MenuItem item = menu.getItem(i);
            if (item.hasSubMenu()) {
                // Un check sub menu items
                unCheckAllMenuItems(item.getSubMenu());
            } else {
                item.setChecked(false);
            }
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

        disconnectXmpp();
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

    private void checkUpdate() {

        int versionCode = BuildConfig.VERSION_CODE;

        AuthApiHelper.checkUpdate(new DataFetchingListener<Response<JsonObject>>() {
            @Override
            public void onDataFetched(Response<JsonObject> response) {
                if (response.code() == 200 || response.code() == 201) {
                    try {
                        String version = response.body().getAsJsonObject("data").getAsJsonObject("Evaly Android").get("version").getAsString();
                        boolean isForce = response.body().getAsJsonObject("data").getAsJsonObject("Evaly Android").get("force").getAsBoolean();
                        int v = Integer.parseInt(version);

                        if (versionCode < v && isForce) {
                            userDetails.clearAll();
                            MyPreference.with(MainActivity.this).clearAll();
                            update(false);
                        } else if (versionCode < v)
                            update(true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(int status) {

            }
        });
    }

    private void update(boolean isCancelable) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle("New update available!");
        builder.setMessage("Please update your app");
        builder.setCancelable(isCancelable);
        builder.setPositiveButton("Update", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            finish();
            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });

        if (isCancelable)
            builder.setNegativeButton("No", ((dialogInterface, i) -> dialogInterface.dismiss()));

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

    }
}
