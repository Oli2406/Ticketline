import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {Globals} from '../global/globals';
import {AdminUserRegistrationDto, UserRegistrationDto} from 'src/app/dtos/register-data'
import {ErrorFormatterService} from "./error-formatter.service";


@Injectable({
  providedIn: "root"
})

export class RegisterService {
  private registerBaseUri: string = this.globals.backendUri + '/register';

  constructor(private httpClient: HttpClient,
              private globals: Globals,
              private errorFormatter: ErrorFormatterService) {}

  registerUser(data: UserRegistrationDto): Observable<UserRegistrationDto> {
    return this.httpClient.post<UserRegistrationDto>(this.registerBaseUri, data).pipe(
      catchError(this.errorFormatter.handleError)
    );
  }
}
