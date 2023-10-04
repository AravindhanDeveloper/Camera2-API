package com.doctor.adiuvo.Model;

public class MediaItem {
    private String path;
    private boolean isImage;

    public MediaItem( String path, boolean isImage) {
        this.path = path;
        this.isImage = isImage;
    }



    public String getPath() {
        return path;
    }

    public boolean isImage() {
        return isImage;
    }
}

