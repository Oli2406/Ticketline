import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { catchError, Observable, of } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard {
  private token: string;

  constructor(
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {

    if (state.url.startsWith('/reset-password') || state.url.startsWith('/verify-reset-code')) {
      const tokenFromUrl = this.extractTokenFromUrl(state.url);
      const tokenFromStorage = this.authService.getResetToken();

      if (tokenFromUrl) {
        this.token = tokenFromUrl;
        this.authService.storeResetToken(this.token);
      } else if (tokenFromStorage) {
        this.token = tokenFromStorage;
      }

      if (!this.token) {
        this.toastr.error('No reset token found. Redirecting to login.', 'Error');
        this.router.navigate(['/login']);
        return false;
      }

      return this.authService.validateResetTokenInBackend().pipe(
        tap((isValid) => {
          if (!isValid) {
            this.toastr.error('Reset token is invalid or expired.', 'Invalid Token');
            this.authService.clearResetToken();
            this.router.navigate(['/login']);
          }
        }),
        catchError((err) => {
          this.toastr.error('Failed to validate reset token.', 'Validation Error');
          this.authService.clearResetToken();
          this.router.navigate(['/login']);
          return of(false);
        })
      );
    }

    this.authService.isCurrentUserLoggedInInBackend().subscribe({
          next: isLoggedIn => {
            if (!isLoggedIn) {
              this.authService.clearAuthToken();
              this.router.navigate(['/login']);
            }
          }, error: () => {
            this.authService.clearAuthToken();
            this.router.navigate(['/login']);
          },
        }
    );

    if (this.authService.isLoggedIn()) {
      return true;
    } else {
      //this.toastr.warning('User is not logged in. Redirecting to login.', 'Access Denied');
      this.router.navigate(['/login']);
      return false;
    }
  }

  extractTokenFromUrl(url: string): string | null {
    const urlParams = new URLSearchParams(url.split('?')[1]);
    return urlParams.get('token');
  }
}
