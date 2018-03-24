package com.example.i550.criminalintent;

import java.util.Date;
import java.util.UUID;

//КЛАСС ПРЕСТУПЛЕНИЯ

public class Crime {
    private UUID mId;   //универсально-уникальные ИД.
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID id){
        mId=id;
        mDate=new Date();
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) { mSuspect = suspect; }

    public String getPhotoFilename() { return "IMG_"+getID().toString()+".jpg"; }

    public UUID getID() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

}
