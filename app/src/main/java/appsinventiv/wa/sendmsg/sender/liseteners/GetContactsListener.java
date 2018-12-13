package appsinventiv.wa.sendmsg.sender.liseteners;

import java.util.List;

import appsinventiv.wa.sendmsg.sender.model.WContact;

public interface GetContactsListener {

    void receiveWhatsappContacts(List<WContact> contacts);
}
