package appsinventiv.wa.sendmsg;

import static appsinventiv.wa.sendmsg.Sender.numbers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import appsinventiv.wa.sendmsg.sender.WhatsappApi;
import appsinventiv.wa.sendmsg.sender.exception.WhatsappNotInstalledException;
import appsinventiv.wa.sendmsg.sender.liseteners.SendMessageListener;
import appsinventiv.wa.sendmsg.sender.model.WContact;
import appsinventiv.wa.sendmsg.sender.model.WMessage;

public class WASenderFgSvc extends Service {

    private static final int NOTIFICATION_ID = 12;
    private static final String NOTIFICATION_CHANNEL_ID = "312312";
    SharedPreferences sp;
    Integer progress = 0;
    List<String> recipientList = new ArrayList<>();
    private NotificationCompat.Builder notificationBuilder;
//    String message;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "dss";
            NotificationChannel serviceChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(serviceChannel);
        }
        Intent stopSelf = new Intent(this, WASenderFgSvc.class);
        stopSelf.setAction("ACTION_STOP_SERVICE");
        PendingIntent pStopSelf = PendingIntent
                .getService(this, 0, stopSelf
                        , PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(
                        0, "Close", pStopSelf
                ).build();
        Notification notification = notificationBuilder
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Ask Question")
                .setContentText("Ask Question is running")
                .addAction(action)
                .setPriority(Notification.PRIORITY_MIN)
                .build();
        notificationManager.notify(1, notification);
        startForeground(1, notification);
        notificationManager.cancel(1);
        Boolean start = intent.getBooleanExtra("start", true);
        Boolean rooted = intent.getBooleanExtra("rooted", false);
        if (start) {
            progress = 0;
            recipientList.clear();
            String[] abc = numbers.getText().toString().split("\n");
            recipientList = Arrays.asList(abc);

            

            sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            if (!rooted) {
                List<WContact> wContactList = new ArrayList<>();
                List<WMessage> wMessageList = new ArrayList<>();
                for (String recepient : recipientList) {
                    wContactList.add(new WContact(recepient, recepient + "@s.whatsapp.net"));
                    wMessageList.add(new WMessage("", null, this));
                }
                try {
                    WhatsappApi.getInstance().sendMessage(wContactList, wMessageList, this, new SendMessageListener() {
                        @Override
                        public void finishSendWMessage(List<WContact> contact, List<WMessage> messages) {
                            Toast.makeText(WASenderFgSvc.this, "Task Completed", Toast.LENGTH_SHORT).show();
                            sp.edit().putBoolean("running", false).commit();
                            notificationBuilder.setContentText("Sent");
                            Notification not = notificationBuilder.build();
                            startForeground(NOTIFICATION_ID, not);
                        }
                    });
                } catch (WhatsappNotInstalledException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Whatsapp not Installed", Toast.LENGTH_SHORT).show();
                }
            } else {
                send();
            }
        } else {
            send();
        }
        return START_STICKY;
    }

    @SuppressLint("ApplySharedPref")
    private void send() {
        if (progress >= recipientList.size()) {
            Toast.makeText(this, "Task Completed", Toast.LENGTH_SHORT).show();
            sp.edit().putBoolean("running", false).commit();
            notificationBuilder.setContentText("Sent");
            Notification not = notificationBuilder.build();
            startForeground(NOTIFICATION_ID, not);
            return;
        }
        String recipient = recipientList.get(progress);
        String message = "*Test%20Message*\n\nPlease%20Ignore%20it\nThanks";
//        Intent sendIntent = new Intent();
//        sendIntent.setPackage("com.whatsapp");
//        sendIntent.setAction("android.intent.action.SEND");
//        sendIntent.setType("text/plain");
//        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
//        sendIntent.putExtra("jid", recipient + "@s.whatsapp.net");
//        String url = "https://api.whatsapp.com/send?phone=" + recipientList.get(progress)[0];
        String url = "https://wa.me/" + recipient + "?text=" + Sender.msg;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setPackage("com.whatsapp");
        i.putExtra("jid", recipient + "@s.whatsapp.net");
        progress++;
        startActivity(i);


//        startActivity(sendIntent);
    }
}