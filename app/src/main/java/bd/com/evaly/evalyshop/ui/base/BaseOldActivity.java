package bd.com.evaly.evalyshop.ui.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import bd.com.evaly.evalyshop.ui.cart.CartActivity;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.networkError.NetworkErrorActivity;

public class BaseOldActivity extends AppCompatActivity {

    /*
        H. M. Tamim
        24/Jun/2019
     */

    public  void adjustFontScale( Configuration configuration) {

        try {

            configuration.fontScale = (float) 1.05;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            assert wm != null;
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            try {
                if (!(Build.MANUFACTURER.toLowerCase().contains("meizu")) && Build.MODEL.contains("RNE-L22"))
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
            if(!((context instanceof CartActivity) || (context instanceof MainActivity))) {
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

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

}
