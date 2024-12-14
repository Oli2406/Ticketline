import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {UserDetailDto} from "../dtos/user-data";
import {tap} from "rxjs/operators";
import {AuthService} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private baseUrl: string = this.globals.backendUri + '/admin';

  constructor(private http: HttpClient, private globals: Globals, private authService: AuthService) {
  }

  getUsers(): Observable<UserDetailDto[]> {
    return this.http.get<UserDetailDto[]>(`${this.baseUrl}`);
  }

  unlockUser(id: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/unlock/${id}`, {});
  }

  lockUser(id: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/lock/${id}`, {});
  }

  sendEmailToResetPassword(email:string): Observable<string> {
    return this.http.post(`${this.baseUrl}/send-email`, email, {responseType: 'text'})
    .pipe(
      tap((authResponse: string) => {
        this.authService.storeResetToken(authResponse);
      })
    );
  }
}
