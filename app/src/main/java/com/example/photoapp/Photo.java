package com.example.photoapp;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Photo implements Parcelable {
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

    protected Photo(Parcel in) {
        imgName = in.readString();
        imgUri = in.readParcelable(Uri.class.getClassLoader());
        geoLocation = in.readString();
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

    public String getStringDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strDate= formatter.format(date);
        return strDate;
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
    }
}
