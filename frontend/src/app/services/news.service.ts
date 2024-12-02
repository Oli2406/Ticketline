import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Globals} from "../global/globals";
import {NewsData} from "../dtos/news-data";
import {catchError, Observable, throwError} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class NewsService {

  private baseURI: string = this.globals.backendUri + '/create-news';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  /**
   * Creates new news in the system
   * @param data NewsData containing title, summary, content, imageURls and date of creation
   * @return Observable of the created news
   * */
  createNews(data: FormData ): Observable<NewsData>{
    console.log("service start");
    console.log(data);
    return this.httpClient.post<NewsData>(this.baseURI, data).pipe(catchError(this.handleError));
  }

  /**
   * Handles API errors and formats error messages.
   * @param error - HttpErrorResponse from the backend
   * @returns Observable that throws a cleaned error message
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    let cleanedError = 'An unexpected error occurred.';
    console.log(error.error.errors);
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
