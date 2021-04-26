package com.codeforcommunity.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codeforcommunity.JooqMock;
import com.codeforcommunity.JooqMock.OperationType;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.enums.ReservationAction;
import com.codeforcommunity.exceptions.AuthException;
import com.codeforcommunity.exceptions.IncorrectBlockStatusException;
import com.codeforcommunity.exceptions.ResourceDoesNotExistException;
import com.codeforcommunity.exceptions.UserDoesNotExistException;
import com.codeforcommunity.exceptions.UserNotOnTeamException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.records.ReservationsRecord;
import org.jooq.generated.tables.records.UsersRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReservationProcessorImplTest {
  JooqMock mockDb;
  ReservationProcessorImpl proc;

  @BeforeEach
  public void setup() {
    mockDb = new JooqMock();
    proc = new ReservationProcessorImpl(mockDb.getContext());
  }

  @Test
  public void testBasicChecksNonExistingBlock() {
    int blockId = 1;
    int userId = 2;
    int teamId = 3;

    mockDb.addExistsReturn(false);

    try {
      this.proc.basicChecks(blockId, userId, teamId);
      fail("Method should've thrown a ResourceDoesNotExist Exception");
    } catch (ResourceDoesNotExistException e) {
      assertEquals(blockId, e.getResourceId());
      assertEquals("block", e.getResourceType());
    }
    assertEquals(1, mockDb.timesCalled(OperationType.EXISTS));
  }

  @Test
  public void testBasicChecksNonExistingTeam() {
    int blockId = 1;
    int userId = 2;
    int teamId = 3;

    mockDb.addExistsReturn(true);
    mockDb.addExistsReturn(false);

    try {
      this.proc.basicChecks(blockId, userId, teamId);
      fail("Method should've thrown a ResourceDoesNotExist Exception");
    } catch (ResourceDoesNotExistException e) {
      assertEquals(teamId, e.getResourceId());
    }
    assertEquals(2, mockDb.timesCalled(OperationType.EXISTS));
  }

  @Test
  public void testBasicChecksNonExistingUser() {
    int blockId = 1;
    int userId = 2;
    int teamId = 3;

    mockDb.addExistsReturn(true);
    mockDb.addExistsReturn(true);
    mockDb.addExistsReturn(true);
    mockDb.addExistsReturn(false);

    try {
      this.proc.basicChecks(blockId, userId, teamId);
      fail("Method should've thrown a UserDoesNotExist Exception");
    } catch (UserDoesNotExistException e) {
      assertEquals("id = " + userId, e.getIdentifierMessage());
    }
    assertEquals(3, mockDb.timesCalled(OperationType.EXISTS));
  }

  @Test
  public void testBasicChecksSuccess() {
    int blockId = 1;
    int userId = 2;
    int teamId = 3;
    UsersRecord usersRecord = new UsersRecord();

    mockDb.addExistsReturn(true);
    mockDb.addExistsReturn(true);
    mockDb.addExistsReturn(true);
    mockDb.addExistsReturn(true);
    mockDb.addReturn(OperationType.SELECT, usersRecord);

    try {
      this.proc.basicChecks(blockId, userId, teamId);
    } catch (UserNotOnTeamException | ResourceDoesNotExistException | UserDoesNotExistException e) {
      System.out.println(e.getMessage());
      fail("Method should've returned without exception");
    }
    assertEquals(3, mockDb.timesCalled(OperationType.EXISTS));
  }

  @Test
  public void testBasicChecksSuccessNullCase() {
    int blockId = 1;
    Integer userId = null;
    Integer teamId = null;

    mockDb.addExistsReturn(true);

    try {
      this.proc.basicChecks(blockId, userId, teamId);
    } catch (UserNotOnTeamException | ResourceDoesNotExistException | UserDoesNotExistException e) {
      fail("Method should've returned without exception");
    }
    assertEquals(1, mockDb.timesCalled(OperationType.EXISTS));
  }

  @Test
  public void testIsAdmin() {
    Set<PrivilegeLevel> adminLevels =
        new HashSet<>(Arrays.asList(PrivilegeLevel.ADMIN, PrivilegeLevel.SUPER_ADMIN));

    for (PrivilegeLevel p : PrivilegeLevel.values()) {
      boolean isAdmin = adminLevels.contains(p);
      try {
        this.proc.isAdminCheck(p);
        if (!isAdmin) {
          fail("Method should've thrown an AuthException with privilege level " + p.getName());
        }
      } catch (AuthException e) {
        if (isAdmin) {
          fail(
              "Method should not have thrown an AuthException with privilege level " + p.getName());
        }
      }
    }

    for (PrivilegeLevel p : adminLevels) {
      try {
        this.proc.isAdminCheck(p);
      } catch (AuthException e) {
        fail("Method should not have thrown an exception for privilege level " + p.getName());
      }
    }
  }

  @Test
  public void testBlockOpenCheckNoAction() {
    mockDb.addEmptyReturn(OperationType.SELECT);

    try {
      this.proc.blockOpenCheck(1);
    } catch (IncorrectBlockStatusException e) {
      fail("Method should not throw exception");
    }
  }

  @Test
  public void testBlockOpenCheckUncomplete() {
    ReservationsRecord mockReservation = mock(ReservationsRecord.class);
    mockDb.addReturn(OperationType.SELECT, mockReservation);

    when(mockReservation.getActionType())
        .thenReturn(ReservationAction.UNCOMPLETE, ReservationAction.UNCOMPLETE);

    try {
      this.proc.blockOpenCheck(1);
    } catch (IncorrectBlockStatusException e) {
      fail("Method should not throw exception");
    }
  }

  @Test
  public void testBlockOpenCheckReserved() {
    ReservationsRecord mockReservation = mockDb.getContext().newRecord(Tables.RESERVATIONS);
    mockReservation.setActionType(ReservationAction.RESERVE);

    mockDb.addReturn(OperationType.SELECT, mockReservation);

    try {
      this.proc.blockOpenCheck(1);
      fail();
    } catch (IncorrectBlockStatusException e) {
      assertEquals(1, e.getBlockId());
      assertEquals("open", e.getExpectedStatus());
    }
  }

  @Test
  public void testBlockReservedCheckNoLastActionFailure() {
    mockDb.addEmptyReturn(OperationType.SELECT);

    try {
      this.proc.blockReservedCheck(1, 15);
      fail();
    } catch (IncorrectBlockStatusException e) {
      assertEquals(1, e.getBlockId());
      assertEquals("reserved", e.getExpectedStatus());
    }
  }

  @Test
  public void testBlockReservedCheckWrongActionTypeFailure() {
    ReservationsRecord mockReservation = mockDb.getContext().newRecord(Tables.RESERVATIONS);
    mockReservation.setActionType(ReservationAction.COMPLETE);
    mockReservation.setUserId(2);
    mockReservation.setTeamId(3);

    mockDb.addReturn(OperationType.SELECT, mockReservation);
    mockDb.addExistsReturn(true);

    try {
      this.proc.blockReservedCheck(1, 15);
      fail();
    } catch (IncorrectBlockStatusException e) {
      assertEquals(1, e.getBlockId());
      assertEquals("reserved", e.getExpectedStatus());
    }

    assertEquals(0, mockDb.timesCalled(OperationType.EXISTS));
  }

  @Test
  public void testBlockReservedCheckLastCaseFailure() {
    ReservationsRecord mockReservation = mockDb.getContext().newRecord(Tables.RESERVATIONS);
    mockReservation.setActionType(ReservationAction.RESERVE);
    mockReservation.setUserId(2);
    mockReservation.setTeamId(3);

    mockDb.addReturn(OperationType.SELECT, mockReservation);
    mockDb.addExistsReturn(false);

    try {
      this.proc.blockReservedCheck(1, 15);
      fail();
    } catch (IncorrectBlockStatusException e) {
      assertEquals(1, e.getBlockId());
      assertEquals("reserved", e.getExpectedStatus());
    }

    assertEquals(1, mockDb.timesCalled(OperationType.EXISTS));
  }

  @Test
  public void testBlockReservedCheckSameUserSuccess() {
    int userId = 15;

    ReservationsRecord mockReservation = mockDb.getContext().newRecord(Tables.RESERVATIONS);
    mockReservation.setActionType(ReservationAction.RESERVE);
    mockReservation.setUserId(userId);
    mockReservation.setTeamId(3);

    mockDb.addReturn(OperationType.SELECT, mockReservation);
    mockDb.addExistsReturn(false);

    try {
      this.proc.blockReservedCheck(1, userId);
    } catch (IncorrectBlockStatusException e) {
      fail("Method should've returned with no exception");
    }

    assertEquals(0, mockDb.timesCalled(OperationType.EXISTS));
  }

  @Test
  public void testBlockReservedCheckSameTeamSuccess() {
    int userId = 15;
    int teamId = 3;

    ReservationsRecord mockReservation = mockDb.getContext().newRecord(Tables.RESERVATIONS);
    mockReservation.setActionType(ReservationAction.RESERVE);
    mockReservation.setUserId(4); // Wrong user id
    mockReservation.setTeamId(teamId);

    mockDb.addReturn(OperationType.SELECT, mockReservation);
    mockDb.addExistsReturn(true);

    try {
      this.proc.blockReservedCheck(1, userId);
    } catch (IncorrectBlockStatusException e) {
      fail("Method should've returned with no exception");
    }

    assertEquals(1, mockDb.timesCalled(OperationType.EXISTS));
  }

  @Test
  public void testBlockCompletedCheckNoLastActionFailure() {
    mockDb.addEmptyReturn(OperationType.SELECT);

    try {
      this.proc.blockCompleteCheck(1);
      fail();
    } catch (IncorrectBlockStatusException e) {
      assertEquals(1, e.getBlockId());
      assertEquals("complete", e.getExpectedStatus());
    }
  }

  @Test
  public void testBlockCompletedCheckWrongActionTypeFailure() {
    ReservationsRecord mockReservation = mockDb.getContext().newRecord(Tables.RESERVATIONS);
    mockReservation.setActionType(ReservationAction.QA);

    mockDb.addReturn(OperationType.SELECT, mockReservation);

    try {
      this.proc.blockCompleteCheck(1);
      fail();
    } catch (IncorrectBlockStatusException e) {
      assertEquals(1, e.getBlockId());
      assertEquals("complete", e.getExpectedStatus());
    }
  }

  @Test
  public void testBlockCompletedCheckSuccess() {
    ReservationsRecord mockReservation = mockDb.getContext().newRecord(Tables.RESERVATIONS);
    mockReservation.setActionType(ReservationAction.COMPLETE);

    mockDb.addReturn(OperationType.SELECT, mockReservation);

    try {
      this.proc.blockCompleteCheck(1);
    } catch (IncorrectBlockStatusException e) {
      fail("Method should return without throwing an exception");
    }
  }

  @Test
  public void testBlockQACheckNoLastActionFailure() {
    mockDb.addEmptyReturn(OperationType.SELECT);

    try {
      this.proc.blockQACheck(1);
      fail();
    } catch (IncorrectBlockStatusException e) {
      assertEquals(1, e.getBlockId());
      assertEquals("QA", e.getExpectedStatus());
    }
  }

  @Test
  public void testBlockQACheckWrongActionTypeFailure() {
    ReservationsRecord mockReservation = mockDb.getContext().newRecord(Tables.RESERVATIONS);
    mockReservation.setActionType(ReservationAction.COMPLETE);

    mockDb.addReturn(OperationType.SELECT, mockReservation);

    try {
      this.proc.blockQACheck(1);
      fail();
    } catch (IncorrectBlockStatusException e) {
      assertEquals(1, e.getBlockId());
      assertEquals("QA", e.getExpectedStatus());
    }
  }

  @Test
  public void testBlockQACheckSuccess() {
    ReservationsRecord mockReservation = mockDb.getContext().newRecord(Tables.RESERVATIONS);
    mockReservation.setActionType(ReservationAction.QA);

    mockDb.addReturn(OperationType.SELECT, mockReservation);

    try {
      this.proc.blockQACheck(1);
    } catch (IncorrectBlockStatusException e) {
      fail("Method should return without throwing an exception");
    }
  }
}
