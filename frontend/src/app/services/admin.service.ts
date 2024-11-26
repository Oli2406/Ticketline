import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Globals } from '../global/globals';
import { AdminUserRegistrationDto } from '../dtos/register-data';

// Dummy user data
const DUMMY_USERS = [
  {
    firstName: 'Max',
    lastName: 'Mustermann',
    email: 'muster@email.com',
    isAdmin: false,
  },
  {
    firstName: 'Sarah',
    lastName: 'Jones',
    email: 'sarah@email.com',
    isAdmin: true,
  },
  {
    firstName: 'Herbert',
    lastName: 'Müller',
    email: 'herbert@email.com',
    isAdmin: false,
  },
  {
    firstName: 'Monika',
    lastName: 'Klinger',
    email: 'monika@email.com',
    isAdmin: false,
  },
  {
    firstName: 'Fridolin',
    lastName: 'Schönemann',
    email: 'fridolin@email.com',
    isAdmin: true,
  },
];

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private adminBaseUri: string = this.globals.backendUri + '/register';

  constructor(private httpClient: HttpClient, private globals: Globals) {}

  /**
   * Creates a new user (admin or regular user).
   * @param data - AdminUserRegistrationDto containing user details and role
   * @returns Observable of the created user
   */
  createUser(data: AdminUserRegistrationDto): Observable<AdminUserRegistrationDto> {
    return this.httpClient.post<AdminUserRegistrationDto>(this.adminBaseUri, data).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Dummy function to fetch users (this should be replaced with a real backend call).
   * @returns Observable with a list of dummy users
   */
  getUsers(): Observable<any[]> {
    // Return the dummy users
    return of(DUMMY_USERS);
  }

  /**
   * Handles API errors and formats error messages.
   * @param error - HttpErrorResponse from the backend
   * @returns Observable that throws a cleaned error message
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    let cleanedError = 'An unexpected error occurred.';
    console.log(error.error.errors);
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
