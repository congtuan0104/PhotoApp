package com.example.photoapp;

public class Video {
    private String vidName;
    private String vidPath;
    private String vidThumb;

    public Video(String vidName, String vidPath, String vidThumb) {
        this.vidName = vidName;
        this.vidPath = vidPath;
        this.vidThumb = vidThumb;
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

    public String getVidThumb() {
        return vidThumb;
    }

    public void setVidThumb(String vidThumb) {
        this.vidThumb = vidThumb;
    }
}
