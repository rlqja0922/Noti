package com.example.notipj;

public class NotificationData {
    String title;
    String text;
    Boolean status;

    String package_name;
    public NotificationData(String title, String text, String package_name){
        this.text = text;
        this.title = title;
        this.package_name = package_name;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setText(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }
}
