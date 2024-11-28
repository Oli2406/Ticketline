import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard {

  constructor(private authService: AuthService,
              private router: Router) {
  }

  canActivate(): boolean {
    if (this.router.url === '/verify-reset-code' || this.router.url === '/reset-password') {
      return true;
    }
    if (this.authService.isLoggedIn()) {
      return true;
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }
}
