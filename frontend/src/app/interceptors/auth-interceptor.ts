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
      `${this.globals.backendUri}/authentication/register`
    ];

    if (excludedPaths.some(path => req.url.startsWith(path))) {
      return next.handle(req);
    }

    const authToken = this.authService.getAuthToken();
    const resetToken = this.authService.getResetToken();
    const tokenToUse = authToken || resetToken;

    if (tokenToUse) {
      const clonedReq = req.clone({
        headers: req.headers.set('Authorization', 'Bearer ' + tokenToUse),
      });
      return next.handle(clonedReq);
    }

    return next.handle(req);
  }

}
