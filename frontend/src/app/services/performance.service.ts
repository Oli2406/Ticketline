import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {Globals} from '../global/globals';
import {
  Performance,
  PerformanceDetailDto,
  PerformanceListDto,
  PerformanceSearch
} from 'src/app/dtos/performance';


@Injectable({
  providedIn: 'root',
})
export class PerformanceService {
  private apiUrl: string = this.globals.backendUri + '/performance';

  constructor(private http: HttpClient, private globals: Globals) {
  }

  get(): Observable<PerformanceListDto[]> {
    return this.http.get<PerformanceListDto[]>(this.apiUrl);
  }

  getByEventId(id: number): Observable<PerformanceDetailDto[]> {
    return this.http.get<PerformanceDetailDto[]>(`${(this.apiUrl)}/event/${id}`);
  }

  getByLocationId(id: number): Observable<PerformanceDetailDto[]> {
    return this.http.get<PerformanceDetailDto[]>(`${(this.apiUrl)}/location/${id}`);
  }

  getAllByFilter(filter: PerformanceSearch): Observable<PerformanceDetailDto[]> {
    let params = new HttpParams();
    if (filter.date?.trim()) {
      params = params.append('date', filter.date);
    }
    if (filter.price != null) {
      params = params.append('price', filter.price);
    }
    if (filter.hall?.trim()) {
      params = params.append('hall', filter.hall);
    }

    return this.http.get<PerformanceDetailDto[]>(this.apiUrl + "/search", {params});
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

  getPerformanceById(id: number): Observable<PerformanceListDto> {
    return this.http.get<PerformanceListDto>(`${(this.apiUrl)}/${id}`).pipe(
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



  updateTicketNumber(performanceId: number, ticketNumber: number): Observable<PerformanceDetailDto> {
    const url = `${this.apiUrl}/${performanceId}`;
    return this.http.put<PerformanceDetailDto>(url, ticketNumber).pipe(
      catchError(this.handleError)
    );
  }

}
