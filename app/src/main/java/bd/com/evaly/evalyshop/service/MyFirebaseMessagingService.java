package bd.com.evaly.evalyshop.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.issue.IssuesActivity;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.util.firebase.generatePictureStyleNotification;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Logger.d(new Gson().toJson(remoteMessage.getData()));
        try {
            if (remoteMessage.getData().size() > 0) {
                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                String type = remoteMessage.getData().get("type");
                String resource_id = remoteMessage.getData().get("resource_id");
                if (type.equals("image") && resource_id != null && !resource_id.isEmpty()) {
                    new generatePictureStyleNotification(this, title, body,
                            resource_id, resource_id).execute();
                } else
                    sendNotification(body, title, resource_id, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String messageBody, String messageTitle, String resourceId, String type) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "Evaly_NOTIFICATION";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Get important notifications from Evaly App");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 500, 250});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent intent;

        if (type.equalsIgnoreCase("issue")) {
            intent = new Intent(this, IssuesActivity.class);
            intent.putExtra("invoice", resourceId);
        }
        if (type.equalsIgnoreCase("order")) {
            intent = new Intent(this, OrderDetailsActivity.class);
            intent.putExtra("orderID", resourceId);
        } else {
            intent = new Intent(this, MainActivity.class);
            if (resourceId != null) {
                intent.putExtra("notification_type", type);
                intent.putExtra("resource_id", resourceId);
            }
            intent.putExtra("pageUrl", resourceId);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.ic_status_icon)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent);

        notificationManager.notify(1, notificationBuilder.build());
    }
}
