import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { Globals } from '../global/globals';
import { Ticket, TicketDto } from 'src/app/dtos/ticket';

@Injectable({
  providedIn: 'root',
})
export class TicketService {
  private apiUrl: string = this.globals.backendUri + '/ticket';

  constructor(private http: HttpClient, private globals: Globals) {}

  /**
   * Sends a request to create a new ticket in the backend.
   * @param ticket - The ticket object to be created.
   * @returns Observable<TicketListDto>
   */
  createTicket(ticket: Ticket): Observable<TicketDto> {
    return this.http.post<TicketDto>(this.apiUrl, ticket).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Retrieves tickets for a specific performance ID from the backend.
   * @param performanceId - The ID of the performance to fetch tickets for.
   * @returns Observable<TicketDto[]>
   */
  getTicketsByPerformanceId(performanceId: number): Observable<TicketDto[]> {
    const url = `${this.apiUrl}/performance/${performanceId}`;
    return this.http.get<TicketDto[]>(url).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Updates the status of a ticket to "RESERVED".
   * @param ticketId - The ID of the ticket to update.
   * @returns Observable<TicketDto>
   */
  updateTicketStatusToReserved(ticketId: number): Observable<TicketDto> {
    const url = `${this.apiUrl}/${ticketId}/status`;
    const updatedStatus = { status: "RESERVED" };
    return this.http.patch<TicketDto>(url, updatedStatus).pipe(
      catchError(this.handleError)
    );
  }


  /**
   * Handles HTTP errors returned from the backend.
   * @param error - The HttpErrorResponse
   * @returns Observable<never>
   */
  public handleError(error: HttpErrorResponse): Observable<never> {
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
}
