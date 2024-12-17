import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {Globals} from '../global/globals';
import {Location, LocationListDto, LocationSearch} from 'src/app/dtos/location';
import {EventListDto, EventSearch} from "../dtos/event";

@Injectable({
  providedIn: 'root',
})
export class LocationService {
  private apiUrl: string = this.globals.backendUri + '/location';

  constructor(private http: HttpClient, private globals: Globals) {}

  getById(id: number): Observable<LocationListDto> {
    return this.http.get<LocationListDto>(`${(this.apiUrl)}/${id}`);
  }

  getLocations(): Observable<LocationListDto[]> {
    return this.http.get<LocationListDto[]>(this.apiUrl);
  }

  getAllByFilter(filter: LocationSearch): Observable<LocationListDto[]> {
    let params = new HttpParams();
    if (filter.name?.trim()) {
      params = params.append('name', filter.name);
    }
    if (filter.street?.trim()) {
      params = params.append('street', filter.street);
    }
    if (filter.city?.trim()) {
      params = params.append('city', filter.city);
    }
    if (filter.country?.trim()) {
      params = params.append('country', filter.country);
    }
    if (filter.postalCode != null) {
      params = params.append('postalCode', filter.postalCode);
    }

    return this.http.get<LocationListDto[]>(this.apiUrl + "/search", {params});
  }

  createLocation(location: Location): Observable<Location> {
    return this.http.put<Location>(this.apiUrl, location).pipe(
      catchError(this.handleError)
    );
  }

  public handleError(error: HttpErrorResponse): Observable<never> {
    let cleanedError = 'An unexpected error occurred.';
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
