import { Injectable } from '@angular/core';
import { AuthRequest, ResetPasswordTokenDto } from '../dtos/auth-request';
import { catchError, Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { jwtDecode } from 'jwt-decode';
import { Globals } from '../global/globals';
import { UserResetPasswordDto } from '../dtos/user-data';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private authBaseUri: string = this.globals.backendUri + '/authentication';
  private resetTokenKey = 'resetPasswordToken';

  constructor(private httpClient: HttpClient, private globals: Globals, private toastr: ToastrService) {}

  /**
   * Logs in the user and stores a valid JWT token upon success.
   *
   * @param authRequest User login credentials
   */
  loginUser(authRequest: AuthRequest): Observable<string> {
    return this.httpClient.post(this.authBaseUri, authRequest, { responseType: 'text' }).pipe(
      tap((authResponse: string) => {
        this.storeAuthToken(authResponse);
        this.toastr.success('Login successful!', 'Success');
      }),
      catchError((err) => {
        this.toastr.error('Login failed. Please check your credentials.', 'Error');
        return of('');
      })
    );
  }

  /**
   * Checks if a valid JWT token is saved in the localStorage.
   */
  isLoggedIn(): boolean {
    const token = this.getAuthToken();
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

  /**
   * Logs out the user and clears their token from localStorage.
   */
  logoutUser(): void {
    const token = this.getAuthToken();
    const email = this.getUserEmailFromToken();

    if (token && email) {
      const userLogoutDto = { email, authToken: token };

      this.httpClient.delete(this.authBaseUri, { body: userLogoutDto }).subscribe({
        next: () => {
          this.clearAuthToken();
          this.toastr.success('You have been logged out successfully.', 'Logout');
        },
        error: () => {
          this.toastr.error('Logout failed. Please try again.', 'Error');
        },
      });
    } else {
      this.toastr.warning('No active session found to log out.', 'Warning');
    }
  }

  /**
   * Retrieves the authentication token from localStorage.
   */
  getAuthToken(): string | null {
    return localStorage.getItem('authToken');
  }

  /**
   * Retrieves the reset token from localStorage.
   */
  getResetToken(): string | null {
    return localStorage.getItem(this.resetTokenKey);
  }

  /**
   * Stores the authentication token in localStorage.
   */
  storeAuthToken(token: string): void {
    localStorage.setItem('authToken', token);
  }

  /**
   * Stores the reset token in localStorage.
   */
  storeResetToken(token: string): void {
    localStorage.setItem(this.resetTokenKey, token);
  }

  /**
   * Clears the authentication token from localStorage.
   */
  clearAuthToken(): void {
    localStorage.removeItem('authToken');
  }

  /**
   * Clears the reset token from localStorage.
   */
  clearResetToken(): void {
    localStorage.removeItem(this.resetTokenKey);
  }

  /**
   * Retrieves the user's role based on the token.
   */
  getUserRole(): string {
    const token = this.getAuthToken();
    if (token) {
      const decoded: any = jwtDecode(token);
      const authInfo: string[] = decoded.rol;
      if (authInfo.includes('ROLE_ADMIN')) {
        return 'ADMIN';
      } else if (authInfo.includes('ROLE_USER')) {
        return 'USER';
      }
    }
    return 'UNDEFINED';
  }

  /**
   * Checks if the logged-in user is an admin.
   */
  isUserAdmin(): boolean {
    return this.getUserRole() === 'ADMIN';
  }

  /**
   * Retrieves the email from the authentication token.
   */
  getUserEmailFromToken(): string | null {
    const token = this.getAuthToken();
    if (token) {
      const decoded: any = jwtDecode(token);
      return decoded.sub;
    }
    return null;
  }

  /**
   * Validates the given token with the backend.
   */
  validateTokenInBackend(token: string): Observable<boolean> {
    return this.httpClient.get<boolean>(`${this.authBaseUri}/validate-token`).pipe(
      tap(() => this.toastr.info('Token validation successful.', 'Info')),
      catchError(() => {
        this.toastr.error('Token validation failed.', 'Error');
        return of(false);
      })
    );
  }

  /**
   * Validates the reset token with the backend.
   */
  validateResetTokenInBackend(token: string): Observable<boolean> {
    return this.httpClient.get<boolean>(`${this.authBaseUri}/validate-reset-token`).pipe(
      tap(() => this.toastr.info('Reset token validation successful.', 'Info')),
      catchError(() => {
        this.toastr.error('Reset token validation failed.', 'Error');
        return of(false);
      })
    );
  }

  /**
   * Sends a reset password email to the user.
   */
  sendEmailToResetPassword(email: string): Observable<string> {
    return this.httpClient.post(`${this.authBaseUri}/send-email`, email, { responseType: 'text' }).pipe(
      tap(() => {
        this.toastr.success('Reset password email sent successfully. Please check your inbox.', 'Success');
      }),
      catchError(() => {
        this.toastr.error('Failed to send reset password email.', 'Error');
        return of('');
      })
    );
  }

  /**
   * Resets the user's password using the provided data.
   */
  resetPassword(data: UserResetPasswordDto): Observable<boolean> {
    return this.httpClient.post<boolean>(`${this.authBaseUri}/reset-password`, data).pipe(
      tap(() => {
        this.toastr.success('Password reset successfully.', 'Success');
      }),
      catchError(() => {
        this.toastr.error('Failed to reset password.', 'Error');
        return of(false);
      })
    );
  }

  /**
   * Verifies the reset code provided by the user.
   */
  verifyResetCode(token: ResetPasswordTokenDto): Observable<void> {
    return this.httpClient.post<void>(`${this.authBaseUri}/verify-reset-code`, token).pipe(
      tap(() => {
        this.toastr.success('Reset code verified successfully.', 'Success');
      }),
      catchError(() => {
        this.toastr.error('Failed to verify reset code.', 'Error');
        return of();
      })
    );
  }
}
