package bd.com.evaly.evalyshop.util;

import android.widget.Toast;

import androidx.annotation.StringRes;

import bd.com.evaly.evalyshop.controller.AppController;


public class ToastUtils {

    public static void show(String message) {
        if (AppController.getmContext() != null)
            Toast.makeText(AppController.getmContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void show(String message, boolean isLong) {
        if (AppController.getmContext() != null)
            Toast.makeText(AppController.getmContext(), message, Toast.LENGTH_LONG).show();
    }

    public static void show(@StringRes int resId) {
        if (AppController.getmContext() != null)
            show(AppController.getmContext().getText(resId).toString());
    }

    public static void show(@StringRes int resId, boolean isLong) {
        if (AppController.getmContext() != null)
            show(AppController.getmContext().getText(resId).toString(), isLong);
    }

}
