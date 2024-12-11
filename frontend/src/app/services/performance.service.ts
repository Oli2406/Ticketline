import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {Globals} from '../global/globals';
import { Performance, PerformanceListDto } from 'src/app/dtos/performance';
import {EventListDto} from "../dtos/event";

@Injectable({
  providedIn: 'root',
})
export class PerformanceService {
  private apiUrl: string = this.globals.backendUri + '/performance';

  constructor(private http: HttpClient, private globals: Globals) {}

  getPerformances(): Observable<PerformanceListDto[]> {
    return this.http.get<PerformanceListDto[]>(this.apiUrl);
  }

  getPerformanceById(id: number): Observable<PerformanceListDto> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<PerformanceListDto>(url).pipe(
      catchError(this.handleError)
    );
  }


  advancedSearchPerformances(query: string): Observable<PerformanceListDto[]> {
    const url = `${this.apiUrl}/advanced-search?query=${query}`;
    return this.http.get<PerformanceListDto[]>(url);
  }

  createPerformance(performance: Performance): Observable<PerformanceListDto> {
    return this.http.put<PerformanceListDto>(this.apiUrl, performance).pipe(
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
