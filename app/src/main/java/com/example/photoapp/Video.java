package com.example.photoapp;

public class Video {
    private String vidName;
    private String vidPath;

    public Video(String vidPath,String vidName) {
        this.vidPath = vidPath;
        this.vidName  = vidName;
    }

    public String getVidName() {
        return vidName;
    }

    public void setVidName(String vidName) {
        this.vidName = vidName;
    }

    public String getVidPath() {
        return vidPath;
    }

    public void setVidPath(String vidPath) {
        this.vidPath = vidPath;
    }
}
