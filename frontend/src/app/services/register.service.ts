import {Injectable} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable} from 'rxjs';
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
    return this.httpClient.post<UserRegistrationDto>(this.registerBaseUri, data);
  }
}
