package bd.com.evaly.evalyshop.util;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;

import bd.com.evaly.evalyshop.R;

public class ViewDialog {

    Activity activity;
    Dialog dialog;

    public ViewDialog(Activity activity) {
        this.activity = activity;
    }

    public void showDialog() {
        try {
            if (dialog == null)
                dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_loading_layout);

            if (!activity.isFinishing() && !dialog.isShowing()) {
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e){

        }
    }

    public void hideDialog(){
        try {
            dialog.dismiss();
        } catch (Exception e){

        }
    }

    public boolean isShowing() {
        if (dialog != null)
            return dialog.isShowing();
        else
            return false;
    }
}