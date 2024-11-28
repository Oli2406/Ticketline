import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-verify-reset-code',
  templateUrl: './send-email.component.html',
  styleUrls: ['./send-email.component.scss']
})
export class SendEmailComponent implements OnInit {
  emailForm!: FormGroup;
  submitted = false;
  error = false;
  errorMessage: string | null = null;

  constructor(
      private fb: FormBuilder,
      private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.emailForm = this.fb.group({
      username: ['', [Validators.required, Validators.email]]
    });
  }

  resetPassword(): void {
    this.submitted = true;

    if (this.emailForm.valid) {
      const email = this.emailForm.controls.username.value;
      this.authService.resetPassword(email).subscribe({
        next: () =>{},
        error: err => {
          console.log(err.message);
          this.error = true;
          if (typeof err.error === 'object') {
            this.errorMessage = err.error.error;
          } else {
            this.errorMessage = err.error;
          }
        }
      });
    }
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }
}
