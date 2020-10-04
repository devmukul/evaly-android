package bd.com.evaly.evalyshop.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Calendar;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.rest.apiHelper.token.ChatApiHelper;
import bd.com.evaly.evalyshop.ui.auth.SignInActivity;
import bd.com.evaly.evalyshop.ui.main.MainViewModel;

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
                openEconnect();
                //  context.startActivity(new Intent(context, NotificationActivity.class));
            }
        });

        ui_hot = root.findViewById(R.id.hotlist_hot);

        if (!CredentialManager.getToken().equals(""))
            getNotificationCount();
    }

    public void getNotificationCount() {

        if (Calendar.getInstance().getTimeInMillis() - CredentialManager.getMessageCounterLastUpdated() < 600000) {
            updateHotCount(CredentialManager.getMessageCount());
            return;
        }

        ChatApiHelper.getMessageCount(new ResponseListenerAuth<CommonDataResponse<String>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<String> response, int statusCode) {
                updateHotCount(response.getCount());
                CredentialManager.setMessageCounterLastUpdated();
                CredentialManager.setMessageCount(response.getCount());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (!logout)
                    getNotificationCount();
                else if (context != null) AppController.logout(context);
            }
        });

    }

    private void updateHotCount(final int new_hot_number) {
        hot_number = new_hot_number;
        if (ui_hot == null) return;

        if (new_hot_number == 0)
            ui_hot.setVisibility(View.INVISIBLE);
        else {
            ui_hot.setVisibility(View.VISIBLE);
            if (new_hot_number > 99)
                ui_hot.setText("99");
            else
                ui_hot.setText(String.format("%d", new_hot_number));
        }

    }

    private void openEconnect() {
        try {
            Intent launchIntent = new Intent("bd.com.evaly.econnect.OPEN_MAINACTIVITY");
            if (launchIntent != null) {
                launchIntent.putExtra("to", "OPEN_CHAT_LIST");
                launchIntent.putExtra("user", CredentialManager.getUserName());
                launchIntent.putExtra("password", CredentialManager.getPassword());
                launchIntent.putExtra("userInfo", new Gson().toJson(CredentialManager.getUserData()));
                if (context != null && !context.isFinishing())
                    context.startActivity(launchIntent);
            }
        } catch (android.content.ActivityNotFoundException e) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "bd.com.evaly.econnect")));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "bd.com.evaly.econnect")));
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "bd.com.evaly.econnect")));
                } catch (Exception e3) {
                    ToastUtils.show("Couldn't open eConnect, please install from Google Playstore");
                }
            }
        }
    }

}
