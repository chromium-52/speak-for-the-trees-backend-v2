package com.codeforcommunity.dto.report;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

public class AdoptedSite {
    private final int site_id;
    private final String address;
    private final String name;
    private final String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private final Timestamp dateAdopted;

    private final int activityCount;
    private final String neighborhood;

    public AdoptedSite(Timestamp dateAdopted) {
        this.site_id = 0;
        this.address = "address";
        this.name = "name";
        this.email = "email";
        this.dateAdopted = dateAdopted;
        this.activityCount = 1;
        this.neighborhood = "neighborhood";
    }

    public AdoptedSite(
            int site_id,
            String address,
            String name,
            String email,
            Timestamp dateAdopted,
            int activityCount,
            String neighborhood) {
        this.site_id = site_id;
        this.address = address;
        this.name = name;
        this.email = email;
        this.dateAdopted = dateAdopted;
        this.activityCount = activityCount;
        this.neighborhood = neighborhood;
    }

    public int getSite_id() {
        return site_id;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Timestamp getDateAdopted() {
        return dateAdopted;
    }

    public int getActivityCount() {
        return activityCount;
    }

    public String getNeighborhood() {
        return neighborhood;
    }
}
