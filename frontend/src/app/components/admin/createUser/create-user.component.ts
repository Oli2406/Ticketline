import { Component } from '@angular/core';
import { AdminRegisterData, AdminUserRegistrationDto } from '../../../dtos/register-data';
import { AdminService } from '../../../services/admin.service';
import { ToastrService } from 'ngx-toastr';
import {RegisterService} from "../../../services/register.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.scss'],
})
export class CreateUserComponent {
  showPassword: boolean = false;
  showConfirmPassword: boolean = false;


  createUserData: AdminRegisterData = {
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmedPassword: '',
    isAdmin: false,
  };

  constructor(
    private adminService: AdminService,
    private toastr: ToastrService,
    private registerService: RegisterService,
    private router: Router,
  ) {}

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  onSubmit() {
    // Check if passwords match
    if (this.createUserData.password !== this.createUserData.confirmedPassword) {
      this.toastr.error('Passwords do not match!', 'Error');
      return;
    }

    // Prepare the payload as `AdminUserRegistrationDto`
    const payload: AdminUserRegistrationDto = {
      firstName: this.createUserData.firstName,
      lastName: this.createUserData.lastName,
      email: this.createUserData.email,
      password: this.createUserData.password,
      isAdmin: this.createUserData.isAdmin,
    };

    // Make the API call to create the user
    this.registerService.registerUser(payload).subscribe({
      next: () => {
        this.toastr.success('User created successfully!', 'Success');
        this.router.navigate(['/admin']);
        this.createUserData = {
          firstName: '',
          lastName: '',
          email: '',
          password: '',
          confirmedPassword: '',
          isAdmin: false,
        };
      },
      error: (err) => {
        console.error('Error creating user:', err.message);
        const errors = Array.isArray(err.message)
          ? err.message
          : err.message.split(/\n/);
        const errorList = errors
        .map((error) => `<li>${error.trim()}</li>`)
        .join('');
        this.toastr.error(`<ul>${errorList}</ul>`, 'Error creating user', {
          enableHtml: true,
        });
      },
    });
  }
}
