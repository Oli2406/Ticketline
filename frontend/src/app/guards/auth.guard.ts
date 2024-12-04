import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';
import {catchError, Observable, of} from 'rxjs';
import {tap} from "rxjs/operators";

@Injectable({
  providedIn: 'root',
})
export class AuthGuard {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    console.log('AuthGuard triggered for URL:', state.url);

    if (state.url.startsWith('/reset-password') || state.url.startsWith('/verify-reset-code')) {
      const token = this.authService.getResetTokenFromStorage();

      if (!token) {
        console.warn('No reset token found in storage');
        this.router.navigate(['/login']);
        return false;
      }

      return this.authService.validateResetTokenInBackend(token).pipe(
        tap((isValid) => {
          if (!isValid) {
            console.warn('Reset token is invalid or expired');
            this.authService.clearResetToken();
            this.router.navigate(['/login']);
          }
        }),
        catchError((err) => {
          console.error('Error validating reset token:', err);
          this.authService.clearResetToken();
          this.router.navigate(['/login']);
          return of(false);
        })
      );
    }

    if (this.authService.isLoggedIn()) {
      return true;
    } else {
      return this.router.createUrlTree(['/login']);
    }
  }

}
