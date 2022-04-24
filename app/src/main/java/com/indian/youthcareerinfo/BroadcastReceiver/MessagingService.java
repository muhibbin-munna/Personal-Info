package com.indian.youthcareerinfo.BroadcastReceiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.indian.youthcareerinfo.R;
import com.indian.youthcareerinfo.ui.NotificationFragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.indian.youthcareerinfo.BroadcastReceiver.App.CHANNEL_1_ID;


public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            showNotification(remoteMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showNotification(RemoteMessage remoteMessage) throws IOException {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        Bitmap bitmap;
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, NotificationFragment.class), 0);
        NotificationCompat.Builder notification;
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if(remoteMessage.getNotification().getImageUrl()!=null){
            bitmap = getBitmapfromUrl(remoteMessage.getNotification().getImageUrl().toString());
            notification = new NotificationCompat.Builder(this,CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.logo1)
                    .setContentTitle(title)
                    .setColor(getResources().getColor(R.color.black))
                    .setContentText(body)
                    .setContentIntent(pi)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap))
                    .setSound(alarmSound)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVibrate(new long[]{0, 100, 200, 300})
                    .setAutoCancel(true);
        }
        else {
            notification = new NotificationCompat.Builder(this,CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.logo1)
                    .setContentTitle(title)
                    .setColor(getResources().getColor(R.color.black))
                    .setContentText(body)
                    .setContentIntent(pi)
                    .setSound(alarmSound)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVibrate(new long[]{0, 100, 200, 300})
                    .setAutoCancel(true);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification.build());
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}
