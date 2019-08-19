package com.genius.crudsec.Encapsulation;

public class Profile {

    private int id;
    private String mName;
    private String mClass;
    private String mSchool;
    private String mEmail;
    private String mAvatar;

    public Profile(int id, String mName, String mClass, String mSchool, String mEmail, String mAvatar) {
        this.id = id;
        this.mName = mName;
        this.mClass = mClass;
        this.mSchool = mSchool;
        this.mEmail = mEmail;
        this.mAvatar = mAvatar;
    }

    public int getId() {
        return id;
    }

    public String getmName() {
        return mName;
    }

    public String getmClass() {
        return mClass;
    }

    public String getmSchool() {
        return mSchool;
    }

    public String getmEmail() {
        return mEmail;
    }

    public String getmAvatar() {
        return mAvatar;
    }

}
