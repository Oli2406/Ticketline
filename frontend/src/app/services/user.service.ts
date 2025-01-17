import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Globals} from "../global/globals";
import {DeleteUserDto, UserUpdateReadNewsDto} from "../dtos/user-data";
import {Observable} from "rxjs";
import {UserToUpdateDto} from "../dtos/register-data";


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

  deleteUser(userToDelete: DeleteUserDto) {
    return this.httpClient.delete(`${this.baseUri}`, {body:userToDelete});
  }

  getUserData(userId: string): Observable<UserToUpdateDto> {
    return this.httpClient.get<UserToUpdateDto>(`${this.baseUri}/${userId}`);
  }

  /**
   * Stores the version of current user data in localStorage.
   */
  storeUserVersion(version: number): void {
    localStorage.setItem('userVersion', String(version));
  }

  clearUserVersion():void {
    localStorage.removeItem('userVersion');
  }

  getUserVersion():number {
    const version = localStorage.getItem('userVersion');
    return Number(version);
  }
}
