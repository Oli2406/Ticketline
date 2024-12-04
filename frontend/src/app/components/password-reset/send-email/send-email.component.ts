import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import {Router} from "@angular/router";

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
      private authService: AuthService,
      private router: Router
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
      this.authService.sendEmailToResetPassword(email).subscribe({
        next: () =>{
          this.router.navigate(['/verify-reset-code']);
        },
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
