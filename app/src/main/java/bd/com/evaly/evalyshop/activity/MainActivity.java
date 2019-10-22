package bd.com.evaly.evalyshop.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.FacebookSdk;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonPrimitive;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.HashMap;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.chat.ChatListActivity;
import bd.com.evaly.evalyshop.fragment.BrandFragment;
import bd.com.evaly.evalyshop.fragment.BrowseProductFragment;
import bd.com.evaly.evalyshop.fragment.HomeFragment;
import bd.com.evaly.evalyshop.fragment.ShopFragment;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.SignupModel;
import bd.com.evaly.evalyshop.service.XmppConnectionIntentService;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.Token;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.util.database.DbHelperCart;
import bd.com.evaly.evalyshop.util.database.DbHelperWishList;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.xmpp.XMPPService;
import bd.com.evaly.evalyshop.xmpp.XmppCustomEventListener;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    HomeFragment homeFragment;
    BottomNavigationView bottomNavigationView;
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView,navigationView2;
    ImageView menuBtn, user;
    String TAG1 = "first", TAG2 = "second", TAG3 = "third";
    UserDetails userDetails;
    TextView userNameNavHeader;
    TextView phoneNavHeader;
    DbHelperWishList dbHelperWishList;
    DbHelperCart dbHelperCart;
    boolean isLaunchActivity = true;
    private View headerView;

    private AppController mChatApp = AppController.getInstance();
    private XMPPHandler xmppHandler;
    ViewDialog dialog;
//    private SessionManager sessionManager;

    private XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {


        @Override
        public void onConnected() {
            xmppHandler = AppController.getmService().xmpp;
            xmppHandler.setUserPassword(CredentialManager.getUserName(), CredentialManager.getPassword());
            xmppHandler.login();
        }

        //Event Listeners
        public void onLoggedIn() {

            Logger.d("LOGIN =========");
            Logger.d(xmppHandler.isConnected());
            VCard vCard = xmppHandler.getCurrentUserDetails();
//            Logger.d(vCard.getFirstName());
            if (CredentialManager.getUserData() != null){
                if ( vCard.getLastName() == null){
                    Logger.d("========");
                    xmppHandler.updateUserInfo(CredentialManager.getUserData());
                }
            }

        }

        public void onLoginFailed(String msg) {
            if (!msg.contains("already logged in")){
                xmppHandler.Signup(new SignupModel(CredentialManager.getUserName(), CredentialManager.getPassword(), CredentialManager.getPassword()), CredentialManager.getUserData().getFirst_name());
            }
        }

        public void onSignupSuccess(){
            Logger.d("Signup success");

//            HashMap<String, String> data = new HashMap<>();
//            data.put("localuser", CredentialManager.getUserName());
//            data.put("localserver", Constants.XMPP_HOST);
//            data.put("user", "09638111666");
//            data.put("server", Constants.XMPP_HOST);
//            data.put("nick", "Evaly");
//            data.put("subs", "both");
//            data.put("group", "evaly");
//            addRosterByOther();
//
//            AuthApiHelper.addRoster(data, new DataFetchingListener<Response<JsonPrimitive>>() {
//                @Override
//                public void onDataFetched(Response<JsonPrimitive> response) {
//                    dialog.hideDialog();
//                    if (response.code() == 200 || response.code() == 201) {
//                        try {
//                            EntityBareJid jid = JidCreate.entityBareFrom("09638111666" + "@"
//                                    + Constants.XMPP_HOST);
//
//                            ChatItem chatItem = new ChatItem("Let's start a conversation", CredentialManager.getUserData().getFirst_name()+" "+CredentialManager.getUserData().getLast_name(), xmppHandler.mVcard.getField("URL"), xmppHandler.mVcard.getNickName(), System.currentTimeMillis(), xmppHandler.mVcard.getFrom().asBareJid().toString(), jid.asUnescapedString() , Constants.TYPE_TEXT, true, "");
//
//                            try {
//                                xmppHandler.sendMessage(chatItem);
//                            } catch (SmackException e) {
//                                e.printStackTrace();
//                            }
//                            RosterTable table = new RosterTable();
//                            table.id = jid.asUnescapedString();
//                            table.rosterName = "Evaly";
//                            table.name = "";
//                            table.status = 0;
//                            table.unreadCount = 0;
//                            table.nick_name = "";
//                            table.imageUrl = "";
//                            table.time = chatItem.getLognTime();
//                            table.lastMessage = new Gson().toJson(chatItem);
//                            AsyncTask.execute(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Logger.d("NEW ENTRY");
//                                    AppController.database.taskDao().addRoster(table);
//                                }
//                            });
//
//                        } catch (XmppStringprepException e) {
//                            e.printStackTrace();
//                        }
//
//
//                    } else {
//                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
//                    }
//                }
//
//                @Override
//                public void onFailed(int status) {
//                    dialog.hideDialog();
//                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
//                }
//            });

            xmppHandler.setUserPassword(CredentialManager.getUserName(), CredentialManager.getPassword());
            xmppHandler.login();
        }
    };

