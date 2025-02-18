import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../../services/auth.service';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {ErrorFormatterService} from "../../../services/error-formatter.service";

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
    private toastr: ToastrService,
    private errorFormatterService: ErrorFormatterService
  ) {
  }

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
        this.toastr.error(err.error,'Error resetting password');
        this.router.navigate(['/login']);
      }
    });
  }
}
