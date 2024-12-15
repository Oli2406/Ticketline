import { Component, OnInit } from "@angular/core";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-user-account',
  templateUrl: './user-account.component.html',
  styleUrls: ['./user-account.component.scss']
})
export class UserAccountComponent implements OnInit {
  editUser: UntypedFormGroup;
  submitted = false;

  constructor(private fb: UntypedFormBuilder, private authService: AuthService) {}

  ngOnInit(): void {
    const email = this.authService.getUserEmailFromToken();
    const firstName = this.authService.getUserFirstNameFromToken();
    const lastName = this.authService.getUserLastNameFromToken();

    this.editUser = this.fb.group({
      username: [email, [Validators.required, Validators.email]],
      firstName: [firstName, Validators.required],
      lastName: [lastName, Validators.required],
    });
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.editUser.invalid) {
      return;
    }

    console.log("submitted");
    //TODO implement
  }
}