//    private void addRosterByOther() {
//        HashMap<String, String> data = new HashMap<>();
//        data.put("localuser", "09638111666");
//        data.put("localserver", Constants.XMPP_HOST);
//        data.put("user", CredentialManager.getUserName());
//        data.put("server", Constants.XMPP_HOST);
//        data.put("nick", CredentialManager.getUserData().getFirst_name());
//        data.put("subs", "both");
//        data.put("group", "evaly");
//        AuthApiHelper.addRoster(data, new DataFetchingListener<Response<JsonPrimitive>>() {
//            @Override
//            public void onDataFetched(Response<JsonPrimitive> response) {
//
//            }
//
//            @Override
//            public void onFailed(int status) {
//
//            }
//        });
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        navigationView2 = findViewById(R.id.nav_view2);
        headerView = navigationView.getHeaderView(0);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        userNameNavHeader=headerView.findViewById(R.id.userNameNavHeader);
        phoneNavHeader = headerView.findViewById(R.id.phone);
//        homeFragment = new HomeFragment();
        userDetails=new UserDetails(this);

        dialog = new ViewDialog(this);

        dbHelperWishList=new DbHelperWishList(this);
        dbHelperCart=new DbHelperCart(this);
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(R.id.nav_wishlist);
        BottomNavigationItemView itemView2 = bottomNavigationView.findViewById(R.id.nav_cart);
        View badge = LayoutInflater.from(MainActivity.this).inflate(R.layout.bottom_navigation_notification, bottomNavigationView, false);
        TextView text = badge.findViewById(R.id.notification);
        text.setText(dbHelperWishList.size()+"");
        itemView.addView(badge);

        if(dbHelperWishList.size()==0){
            badge.setVisibility(View.GONE);
        }

        View badge2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.bottom_navigation_notification, bottomNavigationView, false);
        TextView text2 = badge2.findViewById(R.id.notification);
        text2.setText(dbHelperCart.size()+"");
        itemView2.addView(badge2);

        if(dbHelperCart.size()==0){
            badge2.setVisibility(View.GONE);
        }

        Handler handler = new Handler();
        int delay = 3000;
        handler.postDelayed(new Runnable(){
            public void run(){
                if(dbHelperWishList.size()==0){
                    badge.setVisibility(View.GONE);
                }else{
                    badge.setVisibility(View.VISIBLE);
                }
                if(dbHelperCart.size()==0){
                    badge2.setVisibility(View.GONE);
                }else{
                    badge2.setVisibility(View.VISIBLE);
                }
                text.setText(dbHelperWishList.size()+"");
                text2.setText(dbHelperCart.size()+"");
                handler.postDelayed(this, delay);
            }
        }, delay);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        if(!isConnected(MainActivity.this)){
            buildDialog(MainActivity.this).show();
        }

        if (!CredentialManager.getToken().equals("")){
            if (CredentialManager.getUserName().equals("") || CredentialManager.getPassword().equals("")){
                AppController.logout(MainActivity.this);
            }else {
                if (!CredentialManager.getToken().equals("")){
                    Logger.d("===========");
                    if (AppController.getInstance().isNetworkConnected()){
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




        if(userDetails.getToken().equals("")){
            drawer.removeView(navigationView);
            navigationView2.setNavigationItemSelectedListener(menuItem -> {
                switch(menuItem.getItemId()){
                    case R.id.nav_wishlist:
                        startActivity(new Intent(MainActivity.this,WishListActivity.class));
                        break;
                    case R.id.nav_contact:
                        startActivity(new Intent(MainActivity.this,ContactActivity.class));
                        break;
//                    case R.id.nav_about:
//                        startActivity(new Intent(MainActivity.this,AboutActivity.class));
//                        break;
                    case R.id.nav_sign_in:
                        startActivity(new Intent(MainActivity.this,SignInActivity.class));
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


        }else{


            FirebaseMessaging.getInstance().subscribeToTopic("USER."+userDetails.getUserName());

            userNameNavHeader.setText(userDetails.getFirstName()+" "+userDetails.getLastName());
            phoneNavHeader.setText(userDetails.getUserName());
            drawer.removeView(navigationView2);
            navigationView.setNavigationItemSelectedListener(menuItem -> {
                switch(menuItem.getItemId()){
                    case R.id.nav_wishlist:
                        startActivity(new Intent(MainActivity.this,WishListActivity.class));
                        break;
                    case R.id.nav_contact:
                        startActivity(new Intent(MainActivity.this,ContactActivity.class));
                        break;
//                    case R.id.nav_about:
//                        startActivity(new Intent(MainActivity.this,AboutActivity.class));
//                        break;
                    case R.id.nav_home:
                        finish();
                        startActivity(getIntent());
                        //this.overridePendingTransition(0, 0);
                        break;
                    case R.id.nav_account:
                        startActivity(new Intent(MainActivity.this,UserDashboardActivity.class));
                        break;
                    case R.id.nav_orders:
                        startActivity(new Intent(MainActivity.this,OrderListActivity.class));
                        break;
                    case R.id.nav_cart:
                        startActivity(new Intent(MainActivity.this,CartActivity.class));
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

                }
                new Handler().postDelayed(() -> drawer.closeDrawer(GravityCompat.START), 150);
                return true;
            });
        }



        final FragmentManager  fragmentManager= getSupportFragmentManager();

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {


            Intent intent;
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    while (fragmentManager.getBackStackEntryCount() > 0) {
                        fragmentManager.popBackStackImmediate();
                    }

                    showHomeFragment();
                    break;
                case R.id.nav_wishlist:
                    intent = new Intent(MainActivity.this, WishListActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_cart:

                    intent = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_dashboard:
                    if(userDetails.getToken().equals("")){
                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    }else{
                        startActivity(new Intent(MainActivity.this, UserDashboardActivity.class));
                    }
                    break;



            }
            return true;
        });


        Intent data = getIntent();

        if(data.hasExtra("type")){

            int type = data.getIntExtra("type", 1);
            Bundle bundle = new Bundle();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

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

            }else if (type == 3){

                isLaunchActivity = false;
                bundle.putInt("type", type);
                bundle.putString("shop_slug", data.getStringExtra("shop_slug"));
                bundle.putString("shop_name", data.getStringExtra("shop_name"));
                bundle.putString("category", data.getStringExtra("category"));
                bundle.putString("groups", data.getStringExtra("groups"));

                Fragment fragment3 = new ShopFragment();
                fragment3.setArguments(bundle);
                ft.replace(R.id.fragment_container, fragment3, data.getStringExtra("slug"));
                ft.addToBackStack(null);
                ft.commit();
            } else if (type == 4){

                isLaunchActivity = false;

                Fragment fragment3 = new BrowseProductFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putInt("type", 1);
                bundle2.putString("slug", data.getStringExtra("category_slug"));
                bundle2.putString("category", "root");
                fragment3.setArguments(bundle2);
                ft.setCustomAnimations(R.animator.slide_in_left,R.animator.abc_popup_exit, 0, 0);
                ft.replace(R.id.fragment_container, fragment3, data.getStringExtra("category_slug"));
                ft.addToBackStack(null);
                ft.commit();

            }

        } else {

            showHomeFragment();
        }

        if(data.hasExtra("fromBalance")) {
            Intent ip = new Intent(this, UserDashboardActivity.class);
            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();
            startActivity(ip);
        }

        exitDialogBuilder = new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to close the app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitDialog.dismiss();
                        finish();
                    }

                })
                .setNegativeButton("No", null);
        exitDialog = exitDialogBuilder.create();


        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {

                FacebookSdk.setAutoLogAppEventsEnabled(false);
                FacebookSdk.clearLoggingBehaviors();
                FacebookSdk.fullyInitialize();
            }
        }, 2000);



    }



    public void showHomeFragment() {
       try {
           Fragment fragment3 = new HomeFragment();
           FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
           // ft.setCustomAnimations(R.animator.slide_in_left,R.animator.abc_popup_exit, 0, 0);
           ft.replace(R.id.fragment_container, fragment3, "Home");
           ft.setReorderingAllowed(true);
           ft.addToBackStack("home");
           ft.commit();
       }catch (Exception e){

       }

        Token.update(this);


    }

    AlertDialog exitDialog;
    AlertDialog.Builder exitDialogBuilder;

    public UserDetails getUserDetails(){

        return userDetails;

    }


    private void startXmppService() {

        startService(new Intent(MainActivity.this, XmppConnectionIntentService.class));

        //Start XMPP Service (if not running already)
//        if (!XMPPService.isServiceRunning) {
//            Intent intent = new Intent(this, XMPPService.class);
//            mChatApp.UnbindService();
//            mChatApp.BindService(intent);
//        } else {
//            xmppHandler = AppController.getmService().xmpp;
//            if (!xmppHandler.isConnected()) {
//                xmppHandler.connect();
//            } else {
//                xmppHandler.setUserPassword(CredentialManager.getUserName(), CredentialManager.getPassword());
//                xmppHandler.login();
//            }
//        }

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
//        else{
//
//            showHomeFragment();
//
//
////            finish();
////            this.startActivity(getIntent());
////            this.overridePendingTransition(0, 0);
//
//
////            Fragment fragment3 = new HomeFragment();
////            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
////            //ft.setCustomAnimations(R.animator.slide_in_left,R.animator.abc_popup_exit, 0, 0);
////
////            FragmentManager fm = getSupportFragmentManager();
////            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
////                fm.popBackStack();
////            }
////
////            ft.replace(R.id.fragment_container, fragment3,"Home");
////            ft.addToBackStack(null);
////            ft.commit();
//        }
//
    }

    @Override
    protected void onResume() {
        super.onResume();

        // mChatApp.getEventReceiver().setListener(xmppCustomEventListener);

        if(bottomNavigationView!=null){
            Menu menu = bottomNavigationView.getMenu();
            MenuItem item = menu.getItem(0);
            item.setChecked(true);
        }


        if (userDetails.getToken() != null || !userDetails.getToken().isEmpty()){
            Token.update(this);

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

        if ( exitDialog!=null && exitDialog.isShowing() )
        {
            exitDialog.cancel();
        }
    }

    // 2) :
    @Override
    protected void onDestroy() {
        if ( exitDialog!=null && exitDialog.isShowing())
        {
            exitDialog.cancel();
        }
        if(dbHelperCart!=null){
            dbHelperCart.close();
        }
        if(dbHelperWishList!=null){
            dbHelperWishList.close();
        }
        if (xmppHandler != null){
            xmppHandler.disconnect();
        }
        super.onDestroy();

        // Glide.with(getApplicationContext()).pauseRequests();


    }



    public boolean isConnected(Context context) {
        ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=cm.getActiveNetworkInfo();
        if(activeNetwork!=null){
            if(activeNetwork.getType()== ConnectivityManager.TYPE_WIFI){
                return true;
            }else if(activeNetwork.getType()== ConnectivityManager.TYPE_MOBILE){
                return true;
            }else{
                return false;
            }
        }else {
            return false;
        }
    }

    public AlertDialog.Builder buildDialog(final Context c) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        final String[] a={"Turn on Wi-Fi","Turn on Mobile Data"};
        final int[] select = new int[1];
        builder.setSingleChoiceItems(a, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0)
                    select[0]=1;
                else if(i==1)
                    select[0]=2;
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(select[0]==1){
                    WifiManager wifiManager = (WifiManager)c.getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                    Toast.makeText(MainActivity.this, "Turning on WiFi...", Toast.LENGTH_SHORT).show();

                }else if(select[0]==2){
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }else{
                    WifiManager wifiManager = (WifiManager)c.getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                    Toast.makeText(MainActivity.this, "Turning on WiFi...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder;
    }
}
