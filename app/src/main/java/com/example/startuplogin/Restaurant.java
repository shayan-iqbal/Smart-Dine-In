package com.example.startuplogin;

public class Restaurant {
    String restId;
    String restName;
    String restEmail;
    String restPasscode;
    String restContact;
    String restBranchCode;
    String restLocation;
    String restType;
    String restImage;
    Long maxSeat;

    public Restaurant() {
    }

    public Restaurant(String restId, String restName, String restEmail, String restPasscode, String restContact, String restBranchCode, String restLocation, String restType, String restImage, Long maxSeat) {
        this.restId = restId;
        this.restName = restName;
        this.restEmail = restEmail;
        this.restPasscode = restPasscode;
        this.restContact = restContact;
        this.restBranchCode = restBranchCode;
        this.restLocation = restLocation;
        this.restType = restType;
        this.restImage = restImage;
        this.maxSeat = maxSeat;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public String getRestEmail() {
        return restEmail;
    }

    public void setRestEmail(String restEmail) {
        this.restEmail = restEmail;
    }

    public String getRestPasscode() {
        return restPasscode;
    }

    public void setRestPasscode(String restPasscode) {
        this.restPasscode = restPasscode;
    }

    public String getRestContact() {
        return restContact;
    }

    public void setRestContact(String restContact) {
        this.restContact = restContact;
    }

    public String getRestLocation() {
        return restLocation;
    }

    public void setRestLocation(String restLocation) {
        this.restLocation = restLocation;
    }

    public String getRestId() {
        return restId;
    }

    public void setRestId(String restId) {
        this.restId = restId;
    }

    public String getRestType() {
        return restType;
    }

    public void setRestType(String restType) {
        this.restType = restType;
    }

    public String getRestImage() {
        return restImage;
    }

    public void setRestImage(String restImage) {
        this.restImage = restImage;
    }

    public String getRestBranchCode() {
        return restBranchCode;
    }

    public void setRestBranchCode(String restBranchCode) {
        this.restBranchCode = restBranchCode;
    }

    public Long getMaxSeat() {
        return maxSeat;
    }

    public void setMaxSeat(Long maxSeat) {
        this.maxSeat = maxSeat;
    }
}
