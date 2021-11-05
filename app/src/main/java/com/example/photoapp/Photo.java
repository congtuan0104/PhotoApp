package com.example.photoapp;

public class Photo {
    private String imgName;
    private String imgPath;

    public Photo(String imgPath,String imgName) {
        this.imgPath = imgPath;
        this.imgName  = imgName;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
