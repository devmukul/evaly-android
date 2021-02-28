package bd.com.evaly.evalyshop.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import bd.com.evaly.evalyshop.BuildConfig;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.issue.IssuesActivity;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.order.orderDetails.OrderDetailsActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (BuildConfig.DEBUG)
            Logger.d(new Gson().toJson(remoteMessage.getData()));
        Map<String, String> data = remoteMessage.getData();
        try {
            if (data.size() > 0) {
                String title = data.get("title");
                String body = data.get("body");
                String type = data.get("type");
                String resource_id = data.get("resource_id");
                String imageUrl = null;
                if (data.containsKey("image_url"))
                    imageUrl = data.get("image_url");
                sendNotification(body, title, resource_id, type, imageUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String messageBody, String messageTitle, String resourceId, String type, String imageUrl) {

        if (messageTitle == null || messageTitle.isEmpty())
            return;

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

        if (type != null && type.equalsIgnoreCase("issue")) {
            intent = new Intent(this, IssuesActivity.class);
            intent.putExtra("invoice", resourceId);
        }
        if (type != null && type.equalsIgnoreCase("order")) {
            intent = new Intent(this, OrderDetailsActivity.class);
            intent.putExtra("orderID", resourceId);
        } else {
            intent = new Intent(this, MainActivity.class);
            if (resourceId != null) {
                if (imageUrl != null)
                    type = "deeplink";
                intent.putExtra("notification_type", type);
                intent.putExtra("resource_id", resourceId);
            }
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.ic_status_icon)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent);

        if (imageUrl != null) {
            try {
                FutureTarget<Bitmap> bitmapFutureTarget =
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(imageUrl)
                                .submit();
                Bitmap imageBitmap = bitmapFutureTarget.get();

                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                        .bigPicture(imageBitmap)
                        .setBigContentTitle(messageTitle)
                        .setSummaryText(messageBody);
                notificationBuilder.setStyle(bigPictureStyle);
                Glide.with(getApplicationContext()).clear(bitmapFutureTarget);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        } else {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));
        }

        assert notificationManager != null;
        notificationManager.notify(1, notificationBuilder.build());
    }
}
