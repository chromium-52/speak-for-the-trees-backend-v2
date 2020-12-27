package com.codeforcommunity.processor;

import com.codeforcommunity.api.IReservationProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.reservation.*;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.*;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.*;

import java.sql.Timestamp;
import java.util.Optional;

import static org.jooq.generated.Tables.*;

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

    public void basicChecks(int blockId, Integer userId, Integer teamId) {
        BlocksRecord block = db.selectFrom(BLOCKS).where(BLOCKS.ID.eq(blockId)).fetchOne();

        if (block == null) {
            throw new BlockDoesNotExistException(blockId);
        }

        if (userId != null) {
            UsersRecord user = db.selectFrom(USERS).where(USERS.ID.eq(userId)).fetchOne();

            if (user == null) {
                throw new UserDoesNotExistException(userId);
            }
        }

        if (teamId != null) {
            TeamsRecord team = db.selectFrom(TEAMS).where(TEAMS.ID.eq(teamId)).fetchOne();

            if (team == null) {
                throw new TeamDoesNotExistException(teamId);
            }
        }

        if (userId != null && teamId != null) {
            UsersTeamsRecord user_teams = db.selectFrom(USERS_TEAMS)
                    .where(USERS_TEAMS.USER_ID.eq(userId))
                    .and(USERS_TEAMS.TEAM_ID.eq(teamId))
                    .fetchOne();

            if (user_teams == null) {
                throw new UserNotOnTeamException(userId, teamId);
            }
        }
    }

    @Override
    public void makeReservation(JWTData userData, MakeReservationRequest makeReservationRequest) {
        basicChecks(makeReservationRequest.getBlockID(), userData.getUserId(), makeReservationRequest.getTeamID());

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
                throw new BlockNotOpenException(reservationsRecord.getBlockId());
            }
        }

        reservationsRecord.store();
    }

    @Override
    public void completeReservation(JWTData userData, CompleteReservationRequest completeReservationRequest) {
        basicChecks(completeReservationRequest.getBlockID(),
                userData.getUserId(),
                completeReservationRequest.getTeamID());

        ReservationsRecord reservationsRecord = db.newRecord(RESERVATIONS);
        reservationsRecord.setBlockId(completeReservationRequest.getBlockID());
        reservationsRecord.setUserId(userData.getUserId());
        reservationsRecord.setTeamId(completeReservationRequest.getTeamID());
        reservationsRecord.setActionType("complete");
        reservationsRecord.setPerformedAt(new Timestamp(System.currentTimeMillis()));

        Optional<ReservationsRecord> maybeReservation = lastAction(reservationsRecord.getBlockId());

        // check if block is reserved by the user (already checked if user is on team)
        if (maybeReservation.isPresent()) {
            if (!(maybeReservation.get().getUserId().equals(userData.getUserId()))) {
                throw new BlockNotReservedException(reservationsRecord.getBlockId());
            }
        } else {
            throw new BlockNotReservedException(reservationsRecord.getBlockId());
        }

        reservationsRecord.store();
    }

    @Override
    public void releaseReservation(JWTData userData, ReleaseReservationRequest releaseReservationRequest) {
        basicChecks(releaseReservationRequest.getBlockID(), userData.getUserId(), null);

        ReservationsRecord reservationsRecord = db.newRecord(RESERVATIONS);
        reservationsRecord.setBlockId(releaseReservationRequest.getBlockID());
        reservationsRecord.setUserId(userData.getUserId());
        reservationsRecord.setActionType("release");
        reservationsRecord.setPerformedAt(new Timestamp(System.currentTimeMillis()));

        Optional<ReservationsRecord> maybeReservation = lastAction(reservationsRecord.getBlockId());

        // check if block is reserved by the user (already checked if user is on team)
        if (maybeReservation.isPresent()) {
            if (!(maybeReservation.get().getUserId().equals(userData.getUserId()))) {
                throw new BlockNotReservedException(reservationsRecord.getBlockId());
            }
        } else {
            throw new BlockNotReservedException(reservationsRecord.getBlockId());
        }

        reservationsRecord.store();
    }

    @Override
    public void uncompleteReservation(JWTData userData, UncompleteReservationRequest uncompleteReservationRequest) {
        basicChecks(uncompleteReservationRequest.getBlockID(), userData.getUserId(), null);

        ReservationsRecord reservationsRecord = db.newRecord(RESERVATIONS);
        reservationsRecord.setBlockId(uncompleteReservationRequest.getBlockID());
        reservationsRecord.setUserId(userData.getUserId());
        reservationsRecord.setActionType("uncomplete");
        reservationsRecord.setPerformedAt(new Timestamp(System.currentTimeMillis()));

        Optional<ReservationsRecord> maybeReservation = lastAction(reservationsRecord.getBlockId());

        // check if the user is an admin and if the block has been completed
        if (userData.getPrivilegeLevel().equals(PrivilegeLevel.STANDARD)) {
            throw new AuthException("User does not have the required privilege level.");
        }

        if (maybeReservation.isPresent()) {
            if (!(maybeReservation.get().getActionType().equals("complete"))) {
                throw new BlockNotCompleteException(reservationsRecord.getBlockId());
            }
        } else {
            throw new BlockNotCompleteException(reservationsRecord.getBlockId());
        }

        reservationsRecord.store();
    }

    @Override
    public void markForQA(JWTData userData, MarkForQARequest markForQARequest) {
        basicChecks(markForQARequest.getBlockID(), userData.getUserId(), null);

        ReservationsRecord reservationsRecord = db.newRecord(RESERVATIONS);
        reservationsRecord.setBlockId(markForQARequest.getBlockID());
        reservationsRecord.setUserId(userData.getUserId());
        reservationsRecord.setActionType("qa");
        reservationsRecord.setPerformedAt(new Timestamp(System.currentTimeMillis()));

        Optional<ReservationsRecord> maybeReservation = lastAction(reservationsRecord.getBlockId());

        // check if the user is an admin and if the block has been completed
        if (userData.getPrivilegeLevel().equals(PrivilegeLevel.STANDARD)) {
            throw new AuthException("User does not have the required privilege level.");
        }

        if (maybeReservation.isPresent()) {
            if (!(maybeReservation.get().getActionType().equals("complete"))) {
                throw new BlockNotCompleteException(reservationsRecord.getBlockId());
            }
        } else {
            throw new BlockNotCompleteException(reservationsRecord.getBlockId());
        }

        reservationsRecord.store();
    }
}
