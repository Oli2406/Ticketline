import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-send-email',
  templateUrl: './send-email.component.html',
  styleUrls: ['./send-email.component.scss']
})
export class SendEmailComponent implements OnInit {
  emailForm!: FormGroup;
  submitted = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.emailForm = this.fb.group({
      username: ['', [Validators.required, Validators.email]]
    });
  }

  resetPassword(): void {
    this.submitted = true;

    if (!this.emailForm.valid) {
      this.toastr.warning('Please enter a valid email address.', 'Invalid Input');
      return;
    }

    const email = this.emailForm.controls.username.value;
    this.authService.sendEmailToResetPassword(email).subscribe({
      next: () => {
        this.toastr.success('Password reset email sent successfully. Please check your inbox.', 'Success');
        this.router.navigate(['/verify-reset-code']);
      },
      error: (err) => {
        const errorMessage = typeof err.error === 'object' ? err.error.error : err.error;
        this.toastr.error(errorMessage || 'An error occurred while sending the email. Please try again.', 'Error');
      }
    });
  }
}
