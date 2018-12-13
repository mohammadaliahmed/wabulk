package appsinventiv.wa.sendmsg.sender.liseteners;

import java.util.List;

import appsinventiv.wa.sendmsg.sender.model.WContact;
import appsinventiv.wa.sendmsg.sender.model.WMessage;

public interface SendMessageListener {
    void finishSendWMessage(List<WContact> contact, List<WMessage> messages);
}
