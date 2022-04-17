package com.codeforcommunity.dto.neighborhoods;

public class UserEmailRecord {
    private final String email;
    private final String firstName;
    private final String address;

    public UserEmailRecord(String email, String firstName, String address) {
        this.email = email;
        this.firstName = firstName;
        this.address = address;
    }

    public String getEmail() {
        return this.email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getAddress() {
        return this.address;
    }
}