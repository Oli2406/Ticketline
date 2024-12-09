import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {Globals} from '../global/globals';
import {Artist, ArtistListDto, ArtistSearch} from 'src/app/dtos/artist';

@Injectable({
  providedIn: 'root',
})
export class ArtistService {
  private apiUrl: string = this.globals.backendUri + '/artist';

  constructor(private http: HttpClient, private globals: Globals) {
  }

  getArtists(): Observable<ArtistListDto[]> {
    return this.http.get<ArtistListDto[]>(this.apiUrl).pipe(
      catchError(this.handleError)
    );
  }

  getById(id: number): Observable<ArtistListDto> {
    return this.http.get<ArtistListDto>(`${(this.apiUrl)}/${id}`);
  }

  getAllByFilter(filter: ArtistSearch): Observable<ArtistListDto[]> {
    let params = new HttpParams();
    if (filter.firstName?.trim()) {
      params = params.append('firstName', filter.firstName);
    }
    if (filter.surname?.trim()) {
      params = params.append('surname', filter.surname);
    }
    if (filter.artistName?.trim()) {
      params = params.append('artistName', filter.artistName);
    }

    return this.http.get<ArtistListDto[]>(this.apiUrl + "/search", {params});
  }

  createArtist(artist: Artist): Observable<Artist> {
    return this.http.put<Artist>(this.apiUrl, artist).pipe(
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
