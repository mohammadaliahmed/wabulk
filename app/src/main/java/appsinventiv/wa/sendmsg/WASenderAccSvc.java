package appsinventiv.wa.sendmsg;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import static android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK;

public class WASenderAccSvc extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("running", false)) {
            return;
        }
        if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event.getEventType()) {
            String actname = event.getClassName().toString();
            if (actname != null) {
                if (actname.equals("com.whatsapp.Conversation")) {

                    if (getRootInActiveWindow() != null) {
                        try {
                            if (getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.whatsapp:id/send") != null) {
                                if (!getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.whatsapp:id/send").isEmpty() && getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.whatsapp:id/send") != null) {
                                    if (getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.whatsapp:id/send").size() >= 1) {
                                        try {
                                            if (getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.whatsapp:id/send").get(0) != null) {
                                                try {
                                                    getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.whatsapp:id/send").get(0).performAction(ACTION_CLICK);
                                                    performGlobalAction(GLOBAL_ACTION_BACK);

                                                    if (actname.equals("com.whatsapp.Conversation")) {
                                                        performGlobalAction(GLOBAL_ACTION_BACK);
                                                    }

                                                } catch (IndexOutOfBoundsException e) {
                                                    e.printStackTrace();
                                                    performGlobalAction(GLOBAL_ACTION_BACK);
                                                }
                                            }
                                        } catch (IndexOutOfBoundsException e) {
                                            e.printStackTrace();
                                            performGlobalAction(GLOBAL_ACTION_BACK);
                                        }
                                    }

                                }
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            performGlobalAction(GLOBAL_ACTION_BACK);
                        }
                    }
                } else if (actname.equals("com.whatsapp.HomeActivity")) {
                    sendNext();
                } else if (actname.equals("com.whatsapp.ContactPicker")) {
                    sendNext();
                }


            }
        }
    }


    @Override
    public void onInterrupt() {
        Toast.makeText(this, "Please allow accessibility permission to WhatsApp Sender", Toast.LENGTH_SHORT).show();
    }

    private void sendNext() {
        Intent intent = new Intent(this, WASenderFgSvc.class);
        intent.putExtra("start", false);
        startService(intent);
    }
}
