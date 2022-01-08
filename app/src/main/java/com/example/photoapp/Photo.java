package com.example.photoapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Photo implements Parcelable {
    private String imgName;
    private String realPath;
    private Uri imgUri;
    private Date date;
    private String geoLocation;
    private String albumName;
    public boolean isSelected = false;



    public Photo(String imgName, String realPath, Uri imgUri, Date date, String geoLocation,String albumName) {
        this.imgName = imgName;
        this.imgUri = imgUri;
        this.date = date;
        this.geoLocation = geoLocation;
        this.realPath = realPath;
        this.albumName = albumName;
    }

    protected Photo(Parcel in) {
        imgName = in.readString();
        imgUri = in.readParcelable(Uri.class.getClassLoader());
        geoLocation = in.readString();
        realPath = in.readString();
        albumName = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

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


    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }


    public String getStringDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strDate= formatter.format(date);
        return strDate;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imgName);
        dest.writeParcelable(imgUri, flags);
        dest.writeString(geoLocation);
        dest.writeString(realPath);
        dest.writeString(albumName);
        dest.writeLong(date!=null?date.getTime():-1);
    }
}
