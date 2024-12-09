import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { ResetPasswordTokenDto } from "../../../dtos/auth-request";

@Component({
  selector: 'app-verify-reset-code',
  templateUrl: './verify-reset-code.component.html',
  styleUrls: ['./verify-reset-code.component.scss']
})
export class VerifyResetCodeComponent implements OnInit {
  verifyCodeForm!: FormGroup;
  submitted = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.verifyCodeForm = this.fb.group({
      code: ['', Validators.required]
    });
  }

  verifyCode(): void {
    this.submitted = true;

    if (this.verifyCodeForm.invalid) {
      this.toastr.warning('Please enter the reset code to proceed.', 'Invalid Input');
      return;
    }

    const code = this.verifyCodeForm.value.code;
    const token: ResetPasswordTokenDto = {
      tokenFromStorage: this.authService.getResetToken(),
      code: code
    };

    this.authService.verifyResetCode(token).subscribe({
      next: () => {
        this.toastr.success('Reset code verified successfully. Redirecting to reset password page.', 'Success');
        this.router.navigate(['/reset-password']);
      },
      error: (err) => {
        if(err.error.errorMessage) {
          this.toastr.error(err.error.errorMessage, 'Verification Failed');
        } else if(err.error) {
          // In case too many attempts, redirect to login and delete token from local storage
          this.toastr.error(err.error, 'Verification Failed');
          this.authService.clearResetToken();
          this.router.navigate(['/login']);
        }
      }
    });
  }
}
