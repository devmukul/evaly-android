package bd.com.evaly.evalyshop.util;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.notification.NotificationCount;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.notification.NotificationActivity;
import bd.com.evaly.evalyshop.util.UserDetails;

public class InitializeActionBar {

    private int hot_number = 0;
    private TextView ui_hot = null;
    private UserDetails userDetails;
    private Activity context;

    public InitializeActionBar(LinearLayout root, MainActivity mainActivity, String type) {

        userDetails = mainActivity.getUserDetails();
        context = mainActivity;
        root.bringToFront();
        ImageView menuBtn = root.findViewById(R.id.menuBtn);

        if (type.equals("home"))
            menuBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_menu));
        else
            menuBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_back));

        RelativeLayout notification = root.findViewById(R.id.notification_holder);
        menuBtn.setOnClickListener(v -> {

            if (type.equals("home"))
                mainActivity.drawer.openDrawer(Gravity.START);
            else
                mainActivity.onBackPressed();
        });

        notification.setOnClickListener(v -> {
            if (userDetails.getToken().equals("")) {
                mainActivity.startActivity(new Intent(mainActivity, SignInActivity.class));
            } else {
                mainActivity.startActivity(new Intent(mainActivity, NotificationActivity.class));
            }
        });

        ui_hot = (TextView) root.findViewById(R.id.hotlist_hot);

        if (!userDetails.getToken().equals(""))
            getNotificationCount();

    }




    public void getNotificationCount(){


        GeneralApiHelper.getNotificationCount(CredentialManager.getToken(), "core", new ResponseListenerAuth<NotificationCount, String>() {
            @Override
            public void onDataFetched(NotificationCount response, int statusCode) {
                updateHotCount(response.getCount());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

                if (!logout)
                    getNotificationCount();
                else
                    if (context != null) {
                        Toast.makeText(context, "Token expired, please login again", Toast.LENGTH_LONG).show();
                        AppController.logout(context);
                    }

            }
        });

    }

    public void updateHotCount(final int new_hot_number) {
        hot_number = new_hot_number;
        if (ui_hot == null) return;

        if (new_hot_number == 0)
            ui_hot.setVisibility(View.INVISIBLE);
        else {
            if (new_hot_number > 99)
                ui_hot.setText("99");
            else
                ui_hot.setText(Integer.toString(new_hot_number));
        }

    }

}
