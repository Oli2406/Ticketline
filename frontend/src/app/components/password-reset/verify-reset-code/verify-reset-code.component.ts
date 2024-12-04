import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import {ResetPasswordTokenDto} from "../../../dtos/auth-request";

@Component({
  selector: 'app-verify-reset-code',
  templateUrl: './verify-reset-code.component.html',
  styleUrls: ['./verify-reset-code.component.scss']
})
export class VerifyResetCodeComponent implements OnInit {
  verifyCodeForm!: FormGroup;
  submitted = false;
  errorMessage: string | null = null;

  constructor(
      private fb: FormBuilder,
      private authService: AuthService,
      private router: Router
  ) {}

  ngOnInit(): void {
    this.verifyCodeForm = this.fb.group({
      code: ['', Validators.required]
    });
  }

  verifyCode(): void {
    this.submitted = true;
    if (this.verifyCodeForm.invalid) {
      return;
    }

    const code = this.verifyCodeForm.value.code;
    const token: ResetPasswordTokenDto = {
      email: "anna.simhofer@hotmail.com",
      code: code
    }

    this.authService.verifyResetCode(token).subscribe({
      next: () => {
        this.router.navigate(['/reset-password']);
      },
      error: (error) => {
        console.error('Verification failed:', error);
        this.errorMessage = 'The reset code is invalid or expired.';
      }
    });
  }
}
