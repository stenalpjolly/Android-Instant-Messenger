package com.spj.messenger;

/**
 * Created by Stenal P Jolly on 09-Mar-15.
 */
public class UserDetails {
    private String vUsername;
    private String vPhone;
    private String vEmailId;
    private String vRegID;

    public UserDetails(String vUsername,String vEmailId,String vPhone,String vRegID){
        this.vRegID = vRegID;
        this.vEmailId = vEmailId;
        this.vPhone = vPhone;
        this.vUsername = vUsername;
    }
    public void setvRegID(String vRegID) {
        this.vRegID = vRegID;
    }

    public void setvEmailId(String vEmailId) {
        this.vEmailId = vEmailId;
    }

    public void setvPhone(String vPhone) {
        this.vPhone = vPhone;
    }

    public void setvUsername(String vUsername) {
        this.vUsername = vUsername;
    }

    public String getvRegID() {
        return vRegID;
    }

    public String getvEmailId() {
        return vEmailId;
    }

    public String getvPhone() {
        return vPhone;
    }

    public String getvUsername() {
        return vUsername;
    }
}

