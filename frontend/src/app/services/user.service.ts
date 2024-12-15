import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {UserUpdateReadNewsDto} from "../dtos/user-data";
import {ToastrService} from "ngx-toastr";
import {Observable} from "rxjs";
import {RegisterData, UserRegistrationDto, UserToUpdateDto} from "../dtos/register-data";


@Injectable({
  providedIn: 'root'
})
export class UserService {

  private baseUri: string = this.globals.backendUri + '/users' //globals.backendUri = http://localhost:8080/api/v1

  constructor(private httpClient: HttpClient,
              private globals: Globals) {
  }

  updateReadNews(dto: UserUpdateReadNewsDto): Observable<boolean> {
    return this.httpClient.put<boolean>(`${this.baseUri}`, dto);
  }

  updateUser(userDto: UserToUpdateDto): Observable<string> {
    return this.httpClient.put(`${this.baseUri}/update-user`, userDto, {responseType: 'text'});
  }
}
