package bd.com.evaly.evalyshop;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.orhanobut.logger.Logger;

import bd.com.evaly.evalyshop.activity.CartActivity;
import bd.com.evaly.evalyshop.activity.MainActivity;
import bd.com.evaly.evalyshop.activity.NetworkErrorActivity;
import bd.com.evaly.evalyshop.activity.WishListActivity;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.service.XmppConnectionIntentService;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.xmpp.XMPPService;

public class BaseActivity extends AppCompatActivity {


    /*
        H. M. Tamim
        24/Jun/2019
     */

    public  void adjustFontScale( Configuration configuration) {




        try {

            configuration.fontScale = (float) 1.05;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            // //getBaseContext().getResources().updateConfiguration(configuration, metrics);

            // //metrics.scaledDensity = ((metrics.xdpi / metrics.densityDpi) * metrics.density);


            try {
                if (!(Build.MANUFACTURER.toLowerCase().contains("meizu")))
                        configuration.densityDpi = ((int) getResources().getDisplayMetrics().xdpi);

            } catch (Exception e){
                configuration.densityDpi = ((int) getResources().getDisplayMetrics().xdpi);
            }

            getBaseContext().getResources().updateConfiguration(configuration, metrics);

            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(Color.BLACK);
            }





        }catch (Exception e){

        }



    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale( getResources().getConfiguration());

        if(!isNetworkAvailable(this)){
            Context context = this;
            if(!((context instanceof CartActivity) || (context instanceof WishListActivity) || (context instanceof MainActivity))) {
                Intent intent = new Intent(this, NetworkErrorActivity.class);
                startActivityForResult(intent, 9187);
            }
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9187) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK

                finish();
                startActivity(getIntent());

            }
        }
    }

    @Override
    protected void onUserLeaveHint() {
//        Logger.d("HOME PRESSED");
//        if (AppController.getmService() !=  null && AppController.getmService().xmpp != null){
//           if (AppController.getmService().xmpp.isConnected()){
//               XMPPHandler xmppHandler = AppController.getmService().xmpp;
//               xmppHandler.changePresence();
//               XMPPHandler.disconnect();
//
//           }
//        }
//        super.onUserLeaveHint();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppController.getmService() !=  null && AppController.getmService().xmpp != null){
            if (!AppController.getmService().xmpp.isConnected() && !CredentialManager.getUserName().equals("") && !CredentialManager.getPassword().equals("")){
                XMPPHandler xmppHandler = AppController.getmService().xmpp;
                xmppHandler.connect();
            }
        }
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    /*



            configuration.fontScale = (float) 1.0;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            getBaseContext().getResources().updateConfiguration(configuration, metrics);
     */




}
