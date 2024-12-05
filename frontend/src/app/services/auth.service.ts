import {Injectable} from '@angular/core';
import {AuthRequest, ResetPasswordTokenDto} from '../dtos/auth-request';
import {catchError, Observable, of} from 'rxjs';
import { HttpClient } from '@angular/common/http';
import {tap} from 'rxjs/operators';
import {jwtDecode} from 'jwt-decode';
import {Globals} from '../global/globals';
import {UserRegistrationDto} from "../dtos/register-data";
import {UserResetPasswordDto} from "../dtos/user-data";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private authBaseUri: string = this.globals.backendUri + '/authentication';
  private resetTokenKey = 'resetPasswordToken';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Login in the user. If it was successful, a valid JWT token will be stored
   *
   * @param authRequest User data
   */
  loginUser(authRequest: AuthRequest): Observable<string> {
    return this.httpClient.post(this.authBaseUri, authRequest, {responseType: 'text'})
      .pipe(
        tap((authResponse: string) => this.setToken(authResponse))
      );
  }


  /**
   * Check if a valid JWT token is saved in the localStorage
   */
  isLoggedIn(): boolean {
    if(this.getResetTokenFromStorage()) {
      return false;
    }

    const token = this.getToken();
    return !!token && this.getTokenExpirationDate(token) > new Date();
  }

  private getTokenExpirationDate(token: string): Date {
    const decoded: any = jwtDecode(token);
    if (decoded.exp === undefined) {
      return null;
    }
    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);
    return date;
  }

  logoutUser(): void {
    const token = this.getToken();
    const email = this.getUserEmailFromToken();

    if (token && email) {
      const userLogoutDto = {
        email: email,
        authToken: token
      };

      this.httpClient.delete(this.authBaseUri, {
        body: userLogoutDto
      }).subscribe({
        next: () => {
          localStorage.removeItem('authToken');
        },
        error: (err) => {
          //TODO fehlermeldung
          console.error('Logout failed', err);
        }
      });
    } else {
      //TODO fehlermeldung
      console.warn('No token or email found for logout');
    }
  }


  getToken() {
      return localStorage.getItem('authToken') || this.getResetTokenFromStorage();
  }

  /**
   * Returns the user role based on the current token
   */
  getUserRole() {
    if (this.getToken() != null) {
      const decoded: any = jwtDecode(this.getToken());
      const authInfo: string[] = decoded.rol;
      if (authInfo.includes('ROLE_ADMIN')) {
        return 'ADMIN';
      } else if (authInfo.includes('ROLE_USER')) {
        return 'USER';
      }
    }
    return 'UNDEFINED';
  }

  isUserAdmin() {
    return this.getUserRole() === 'ADMIN';
  }

  private setToken(authResponse: string) {
    localStorage.setItem('authToken', authResponse);
  }

  getUserEmailFromToken(): string | null {
    const token = this.getToken();
    if (token) {
      const decoded: any = jwtDecode(token);
      return decoded.sub;
    }
    return null;
  }

  validateTokenInBackend(token: string): Observable<boolean> {
    return this.httpClient.get<boolean>(`${this.authBaseUri}/validate-token`).pipe(
      catchError(() => of(false))
    );
  }

  validateResetTokenInBackend(token: string): Observable<boolean> {
    return this.httpClient.get<boolean>(`${this.authBaseUri}/validate-reset-token`)
    .pipe(
      catchError((err) => {
        console.error('Failed to validate reset token:', err);
        return of(false);
      })
    );
  }

  validateToken() {
    const token = this.getToken();

    if (!token) {
      return of(false);
    }

    this.validateTokenInBackend(token);
    this.validateResetTokenInBackend(token);
  }

  storeResetToken(token: string): void {
    localStorage.setItem(this.resetTokenKey, token);
  }

  getResetTokenFromStorage(): string | null {
    return localStorage.getItem(this.resetTokenKey);
  }

  clearResetToken(): void {
    localStorage.removeItem(this.resetTokenKey);
  }

  sendEmailToResetPassword(email:string): Observable<string> {
    return this.httpClient.post(`${this.authBaseUri}/send-email`, email, {responseType: 'text'})
    .pipe(
      tap((authResponse: string) => {
        this.storeResetToken(authResponse);
        console.log(authResponse)
      })
    );
  }

  resetPassword(data: UserResetPasswordDto): Observable<boolean> {
    return this.httpClient.post<boolean>(`${this.authBaseUri}/reset-password`, data).pipe(
      catchError((err) => {
        console.error('Failed to validate reset token:', err);
        return of(false);
      })
    );
  }

  verifyResetCode(token: ResetPasswordTokenDto) {
    return this.httpClient.post<void>(`${this.authBaseUri}/verify-reset-code`, token);
  }
}
