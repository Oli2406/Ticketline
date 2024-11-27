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

  constructor(private authService: AuthService,
              private userService: AdminService,
              private router: Router) {}

  ngOnInit(): void {
    this.loadUsers();
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
    //TODO implement
  }

  /**
   * Unlock a user account
   */
  unlockUser(userId: number): void {
    this.userService.unlockUser(userId).subscribe(() => this.loadUsers());
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
    console.log('Create News functionality will be implemented.');
  }

  navigateToCreateShow(): void {
    console.log('Create Show functionality will be implemented.');
  }

  navigateToCreateEvent(): void {
    console.log('Create Event functionality will be implemented.');
  }
}
