package com.codeforcommunity.processor;

import static org.jooq.generated.Tables.TEAMS;
import static org.jooq.generated.Tables.USERS;
import static org.jooq.generated.Tables.USERS_TEAMS;
import static org.jooq.generated.Tables.VERIFICATION_KEYS;

import com.codeforcommunity.api.IProtectedUserProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.auth.Passwords;
import com.codeforcommunity.dataaccess.AuthDatabaseOperations;
import com.codeforcommunity.dto.user.ChangeEmailRequest;
import com.codeforcommunity.dto.user.ChangePasswordRequest;
import com.codeforcommunity.dto.user.ChangePrivilegeLevelRequest;
import com.codeforcommunity.dto.user.ChangeUsernameRequest;
import com.codeforcommunity.dto.user.DeleteUserRequest;
import com.codeforcommunity.dto.user.Team;
import com.codeforcommunity.dto.user.UserDataResponse;
import com.codeforcommunity.dto.user.UserTeamsResponse;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.enums.TeamRole;
import com.codeforcommunity.exceptions.AuthException;
import com.codeforcommunity.exceptions.EmailAlreadyInUseException;
import com.codeforcommunity.exceptions.SamePrivilegeLevelException;
import com.codeforcommunity.exceptions.UserDeletedException;
import com.codeforcommunity.exceptions.UserDoesNotExistException;
import com.codeforcommunity.exceptions.UsernameAlreadyInUseException;
import com.codeforcommunity.exceptions.WrongPasswordException;
import com.codeforcommunity.requester.Emailer;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.generated.tables.pojos.Users;
import org.jooq.generated.tables.records.UsersRecord;

public class ProtectedUserProcessorImpl implements IProtectedUserProcessor {

  private final DSLContext db;
  private final Emailer emailer;

  public ProtectedUserProcessorImpl(DSLContext db, Emailer emailer) {
    this.db = db;
    this.emailer = emailer;
  }

  private UsersRecord userExistsCheck(JWTData userData) {
    int userId = userData.getUserId();
    UsersRecord user = db.selectFrom(USERS).where(USERS.ID.eq(userId)).fetchOne();
    return userRecordExistsCheck(user, new UserDoesNotExistException(userId));
  }

  private UsersRecord userRecordExistsCheck(UsersRecord user, UserDoesNotExistException exc) {
    if (user == null) {
      throw exc;
    }
    if (user.getDeletedAt() != null) {
      throw new UserDeletedException(user.getId());
    }
    return user;
  }

  @Override
  public void deleteUser(JWTData userData, DeleteUserRequest deleteUserRequest) {
    UsersRecord user = userExistsCheck(userData);

    if (!Passwords.isExpectedPassword(deleteUserRequest.getPassword(), user.getPasswordHash())) {
      throw new WrongPasswordException();
    }

    int userId = userData.getUserId();

    db.deleteFrom(VERIFICATION_KEYS).where(VERIFICATION_KEYS.USER_ID.eq(userId)).executeAsync();

    Timestamp now = Timestamp.from(Instant.now());
    user.setDeletedAt(now);

    user.store();

    emailer.sendAccountDeactivatedEmail(
        user.getEmail(), AuthDatabaseOperations.getFullName(user.into(Users.class)));
  }

  @Override
  public void changePassword(JWTData userData, ChangePasswordRequest changePasswordRequest) {
    UsersRecord user = userExistsCheck(userData);

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
    UsersRecord user = userExistsCheck(userData);

    return new UserDataResponse(user.getFirstName(), user.getLastName(), user.getEmail());
  }

  @Override
  public UserTeamsResponse getUserTeams(JWTData userData) {
    int userId = userData.getUserId();
    userExistsCheck(userData);

    Result<Record2<String, Integer>> teams =
        db.select(TEAMS.TEAM_NAME, TEAMS.ID)
            .from(USERS_TEAMS)
            .join(TEAMS)
            .onKey()
            .where(USERS_TEAMS.USER_ID.eq(userId))
            .and(USERS_TEAMS.TEAM_ROLE.notEqual(TeamRole.None))
            .and(USERS_TEAMS.TEAM_ROLE.notEqual(TeamRole.PENDING))
            .fetch();

    List<Team> result =
        teams.stream()
            .map(team -> new Team(team.value2(), team.value1()))
            .collect(Collectors.toList());

    return new UserTeamsResponse(result);
  }

  @Override
  public void changeEmail(JWTData userData, ChangeEmailRequest changeEmailRequest) {
    UsersRecord user = userExistsCheck(userData);

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
  public void changeUsername(JWTData userData, ChangeUsernameRequest changeUsernameRequest) {
    UsersRecord user = userExistsCheck(userData);

    if (Passwords.isExpectedPassword(changeUsernameRequest.getPassword(), user.getPasswordHash())) {
      if (db.fetchExists(USERS, USERS.USERNAME.eq(changeUsernameRequest.getNewUsername()))) {
        throw new UsernameAlreadyInUseException(changeUsernameRequest.getNewUsername());
      }
      user.setUsername(changeUsernameRequest.getNewUsername());
      user.store();
    } else {
      throw new WrongPasswordException();
    }
  }

  @Override
  public void changePrivilegeLevel(
      JWTData userData, ChangePrivilegeLevelRequest changePrivilegeLevelRequest) {
    // check if user is admin
    if (!(userData.getPrivilegeLevel() == PrivilegeLevel.ADMIN
        || userData.getPrivilegeLevel() == PrivilegeLevel.SUPER_ADMIN)) {
      throw new AuthException("User does not have the required privilege level");
    }
    String targetEmail = changePrivilegeLevelRequest.getTargetUserEmail();
    UsersRecord user = db.selectFrom(USERS).where(USERS.EMAIL.eq(targetEmail)).fetchOne();

    userRecordExistsCheck(user, new UserDoesNotExistException(targetEmail));

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
