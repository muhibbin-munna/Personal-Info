package com.indian.youthcareerinfo.model;

import java.util.ArrayList;
import java.util.List;

public class Upload {
    public String firstName, lastName, email, phoneNo, gender, education, dob, nidurl, photourl, password;
    private List<String> sms = new ArrayList<>();

    public Upload() {
    }

    public Upload(String firstName, String lastName, String email, String phoneNo, String gender, String education, String dob, String nidurl, String photourl, String password, List<String> sms) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.gender = gender;
        this.education = education;
        this.dob = dob;
        this.nidurl = nidurl;
        this.photourl = photourl;
        this.password = password;
        this.sms = sms;
    }

    public Upload(String firstName, String lastName, String email, String phoneNo, String gender, String education, String dob, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.gender = gender;
        this.education = education;
        this.dob = dob;
        this.password = password;
    }

    public List<String> getSms() {
        return sms;
    }

    public void setSms(List<String> sms) {
        this.sms = sms;
    }

    public Upload(String name) {
        this.firstName = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getNidurl() {
        return nidurl;
    }

    public void setNidurl(String nidurl) {
        this.nidurl = nidurl;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
