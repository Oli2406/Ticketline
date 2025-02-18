import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {Globals} from '../global/globals';
import {Event, EventListDto, EventSalesDto, EventSearch} from 'src/app/dtos/event';

@Injectable({
  providedIn: 'root',
})
export class EventService {
  private apiUrl: string = this.globals.backendUri + '/event';

  constructor(private http: HttpClient, private globals: Globals) {
  }

  createEvent(event: Event): Observable<Event> {
    return this.http.put<Event>(this.apiUrl, event).pipe(
      catchError(this.handleError)
    );
  }

  getById(id: number): Observable<EventListDto> {
    return this.http.get<EventListDto>(`${(this.apiUrl)}/${id}`);
  }

  get(): Observable<EventListDto[]> {
    return this.http.get<EventListDto[]>(this.apiUrl).pipe(
      catchError(this.handleError)
    );
  }

  getAllByFilter(filter: EventSearch): Observable<EventListDto[]> {
    let params = new HttpParams();
    if (filter.title?.trim()) {
      params = params.append('title', filter.title);
    }
    if (filter.category?.trim()) {
      params = params.append('category', filter.category);
    }
    if (filter.dateEarliest?.trim()) {
      params = params.append('dateEarliest', filter.dateEarliest);
    }
    if (filter.dateLatest?.trim()) {
      params = params.append('dateLatest', filter.dateLatest);
    }

    return this.http.get<EventListDto[]>(this.apiUrl + "/search", {params});
  }

  getEventsByArtistId(id: number): Observable<EventListDto[]> {
    return this.http.get<EventListDto[]>(`${(this.apiUrl)}/artist/${id}`);
  }

  getTop10Events(month: string, category: string) {
    return this.http.get<EventSalesDto[]>(`${this.apiUrl}/top10`, {
      params: {
        month: month,
        category: category
      }
    });
  }

  getAllCategories(): Observable<string[]> {
    return this.http.get<string[]>(this.apiUrl + "/categories");
  }


  public handleError(error: HttpErrorResponse): Observable<never> {
    let cleanedError = 'An unexpected error occurred.';
    if (error.error) {
      if (error.error.errors) {
        try {
          const rawDetails = error.error.errors.replace(/^\[|\]$/g, '');
          const errors = rawDetails.split(/(?=[A-Z])/);
          const cleanedErrors = errors.map((err) => err.replace(/,\s*$/, '').trim());
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
