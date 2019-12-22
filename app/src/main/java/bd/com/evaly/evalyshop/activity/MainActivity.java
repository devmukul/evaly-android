package bd.com.evaly.evalyshop.activity;

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
import android.util.Log;
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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.chat.ChatListActivity;
import bd.com.evaly.evalyshop.fragment.BrandFragment;
import bd.com.evaly.evalyshop.fragment.BrowseProductFragment;
import bd.com.evaly.evalyshop.fragment.HomeFragment;
import bd.com.evaly.evalyshop.fragment.ShopFragment;
import bd.com.evaly.evalyshop.fragment.WishListFragment;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.xmpp.SignupModel;
import bd.com.evaly.evalyshop.preference.MyPreference;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.service.XmppConnectionIntentService;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.database.DbHelperCart;
import bd.com.evaly.evalyshop.util.database.DbHelperWishList;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.xmpp.XMPPService;
import bd.com.evaly.evalyshop.xmpp.XmppCustomEventListener;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private BottomNavigationView bottomNavigationView;
    public DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView, navigationView2;
    private UserDetails userDetails;
    private TextView userNameNavHeader;
    private TextView phoneNavHeader;
    private DbHelperWishList dbHelperWishList;
    private DbHelperCart dbHelperCart;
    public boolean isLaunchActivity = true;
    private View headerView;
    private AppController mChatApp = AppController.getInstance();
    private XMPPHandler xmppHandler;


    private NavGraph navGraph;
    private NavController navController;
    private NavInflater navInflater;
    private NavHostFragment navHost;


    private XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {

        @Override
        public void onConnected() {
            xmppHandler = AppController.getmService().xmpp;
            xmppHandler.setUserPassword(CredentialManager.getUserName(), CredentialManager.getPassword());
            xmppHandler.login();
        }

        //Event Listeners
        public void onLoggedIn() {

            if (xmppHandler != null) {
                CredentialManager.saveUserRegistered(true);
                if (xmppHandler.isLoggedin()) {
                    VCard vCard = xmppHandler.mVcard;
                    if (CredentialManager.getUserData() != null) {
                        if (vCard != null) {
                            if (vCard.getFirstName() == null || vCard.getLastName() == null) {
                                Logger.d("========");
                                xmppHandler.updateUserInfo(CredentialManager.getUserData());
                            }
                            disconnectXmpp();
                        }
                    }
                }
            }
        }

        public void onLoginFailed(String msg) {
            Logger.d(msg);
            if (!msg.contains("already logged in")) {
                if (xmppHandler == null){
                    xmppHandler = AppController.getmService().xmpp;
                }
                if (xmppHandler.isConnected()){
                    xmppHandler.Signup(new SignupModel(CredentialManager.getUserName(), CredentialManager.getPassword(), CredentialManager.getPassword()));
                }
            } else {
                CredentialManager.saveUserRegistered(true);
                disconnectXmpp();
            }
        }

        public void onSignupSuccess() {
            Logger.d("Signup success");

            xmppHandler.setUserPassword(CredentialManager.getUserName(), CredentialManager.getPassword());
            xmppHandler.login();
            disconnectXmpp();
        }

        public void onSignupFailed(String msg) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);


        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        navigationView2 = findViewById(R.id.nav_view2);
        headerView = navigationView.getHeaderView(0);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        userNameNavHeader = headerView.findViewById(R.id.userNameNavHeader);
        phoneNavHeader = headerView.findViewById(R.id.phone);
        userDetails = new UserDetails(this);


        // check for update

        checkUpdate();

        dbHelperWishList = new DbHelperWishList(this);
        dbHelperCart = new DbHelperCart(this);
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(R.id.nav_wishlist);
        BottomNavigationItemView itemView2 = bottomNavigationView.findViewById(R.id.nav_cart);
        View badge = LayoutInflater.from(MainActivity.this).inflate(R.layout.bottom_navigation_notification, bottomNavigationView, false);
        TextView text = badge.findViewById(R.id.notification);
        text.setText(dbHelperWishList.size() + "");
        itemView.addView(badge);

        if (dbHelperWishList.size() == 0) {
            badge.setVisibility(View.GONE);
        }

        View badge2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.bottom_navigation_notification, bottomNavigationView, false);
        TextView text2 = badge2.findViewById(R.id.notification);
        text2.setText(dbHelperCart.size() + "");
        itemView2.addView(badge2);

        if (dbHelperCart.size() == 0) {
            badge2.setVisibility(View.GONE);
        }

        Handler handler = new Handler();
        int delay = 3000;
        handler.postDelayed(new Runnable() {
            public void run() {
                if (dbHelperWishList.size() == 0) {
                    badge.setVisibility(View.GONE);
                } else {
                    badge.setVisibility(View.VISIBLE);
                }
                if (dbHelperCart.size() == 0) {
                    badge2.setVisibility(View.GONE);
                } else {
                    badge2.setVisibility(View.VISIBLE);
                }
                text.setText(dbHelperWishList.size() + "");
                text2.setText(dbHelperCart.size() + "");
                handler.postDelayed(this, delay);
            }
        }, delay);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        if (!isConnected(MainActivity.this)) {
            buildDialog(MainActivity.this).show();
        }

        if (!CredentialManager.getToken().equals("")) {
            if (CredentialManager.getUserName().equals("") || CredentialManager.getPassword().equals("")) {
                AppController.logout(MainActivity.this);
            } else {
                if (!CredentialManager.getToken().equals("") && !CredentialManager.isUserRegistered()) {
                    Logger.d("===========");
                    if (AppController.getInstance().isNetworkConnected()) {
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                Logger.d("START_SERVICE");
                                startXmppService();
                            }
                        });
                    }
                }
            }
        }


        FirebaseMessaging.getInstance().subscribeToTopic("all_user");
        String email = CredentialManager.getUserName();
        String strNew = email.replaceAll("[^A-Za-z0-9]", "");

