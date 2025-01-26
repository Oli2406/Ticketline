import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Globals} from "../global/globals";
import {PurchaseDetailDto, PurchaseListDto} from "../dtos/purchase";
import {Observable, throwError} from "rxjs";
import {catchError} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class CancelpurchaseService {

  private apiUrl: string = this.globals.backendUri + '/cancelpurchase';

  constructor(private http: HttpClient, private globals: Globals) {}

  //TODO see if this works
  saveCancelledPurchase(purchase: PurchaseListDto): Observable<PurchaseListDto> {
    console.log(this.apiUrl);
    const url = `${this.apiUrl}/${purchase.purchaseId}`;
    return this.http.put<PurchaseListDto>(url, purchase).pipe(
      catchError(this.handleError)
    );
  }

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
   * Get all detailed purchases for a specific user by userId
   */
  getPurchaseDetailsByUser(encryptedUserId: string): Observable<PurchaseDetailDto[]> {
    const url = `${this.apiUrl}/details/${encryptedUserId}`;
    return this.http.get<PurchaseDetailDto[]>(url).pipe(
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
