import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {Globals} from '../global/globals';
import {UserDetailDto} from "../dtos/user-data";

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private baseUrl: string = this.globals.backendUri + '/admin';
  constructor(private http: HttpClient, private globals: Globals) {}

  getUsers(): Observable<UserDetailDto[]> {
    return this.http.get<UserDetailDto[]>(`${this.baseUrl}`);
  }

  unlockUser(id: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/unlock/${id}`, {});
  }
}
