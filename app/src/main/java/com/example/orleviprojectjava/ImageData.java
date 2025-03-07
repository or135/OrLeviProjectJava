package com.example.orleviprojectjava;

public class ImageData {
    private String imageId;
    private String userId;
    private String imageBase64;
    private String userEmail;
    private String lastCommentText;

    public ImageData() {
        // Default constructor (required for Firebase)
    }

    public ImageData(String imageId, String userId, String imageBase64, String userEmail, String lastCommentText) {
        this.imageId = imageId;
        this.userId = userId;
        this.imageBase64 = imageBase64;
        this.userEmail = userEmail;
        this.lastCommentText = lastCommentText;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getLastCommentText() {
        return lastCommentText;
    }

    public void setLastCommentText(String lastCommentText) {
        this.lastCommentText = lastCommentText;
    }
}
