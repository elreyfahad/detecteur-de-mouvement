package km.abdere.fahad.projetarduino;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
            // new push notification is received

            String message = intent.getStringExtra("message");

            Toast.makeText(context, "Push notification: " + message, Toast.LENGTH_LONG).show();
            Intent in=new Intent(context,UserActivity.class);
            in.putExtra("msg",message);

            context.startActivity(in);

            //startActivity(in);

            //txtMessage.setText(message);


        }
    }
}
