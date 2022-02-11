package dev.oriya.nannysitter;

import android.net.Uri;

import java.net.URL;

public class User {

    private int id;
    private int idCount=0;
    private String name;
    private String mail;
    private String favoriteSong;
    private int temperatureTreshold;
    private String photoUrl;

    public User(){
        this.id = idCount++;
    }
//    public User(){
//    }


    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", favoriteSong='" + favoriteSong + '\'' +
                '}';
    }

    public User(String name, String mail, String favoriteSong, int temperatureTreshold, String photoUrl) {
        this.id = idCount++;
        this.name = name;
        this.mail = mail;
        this.favoriteSong = favoriteSong;
        this.temperatureTreshold = temperatureTreshold;
        this.photoUrl = photoUrl;
    }

    public int getId() {
        return id;
    }



    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getMail() {
        return mail;
    }

    public User setMail(String mail) {
        this.mail = mail;
        return this;
    }

    public String getFavoriteSong() {
        return favoriteSong;
    }

    public User setFavoriteSong(String favoriteSong) {
        this.favoriteSong = favoriteSong;
        return this;
    }

    public int getTemperatureTreshold() {
        return temperatureTreshold;
    }

    public User setTemperatureTreshold(int temperatureTreshold) {
        this.temperatureTreshold = temperatureTreshold;
        return this;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public User setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        return this;
    }
}
