import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {Globals} from '../global/globals';
import { Performance, PerformanceListDto } from 'src/app/dtos/performance';

@Injectable({
  providedIn: 'root',
})
export class PerformanceService {
  private apiUrl: string = this.globals.backendUri + '/performance';

  constructor(private http: HttpClient, private globals: Globals) {}

  getPerformances(): Observable<PerformanceListDto[]> {
    return this.http.get<PerformanceListDto[]>(this.apiUrl);
  }

  createPerformance(performance: Performance): Observable<PerformanceListDto> {
    return this.http.put<PerformanceListDto>(this.apiUrl, performance);
  }
}
