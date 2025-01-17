import {Component} from '@angular/core';
import {AuthService} from "./services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'Ticketline';

  constructor(private authService: AuthService, public router: Router) {
  }

  ngOnInit(): void {
    if (this.authService.getAuthToken()) {
      this.authService.validateTokenInBackend().subscribe((isValid) => {
        if (!isValid) {
          this.authService.logoutUser();
          //this.router.navigate(['/home']);
        }
      }, error => {
        this.authService.clearAuthToken();
      });
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

    if (this.authService.getResetToken()) {
      this.authService.validateResetTokenInBackend().subscribe((isValid) => {
        if (!isValid) {
          this.authService.clearResetToken();
        }
      }, error => {
        this.authService.clearResetToken();
      });
    }
  }
  }
