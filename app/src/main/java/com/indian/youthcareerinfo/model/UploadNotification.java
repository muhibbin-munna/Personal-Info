package com.indian.youthcareerinfo.model;

public class UploadNotification {
    String title, body;
    long time;

    public UploadNotification(){

    }

    public UploadNotification(String title, String body, long time) {
        this.title = title;
        this.body = body;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
