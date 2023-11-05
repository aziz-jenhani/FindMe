package ayechi.nour.findme;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String messageBody, phoneNumber;
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                if (messages.length > -1) {
                    messageBody = messages[0].getMessageBody();
                    phoneNumber = messages[0].getDisplayOriginatingAddress();

                    Toast.makeText(context, "Message : " + messageBody + " Reçu de la part de " + phoneNumber, Toast.LENGTH_LONG).show();

                    if (messageBody.contains("FindMe: Envoyer moi votre position")) {
                        // Tester un service de localisation
                        Intent i = new Intent(context, MyLocationService.class);
                        i.putExtra("Number", phoneNumber);
                        context.startService(i);
                    }
                    if (messageBody.contains("FindMe: Ma position est")) {
                        String longitudes = messageBody.split("#")[1];
                        String latitudes = messageBody.split("#")[2];
                        double latitude = Double.parseDouble(latitudes);
                        double longitude = Double.parseDouble(longitudes);

                        NotificationCompat.Builder mynotif = new NotificationCompat.Builder(
                                context,
                                "myapplication_channel"
                        );
                        mynotif.setContentTitle("Position est :");
                        mynotif.setContentText("long: " + longitude + " lat: " + latitude);
                        mynotif.setSmallIcon(android.R.drawable.ic_dialog_map);
                        mynotif.setAutoCancel(true);

                        // Intent pour lancer MapsActivity
                        Intent mapIntent = new Intent(context, MapsActivity.class);
                        mapIntent.putExtra("latitude", latitude);
                        mapIntent.putExtra("longitude", longitude);

                        // Ajouter l'indicateur FLAG_ACTIVITY_NEW_TASK
                        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        // Lancer l'activité
                        context.startActivity(mapIntent);
                    }
                }
            }
        }
    }
}