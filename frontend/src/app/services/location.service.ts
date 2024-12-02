import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {Globals} from '../global/globals';
import { Location, LocationListDto } from 'src/app/dtos/location';

@Injectable({
  providedIn: 'root',
})
export class LocationService {
  private apiUrl: string = this.globals.backendUri + '/location';

  constructor(private http: HttpClient, private globals: Globals) {}

  getLocations(): Observable<LocationListDto[]> {
    return this.http.get<LocationListDto[]>(this.apiUrl);
  }

  createLocation(location: Location): Observable<Location> {
    return this.http.put<Location>(this.apiUrl, location);
  }
}
