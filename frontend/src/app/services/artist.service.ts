import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {Globals} from '../global/globals';
import { Artist, ArtistListDto } from 'src/app/dtos/artist';

@Injectable({
  providedIn: 'root',
})
export class ArtistService {
  private apiUrl: string = this.globals.backendUri + '/artist';

  constructor(private http: HttpClient, private globals: Globals) {}

  getArtists(): Observable<ArtistListDto[]> {
    return this.http.get<ArtistListDto[]>(this.apiUrl);
  }

  createArtist(artist: Artist): Observable<Artist> {
    return this.http.put<Artist>(this.apiUrl, artist);
  }
}
