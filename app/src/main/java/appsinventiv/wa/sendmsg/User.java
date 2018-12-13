package appsinventiv.wa.sendmsg;

/**
 * Created by AliAh on 12/11/2018.
 */

public class User {
    String  username,name,password,fcmKey;

    long time;
    boolean isActive;
    String phoneNumber;
    public User(String username, String name, String password, String fcmKey, long time, boolean isActive
        ,String phoneNumber
    ) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.fcmKey = fcmKey;
        this.time = time;
        this.isActive = isActive;
        this.phoneNumber=phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public User() {
    }

    public String getFcmKey() {
        return fcmKey;
    }

    public void setFcmKey(String fcmKey) {
        this.fcmKey = fcmKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
