import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { AuthService } from '../services/auth.service';
import { Observable } from 'rxjs';
import { Globals } from '../global/globals';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService, private globals: Globals) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const excludedPaths = [
      `${this.globals.backendUri}/authentication/login`,
      `${this.globals.backendUri}/authentication/register`,
    ];

    const resetPasswordPaths = [
      `${this.globals.backendUri}/authentication/send-email`,
      `${this.globals.backendUri}/authentication/verify-reset-code`,
      `${this.globals.backendUri}/authentication/reset-password`,
      `${this.globals.backendUri}/authentication/validate-reset-token`
    ];

    // Do not intercept requests to excluded paths
    if (excludedPaths.some(path => req.url.startsWith(path))) {
      return next.handle(req);
    }

    // Use Reset-Token only for reset-password endpoints
    if (resetPasswordPaths.some(path => req.url.startsWith(path))) {
      const resetToken = this.authService.getResetToken();
      if (resetToken) {
        const clonedReq = req.clone({
          headers: req.headers.set('Authorization', 'Bearer ' + resetToken),
        });
        return next.handle(clonedReq);
      }
    }

    // Use Auth-Token for all other requests
    const authToken = this.authService.getAuthToken();
    if (authToken) {
      const clonedReq = req.clone({
        headers: req.headers.set('Authorization', 'Bearer ' + authToken),
      });
      return next.handle(clonedReq);
    }

    // Proceed without a token if none is found
    return next.handle(req);
  }
}
