package com.peason.databasetables;

import java.util.ArrayList;

public class USERPROFILE {
    int profileid;
    String userName;
    String password;
    ArrayList<SOURCES> sources;
    String errMsg;

    public int getProfileid() {
        return profileid;
    }

    public void setProfileid(int profileid) {
        this.profileid = profileid;
    }

    public ArrayList<SOURCES> getSources() {
        return sources;
    }

    public void setSources(ArrayList<SOURCES> sources) {
        this.sources = sources;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        //decrypt pwd
        return password;
    }

    public void setPassword(String password) {
        //encrypt pwd
        this.password = password;
    }
}
