import {Injectable} from "@angular/core";
import {HttpErrorResponse} from "@angular/common/http";
import {Observable, throwError} from "rxjs";

@Injectable({
  providedIn: "root"
})

export class ErrorFormatterService {


  public handleError(error: HttpErrorResponse): Observable<never> {
    let cleanedError = 'An unexpected error occurred.';
    console.log(error.error.errors)
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
