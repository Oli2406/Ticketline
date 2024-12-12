import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {catchError, Observable} from 'rxjs';
import {Merchandise} from "../dtos/merchandise";
import {ErrorFormatterService} from "./error-formatter.service";
import {Globals} from "../global/globals";

@Injectable({
  providedIn: 'root',
})
export class MerchandiseService {
  private apiUrl = this.globals.backendUri + '/merchandise';

  constructor(private http: HttpClient,
              private errorFormatter: ErrorFormatterService,
              private globals: Globals) {}

  createMerchandise(merchandiseData: any, imageFile: File): Observable<any> {
    const formData = new FormData();
    formData.append(
      'merchandise',
      new Blob([JSON.stringify(merchandiseData)], { type: 'application/json' })
    );
    if (imageFile) {
      formData.append('image', imageFile);
    }
    return this.http.post(this.apiUrl + "/create", formData).pipe(
      catchError(this.errorFormatter.handleError)
    );
  }

  getMerchandise(): Observable<Merchandise[]> {
    return this.http.get<Merchandise[]>(this.apiUrl);
  }
}
