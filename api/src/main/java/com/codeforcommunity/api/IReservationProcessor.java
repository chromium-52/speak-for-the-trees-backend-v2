package com.codeforcommunity.api;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.reservation.*;

public interface IReservationProcessor {

  /**
   * Marks the given block as reserved for the given user/team by creating a new entry in the
   * reservation table
   */
  void makeReservation(JWTData userData, MakeReservationRequest makeReservationRequest);

  /**
   * Marks the given block as complete for the given user/team by creating a new entry in the
   * reservation table
   */
  void completeReservation(JWTData userData, CompleteReservationRequest completeReservationRequest);

  /** Marks the given block as open, thereby releasing the reservation on the given block */
  void releaseReservation(JWTData userData, ReleaseReservationRequest releaseReservationRequest);

  /**
   * Marks the given block as open and marking the previous completion invalid for the given block
   */
  void uncompleteReservation(
      JWTData userData, UncompleteReservationRequest uncompleteReservationRequest);

  /** Marks the given block for QA, meaning SFTT wants to check the completion of the given block */
  void markForQA(JWTData userData, MarkForQARequest markForQARequest);
}
