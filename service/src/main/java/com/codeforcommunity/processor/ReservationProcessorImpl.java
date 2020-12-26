package com.codeforcommunity.processor;

import static org.jooq.generated.Tables.RESERVATIONS;

import com.codeforcommunity.api.IReservationProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.reservation.*;
import com.codeforcommunity.enums.PrivilegeLevel;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.ReservationsRecord;

import java.sql.Timestamp;
import java.util.Optional;

public class ReservationProcessorImpl implements IReservationProcessor {

    private final DSLContext db;

    public ReservationProcessorImpl(DSLContext db) {
        this.db = db;
    }

    public Optional<ReservationsRecord> lastAction(int blockId) {
        return Optional.ofNullable(db.selectFrom(RESERVATIONS)
                .where(RESERVATIONS.BLOCK_ID.eq(blockId))
                .orderBy(RESERVATIONS.PERFORMED_AT.desc())
                .limit(1)
                .fetchOne());
    }

    @Override
    public void makeReservation(JWTData userData, MakeReservationRequest makeReservationRequest) {
        ReservationsRecord reservationsRecord = db.newRecord(RESERVATIONS);
        reservationsRecord.setBlockId(makeReservationRequest.getBlockID());
        reservationsRecord.setUserId(userData.getUserId());
        reservationsRecord.setTeamId(makeReservationRequest.getTeamID());
        reservationsRecord.setActionType("reserve");
        reservationsRecord.setPerformedAt(new Timestamp(System.currentTimeMillis()));

        Optional<ReservationsRecord> maybeReservation = lastAction(reservationsRecord.getBlockId());

        // check if block is open (released, uncomplete or no record)
        if (maybeReservation.isPresent()) {
            if (maybeReservation.get().getActionType().equals("reserve") ||
                    maybeReservation.get().getActionType().equals("complete") ||
                    maybeReservation.get().getActionType().equals("qa")) {
                //throw exception
            }
        }

        reservationsRecord.store();
    }

    @Override
    public void completeReservation(JWTData userData, CompleteReservationRequest completeReservationRequest) {
        ReservationsRecord reservationsRecord = db.newRecord(RESERVATIONS);
        reservationsRecord.setBlockId(completeReservationRequest.getBlockID());
        reservationsRecord.setUserId(userData.getUserId());
        reservationsRecord.setTeamId(completeReservationRequest.getTeamID());
        reservationsRecord.setActionType("complete");
        reservationsRecord.setPerformedAt(new Timestamp(System.currentTimeMillis()));

        Optional<ReservationsRecord> maybeReservation = lastAction(reservationsRecord.getBlockId());

        // check if block is reserved by the user
        if (maybeReservation.isPresent()) {
            if (!(maybeReservation.get().getUserId().equals(userData.getUserId()))) {
                //throw exception
            }
        } else {
            //throw exception
        }

        reservationsRecord.store();
    }

    @Override
    public void releaseReservation(JWTData userData, ReleaseReservationRequest releaseReservationRequest) {
        ReservationsRecord reservationsRecord = db.newRecord(RESERVATIONS);
        reservationsRecord.setBlockId(releaseReservationRequest.getBlockID());
        reservationsRecord.setUserId(userData.getUserId());
        reservationsRecord.setActionType("release");
        reservationsRecord.setPerformedAt(new Timestamp(System.currentTimeMillis()));

        Optional<ReservationsRecord> maybeReservation = lastAction(reservationsRecord.getBlockId());

        // check if block is reserved by user
        if (maybeReservation.isPresent()) {
            if (!(maybeReservation.get().getUserId().equals(userData.getUserId()))) {
                //throw exception
            }
        } else {
            //throw exception
        }

        reservationsRecord.store();
    }

    @Override
    public void uncompleteReservation(JWTData userData, UncompleteReservationRequest uncompleteReservationRequest) {
        ReservationsRecord reservationsRecord = db.newRecord(RESERVATIONS);
        reservationsRecord.setBlockId(uncompleteReservationRequest.getBlockID());
        reservationsRecord.setUserId(userData.getUserId());
        reservationsRecord.setActionType("uncomplete");
        reservationsRecord.setPerformedAt(new Timestamp(System.currentTimeMillis()));

        Optional<ReservationsRecord> maybeReservation = lastAction(reservationsRecord.getBlockId());

        // check if the user is an admin and if the block has been completed
        if (userData.getPrivilegeLevel().equals(PrivilegeLevel.STANDARD)) {
            //throw exception
        }

        if (maybeReservation.isPresent()) {
            if (!(maybeReservation.get().getActionType().equals("complete"))) {
                //throw exception
            }
        } else {
            //throw exception
        }

        reservationsRecord.store();
    }

    @Override
    public void markForQA(JWTData userData, MarkForQARequest markForQARequest) {
        ReservationsRecord reservationsRecord = db.newRecord(RESERVATIONS);
        reservationsRecord.setBlockId(markForQARequest.getBlockID());
        reservationsRecord.setUserId(userData.getUserId());
        reservationsRecord.setActionType("qa");
        reservationsRecord.setPerformedAt(new Timestamp(System.currentTimeMillis()));

        Optional<ReservationsRecord> maybeReservation = lastAction(reservationsRecord.getBlockId());

        // check if the user is an admin and if the block has been completed
        if (userData.getPrivilegeLevel().equals(PrivilegeLevel.STANDARD)) {
            //throw exception
        }

        if (maybeReservation.isPresent()) {
            if (!(maybeReservation.get().getActionType().equals("complete"))) {
                //throw exception
            }
        } else {
            //throw exception
        }

        reservationsRecord.store();
    }
}
