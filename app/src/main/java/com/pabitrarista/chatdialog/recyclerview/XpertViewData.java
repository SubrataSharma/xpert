package com.pabitrarista.chatdialog.recyclerview;

public class XpertViewData {

    private String xpertProfileName;
    private String xpertProfileImage;

    public XpertViewData(String xpertProfileName, String xpertProfileImage) {
        this.xpertProfileName = xpertProfileName;
        this.xpertProfileImage = xpertProfileImage;
    }

    public void setXpertProfileName(String xpertProfileName) {
        this.xpertProfileName = xpertProfileName;
    }

    public void setXpertProfileImage(String xpertProfileImage) {
        this.xpertProfileImage = xpertProfileImage;
    }

    public String getXpertProfileName() {
        return xpertProfileName;
    }

    public String getXpertProfileImage() {
        return xpertProfileImage;
    }
}
