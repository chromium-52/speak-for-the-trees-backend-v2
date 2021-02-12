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
import org.junit.jupiter.api.Test;

public class ReservationProcessorImplTest {
  JooqMock mockDb;
  ReservationProcessorImpl proc;

  /**
   * Method to setup mockDb and processor
   *
   * <p>TODO: This can't be run with the @BeforeEach annotation by maven currently. The way to
   * enable this is outlined in this Stack Overflow post:
   * https://stackoverflow.com/questions/51382356/maven-does-not-run-beforeeach-methods-while-running
   * This isn't currently being done because our version of maven surefire is auto imported and is
   * only version 2.12.4
   */
  void setup() {
    mockDb = new JooqMock();
    proc = new ReservationProcessorImpl(mockDb.getContext());
  }

  @Test
  public void testBasicChecksNonExistingBlock() {
    setup();
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
  public void testBasicChecksNonExistingUser() {
    setup();
    int blockId = 1;
    int userId = 2;
    int teamId = 3;

    mockDb.addExistsReturn(true);
    mockDb.addExistsReturn(false);

    try {
      this.proc.basicChecks(blockId, userId, teamId);
      fail("Method should've thrown a UserDoesNotExist Exception");
    } catch (UserDoesNotExistException e) {
      assertEquals("id = " + userId, e.getIdentifierMessage());
    }
    assertEquals(2, mockDb.timesCalled(OperationType.EXISTS));
  }

  @Test
  public void testBasicChecksNonExistingTeam() {
    setup();
    int blockId = 1;
    int userId = 2;
    int teamId = 3;

    mockDb.addExistsReturn(true);
    mockDb.addExistsReturn(true);
    mockDb.addExistsReturn(false);

    try {
      this.proc.basicChecks(blockId, userId, teamId);
      fail("Method should've thrown a ResourceDoesNotExist Exception");
    } catch (ResourceDoesNotExistException e) {
      assertEquals(teamId, e.getResourceId());
      assertEquals("team", e.getResourceType());
    }
    assertEquals(3, mockDb.timesCalled(OperationType.EXISTS));
  }

  @Test
  public void testBasicChecksNotOnTeam() {
    setup();
    int blockId = 1;
    int userId = 2;
    int teamId = 3;

    mockDb.addExistsReturn(true);
    mockDb.addExistsReturn(true);
    mockDb.addExistsReturn(true);
    mockDb.addExistsReturn(false);

    try {
      this.proc.basicChecks(blockId, userId, teamId);
      fail("Method should've thrown a UserNotOnTeam Exception");
    } catch (UserNotOnTeamException e) {
      assertEquals(userId, e.getUserId());
      assertEquals(teamId, e.getTeamId());
    }
    assertEquals(4, mockDb.timesCalled(OperationType.EXISTS));
  }

  @Test
  public void testBasicChecksSuccess() {
    setup();
    int blockId = 1;
    int userId = 2;
    int teamId = 3;

    mockDb.addExistsReturn(true);
    mockDb.addExistsReturn(true);
    mockDb.addExistsReturn(true);
    mockDb.addExistsReturn(true);

    try {
      this.proc.basicChecks(blockId, userId, teamId);
    } catch (UserNotOnTeamException | ResourceDoesNotExistException | UserDoesNotExistException e) {
      fail("Method should've returned without exception");
    }
    assertEquals(4, mockDb.timesCalled(OperationType.EXISTS));
  }

  @Test
  public void testBasicChecksSuccessNullCase() {
    setup();
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
    setup();
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
    setup();
    mockDb.addEmptyReturn(OperationType.SELECT);

    try {
      this.proc.blockOpenCheck(1);
    } catch (IncorrectBlockStatusException e) {
      fail("Method should not throw exception");
    }
  }

  @Test
  public void testBlockOpenCheckUncomplete() {
    setup();
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
    setup();
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
    setup();
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
    setup();
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
    setup();
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
    setup();
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
    setup();
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
    setup();
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
    setup();
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
    setup();
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
    setup();
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
    setup();
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
    setup();
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
