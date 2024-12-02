import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { AdminService } from '../../services/admin.service';
import { Router } from '@angular/router';
import { RegisterUser } from '../../dtos/register-data';


@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {
  // Error flag
  error = false;
  errorMessage = '';

  // User data
  users: any[] = [];
  currentUserEmail = '';

  constructor(private authService: AuthService,
              private userService: AdminService,
              private router: Router) {}

  ngOnInit(): void {
    this.loadUsers();
    this.currentUserEmail = this.authService.getUserEmailFromToken();
  }

  /**
   * Load users from the backend
   */
  loadUsers(): void {
    this.userService.getUsers().subscribe({
      next: (data) => {
        this.users = data;
      },
      error: (err) => {
        this.error = true;
        this.errorMessage = 'Failed to load users.';
      }
    });
  }

  /**
   * Lock a user account
   */
  lockUser(userId: number): void {
    this.userService.lockUser(userId).subscribe({
      next: () => this.loadUsers(),
      error: (err) => {
        this.error = true;
        this.errorMessage = err.error.errorMessage;
      }
    });
  }

  /**
   * Unlock a user account
   */
  unlockUser(userId: number): void {
    this.userService.unlockUser(userId).subscribe( {
      next:() => this.loadUsers(),
      error: err => {
        this.error = true;
        this.errorMessage = err.error.errorMessage;
      }
    });
  }

  /**
   * Reset a user's password
   */
  resetPassword(userId: number): void {
    //TODO implement
  }

  /**
   * Clear error messages
   */
  vanishError(): void {
    this.error = false;
  }

  navigateToCreateUser(): void {
    this.router.navigate(['/admin/createUser']);
    console.log('Navigating to Create User');
  }

  navigateToCreateNews(): void {
    this.router.navigate(['/admin/createNews']);
  }

  navigateToCreateMerchandise(): void {
    this.router.navigate(['admin/createMerchandise']);
  }

  navigateToCreateEvent(): void {
    this.router.navigate(['/admin/createEvent']);
    console.log('Navigating to Create Event');
  }
}
