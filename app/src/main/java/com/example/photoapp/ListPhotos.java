package com.example.photoapp;

import java.util.ArrayList;
import java.util.Date;

public class ListPhotos {
    private String date;
    private ArrayList<Photo> photos;

    public ListPhotos(String date, ArrayList<Photo> photos) {
        this.date = date;
        this.photos = photos;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }
}
