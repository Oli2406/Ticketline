import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {Globals} from '../global/globals';
import { Event } from 'src/app/dtos/event';

@Injectable({
  providedIn: 'root',
})
export class EventService {
  private apiUrl: string = this.globals.backendUri + '/event';

  constructor(private http: HttpClient, private globals: Globals) {}

  createEvent(event: Event): Observable<Event> {
    return this.http.put<Event>(this.apiUrl, event);
  }
}
