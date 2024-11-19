import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {Globals} from '../global/globals';
import {UserRegistrationDto} from 'src/app/dtos/register-data'


@Injectable({
  providedIn: "root"
})

export class RegisterService {
  private registerBaseUri: string = this.globals.backendUri + '/register';

  constructor(private httpClient: HttpClient,
              private globals: Globals) {}

  registerUser(data: UserRegistrationDto): Observable<UserRegistrationDto> {
    return this.httpClient.post<UserRegistrationDto>(this.registerBaseUri, data).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let cleanedError = 'An unexpected error occurred.';
    console.log(error.error.errors)
    if (error.error) {
      if (error.error.errors) {
        try {
          const rawDetails = error.error.errors.replace(/^\[|\]$/g, '');
          const errors = rawDetails.split(/(?=[A-Z])/);
          const cleanedErrors = errors.map((err) => err.replace(/,\s*$/, '').trim());
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
