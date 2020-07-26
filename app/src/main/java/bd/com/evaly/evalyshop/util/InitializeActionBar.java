package bd.com.evaly.evalyshop.util;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.notification.NotificationCount;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;
import bd.com.evaly.evalyshop.rest.apiHelper.token.ChatApiHelper;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;
import bd.com.evaly.evalyshop.ui.notification.NotificationActivity;

public class InitializeActionBar {

    private int hot_number = 0;
    private TextView ui_hot;
    private Activity context;

    public InitializeActionBar(LinearLayout root, Activity context, String type, MainViewModel mainViewModel) {

        this.context = context;
        root.bringToFront();
        ImageView menuBtn = root.findViewById(R.id.menuBtn);

        if (type.equals("home"))
            menuBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_menu));
        else
            menuBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_back));

        RelativeLayout notification = root.findViewById(R.id.notification_holder);
        menuBtn.setOnClickListener(v -> {

            if (type.equals("home"))
                mainViewModel.setDrawerOnClick(true);
            else
                mainViewModel.setBackOnClick(true);
        });

        notification.setOnClickListener(v -> {
            if (CredentialManager.getToken().equals("")) {
                context.startActivity(new Intent(context, SignInActivity.class));
            } else {
                context.startActivity(new Intent(context, NotificationActivity.class));
            }
        });

        ui_hot = root.findViewById(R.id.hotlist_hot);

        if (!CredentialManager.getToken().equals(""))
            getNotificationCount();
    }

    public void getNotificationCount(){

        ChatApiHelper.getMessageCount(new ResponseListenerAuth<CommonDataResponse<String>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<String> response, int statusCode) {
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
                if (context != null) AppController.logout(context);
            }
        });

    }

    private void updateHotCount(final int new_hot_number) {
        hot_number = new_hot_number;
        if (ui_hot == null) return;

        if (new_hot_number == 0)
            ui_hot.setVisibility(View.INVISIBLE);
        else {
            if (new_hot_number > 99)
                ui_hot.setText("99");
            else
                ui_hot.setText(String.format("%d", new_hot_number));
        }

    }

}
