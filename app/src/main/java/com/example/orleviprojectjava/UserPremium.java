package com.example.orleviprojectjava;

public class UserPremium extends UserOC {
    private boolean isPremium;

    public UserPremium(String userId, String email) {
        super(userId, email);
        this.isPremium = false;
    }

    public boolean isPremium() {
        return isPremium;
    }

    @Override
    public void setNumberOfPhotos(int numberOfPhotos) {
        super.setNumberOfPhotos(numberOfPhotos);
        checkPremiumStatus();
    }

    private void checkPremiumStatus() {
        if (getNumberOfPhotos() >= 3) {
            this.isPremium = true;
        }
    }
}