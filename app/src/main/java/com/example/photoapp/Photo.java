package com.example.photoapp;

import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Photo {
    private String imgName;
    private Uri imgUri;
    private Date date;
    private String geoLocation;

    public Photo(String imgName, Uri imgUri, Date date, String geoLocation) {
        this.imgName = imgName;
        this.imgUri = imgUri;
        this.date = date;
        this.geoLocation = geoLocation;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public Uri getImgUri() {
        return imgUri;
    }

    public void setImgUri(Uri imgUri) {
        this.imgUri = imgUri;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
    }

    public String getStringDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strDate= formatter.format(date);
        return strDate;
    }


}
