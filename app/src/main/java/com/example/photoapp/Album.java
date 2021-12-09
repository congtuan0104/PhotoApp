package com.example.photoapp;

import java.util.ArrayList;

public class Album {
    private String albumName;
    private ArrayList<Photo> photos;

    public Album(String albumName, ArrayList<Photo> photos) {
        this.albumName = albumName;
        this.photos = photos;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }
}
