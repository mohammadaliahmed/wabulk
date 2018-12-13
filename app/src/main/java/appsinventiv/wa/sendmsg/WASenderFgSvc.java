package appsinventiv.wa.sendmsg;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import appsinventiv.wa.sendmsg.sender.WhatsappApi;
import appsinventiv.wa.sendmsg.sender.exception.WhatsappNotInstalledException;
import appsinventiv.wa.sendmsg.sender.liseteners.SendMessageListener;
import appsinventiv.wa.sendmsg.sender.model.WContact;
import appsinventiv.wa.sendmsg.sender.model.WMessage;

public class WASenderFgSvc extends Service {

    private static final int NOTIFICATION_ID = 12;
    Notification.Builder notificationBuilder;
    SharedPreferences sp;
    Integer progress = 0;
    List<String[]> recipientList = new ArrayList<>();
//    String message;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationBuilder = new Notification.Builder(this);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        Boolean start = intent.getBooleanExtra("start", true);
        Boolean rooted = intent.getBooleanExtra("rooted", false);
        if (start) {
            progress = 0;
            recipientList.clear();
            Uri uri = intent.getParcelableExtra("uri");
            try {
                InputStream file = getContentResolver().openInputStream(uri);
                CSVReader csvReader = new CSVReader(new InputStreamReader(file));
                recipientList = csvReader.readAll();
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "File not Found", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Not a CSV file", Toast.LENGTH_SHORT).show();
            }
            sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            notificationBuilder.setContentText("Sending Messages");
            Notification not = notificationBuilder.build();
            startForeground(NOTIFICATION_ID, not);
            if (rooted) {
                List<WContact> wContactList = new ArrayList<>();
                List<WMessage> wMessageList = new ArrayList<>();
                for (String[] recepient : recipientList) {
                    wContactList.add(new WContact(recepient[0], recepient[0] + "@s.whatsapp.net"));
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
        String recipient = recipientList.get(progress)[0];
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