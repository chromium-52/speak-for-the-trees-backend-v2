package com.codeforcommunity.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeforcommunity.JooqMock;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.enums.ReservationAction;
import com.codeforcommunity.exceptions.AuthException;
import com.codeforcommunity.exceptions.IncorrectBlockStatusException;
import java.util.Optional;
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
    proc = spy(new ReservationProcessorImpl(mockDb.getContext()));
  }

  @Test
  public void testIsAdmin() {
    setup();
    for (PrivilegeLevel p : PrivilegeLevel.values()) {
      boolean isAdmin = p.equals(PrivilegeLevel.ADMIN) || p.equals(PrivilegeLevel.SUPER_ADMIN);
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

    try {
      this.proc.isAdminCheck(PrivilegeLevel.ADMIN);
    } catch (AuthException e) {
      fail("Method should not have thrown an exception");
    }

    try {
      this.proc.isAdminCheck(PrivilegeLevel.SUPER_ADMIN);
    } catch (AuthException e) {
      fail("Method should not have thrown an exception");
    }
  }

  @Test
  public void testBlockOpenCheckNoAction() {
    setup();
    doReturn(Optional.empty()).when(this.proc).lastAction(anyInt());

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
    doReturn(Optional.of(mockReservation)).when(this.proc).lastAction(anyInt());

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
    ReservationsRecord mockReservation = mock(ReservationsRecord.class);
    doReturn(Optional.of(mockReservation)).when(this.proc).lastAction(anyInt());

    when(mockReservation.getActionType())
        .thenReturn(ReservationAction.RESERVE, ReservationAction.RESERVE);

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
    doReturn(Optional.empty()).when(this.proc).lastAction(anyInt());

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
    ReservationsRecord mockReservation = mock(ReservationsRecord.class);
    doReturn(Optional.of(mockReservation)).when(this.proc).lastAction(anyInt());
    doReturn(false).when(this.proc).isOnTeam(anyInt(), anyInt());

    when(mockReservation.getActionType()).thenReturn(ReservationAction.COMPLETE);
    when(mockReservation.getUserId()).thenReturn(2, 2);
    when(mockReservation.getTeamId()).thenReturn(3, 3);

    try {
      this.proc.blockReservedCheck(1, 15);
      fail();
    } catch (IncorrectBlockStatusException e) {
      assertEquals(1, e.getBlockId());
      assertEquals("reserved", e.getExpectedStatus());
    }

    verify(mockReservation, times(1)).getActionType();
    verify(mockReservation, times(0)).getUserId();
    verify(mockReservation, times(0)).getTeamId();
    verify(this.proc, times(0)).isOnTeam(anyInt(), anyInt());
  }

  @Test
  public void testBlockReservedCheckLastCaseFailure() {
    setup();
    ReservationsRecord mockReservation = mock(ReservationsRecord.class);
    doReturn(Optional.of(mockReservation)).when(this.proc).lastAction(anyInt());
    doReturn(false).when(this.proc).isOnTeam(anyInt(), anyInt());

    when(mockReservation.getActionType()).thenReturn(ReservationAction.RESERVE);
    when(mockReservation.getUserId()).thenReturn(2, 2);
    when(mockReservation.getTeamId()).thenReturn(3, 3);

    try {
      this.proc.blockReservedCheck(1, 15);
      fail();
    } catch (IncorrectBlockStatusException e) {
      assertEquals(1, e.getBlockId());
      assertEquals("reserved", e.getExpectedStatus());
    }

    verify(mockReservation, times(1)).getActionType();
    verify(mockReservation, times(2)).getUserId();
    verify(mockReservation, times(2)).getTeamId();
    verify(this.proc, times(1)).isOnTeam(anyInt(), anyInt());
  }

  @Test
  public void testBlockReservedCheckSameUserSuccess() {
    setup();
    ReservationsRecord mockReservation = mock(ReservationsRecord.class);
    doReturn(Optional.of(mockReservation)).when(this.proc).lastAction(anyInt());
    doReturn(false).when(this.proc).isOnTeam(anyInt(), anyInt());

    int userId = 15;
    when(mockReservation.getActionType()).thenReturn(ReservationAction.RESERVE);
    when(mockReservation.getUserId()).thenReturn(userId, userId);
    when(mockReservation.getTeamId()).thenReturn(3, 3);

    try {
      this.proc.blockReservedCheck(1, userId);
    } catch (IncorrectBlockStatusException e) {
      fail("Method should've returned with no exception");
    }

    verify(mockReservation, times(1)).getActionType();
    verify(mockReservation, times(2)).getUserId();
    verify(mockReservation, times(0)).getTeamId();
    verify(this.proc, times(0)).isOnTeam(anyInt(), anyInt());
  }

  @Test
  public void testBlockReservedCheckSameTeamSuccess() {
    setup();
    ReservationsRecord mockReservation = mock(ReservationsRecord.class);
    doReturn(Optional.of(mockReservation)).when(this.proc).lastAction(anyInt());
    doReturn(true).when(this.proc).isOnTeam(anyInt(), anyInt());

    int userId = 15;
    int teamId = 3;

    when(mockReservation.getActionType()).thenReturn(ReservationAction.RESERVE);
    when(mockReservation.getUserId()).thenReturn(4, 4); // Wrong user ID
    when(mockReservation.getTeamId()).thenReturn(teamId, teamId);

    try {
      this.proc.blockReservedCheck(1, userId);
    } catch (IncorrectBlockStatusException e) {
      fail("Method should've returned with no exception");
    }

    verify(mockReservation, times(1)).getActionType();
    verify(mockReservation, times(2)).getUserId();
    verify(mockReservation, times(2)).getTeamId();
    verify(this.proc, times(1)).isOnTeam(userId, teamId);
  }

  @Test
  public void testBlockCompletedCheckNoLastActionFailure() {
    setup();
    doReturn(Optional.empty()).when(this.proc).lastAction(anyInt());

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
    ReservationsRecord mockReservation = mock(ReservationsRecord.class);
    doReturn(Optional.of(mockReservation)).when(this.proc).lastAction(anyInt());

    when(mockReservation.getActionType()).thenReturn(ReservationAction.QA);

    try {
      this.proc.blockCompleteCheck(1);
      fail();
    } catch (IncorrectBlockStatusException e) {
      assertEquals(1, e.getBlockId());
      assertEquals("complete", e.getExpectedStatus());
    }

    verify(mockReservation, times(1)).getActionType();
  }

  @Test
  public void testBlockCompletedCheckSuccess() {
    setup();
    ReservationsRecord mockReservation = mock(ReservationsRecord.class);
    doReturn(Optional.of(mockReservation)).when(this.proc).lastAction(anyInt());

    when(mockReservation.getActionType()).thenReturn(ReservationAction.COMPLETE);

    try {
      this.proc.blockCompleteCheck(1);
    } catch (IncorrectBlockStatusException e) {
      fail("Method should return without throwing an exception");
    }

    verify(mockReservation, times(1)).getActionType();
  }

  @Test
  public void testBlockQACheckNoLastActionFailure() {
    setup();
    doReturn(Optional.empty()).when(this.proc).lastAction(anyInt());

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
    ReservationsRecord mockReservation = mock(ReservationsRecord.class);
    doReturn(Optional.of(mockReservation)).when(this.proc).lastAction(anyInt());

    when(mockReservation.getActionType()).thenReturn(ReservationAction.COMPLETE);

    try {
      this.proc.blockQACheck(1);
      fail();
    } catch (IncorrectBlockStatusException e) {
      assertEquals(1, e.getBlockId());
      assertEquals("QA", e.getExpectedStatus());
    }

    verify(mockReservation, times(1)).getActionType();
  }

  @Test
  public void testBlockQACheckSuccess() {
    setup();
    ReservationsRecord mockReservation = mock(ReservationsRecord.class);
    doReturn(Optional.of(mockReservation)).when(this.proc).lastAction(anyInt());

    when(mockReservation.getActionType()).thenReturn(ReservationAction.QA);

    try {
      this.proc.blockQACheck(1);
    } catch (IncorrectBlockStatusException e) {
      fail("Method should return without throwing an exception");
    }

    verify(mockReservation, times(1)).getActionType();
  }
}
