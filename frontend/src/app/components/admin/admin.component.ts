import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {AdminService} from '../../services/admin.service';
import {Router} from '@angular/router';
import { ToastrService } from 'ngx-toastr';

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
              private adminService: AdminService,
              private router: Router,
              private toastr: ToastrService) {
  }

  ngOnInit(): void {
    this.loadUsers();
    this.currentUserEmail = this.authService.getUserEmailFromToken();
  }

  /**
   * Load users from the backend
   */
  loadUsers(): void {
    this.adminService.getUsers().subscribe({
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
    this.adminService.lockUser(userId).subscribe({
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
    this.adminService.unlockUser(userId).subscribe({
      next: () => this.loadUsers(),
      error: err => {
        this.error = true;
        this.errorMessage = err.error.errorMessage;
      }
    });
  }

  /**
   * Reset a user's password
   */
  resetPassword(email: string): void {
    this.adminService.sendEmailToResetPassword(email).subscribe({
      next: () => {
        this.toastr.success('Reset password e-Mail sent to ' + email, 'Success');
      },
      error: err => {
        this.error = true;
        this.errorMessage = err.error.errorMessage;
      }
    });
  }

  /**
   * Clear error messages
   */
  vanishError(): void {
    this.error = false;
  }

  navigateToCreateUser(): void {
    this.router.navigate(['/admin/createUser']);
  }

  navigateToCreateNews(): void {
    this.router.navigate(['/admin/createNews']);
  }

  navigateToCreateEvent(): void {
    this.router.navigate(['/admin/createEvent']);
  }

  navigateToCreateMerchandise() {
    this.router.navigate(['/admin/createMerchandise']);
  }
}
