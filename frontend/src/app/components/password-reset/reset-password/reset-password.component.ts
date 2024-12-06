import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { UserResetPasswordDto } from "../../../dtos/user-data";

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {
  resetPasswordForm!: FormGroup;
  submitted = false;
  errorMessage: string | null = null;

  showNewPassword: boolean = false;
  showConfirmPassword: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.resetPasswordForm = this.fb.group(
      {
        newPassword: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', Validators.required]
      },
      {
        validators: this.passwordsMatchValidator
      }
    );
  }

  private passwordsMatchValidator(group: FormGroup): { [key: string]: boolean } | null {
    return group.get('newPassword')?.value === group.get('confirmPassword')?.value
      ? null
      : { passwordMismatch: true };
  }

  resetPassword(): void {
    this.submitted = true;

    if (this.resetPasswordForm.invalid) {
      this.toastr.warning('Please fill out all required fields and ensure passwords match.', 'Form Invalid');
      return;
    }

    const userToResetPassword: UserResetPasswordDto = {
      tokenToResetPassword: this.authService.getResetToken(),
      newPassword: this.resetPasswordForm.value.newPassword,
      newConfirmedPassword: this.resetPasswordForm.value.confirmPassword
    };

    this.authService.resetPassword(userToResetPassword).subscribe({
      next: () => {
        this.toastr.success('Password reset successful! You can now log in with your new password.', 'Success');
        this.authService.clearResetToken();
        this.router.navigate(['/login']);
      },
      error: (error) => {
        this.toastr.error('Failed to reset password. Please try again.', 'Error');
      }
    });
  }

  toggleNewPasswordVisibility(): void {
    this.showNewPassword = !this.showNewPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }
}
