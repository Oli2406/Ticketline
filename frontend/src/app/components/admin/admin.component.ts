import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {AdminService} from '../../services/admin.service';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {DeleteUserDto} from "../../dtos/user-data";
import {ConfirmationDialogMode} from "../confirm-dialog/confirm-dialog.component";
import {UserService} from "../../services/user.service";
import {ErrorFormatterService} from "../../services/error-formatter.service";

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {
  error = false;
  errorMessage = '';

  users: any[] = [];
  displayedUsers: any[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 50;
  totalPages: number = 0;
  currentUserEmail = '';

  ConfirmationDialogMode = ConfirmationDialogMode;
  showConfirmDeletionDialog = false;
  deleteMessage = 'Do you really want to delete this customer?';

  selectedUserEmail = '';

  constructor(private authService: AuthService,
              private adminService: AdminService,
              private userService: UserService,
              private router: Router,
              private toastr: ToastrService,) {
  }

  ngOnInit(): void {
    this.loadUsers();
    this.currentUserEmail = this.authService.getUserEmailFromToken();
  }

  loadUsers(): void {
    this.adminService.getUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.updateTotalPages();
        this.updateDisplayedUsers();
      },
      error: (err) => {
        this.error = true;
        this.errorMessage = 'Failed to load users.';
      }
    });
  }

  lockUser(userId: number): void {
    this.adminService.lockUser(userId).subscribe({
      next: () => this.loadUsers(),
      error: (err) => {
        this.error = true;
        this.errorMessage = err.error.errorMessage;
      }
    });
  }

  unlockUser(userId: number): void {
    this.adminService.unlockUser(userId).subscribe({
      next: () => this.loadUsers(),
      error: err => {
        this.error = true;
        this.errorMessage = err.error.errorMessage;
      }
    });
  }

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

  deleteUser() {
    let userToDelete: DeleteUserDto;

    if (this.currentUserEmail === this.selectedUserEmail) {
      userToDelete = {
        email: this.selectedUserEmail,
        authToken: this.authService.getAuthToken()
      }
    } else {
      userToDelete = {
        email: this.selectedUserEmail,
        authToken: null
      }
    }

    this.userService.deleteUser(userToDelete).subscribe({
      next: () => {
        this.toastr.success("Successfully deleted account.", "Success");
        this.showConfirmDeletionDialog = false;
        this.ngOnInit();

        if (userToDelete.authToken !== null) {
          this.authService.clearAuthToken();
        }
      },
      error: (err) => {
        const errors = Array.isArray(err.error.errors)
          ? err.error.errors
          : err.error.errors.split(/\n/);
        const errorList = errors
        .map((error) => `<li>${error.trim().replace("[", "").replace("]", "")}</li>`)
        .join('');
        this.toastr.error(`<ul>${errorList}</ul>`, "Error", {
          enableHtml: true,
        });

        this.showConfirmDeletionDialog = false;
      }
    });
  }

  showDeleteMessage(email: string) {
    this.showConfirmDeletionDialog = true;
    this.selectedUserEmail = email;
  }

  updateTotalPages() {
    this.totalPages = Math.ceil(this.users.length / this.itemsPerPage);
  }

  updateDisplayedUsers() {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;

    this.displayedUsers = this.users.slice(startIndex, endIndex);
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updateDisplayedUsers();
    }
  }

  paginationRange: number = 5;

  get paginationButtons(): number[] {
    const start = Math.max(1, this.currentPage - Math.floor(this.paginationRange / 2));
    const end = Math.min(this.totalPages, start + this.paginationRange - 1);
    const adjustedStart = Math.max(1, end - this.paginationRange + 1);

    return Array.from({ length: end - adjustedStart + 1 }, (_, i) => adjustedStart + i);
  }
}
