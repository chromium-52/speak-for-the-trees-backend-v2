package com.codeforcommunity.processor;

import com.codeforcommunity.auth.AuthUtils;
import com.codeforcommunity.exceptions.AuthException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.VerificationKeys;
import org.jooq.generated.tables.records.NoteUserRecord;

import java.sql.Timestamp;
import java.time.Instant;
import org.jooq.generated.tables.records.VerificationKeysRecord;

import static org.jooq.generated.Tables.NOTE_USER;
import static org.jooq.generated.Tables.VERIFICATION_KEYS;

public class AuthDatabase {

    private final DSLContext db;
    private AuthUtils sha;

    public AuthDatabase(DSLContext db) {
        this.sha = new AuthUtils();
        this.db = db;
    }

    public boolean isValidLogin(String user, String pass) {

        Result<NoteUserRecord> noteUser = db.selectFrom(NOTE_USER).where(NOTE_USER.USER_NAME.eq(user)).fetch();

        String pass_hash = noteUser.getValue(0, NOTE_USER.PASS_HASH);

        return sha.hash(pass).equals(pass_hash);
    }

    public void createNewUser(String username, String email, String password, String firstName, String lastName) {

        boolean emailUsed = db.fetchExists(db.selectFrom(NOTE_USER).where(NOTE_USER.EMAIL.eq(email)));
        boolean usernameUsed = db.fetchExists(db.selectFrom(NOTE_USER).where(NOTE_USER.USER_NAME.eq(username)));
        if (emailUsed || usernameUsed) {
            //TODO: Throw some exception type thing
        }

        String pass_hash = sha.hash(password);
        db.insertInto(NOTE_USER, NOTE_USER.USER_NAME, NOTE_USER.EMAIL, NOTE_USER.PASS_HASH,
            NOTE_USER.FIRST_NAME, NOTE_USER.LAST_NAME).values(username, email, pass_hash,
            firstName, lastName).execute();
    }

    public void addToBlackList(String signature) {
        Timestamp timestamp = Timestamp.from(Instant.now().plusMillis(AuthUtils.refresh_exp));
        db.insertInto(Tables.BLACKLISTED_REFRESHES, Tables.BLACKLISTED_REFRESHES.REFRESH_HASH,
            Tables.BLACKLISTED_REFRESHES.EXPIRES).values(signature, timestamp).execute();
    }

    public boolean isOnBlackList(String signature) {

        int count = db.selectCount().from(Tables.BLACKLISTED_REFRESHES)
            .where(Tables.BLACKLISTED_REFRESHES.REFRESH_HASH.eq(signature))
            .fetchOne(0, int.class);

        return count == 1;

    }

    public void createSecretKey(int userId, String token) throws AuthException {
        if (!doesUserExist(userId)) {
            throw new AuthException("User does not exist.");
        }

        db.insertInto(Tables.VERIFICATION_KEYS).columns(VERIFICATION_KEYS.ID, VERIFICATION_KEYS.USER_ID)
            .values(token, userId).execute();
    }

    private boolean doesUserExist(int userId) {
        Result<NoteUserRecord> userRecord = db.selectFrom(Tables.NOTE_USER).where(NOTE_USER.ID.eq(userId)).fetch();
        return userRecord.isNotEmpty();
    }

    public int validateSecretKey(String secretKey) throws AuthException {
        Timestamp cutoffDate = Timestamp.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        Result<VerificationKeysRecord> veriKey = db.selectFrom(Tables.VERIFICATION_KEYS)
            .where(VERIFICATION_KEYS.ID.eq(secretKey)
                .and(VERIFICATION_KEYS.USED.eq((short)0))).fetch();

        if (veriKey.isEmpty()) {
            throw new AuthException("Token is invalid.");
        }

        if (veriKey.get(0).getCreated().before(cutoffDate)) {
            throw new AuthException("Token has expired.");
        }

        int userId = veriKey.get(0).getUserId();
        db.update(Tables.VERIFICATION_KEYS).set(VERIFICATION_KEYS.USED, (short)1).execute();
        return userId;
    }
}
