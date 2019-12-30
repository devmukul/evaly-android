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
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.util.firebase.generatePictureStyleNotification;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.ui.issue.IssuesActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        final String TAG = "Firebase";

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        Logger.d(new Gson().toJson(remoteMessage.getData()));

        //showNotification(remoteMessage.getNotification().getBody());

        Context context = getApplicationContext();


        // Check if message contains a data payload.
        try {
            if (remoteMessage.getData().size() > 0) {


                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                String type = remoteMessage.getData().get("type");
                String extra = remoteMessage.getData().get("pageUrl");
                String resource_id = remoteMessage.getData().get("resource_id");




                if(type.equals("text")){
                    sendNotification(body, title, extra, type);
                } else if(type.equals("image")){
                    String image_url = remoteMessage.getData().get("image_url");

                    new generatePictureStyleNotification(this,title, body,
                            image_url, extra).execute();

                } else if (type.equalsIgnoreCase("issue")){
                    sendNotification(body, title, resource_id, type );
                }


            }

        }catch (Exception e){
            e.printStackTrace();
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }




    private void sendNotification(String messageBody, String messageTitle, String pageUrl, String type) {


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "Evaly_NOTIFICATION";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Get important notifications from Evaly App");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 500, 250});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent intent;


        if (type.equalsIgnoreCase("issue")){
            intent = new Intent(this, IssuesActivity.class);
            intent.putExtra("invoice", pageUrl);
        }else {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("pageUrl", pageUrl);
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_ONE_SHOT);



        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
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