//        Logger.d(strNew);
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(Constants.BUILD + "_" + strNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Logger.d(task.isSuccessful());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (userDetails.getToken().equals("")) {
            drawer.removeView(navigationView);
            navigationView2.setNavigationItemSelectedListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.nav_wishlist:
                        startActivity(new Intent(MainActivity.this, WishListActivity.class));
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

                new Handler().postDelayed(() -> drawer.closeDrawer(GravityCompat.START), 150);
                return true;
            });


        } else {


            FirebaseMessaging.getInstance().subscribeToTopic("USER." + userDetails.getUserName());

            userNameNavHeader.setText(userDetails.getFirstName() + " " + userDetails.getLastName());
            phoneNavHeader.setText(userDetails.getUserName());
            drawer.removeView(navigationView2);
            navigationView.setNavigationItemSelectedListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.nav_wishlist:
                        startActivity(new Intent(MainActivity.this, WishListActivity.class));
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
                new Handler().postDelayed(() -> drawer.closeDrawer(GravityCompat.START), 150);
                return true;
            });
        }


        final FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            Intent intent;
            switch (menuItem.getItemId()) {
                case R.id.nav_home:

                    try {

                        WishListFragment myFragment = (WishListFragment) fragmentManager.findFragmentByTag("wishlist");
                        if (myFragment != null && myFragment.isVisible()) {
                            ft.hide(fragmentWishlist);
                            ft.show(fragmentHome);
                            ft.commit();
                        } else {

                            while (fragmentManager.getBackStackEntryCount() > 0) {
                                fragmentManager.popBackStackImmediate();
                            }
                            showHomeFragment();
                        }
                    } catch (Exception e){

                        showHomeFragment();

                    }

                    break;
                case R.id.nav_wishlist:

                    try {
                        Fragment fragmentW = fragmentManager.findFragmentByTag("wishlist");
                        if (fragmentW == null) {

                            ft.add(R.id.fragment_container, fragmentWishlist, "wishlist");
                            ft.addToBackStack("wishlist");
                        }

                        ft.hide(fragmentHome);
                        ft.show(fragmentWishlist);
                        ft.commit();
                    } catch (Exception e){

                        startActivity(new Intent(this, WishListActivity.class));
                    }

                    break;
                case R.id.nav_cart:

                    intent = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_dashboard:
                    if (userDetails.getToken().equals("")) {
                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, UserDashboardActivity.class));
                    }
                    break;


            }
            return true;
        });


        Intent data = getIntent();

        if (data.hasExtra("type")) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            int type = data.getIntExtra("type", 1);
            Bundle bundle = new Bundle();

            if (type == 2) {

                isLaunchActivity = false;

                Log.d("json", data.getStringExtra("brand_name"));
                bundle.putInt("type", type);
                bundle.putString("brand_slug", data.getStringExtra("brand_slug"));
                bundle.putString("brand_name", data.getStringExtra("brand_name"));
                bundle.putString("category", data.getStringExtra("category"));
                bundle.putString("image_url", data.getStringExtra("image_url"));
                Fragment fragment3 = new BrandFragment();
                fragment3.setArguments(bundle);
                ft.replace(R.id.fragment_container, fragment3, data.getStringExtra("slug"));
                ft.addToBackStack(null);
                ft.commit();

            } else if (type == 3 || type == 6) {

                isLaunchActivity = false;
                bundle.putInt("type", type);
                bundle.putString("shop_slug", data.getStringExtra("shop_slug"));
                bundle.putString("shop_name", data.getStringExtra("shop_name"));
                bundle.putString("logo_image", data.getStringExtra("logo_image"));
                bundle.putString("category", data.getStringExtra("category"));
                bundle.putString("groups", data.getStringExtra("groups"));
                bundle.putString("campaign_slug", data.getStringExtra("campaign_slug"));
                Fragment fragment3 = new ShopFragment();
                fragment3.setArguments(bundle);
                ft.replace(R.id.fragment_container, fragment3, data.getStringExtra("slug"));
                ft.addToBackStack(null);
                ft.commit();
            } else if (type == 4) {

                isLaunchActivity = false;

                Fragment fragment3 = new BrowseProductFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putInt("type", 1);
                bundle2.putString("slug", data.getStringExtra("category_slug"));
                bundle2.putString("category", "root");
                fragment3.setArguments(bundle2);
                ft.setCustomAnimations(R.animator.slide_in_left, R.animator.abc_popup_exit, 0, 0);
                ft.replace(R.id.fragment_container, fragment3, data.getStringExtra("category_slug"));
                ft.addToBackStack(null);
                ft.commit();

            }

        } else {

            showHomeFragment();
        }

        if (data.hasExtra("fromBalance")) {
            Intent ip = new Intent(this, UserDashboardActivity.class);
            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();
            startActivity(ip);
        }

        exitDialogBuilder = new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to close the app?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    exitDialog.dismiss();
                    finish();
                })
                .setNegativeButton("No", null);
        exitDialog = exitDialogBuilder.create();

    }



    private HomeFragment fragmentHome = HomeFragment.newInstance();
    private WishListFragment fragmentWishlist = WishListFragment.newInstance();


    public void showHomeFragment() {

        navController.navigate(R.id.homeFragment);



//        try {
//            fragmentHome = HomeFragment.newInstance();
//
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            // ft.setCustomAnimations(R.animator.slide_in_left,R.animator.abc_popup_exit, 0, 0);
//            ft.replace(R.id.fragment_container, fragmentHome, "fragmentHome");
//            ft.setReorderingAllowed(true);
//            ft.addToBackStack("fragmentHome");
//            ft.commit();
//        } catch (Exception e) {
//
//        }
    }


    public void changeFragment(Fragment fragment, String tagFragmentName) {

        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        Fragment currentFragment = mFragmentManager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            fragmentTransaction.detach(currentFragment);
        }

        Fragment fragmentTemp = mFragmentManager.findFragmentByTag(tagFragmentName);
        if (fragmentTemp == null) {
            fragmentTemp = fragment;
            fragmentTransaction.add(R.id.fragment_container, fragmentTemp, tagFragmentName);
        } else {
            fragmentTransaction.attach(fragmentTemp);
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();
    }


    AlertDialog exitDialog;
    AlertDialog.Builder exitDialogBuilder;

    public UserDetails getUserDetails() {

        return userDetails;

    }


    private void startXmppService() {
        startService(new Intent(MainActivity.this, XmppConnectionIntentService.class));
    }

    private void disconnectXmpp(){
        XMPPHandler.disconnect();
        stopService(new Intent(MainActivity.this, XMPPService.class));
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 1) {

            // this one works

            if (isLaunchActivity) {
                exitDialog.show();
            } else {
                super.onBackPressed();
                finish();
            }

        } else {
            getSupportFragmentManager().popBackStack();
        }

        HomeFragment test = (HomeFragment) getSupportFragmentManager().findFragmentByTag("Home");
        if (test != null && test.isVisible()) {

            if (isLaunchActivity) {
                exitDialog.show();
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mChatApp.getEventReceiver().setListener(xmppCustomEventListener);
        if (bottomNavigationView != null) {
            Menu menu = bottomNavigationView.getMenu();
            MenuItem item = menu.getItem(0);
            item.setChecked(true);
        }


        if (userDetails.getToken() != null || !userDetails.getToken().isEmpty()) {

            ImageView profilePicNav = headerView.findViewById(R.id.profilePicNav);


            if (!userDetails.getProfilePictureSM().equals("null")) {
                Glide.with(this)
                        .asBitmap()
                        .load(userDetails.getProfilePictureSM())
                        .skipMemoryCache(true)
                        .fitCenter()
                        .centerCrop()
                        .apply(new RequestOptions().override(200, 200))
                        .into(profilePicNav);
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

    // 2) :
    @Override
    protected void onDestroy() {
        if (exitDialog != null && exitDialog.isShowing()) {
            exitDialog.cancel();
        }
        if (dbHelperCart != null) {
            dbHelperCart.close();
        }
        if (dbHelperWishList != null) {
            dbHelperWishList.close();
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



    private void checkUpdate(){


        int versionCode = BuildConfig.VERSION_CODE;

        AuthApiHelper.checkUpdate(new DataFetchingListener<Response<JsonObject>>() {
            @Override
            public void onDataFetched(Response<JsonObject> response) {
                if (response.code() == 200 || response.code() == 201){
                    try {
                        String version = response.body().getAsJsonObject("data").getAsJsonObject("Evaly Android").get("version").getAsString();
                        boolean isForce = response.body().getAsJsonObject("data").getAsJsonObject("Evaly Android").get("force").getAsBoolean();
                        int v = Integer.parseInt(version);

                        if (versionCode < v && isForce){
                            userDetails.clearAll();
                            MyPreference.with(MainActivity.this).clearAll();
                            update(false);
                        } else if (versionCode < v){

                            update(true);

                        }
                    }catch (Exception e){
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
