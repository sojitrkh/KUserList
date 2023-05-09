package com.example.demo2;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "userdata")
public class UserModel {

   @PrimaryKey(autoGenerate = true)
    private int id;

   @ColumnInfo(name = "proavatar")
    private byte[] proavatar;

   @ColumnInfo(name = "name")
   private String name;

    @ColumnInfo(name = "mobile")
    private String mobile;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "gender")
    private int gender;

    @ColumnInfo(name = "createdAt")
    private  String createdAt;

    @ColumnInfo(name = "updatedAt")
    private String updatedAt;

    @ColumnInfo(name = "isSelected")
    public boolean isSelected = false;

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public UserModel(int id, byte[] proavatar, String name, String mobile, String email, int gender) {
        this.id = id;
        this.proavatar = proavatar;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.gender = gender;
    }

    @Ignore
    public UserModel(byte[] proavatar, String name, String mobile, String email, int gender) {
        this.proavatar = proavatar;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.gender = gender;
    }
    @Ignore
    public UserModel(String name, String mobile, String email, int gender) {
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.gender = gender;
    }

    @Ignore
    public UserModel(int id, String name, String mobile, String email, int gender) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.gender = gender;
    }

    @Ignore
    public UserModel(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getProavatar() {
        return proavatar;
    }

    public void setProavatar(byte[] proavatar) {
        this.proavatar = proavatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
