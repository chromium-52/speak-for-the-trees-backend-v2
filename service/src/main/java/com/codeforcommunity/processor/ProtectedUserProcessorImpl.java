package com.codeforcommunity.processor;

import static org.jooq.generated.Tables.USERS;
import static org.jooq.generated.Tables.VERIFICATION_KEYS;

import com.codeforcommunity.api.IProtectedUserProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.auth.Passwords;
import com.codeforcommunity.dataaccess.AuthDatabaseOperations;
import com.codeforcommunity.dto.user.ChangeEmailRequest;
import com.codeforcommunity.dto.user.ChangePasswordRequest;
import com.codeforcommunity.dto.user.ChangePrivilegeLevelRequest;
import com.codeforcommunity.dto.user.UserDataResponse;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.*;
import com.codeforcommunity.requester.Emailer;
import org.jooq.DSLContext;
import org.jooq.generated.tables.pojos.Users;
import org.jooq.generated.tables.records.UsersRecord;

public class ProtectedUserProcessorImpl implements IProtectedUserProcessor {

  private final DSLContext db;
  private final Emailer emailer;

  public ProtectedUserProcessorImpl(DSLContext db, Emailer emailer) {
    this.db = db;
    this.emailer = emailer;
  }

  @Override
  public void deleteUser(JWTData userData) {
    int userId = userData.getUserId();

    db.deleteFrom(VERIFICATION_KEYS).where(VERIFICATION_KEYS.USER_ID.eq(userId)).executeAsync();

    UsersRecord user = db.selectFrom(USERS).where(USERS.ID.eq(userId)).fetchOne();
    user.delete();

    emailer.sendAccountDeactivatedEmail(
        user.getEmail(), AuthDatabaseOperations.getFullName(user.into(Users.class)));
  }

  @Override
  public void changePassword(JWTData userData, ChangePasswordRequest changePasswordRequest) {
    UsersRecord user = db.selectFrom(USERS).where(USERS.ID.eq(userData.getUserId())).fetchOne();

    if (user == null) {
      throw new UserDoesNotExistException(userData.getUserId());
    }

    if (Passwords.isExpectedPassword(
        changePasswordRequest.getCurrentPassword(), user.getPasswordHash())) {
      user.setPasswordHash(Passwords.createHash(changePasswordRequest.getNewPassword()));
      user.store();
    } else {
      throw new WrongPasswordException();
    }

    emailer.sendPasswordChangeConfirmationEmail(
        user.getEmail(), AuthDatabaseOperations.getFullName(user.into(Users.class)));
  }

  @Override
  public UserDataResponse getUserData(JWTData userData) {
    UsersRecord user = db.selectFrom(USERS).where(USERS.ID.eq(userData.getUserId())).fetchOne();

    if (user == null) {
      throw new UserDoesNotExistException(userData.getUserId());
    }

    return new UserDataResponse(user.getFirstName(), user.getLastName(), user.getEmail());
  }

  @Override
  public void changeEmail(JWTData userData, ChangeEmailRequest changeEmailRequest) {
    UsersRecord user = db.selectFrom(USERS).where(USERS.ID.eq(userData.getUserId())).fetchOne();
    if (user == null) {
      throw new UserDoesNotExistException(userData.getUserId());
    }

    String previousEmail = user.getEmail();
    if (Passwords.isExpectedPassword(changeEmailRequest.getPassword(), user.getPasswordHash())) {
      if (db.fetchExists(USERS, USERS.EMAIL.eq(changeEmailRequest.getNewEmail()))) {
        throw new EmailAlreadyInUseException(changeEmailRequest.getNewEmail());
      }
      user.setEmail(changeEmailRequest.getNewEmail());
      user.store();
    } else {
      throw new WrongPasswordException();
    }

    emailer.sendEmailChangeConfirmationEmail(
        previousEmail,
        AuthDatabaseOperations.getFullName(user.into(Users.class)),
        changeEmailRequest.getNewEmail());
  }

  @Override
  public void changePrivilegeLevel(
      JWTData userData, ChangePrivilegeLevelRequest changePrivilegeLevelRequest) {
    // check if user is admin
    if (!(userData.getPrivilegeLevel() == PrivilegeLevel.ADMIN
        || userData.getPrivilegeLevel() == PrivilegeLevel.SUPER_ADMIN)) {
      throw new AuthException("User does not have the required privilege level");
    }

    UsersRecord user =
        db.selectFrom(USERS)
            .where(USERS.EMAIL.eq(changePrivilegeLevelRequest.getTargetUserEmail()))
            .fetchOne();
    if (user == null) {
      throw new UserDoesNotExistException(changePrivilegeLevelRequest.getTargetUserEmail());
    }

    // normal admins can't create super admins
    if (userData.getPrivilegeLevel() == PrivilegeLevel.ADMIN
        && changePrivilegeLevelRequest.getNewLevel() == PrivilegeLevel.SUPER_ADMIN) {
      throw new AuthException("User does not have the required privilege level");
    }

    byte[] passwordHash =
        db.selectFrom(USERS).where(USERS.ID.eq(userData.getUserId())).fetchOne().getPasswordHash();

    // check password and if the privilege level is different
    if (Passwords.isExpectedPassword(changePrivilegeLevelRequest.getPassword(), passwordHash)) {
      if (user.getPrivilegeLevel().equals(changePrivilegeLevelRequest.getNewLevel())) {
        throw new SamePrivilegeLevelException();
      }
      user.setPrivilegeLevel(changePrivilegeLevelRequest.getNewLevel());
      user.store();
    } else {
      throw new WrongPasswordException();
    }
  }
}
