import { Component, OnInit } from "@angular/core";
import {FormGroup, UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../services/auth.service";
import {UserService} from "../../services/user.service";
import {UserToUpdateDto} from "../../dtos/register-data";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-user-account',
  templateUrl: './user-account.component.html',
  styleUrls: ['./user-account.component.scss']
})
export class UserAccountComponent implements OnInit {
  editUser: UntypedFormGroup;
  submitted = false;

  showNewPassword: boolean = false;
  showConfirmPassword: boolean = false;

  constructor(private fb: UntypedFormBuilder,
              private authService: AuthService,
              private userService: UserService,
              private router: Router,
              private toastr: ToastrService) {}

  ngOnInit(): void {
    this.fillForm();
  }

  private fillForm() {
    const email = this.authService.getUserEmailFromToken();
    const firstName = this.authService.getUserFirstNameFromToken();
    const lastName = this.authService.getUserLastNameFromToken();

    this.editUser = this.fb.group(
      {
        username: [email, [Validators.required, Validators.email]],
        firstName: [firstName, Validators.required],
        lastName: [lastName, Validators.required],
        newPassword: ['', [Validators.minLength(8)]],
        confirmPassword: ['']
      },
      {
        validators: this.passwordsMatchValidator
      }
    );
  }

  private passwordsMatchValidator(group: FormGroup): { [key: string]: boolean } | null {
    return group.get('newPassword')?.value === group.get('confirmPassword')?.value
      ? null
      : { passwordMismatch: true };
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.editUser.invalid) {
      this.toastr.warning('Please fill out all required fields and ensure passwords match.', 'Form Invalid');
      return;
    }

    const userDto: UserToUpdateDto = {
      id: this.authService.getUserIdFromToken(),
      email : this.editUser.value.username,
      firstName: this.editUser.value.firstName,
      lastName: this.editUser.value.lastName,
      password: this.editUser.value.newPassword,
      confirmedPassword: this.editUser.value.confirmPassword,
      currentAuthToken: this.authService.getAuthToken()
    }
    this.userService.updateUser(userDto).subscribe({
      next: (newAuthToken:string) => {
        this.authService.clearAuthToken();
        this.authService.storeAuthToken(newAuthToken);

        this.toastr.success('Successfully updated account information.', 'Success');
        this.router.navigate(['/home']);
      },
      error: (err) => {
        this.toastr.error(err.errors.errors, 'FAILED to update account');
      }
    });

  }

  toggleNewPasswordVisibility(): void {
    this.showNewPassword = !this.showNewPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  reset() {
    this.fillForm();
  }
}
