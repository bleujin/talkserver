package net.ion.message.push.sender;

public enum Vendor {

    APPLE, GOOGLE;

    public boolean isApple() {
        return this == APPLE;
    }

    public boolean isGoogle() {
        return this == GOOGLE;
    }
}
