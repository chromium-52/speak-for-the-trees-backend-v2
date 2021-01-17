package com.codeforcommunity.dto.imports;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ImportReservationsRequest extends ApiDto {
  private List<ReservationImport> reservations;

  public ImportReservationsRequest(List<ReservationImport> reservations) {
    this.reservations = reservations;
  }

  private ImportReservationsRequest() {}

  public List<ReservationImport> getReservations() {
    return reservations;
  }

  public void setReservations(List<ReservationImport> reservations) {
    this.reservations = reservations;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String newFieldPrefix = fieldPrefix + "importReservationRequest.";

    if (reservations == null) {
      return Collections.singletonList(newFieldPrefix + "reservations");
    }

    return reservations.stream()
        .flatMap(ni -> ni.validateFields(newFieldPrefix).stream())
        .collect(Collectors.toList());
  }
}
