package km.abdere.fahad.projetarduino;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import static km.abdere.fahad.projetarduino.Config.NOTIFICATION_ID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());

            sendVisualNotification(remoteMessage.getNotification().getBody());
        }


    }



    private void sendVisualNotification(String messageBody) {

            // 1 - Create an Intent that will be shown when user will click on the Notification
            ///Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            Intent intent = new Intent(getApplicationContext(),UserActivity.class);

            intent.putExtra("msg",messageBody);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

            // 2 - Create a Style for the Notification
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle(getString(R.string.notification_title));
            inboxStyle.addLine(messageBody);

            // 3 - Create a Channel (Android 8)
            String channelId = getString(R.string.default_notification_channel_id);

            // 4 - Build a Notification object
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(getString(R.string.notification_title))
                            .setAutoCancel(true)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setContentIntent(pendingIntent)
                            .setStyle(inboxStyle);

            // 5 - Add the Notification to the Notification Manager and show it.
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // 6 - Support Version >= Android 8
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence channelName = "Message provenant de Firebase";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            // 7 - Show notification
            notificationManager.notify(TAG, NOTIFICATION_ID, notificationBuilder.build());
        }

}