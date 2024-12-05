import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {AuthRequest} from '../../dtos/auth-request';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm: UntypedFormGroup;
  // After first submission attempt, form validation will start
  submitted = false;
  showPassword: boolean = false;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  /**
   * Form validation will start after the method is called, additionally an AuthRequest will be sent
   */
  loginUser() {
    this.submitted = true;
    if (this.loginForm.valid) {
      const authRequest: AuthRequest = new AuthRequest(
        this.loginForm.controls.username.value,
        this.loginForm.controls.password.value
      );
      this.authenticateUser(authRequest);
    } else {
      this.toastr.error('Please fill in all fields correctly.', 'Invalid Input');
    }
  }

  /**
   * Send authentication data to the authService. If the authentication was successful, the user will be forwarded to the message page
   *
   * @param authRequest authentication data from the user login form
   */
  authenticateUser(authRequest: AuthRequest) {
    console.log('Try to authenticate user: ' + authRequest.email);
    this.authService.loginUser(authRequest).subscribe({
      next: () => {
        this.toastr.success('Login successful!', 'Success');
        this.router.navigate(['/message']);
      },
      error: error => {
        if (typeof error.error === 'object') {
          this.toastr.error(error.error.error, 'Authentication Failed');
        } else {
          this.toastr.error(error.error, 'Authentication Failed');
        }
      }
    });
  }

  ngOnInit() {}

}
