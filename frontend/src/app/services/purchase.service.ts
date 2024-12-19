import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Globals } from '../global/globals';
import { Purchase, PurchaseListDto } from '../dtos/purchase';

@Injectable({
  providedIn: 'root',
})
export class PurchaseService {
  private apiUrl: string = this.globals.backendUri + '/purchase';

  constructor(private http: HttpClient, private globals: Globals) {}

  /**
   * Get all purchases for a specific user by userId
   */
  getPurchasesByUser(encryptedUserId: string): Observable<PurchaseListDto[]> {
    const url = `${this.apiUrl}/user/${encryptedUserId}`;
    return this.http.get<PurchaseListDto[]>(url).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Get a specific purchase by purchaseId
   */
  getPurchaseById(purchaseId: number): Observable<PurchaseListDto> {
    const url = `${this.apiUrl}/${purchaseId}`;
    return this.http.get<PurchaseListDto>(url).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Create a new purchase
   */
  createPurchase(purchase: Purchase): Observable<Purchase> {
    console.log(this.apiUrl);
    return this.http.post<Purchase>(this.apiUrl, purchase).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Handle HTTP errors
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
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
