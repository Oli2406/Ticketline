import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Globals } from '../global/globals';
import { Reservation, ReservationListDto } from '../dtos/reservation';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  private apiUrl: string = this.globals.backendUri + '/reserved';

  constructor(private http: HttpClient, private globals: Globals) {}

  /**
   * Get all reservations for a specific user by userId
   */
  getReservationsByUser(encryptedUserId: string): Observable<ReservationListDto[]> {
    const url = `${this.apiUrl}/user/${encryptedUserId}`;
    return this.http.get<ReservationListDto[]>(url).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Get a specific reservation by reservationId
   */
  getReservationById(reservationId: number): Observable<ReservationListDto> {
    const url = `${this.apiUrl}/${reservationId}`;
    return this.http.get<ReservationListDto>(url).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Create a new reservation
   */
  createReservation(reservation: Reservation): Observable<Reservation> {
    console.log(this.apiUrl);
    return this.http.post<Reservation>(this.apiUrl, reservation).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Update a reservation after cancelling a ticket
   */
  updateReservation(reservation: ReservationListDto): Observable<ReservationListDto> {
    console.log(this.apiUrl);
    const url = `${this.apiUrl}/${reservation.reservedId}`;
    return this.http.put<ReservationListDto>(url, reservation).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Handle HTTP errors
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    let cleanedError = 'An unexpected error occurred.';
    if (error.error) {
      if (error.error.errors) {
        try {
          const rawDetails = error.error.errors.replace(/^\[|\]$/g, '');
          const errors = rawDetails.split(/(?=[A-Z])/);
          const cleanedErrors = errors.map((err) =>
            err.replace(/,\s*$/, '').trim()
          );
          cleanedError = cleanedErrors.join('\n');
        } catch {
          cleanedError = error.error.details;
        }
      } else if (typeof error.error === 'string') {
        cleanedError = error.error;
      } else if (error.error.message) {
        cleanedError = error.error.message;
      }
    }
    return throwError(() => new Error(cleanedError));
  }

  deleteTicketFromReservation(reservationId: number, ticketId: number): Observable<void> {
    const url = `${this.apiUrl}/${reservationId}/ticket/${ticketId}`;
    return this.http.delete<void>(url).pipe(
      catchError(this.handleError)
    );
  }

}
