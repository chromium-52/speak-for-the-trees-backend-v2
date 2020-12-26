package com.codeforcommunity.processor;

import static org.jooq.generated.Tables.RESERVATIONS;

import com.codeforcommunity.api.IReservationProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.reservation.*;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.ReservationsRecord;

import java.sql.Timestamp;
import java.util.Optional;

public class ReservationProcessorImpl implements IReservationProcessor {

    private final DSLContext db;

    public ReservationProcessorImpl(DSLContext db) {
        this.db = db;
    }


    @Override
    public void makeReservation(JWTData userData, MakeReservationRequest makeReservationRequest) {
        ReservationsRecord reservationsRecord = db.newRecord(RESERVATIONS);
        reservationsRecord.setBlockId(makeReservationRequest.getBlockID());
        reservationsRecord.setUserId(userData.getUserId());
        reservationsRecord.setTeamId(makeReservationRequest.getTeamID());
        reservationsRecord.setActionType("reserve");
        reservationsRecord.setPerformedAt(new Timestamp(System.currentTimeMillis()));

        // check if block is open (released, uncomplete or no record)
        Optional<ReservationsRecord> maybeReservation =
                Optional.ofNullable(db.selectFrom(RESERVATIONS)
                        .where(RESERVATIONS.BLOCK_ID.eq(makeReservationRequest.getBlockID()))
                        .orderBy(RESERVATIONS.PERFORMED_AT.desc())
                        .limit(1)
                        .fetchOne());

        if (maybeReservation.get().getActionType().equals("reserve") ||
                maybeReservation.get().getActionType().equals("complete") ||
                maybeReservation.get().getActionType().equals("qa")) {
            //throw exception
        }

        reservationsRecord.store();
    }

    @Override
    public void completeReservation(JWTData userData, CompleteReservationRequest completeReservationRequest) {

    }

    @Override
    public void releaseReservation(JWTData userData, ReleaseReservationRequest releaseReservationRequest) {

    }

    @Override
    public void uncompleteReservation(JWTData userData, UncompleteReservationRequest uncompleteReservationRequest) {

    }

    @Override
    public void markForQA(JWTData userData, MarkForQARequest markForQARequest) {

    }
}
