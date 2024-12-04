import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { ActivatedRoute } from '@angular/router';

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
      private route: ActivatedRoute
  ) {}

  ngOnInit(): void {

    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      if (token) {
        this.verifyToken(token);
      } else {
        console.error('No token found in the URL');
      }
    });


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

  verifyToken(token: string): void {
    this.authService.validateResetTokenInBackend(token).subscribe({
      next: (isValid) => {
        if (!isValid) {
          console.warn('Token is invalid or expired');
          this.router.navigate(['/login']);
        }
      },
      error: (err) => {
        console.error('Error validating token:', err);
        this.router.navigate(['/login']);
      }
    });
  }

  private passwordsMatchValidator(group: FormGroup): { [key: string]: boolean } | null {
    return group.get('newPassword')?.value === group.get('confirmPassword')?.value
        ? null
        : { passwordMismatch: true };
  }

  resetPassword(): void {
    this.submitted = true;
    if (this.resetPasswordForm.invalid) {
      return;
    }

    const newPassword = this.resetPasswordForm.value.newPassword;

    this.authService.resetPassword(newPassword).subscribe({
      next: () => {
        console.log('Password reset successful');
        this.router.navigate(['/login']); // Redirect to login after successful reset
      },
      error: (error) => {
        console.error('Password reset failed:', error);
        this.errorMessage = 'Failed to reset password. Please try again.';
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
