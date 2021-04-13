package bd.com.evaly.evalyshop.util;

import android.widget.Toast;

import androidx.annotation.StringRes;

import bd.com.evaly.evalyshop.controller.AppController;


public class ToastUtils {

    public static void show(String message) {
        if (AppController.getContext() != null)
            Toast.makeText(AppController.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void show(String message, boolean isLong) {
        if (AppController.getContext() != null)
            Toast.makeText(AppController.getContext(), message, Toast.LENGTH_LONG).show();
    }

    public static void show(@StringRes int resId) {
        if (AppController.getContext() != null)
            show(AppController.getContext().getText(resId).toString());
    }

    public static void show(@StringRes int resId, boolean isLong) {
        if (AppController.getContext() != null)
            show(AppController.getContext().getText(resId).toString(), isLong);
    }

}
