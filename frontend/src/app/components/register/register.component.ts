import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RegisterData } from '../../dtos/register-data';
import { RegisterService } from '../../services/register.service';
import { ToastrService } from 'ngx-toastr';
import { UserRegistrationDto } from "../../dtos/register-data";
import { Router } from '@angular/router';
import {NgClass} from "@angular/common";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    FormsModule,
    NgClass,
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {
  showPassword: boolean = false;
  showConfirmPassword: boolean = false;

  registerData: RegisterData = {
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmedPassword: '',
  };

  constructor(
    private registerService: RegisterService,
    private toastr: ToastrService,
    private router: Router
  ) {}

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  onSubmit() {
    const payload: UserRegistrationDto = {
      firstName: this.registerData.firstName,
      lastName: this.registerData.lastName,
      email: this.registerData.email,
      password: this.registerData.password,
    };
    if (this.registerData.password !== this.registerData.confirmedPassword) {
      this.toastr.error('Passwords do not match!', 'Error');
      return;
    }
    this.registerService.registerUser(payload).subscribe({
      next: () => {
        this.toastr.success('Registration successful!', 'Success');
        this.router.navigate(['']);
      },
      error: (err) => {
        console.error('Error during registration:', err.message);
        const errors = Array.isArray(err.message)
          ? err.message
          : err.message.split(/\n/);
        const errorList = errors
          .map((error) => `<li>${error.trim()}</li>`)
          .join('');
        this.toastr.error(`<ul>${errorList}</ul>`, 'Error creating account', {
          enableHtml: true,
        });
      },
    });
  }
}
