package com.example.orleviprojectjava;

public class UserPremium extends UserOC {
    private boolean premium;

    public UserPremium() {
        // Default constructor required for Firebase
    }

    public UserPremium(String userId, String email) {
        super(userId, email);
        this.premium = false;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    @Override
    public void setNumberOfPhotos(int numberOfPhotos) {
        super.setNumberOfPhotos(numberOfPhotos);
        checkPremiumStatus();
    }

    private void checkPremiumStatus() {
        if (getNumberOfPhotos() >= 3) {
            this.premium = true;
        }
    }
}