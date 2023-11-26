package com.kientruchanoi.ecommerce.authserviceshare.payload.enumerate;

public enum Role {
    ROOT(4),
    ADMIN(3),
    MODERATOR(2),
    USER(1);

    private int numVal;

    Role(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
