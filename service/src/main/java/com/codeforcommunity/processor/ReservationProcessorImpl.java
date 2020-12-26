package com.codeforcommunity.processor;

import com.codeforcommunity.api.IReservationProcessor;
import org.jooq.DSLContext;

public class ReservationProcessorImpl implements IReservationProcessor {
    private final DSLContext db;

    public ReservationProcessorImpl(DSLContext db) {
        this.db = db;
    }

    @Override
    public void makeReservation(JWTData userData, MakeReservationRequest makeReservationRequest) {
        int userId = userData.getUserId();

        db.
    }
}