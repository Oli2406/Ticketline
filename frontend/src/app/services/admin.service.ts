import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {Globals} from '../global/globals';
import {UserDetailDto} from "../dtos/user-data";
import {AdminUserRegistrationDto} from "../dtos/register-data";

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private baseUrl: string = this.globals.backendUri + '/admin';

  constructor(private http: HttpClient, private globals: Globals) {
  }

  getUsers(): Observable<UserDetailDto[]> {
    return this.http.get<UserDetailDto[]>(`${this.baseUrl}`);
  }

  unlockUser(id: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/unlock/${id}`, {});
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
