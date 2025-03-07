package com.example.orleviprojectjava;

public class UserOC {
    private String userId;
    private String email;
    private int numberOfLikes;
    private int numberOfPhotos;

    public UserOC() {
        // Default constructor required for Firebase
    }

    public UserOC(String userId, String email) {
        this.userId = userId;
        this.email = email;
        this.numberOfLikes = 0;
        this.numberOfPhotos = 0;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public int getNumberOfPhotos() {
        return numberOfPhotos;
    }

    public void setNumberOfPhotos(int numberOfPhotos) {
        this.numberOfPhotos = numberOfPhotos;
    }
}